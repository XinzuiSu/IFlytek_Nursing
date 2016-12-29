package com.iflytek.medicalsdk_nursing.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.iflytek.medicalsdk_nursing.R;


/**
 * 语音动画效果
 * 绘制波形
 *
 * 使用surfaceview，在布局中添加该类即可
 *
 * 调用方法：
 * 1.       初始化 init（记得调用逆初始化destroy销毁线程）
 * 2.       调用start开始一次会话
 * 3.       调用setVolume设置音量（0-30，可以自己修改最大值）
 * 4.       调用stop停止录音等待结果
 * 5.       调用reset恢复初始状态
 * 6.       点击事件和按钮请自行添加逻辑
 *
 * Created by dingxiaolei on 16/10/21.
 */

public class GLWaveformView extends SurfaceView implements
		SurfaceHolder.Callback {
	private static final String TAG = GLWaveformView.class.getSimpleName();

	private SurfaceHolder holder;
	private HandlerThread mThread;
	public Handler mHandler = null;

	private static final float FRAME_PER_SECOND = 30;
	private static final long TIME_PER_FRAME = 1000 / (int)FRAME_PER_SECOND;

	private static final float LINE_PAINT_WIDTH = 3.0f;
	/** 圆点半径 */
	static final float DOTS_RADIUS = 6.0f;
	private Paint mDotsPaint;
	private Paint mLinePaint;
	private Path mPath;

	static final int State_Init 		= 1;
	static final int State_StartRecord 	= 2;
	static final int State_Recording 	= 3;
	/** 停止录音，振幅下降到最小值 */
	static final int State_StopRecord 	= 4;
	/** 直线拉开过渡效果 */
	static final int State_Transform 	= 5;
	/** 直线运动到圆弧效果 */
	static final int State_StartRecognize = 6;
	/** 圆弧等待效果 */
	static final int State_Waiting = 7;
	/** 长按话筒改变颜色 */
	static final int State_LongClick = 8;

	static final int DefaultStartRecordInterval 	= 10;
	static final int DefaultStopRecordInterval 		= 5;
	static final int DefaultTransformInterval 		= 5;
	static final int DefaultStartWaitInterval 		= 10;

	static final float kTrigW = 1.0f; // 周期
	static final float kGausA = 1.0f;
	static final float kGausB = 0.0f;
	static final float kGausC = 3.0f;

	/** 采样的范围 */
	static final float kMIN = (float) (-2.0f * Math.PI);
	static final float kMAX = (float) (2.0f * Math.PI);

	static final float kMicRadian = 0.75f; // 麦克风转动条的相对弧度（0到1之间，为1时是半圆）
	static final float kTrailRadian = (float) (Math.PI * 0.07f);

	/** 当前状态 */
	volatile int state;
	/** 音量 */
	float volume = 0f;
	/** 振幅 */
	float amplitude = 0f;
	/** 静音振幅 */
	static final float kDefaultSilenceAmpRate = 0.2f;
	float silenceAmpRate = kDefaultSilenceAmpRate;
	float stopAmpRate = 0;

	/** 正余弦函数相位 */
	float phase = 0f;
	float percent = 0f;

	/** 麦克风半径 */
	private float MIC_RADIUS = 80.0f;

	private Bitmap mWaitMicBitMap = null;
	private Bitmap mInitMicBitMap = null;
	private Bitmap mLongMicBitMap = null;

	/** 识别是否结束 */
	private boolean mIsOver = false;
	/** 是否是绘制在顶层 */
	private boolean isDrawOnTop = true;

	private boolean mInited = false;

	private onAnimationListener mAnimationListener = null;

	public interface onAnimationListener {
		//等待动画结束回调
		void onWaitingEnd();
	}

	public GLWaveformView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "init");
		holder = this.getHolder();
		holder.addCallback(this);
		holder.setFormat(PixelFormat.TRANSPARENT);
		initMicRadius();
	}

	private void initMicRadius() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		float circleRadius =  dm.widthPixels * 80.0f / 1080;
		this.MIC_RADIUS = circleRadius;
	}

	public synchronized void init() {
		Log.d(TAG, "initialize");
		if(!mInited) {
			//初始化画笔
			mDotsPaint = new Paint();
			mDotsPaint.setStrokeWidth(DOTS_RADIUS);
			mDotsPaint.setAntiAlias(true);
			mDotsPaint.setStyle(Paint.Style.FILL);
			mDotsPaint.setColor(Color.rgb(0, 204, 203));

			mLinePaint = new Paint();
			mLinePaint.setStrokeWidth(LINE_PAINT_WIDTH);
			mLinePaint.setAntiAlias(true);
			mLinePaint.setStyle(Paint.Style.STROKE);
			mLinePaint.setColor(Color.rgb(0, 204, 203));

			mPath = new Path();

			//bitmap 需要捕获oom异常
			try {
				mWaitMicBitMap = BitmapFactory.decodeResource(getResources(),
						R.mipmap.wave_form_mic);

				mInitMicBitMap = BitmapFactory.decodeResource(getResources(),
						R.mipmap.wave_form_mic2);
				mLongMicBitMap = BitmapFactory.decodeResource(getResources(),
						R.mipmap.wave_form_mic3);
			} catch (Exception e) {
			}

			initHandler();

			mInited = true;
		}
	}

	private void initHandler() {
		Log.d(TAG, "initHandler");
		mThread = new HandlerThread("waveform");// 创建一个绘图线程
		mThread.start();

		mHandler = new Handler(mThread.getLooper()) {

			@Override
			public void handleMessage(Message msg) {
				Log.d(TAG, "handlemsg:" + msg.what);
				switch (msg.what) {
					case State_Init: {
						percent = 0;
						myDraw(holder);
						break;
					}
					case State_StartRecord: {
						if (percent >= 1.0f) {
							state += 1;
							percent = 0;
							sendEmptyMessageDelayed(state, TIME_PER_FRAME);
						} else {
							sendEmptyMessageDelayed(state, TIME_PER_FRAME);

							flow();
							wave();
							percent += (1.0f / DefaultStartRecordInterval);
							myDraw(holder);

						}
						break;
					}
					case State_Recording: {
						sendEmptyMessageDelayed(state, TIME_PER_FRAME);

						flow();
						wave();
						myDraw(holder);
						break;
					}
					case State_StopRecord: {

						boolean isDraw = false;
						synchronized (this) {
							if(state != State_StopRecord) {
								return;
							}

							if (percent >= 1.0f) {
								state += 1;
								percent = 0;
								sendEmptyMessageDelayed(state, TIME_PER_FRAME);
							} else {
								sendEmptyMessageDelayed(state, TIME_PER_FRAME);

								Log.d(TAG, "handlemsg:" + state + ", percent=" + percent + ",amplitude=" + amplitude + ",silenceAmpRate=" + silenceAmpRate +
										", stopAmpRate=" + stopAmpRate);
								if(stopAmpRate == 0) {
									stopAmpRate = silenceAmpRate;
								}
								{
									percent += (1.0f / (DefaultStopRecordInterval / silenceAmpRate * stopAmpRate));
									if (percent >= 1.0f) {
										state += 1;
										percent = 0;
										sendEmptyMessageDelayed(state, TIME_PER_FRAME);
									} {
									flow();
									wave();
									isDraw = true;
								}
								}
							}

							Log.d(TAG, "handlemsg:" + state + ", " + percent + "," + amplitude);
						}

						if (isDraw){
							myDraw(holder);
						}
						break;
					}
					case State_Transform: {
						if (percent >= 1.0f) {
							state += 1;
							percent = 0;
							sendEmptyMessageDelayed(state, TIME_PER_FRAME);
						} else {
							sendEmptyMessageDelayed(state, TIME_PER_FRAME);

							percent += (1.0f / DefaultTransformInterval);
							myDraw(holder);
						}
						break;
					}
					case State_StartRecognize: {
						if (percent >= 1.0f) {
							state += 1;
							percent = 0;
							sendEmptyMessageDelayed(state, TIME_PER_FRAME);

						} else {
							sendEmptyMessageDelayed(state, TIME_PER_FRAME);
							percent += (1.0f / DefaultStartWaitInterval);
							myDraw(holder);
						}
						break;
					}
					case State_Waiting: {
						if (mIsOver) {
							if (mAnimationListener != null) {
								mAnimationListener.onWaitingEnd();
							}
							reset();
						} else {
							sendEmptyMessageDelayed(state, TIME_PER_FRAME);
							percent += (1.0f / DefaultStartRecordInterval);
							myDraw(holder);
						}

						break;
					}
					case State_LongClick:
						myDraw(holder);
						break;
					default:
						break;
				}
			}
		};

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
	                           int height) {
		Log.d(TAG, "surfaceChanged");
	}

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated");
		this.holder = holder;
		sendMsg(State_Init);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surfaceDestroyed");
	}

	public synchronized void destroy() {
		// 线程需要销毁
		if (mThread != null && mThread.isAlive()) {
			mHandler = null;
			mThread.quit();
			mThread = null;
		}

		//bitmap回收
		if(mInitMicBitMap != null) {
			mInitMicBitMap.recycle();
			mInitMicBitMap = null;
		}

		if(mWaitMicBitMap != null) {
			mWaitMicBitMap.recycle();
			mWaitMicBitMap = null;
		}

		//bitmap回收
		if(mLongMicBitMap != null) {
			mLongMicBitMap.recycle();
			mLongMicBitMap = null;
		}
	}

	public void setCircleRadius(float radius) {
		MIC_RADIUS = radius;
	}

	public synchronized boolean start() {
		Log.d(TAG, "start state:" + state);

//		if (state == State_StopRecord || state == State_Transform
//				|| state == State_StartRecognize) { 
//			return false;
//		}

		Log.d(TAG, "start2 state:" + state);

		mIsOver = false;

		mHandler.removeCallbacksAndMessages(null);
		sendMsg(State_StartRecord);
		return true;
	}

	public synchronized void stopListening() {

		Log.d(TAG, "stopListening state:" + state);
		if (state == State_Init || state == State_StopRecord
				|| state == State_Transform || state == State_StartRecognize
				|| state == State_Waiting) {
			return;
		}

		Log.d(TAG, "stopListening 2 state:" + state);

		stopAmpRate = amplitude;

		if(mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
			sendMsg(State_StopRecord);
		}
	}

	public synchronized void waiting() {

	}

	public synchronized void setOver(boolean over) {
		mIsOver = over;
	}


	public void setListener(onAnimationListener listener) {
		mAnimationListener = listener;
	}

	public synchronized void reset() {
		if(mHandler == null){
			return;
		}
		mIsOver = true;
		mHandler.removeCallbacksAndMessages(null);
		sendMsg(State_Init);
	}

	private void sendMsg(int what) {
		state = what;
		try {
			mHandler.sendMessage(Message.obtain(mHandler, state));
		} catch(Exception e) {
			Log.d(TAG, "sendMsg exception " + what);
			e.printStackTrace();
		}
	}

	private static final float amplitudeDelta = 0.05f;
	public void setVolume(int vol) {

		float v = vol / 30.0f;
		float delta = Math.abs(v - volume);

		Log.d(TAG, "Volume vol="+ vol + ",cur=" + v + ",last=" + volume);
		if(delta <= amplitudeDelta) {
			this.volume = v;
		} else  {
			if( v >= volume) {
				this.volume += amplitudeDelta;
			} else {
				this.volume -= amplitudeDelta;
			}
		}

	}

	private void flow() {
		float T = 0.5f; // 流动的速度，这里是0.5秒流动个一个周期（2π）
		float count = T / (1.0f / FRAME_PER_SECOND); // 需要多少帧
		float delta = (float) (2.0f * Math.PI / count); // 每帧的增量
		phase += delta;
	}

	private void wave() {
		switch (state) {
			case State_StartRecord:
				amplitude = silenceAmpRate * percent; // 从直线变曲线
				break;
			case State_Recording:
				amplitude = silenceAmpRate + (1 - silenceAmpRate-0.1f) * volume; // 响应音量
				Log.d(TAG, "Volume r"+ amplitude);
				break;
			case State_StopRecord:
				amplitude = stopAmpRate * (1 - percent); // 从曲线变直线
			default:
				break;
		}
	}


	float functionValue(float x) {
		return (float) (amplitude * Math.cos(kTrigW * x - phase) * kGausA * Math.pow(Math.E, - Math.pow(x - kGausB, 2.0f) / Math.pow(kGausC, 2.0f)));
	}

	private float piToAngle(double pi) {
		return (float) (pi * 360 / (2 * Math.PI));
	}

	public static void drawImage(Canvas canvas, Bitmap blt, float left, float top, float h, float w) {

		if(blt == null)
			return;

		RectF dst = new RectF();// 屏幕 >>目标矩形

		dst.left = left;
		dst.top = top;
		dst.right = left + h;
		dst.bottom = top + w;

		canvas.drawBitmap(blt, null, dst, null);
		dst = null;
	}

	protected void myDraw(SurfaceHolder holder) {
		Canvas canvas = null;
		synchronized (holder) {
			// 锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了，需要调用unlockCanvasAndPost(canvas)
			try {
				canvas = holder.lockCanvas();
			} catch (Exception e) {
				//部分手机抛java.lang.IllegalArgumentException异常
				Log.d(TAG, "lockCanvas exception!");
			}
			if (canvas == null) {
				return;
			}
//			canvas.drawColor(Color.WHITE);
			drawBg(canvas);
		}

		try {
			int width = getWidth();
			int height = getHeight();
			float midW = width / 2.0f;
			float midH = height / 2.0f;

			switch (state) {
				case State_Init: {
					// 绘制麦克风
					drawImage(canvas, mInitMicBitMap, midW - MIC_RADIUS, midH - MIC_RADIUS, MIC_RADIUS * 2, MIC_RADIUS * 2);
					break;
				}
				case State_StartRecord:
				case State_Recording:
				case State_StopRecord: {
					// 纠正坐标系，将坐标原点从左上角移到中心
					float xScale = width / (kMAX - kMIN);
					float yScale = midH * (-1); // 需要翻转
					float xOffset = midW;
					float yOffset = midH;

					float delta = 2.0f / xScale;

					mPath.reset();
					for (float x = kMIN; x <= kMAX + delta; x += delta) { // 0.2采样间隔
						float y = functionValue(x);
						float sx = x * xScale + xOffset;
						float sy = y * yScale + yOffset;
						if (x == kMIN) {
							mPath.moveTo(sx, sy);
						} else {
							mPath.lineTo(sx, sy);
						}
					}

					canvas.drawPath(mPath, mLinePaint);

					break;
				}
				case State_Transform: {
					float offset = MIC_RADIUS * percent;
					// 两个小圆
					canvas.drawCircle(midW - offset, midH, DOTS_RADIUS, mDotsPaint);
					canvas.drawCircle(midW + offset, midH, DOTS_RADIUS, mDotsPaint);

					// 两条直线
					mPath.reset();
					mPath.moveTo(midW - offset, midH);
					mPath.lineTo(0, midH);

					mPath.moveTo(midW + offset, midH);
					mPath.lineTo(width, midH);
					canvas.drawPath(mPath, mLinePaint);

					break;
				}
				case State_StartRecognize: {
					float offset = MIC_RADIUS;
					float delta = (midW - offset) * Math.min(percent, 1.0f);

					// 两条直线
					mPath.reset();
					mPath.moveTo(midW - offset, midH);
					mPath.lineTo(delta, midH);

					mPath.moveTo(midW + offset, midH);
					mPath.lineTo(width - delta, midH);

					canvas.drawPath(mPath, mLinePaint);

					// 绘制麦克风
					drawImage(canvas, mWaitMicBitMap, midW - MIC_RADIUS, midH- MIC_RADIUS, MIC_RADIUS * 2,MIC_RADIUS * 2);

					// 两段圆弧（顶部有小圆）
					RectF rect = new RectF(midW - MIC_RADIUS, midH - MIC_RADIUS, midW
							+ MIC_RADIUS, midH + MIC_RADIUS);
					canvas.drawArc(rect, piToAngle(Math.PI), piToAngle(Math.PI
							* kMicRadian * percent), false, mLinePaint);

					float headx = (float) (midW + MIC_RADIUS
							* Math.cos(Math.PI + Math.PI * kMicRadian * percent));
					float heady = (float) (midH + MIC_RADIUS
							* Math.sin(Math.PI + Math.PI * kMicRadian * percent));
					canvas.drawCircle(headx, heady, DOTS_RADIUS, mDotsPaint);

					canvas.drawArc(rect, piToAngle(0), piToAngle(Math.PI * kMicRadian
							* percent), false, mLinePaint);
					headx = (float) (midW + MIC_RADIUS
							* Math.cos(Math.PI * kMicRadian * percent));
					heady = (float) (midH + MIC_RADIUS
							* Math.sin(Math.PI * kMicRadian * percent));
					canvas.drawCircle(headx, heady, DOTS_RADIUS, mDotsPaint);

					break;
				}
				case State_Waiting: {
					// 绘制麦克风
					drawImage(canvas, mWaitMicBitMap, midW - MIC_RADIUS, midH- MIC_RADIUS, MIC_RADIUS * 2,MIC_RADIUS * 2);

					// 两段圆弧（顶部有小圆）
					// 尾部轨迹模拟

					RectF rect = new RectF(midW - MIC_RADIUS, midH - MIC_RADIUS, midW
							+ MIC_RADIUS, midH + MIC_RADIUS);

					// paint need change to half
					mLinePaint.setStrokeWidth(LINE_PAINT_WIDTH / 2);
					canvas.drawArc(rect, piToAngle(Math.PI + Math.PI * kMicRadian
							* percent), piToAngle(kTrailRadian), false, mLinePaint);
					canvas.drawArc(rect, piToAngle(Math.PI * kMicRadian * percent),
							piToAngle(kTrailRadian), false, mLinePaint);

					mLinePaint.setStrokeWidth(LINE_PAINT_WIDTH);
					canvas.drawArc(rect, piToAngle(Math.PI + Math.PI * kMicRadian
							* percent + kTrailRadian), piToAngle(Math.PI * kMicRadian
							- kTrailRadian), false, mLinePaint);

					float headx = (float) (midW + MIC_RADIUS
							* Math.cos(Math.PI + Math.PI * kMicRadian * (1 + percent)));
					float heady = (float) (midH + MIC_RADIUS
							* Math.sin(Math.PI + Math.PI * kMicRadian * (1 + percent)));
					canvas.drawCircle(headx, heady, DOTS_RADIUS, mDotsPaint);

					canvas.drawArc(rect, piToAngle(Math.PI * kMicRadian * percent
							+ kTrailRadian), piToAngle(Math.PI * kMicRadian
							- kTrailRadian), false, mLinePaint);
					headx = (float) (midW + MIC_RADIUS
							* Math.cos(Math.PI * kMicRadian * (1 + percent)));
					heady = (float) (midH + MIC_RADIUS
							* Math.sin(Math.PI * kMicRadian * (1 + percent)));
					canvas.drawCircle(headx, heady, DOTS_RADIUS, mDotsPaint);
					break;
				}
				case State_LongClick: {
					// 绘制反色的麦克风
					drawImage(canvas, mLongMicBitMap, midW - MIC_RADIUS, midH - MIC_RADIUS, MIC_RADIUS * 2, MIC_RADIUS * 2);
				}
					break;
				default:
					break;
			}
		} catch(Exception e) {
			//部分手机抛出 java.lang.IllegalArgumentException异常，按道理是内部捕获的，部分手机系统未处理
			//E/SurfaceHolder(13213): 	at android.view.Surface.lockCanvasNative(Native Method)
			Log.d(TAG, "mydraw exception");
		}

		//一定要解锁画布
		if (canvas != null) {
			//部分机型unlockCanvas有崩溃问题
			try {
				holder.unlockCanvasAndPost(canvas);
				Log.d(TAG, "unlockCanvas");
			} catch (Exception e) {
				//部分手机抛java.lang.IllegalArgumentException异常
				Log.d(TAG, "unlockCanvasAndPost exception!");
			}

		}
	}

	@Override
	public void setZOrderOnTop(boolean onTop) {
		super.setZOrderOnTop(onTop);
	}

	/**
	 * 绘制背景
	 * @param canvas
	 */
	private void drawBg(Canvas canvas)
	{
        canvas.drawColor(Color.WHITE);
	}

	public synchronized void longClickGLWV() {
		sendMsg(State_LongClick);
	}


}
