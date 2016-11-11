package com.iflytek.medicalsdk_nursing.util;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * 音频播放工具类
 * Created by dingxiaolei on 16/9/19.
 */
public class MediaplayerUtil implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
		MediaPlayer.OnCompletionListener {
	private static final String TAG = "MediaplayerUtil";
	/**
	 * 播放状态
	 */
	private static final int PLAYSOUND = 0;
	private static final int PLAYSOUNDLIST = 1;
	private int playState;

	private MediaPlayer mediaPlayer = null;

	/**
	 * 一共多少条语音
	 */
	private int soundListSize;
	/**
	 * 正在播放第几条语音
	 */
	private int playSoundIndex;
	/**
	 * 语音文件地址列表
	 */
	private List<String> filePathList;

	private MediaplayerPlayStateListener mediaplayerPlayStateListener;

	public MediaplayerUtil() {
		playState = PLAYSOUND;//默认播放状态为播放单个声音
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
		}
	}

	/**
	 * 播放语音文件，所有判断与操作都在此类中执行
	 *
	 * @param filePath                     语音文件地址
	 * @param mediaplayerPlayStateListener 状态接口
	 */
	public void playSound(String filePath, MediaplayerPlayStateListener mediaplayerPlayStateListener) {
		this.mediaplayerPlayStateListener = mediaplayerPlayStateListener;
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaplayerPlayStateListener.stopPlay();
			Log.d(TAG, "playSound: stop");
		} else {
			try {
				if (mediaPlayer == null) {
					mediaPlayer = new MediaPlayer();
				}
				mediaPlayer.reset();
				mediaPlayer.setOnCompletionListener(this);
				mediaPlayer.setDataSource(filePath);
				mediaPlayer.setOnPreparedListener(this);
				mediaPlayer.setOnErrorListener(this);
				mediaPlayer.prepareAsync();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 播放语音文件列表
	 *
	 * @param filePathList                 语音文件名列表
	 * @param mediaplayerPlayStateListener 状态接口
	 */
	public void playSoundList(List<String> filePathList, MediaplayerPlayStateListener mediaplayerPlayStateListener) {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaplayerPlayStateListener.stopPlay();
			Log.d(TAG, "playSoundList: stop");
		} else {
			this.filePathList = filePathList;
			playState = PLAYSOUNDLIST;//确定播放状态
			this.mediaplayerPlayStateListener = mediaplayerPlayStateListener;
			soundListSize = filePathList.size();
			playSoundIndex = 0;
			//拼接文件地址
			String filePath = Environment.getExternalStorageDirectory() + "/iflytek/" + filePathList.get
					(playSoundIndex);

			playSound(filePath, mediaplayerPlayStateListener);
		}
	}

	/**
	 * 停止播放语音
	 */
	public void stopPlay() {
		if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Log.d(TAG, "onError: ");
		mediaPlayer.reset();
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		Log.d(TAG, "onPrepared: ");
		mediaPlayer.start();
		mediaplayerPlayStateListener.startPlay();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		switch (playState) {
			case PLAYSOUND:
				mediaPlayer.stop();
				mediaplayerPlayStateListener.stopPlay();
				break;
			case PLAYSOUNDLIST:
				if (playSoundIndex < soundListSize - 1) {
					playSoundIndex++;
					//拼接文件地址
					String filePath = Environment.getExternalStorageDirectory() + "/iflytek/" + filePathList
							.get(playSoundIndex);
					playSound(filePath, mediaplayerPlayStateListener);
				} else {
					mediaPlayer.stop();
					mediaplayerPlayStateListener.stopPlay();
				}
				break;
		}
	}

	/**
	 * 释放资源，如果正在播放语音，先停止播放
	 */
	public void release() {
		Log.d(TAG, "release: ");
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	/**
	 * 状态接口
	 */
	public interface MediaplayerPlayStateListener {
		//开始播放语音
		public void startPlay();

		//停止播放语音
		public void stopPlay();
	}
}
