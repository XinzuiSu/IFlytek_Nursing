/*
 *  Android开发之SurfaceView
 *  SurfaceView01.java
 *  Created on: 2011-8-25
 *  Author: blueeagle
 *  Email: liujiaxiang@gmail.com
 */

package com.iflytek.medicalsdk_nursing.util;

import android.content.Context;
import android.graphics.Bitmap;
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
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 绘制波形图
 * 
 * @author longwang
 * 
 */
public class MiniWaveSurface extends SurfaceView implements
		SurfaceHolder.Callback {
	private static final String TAG = MiniWaveSurface.class.getSimpleName();
	
	private SurfaceHolder holder;
	private HandlerThread mThread;
	public Handler mHandler = null;
	
	private static final float FRAME_PER_SECOND = 60;
	private static final long TIME_PER_FRAME = 1000 / (int)FRAME_PER_SECOND;

	private static final float LINE_PAINT_WIDTH = 3.0f;
	/** 圆点半径 */
	static final float DOTS_RADIUS = 3.0f;
	private Paint mDotsPaint;
	private Paint mDotsPaint2;
	private Paint mLinePaint;
	private Path mPath;

	static final int State_Init 		= 1;
	static final int State_StartRecord 	= 2;
	static final int State_Recording 	= 3;
	/** 停止录音，振幅下降到最小值 */
	static final int State_StopRecord 	= 4;
	/** 直线拉开变成原点效果 */
	static final int State_Transform 	= 5;
	/** 直线运动到圆弧效果 */
	static final int State_StartRecognize = 6;
	/** 圆弧等待效果 */
	static final int State_Waiting = 7;
	
	static final int LineCount 	= 20;
	
	private int dotsDelta = 0; 
	private static int StateLineCount = 3; 
	static final int DefaultStartRecordInterval 	= 20;
	static final int DefaultStopRecordInterval 		= 10;
	static final int DefaultTransformInterval 		= 20;
	static final int DefaultRecognizeInterval		= 10;
	static final int DefaultStartWaitInterval 		= 20;

	static final float kTrigW = 2.0f; // 周期
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

	/** 识别是否结束 */
	private boolean mIsOver = false;
	/** 是否是绘制在顶层 */
	private boolean isDrawOnTop = false;
	
	private boolean mInited = false;
	
	public MiniWaveSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "init");
		holder = this.getHolder();
		holder.addCallback(this);
		holder.setFormat(PixelFormat.TRANSPARENT);
	}

	public synchronized void init() {
		Log.d(TAG, "initialize");
		if(!mInited) {
			mDotsPaint = new Paint();
			mDotsPaint.setStrokeWidth(DOTS_RADIUS);
			mDotsPaint.setAntiAlias(true);
			mDotsPaint.setStyle(Paint.Style.FILL);
			mDotsPaint.setColor(Color.rgb(0, 204, 203));
			
			mDotsPaint2 = new Paint();
			mDotsPaint2.setStrokeWidth(DOTS_RADIUS*2);
			mDotsPaint2.setAntiAlias(true);
			mDotsPaint2.setStyle(Paint.Style.STROKE);
			mDotsPaint2.setColor(Color.rgb(0, 204, 203));

			mLinePaint = new Paint();
			mLinePaint.setStrokeWidth(LINE_PAINT_WIDTH);
			mLinePaint.setAntiAlias(true);
			mLinePaint.setStyle(Paint.Style.STROKE);
			mLinePaint.setColor(Color.rgb(0, 204, 203));
			mPath = new Path();
			
			initHandler();
			
			mInited = true;
		}
	}

	private long t = 0;
	
	private void initHandler() {
		Log.d(TAG, "initHandler");
		//mThread = new HandlerThread("waveform", Process.THREAD_PRIORITY_DISPLAY);// 创建一个绘图线程
		mThread = new HandlerThread("waveform");// 创建一个绘图线程
		mThread.start();

		mHandler = new Handler(mThread.getLooper()) {

			@Override
			public void handleMessage(Message msg) {
				Log.d(TAG, "handlemsg:" + msg.what+ ", time:" + (System.currentTimeMillis() - t));
				t = System.currentTimeMillis();
				Log.d(TAG, "handlemsg:" + msg.what);
				switch (msg.what) {
				case State_Init: {
					percent = 0;
					dotsDelta = 0;
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
						percent += (1.0f / DefaultRecognizeInterval);
						myDraw(holder);
					}
					break;
				}
				case State_Waiting: {
					if (mIsOver) {
						reset();
					} else {
						sendEmptyMessageDelayed(state, TIME_PER_FRAME * 4);
						dotsDelta++;
						if (dotsDelta >= LineCount) {
							dotsDelta = 0;
						}
						myDraw(holder);
					}

					break;
				}
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
		reset();
	}

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated");
		this.holder = holder;
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surfaceDestroyed");
	}
	
	public synchronized void destroy() {
		Log.d(TAG, "destroy");
		// 线程需要销毁
		if (mThread != null && mThread.isAlive()) {
			mHandler = null;
			mThread.quit();
			mThread = null;
		}
		
		
	}

	public synchronized boolean start() {
		t = System.currentTimeMillis();
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

	public synchronized void reset() {
		Log.d(TAG, "reset");
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

	private static final float amplitudeDelta = 0.04f;
	public void setVolume(int vol) {
		
		float v = vol / 30.0f;
		float delta = Math.abs(v - volume);
		
		Log.d(TAG, "Volume vol="+ vol + ",cur=" + v + ",last=" + volume);
		if(delta <= amplitudeDelta) {
			this.volume = v;
		} else  {
			Log.d(TAG, "Volume delta too large " + delta);
			if( v >= volume) {
				this.volume += amplitudeDelta;
			} else {
				this.volume -= amplitudeDelta;
			}
		} 
		
	}

	private void flow() {
		float T = 0.4f; // 流动的速度，这里是0.5秒流动个一个周期（2π）
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
		return (float) (amplitude * Math.cos(kTrigW * x - phase) * kGausA * Math .pow(Math.E, - Math.pow(x - kGausB, 2.0f) / Math.pow(kGausC, 2.0f)));
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
		holder = getHolder();
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
			canvas.drawColor(Color.WHITE);
			drawBg(canvas);
		}
		
		try {
//			canvas.drawColor(getContext().getResources().getColor(R.color.status_bg));
			int width = getWidth();
			int height = getHeight();
			float midW = width / 2.0f;
			float midH = height / 2.0f;

			switch (state) {
			case State_Init: {
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
				
				float perLineLen =  (float)width / LineCount;
				float perLineLenCur =  (float)width / LineCount * (1 - percent);
				for (int i= 0; i < LineCount; i++) {
					mPath.reset();
					mPath.moveTo(i * perLineLen + perLineLen / 2.0f - perLineLenCur / 2, midH);
					mPath.lineTo(i * perLineLen + perLineLen / 2.0f + perLineLenCur / 2, midH);
					canvas.drawPath(mPath, mLinePaint);
				}
				
//				float offset = MIC_RADIUS * percent;
//				// 两个小圆
//				canvas.drawCircle(midW - offset, midH, DOTS_RADIUS, mDotsPaint);
//				canvas.drawCircle(midW + offset, midH, DOTS_RADIUS, mDotsPaint);
//
//				// 两条直线
//				mPath.reset();
//				mPath.moveTo(midW - offset, midH);
//				mPath.lineTo(0, midH);
//
//				mPath.moveTo(midW + offset, midH);
//				mPath.lineTo(width, midH);
//				canvas.drawPath(mPath, mLinePaint);

				break;
			}
			case State_StartRecognize: {
				float perLineLen =  (float)width / LineCount;
				for (int i= 0; i < LineCount; i++) {
					float circle_x = i * perLineLen + perLineLen / 2.0f;
					canvas.drawCircle(circle_x, midH, DOTS_RADIUS * percent, mDotsPaint);
				}
				
				
				
				

//				// 两条直线
//				mPath.reset();
//				mPath.moveTo(midW - offset, midH);
//				mPath.lineTo(delta, midH);
//
//				mPath.moveTo(midW + offset, midH);
//				mPath.lineTo(width - delta, midH);
//
//				canvas.drawPath(mPath, mLinePaint);
//
//				// 绘制麦克风
//				drawImage(canvas, mWaitMicBitMap, midW - MIC_RADIUS, midH- MIC_RADIUS, MIC_RADIUS * 2,MIC_RADIUS * 2);
//
//				// 两段圆弧（顶部有小圆）
//				RectF rect = new RectF(midW - MIC_RADIUS, midH - MIC_RADIUS, midW
//						+ MIC_RADIUS, midH + MIC_RADIUS);
//				canvas.drawArc(rect, piToAngle(Math.PI), piToAngle(Math.PI
//						* kMicRadian * percent), false, mLinePaint);
//
//				float headx = (float) (midW + MIC_RADIUS
//						* Math.cos(Math.PI + Math.PI * kMicRadian * percent));
//				float heady = (float) (midH + MIC_RADIUS
//						* Math.sin(Math.PI + Math.PI * kMicRadian * percent));
//				canvas.drawCircle(headx, heady, DOTS_RADIUS, mDotsPaint);
//
//				canvas.drawArc(rect, piToAngle(0), piToAngle(Math.PI * kMicRadian
//						* percent), false, mLinePaint);
//				headx = (float) (midW + MIC_RADIUS
//						* Math.cos(Math.PI * kMicRadian * percent));
//				heady = (float) (midH + MIC_RADIUS
//						* Math.sin(Math.PI * kMicRadian * percent));
//				canvas.drawCircle(headx, heady, DOTS_RADIUS, mDotsPaint);

				break;
			}
			case State_Waiting: {
				float perLineLen =  (float)width / LineCount;
				for (int i= 0; i < LineCount; i++) {
					float circle_x = i * perLineLen + perLineLen / 2.0f;
					canvas.drawCircle(circle_x, midH, DOTS_RADIUS, mDotsPaint);
				}
	
				float arr[] = {9, midH / 3, midH * 2 / 3};
				for (int i = dotsDelta; i < LineCount && i < dotsDelta + StateLineCount; i++) {
					mPath.reset();
					float lineH = arr[i - dotsDelta];
//					float lineH = (midH - dotLineMinH) / (StateLineCount - 1) * (i - dotsDelta) + 9;
					float x = i * perLineLen + perLineLen / 2.0f;
					float y1 = midH - lineH / 2;
					float y2 = midH + lineH / 2;
					Log.d(TAG, "x=" + x + ",y1=" + y1 + ",y2="+y2);
					mPath.moveTo(x, y1);
					mPath.lineTo(x, y2);
					canvas.drawPath(mPath, mDotsPaint2);
					canvas.drawCircle(x, y1, DOTS_RADIUS, mDotsPaint);
					canvas.drawCircle(x, y2, DOTS_RADIUS, mDotsPaint);
				}
				
				
//				// 绘制麦克风
//				drawImage(canvas, mWaitMicBitMap, midW - MIC_RADIUS, midH- MIC_RADIUS, MIC_RADIUS * 2,MIC_RADIUS * 2);
//
//				// 两段圆弧（顶部有小圆）
//				// 尾部轨迹模拟
//
//				RectF rect = new RectF(midW - MIC_RADIUS, midH - MIC_RADIUS, midW
//						+ MIC_RADIUS, midH + MIC_RADIUS);
//
//				// paint need change to half
//				mLinePaint.setStrokeWidth(LINE_PAINT_WIDTH / 2);
//				canvas.drawArc(rect, piToAngle(Math.PI + Math.PI * kMicRadian
//						* percent), piToAngle(kTrailRadian), false, mLinePaint);
//				canvas.drawArc(rect, piToAngle(Math.PI * kMicRadian * percent),
//						piToAngle(kTrailRadian), false, mLinePaint);
//
//				mLinePaint.setStrokeWidth(LINE_PAINT_WIDTH);
//				canvas.drawArc(rect, piToAngle(Math.PI + Math.PI * kMicRadian
//						* percent + kTrailRadian), piToAngle(Math.PI * kMicRadian
//								- kTrailRadian), false, mLinePaint);
//
//				float headx = (float) (midW + MIC_RADIUS
//						* Math.cos(Math.PI + Math.PI * kMicRadian * (1 + percent)));
//				float heady = (float) (midH + MIC_RADIUS
//						* Math.sin(Math.PI + Math.PI * kMicRadian * (1 + percent)));
//				canvas.drawCircle(headx, heady, DOTS_RADIUS, mDotsPaint);
//
//				canvas.drawArc(rect, piToAngle(Math.PI * kMicRadian * percent
//						+ kTrailRadian), piToAngle(Math.PI * kMicRadian
//								- kTrailRadian), false, mLinePaint);
//				headx = (float) (midW + MIC_RADIUS
//						* Math.cos(Math.PI * kMicRadian * (1 + percent)));
//				heady = (float) (midH + MIC_RADIUS
//						* Math.sin(Math.PI * kMicRadian * (1 + percent)));
//				canvas.drawCircle(headx, heady, DOTS_RADIUS, mDotsPaint);
				break;
			}
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
		isDrawOnTop = onTop;
	}
	
	/**
	 * 绘制背景
	 * @param canvas
	 */
	private void drawBg(Canvas canvas)
	{
		if(isDrawOnTop)
		{
			canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
		}
		else
		{			
			canvas.drawColor(Color.WHITE);
		}
	}

}
