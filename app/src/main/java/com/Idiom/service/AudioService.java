package com.Idiom.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.Idiom.R;

/**
 * ================================================
 * 作    者：FANGYI <87649669@qq.com>
 * 版    本：1.0.0
 * 日    期：2018/5/16
 * 说    明：
 * ================================================
 */
public class AudioService extends Service {
    private MediaPlayer player;
    private final IBinder binder = new AudioBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        //从 raw 目录中获取一个应用自带的 mp3 文件
        player = MediaPlayer.create(this, R.raw.t);
        player.setLooping(true);
        //当 Audio 播放完的时候触发该动作
        player.setOnCompletionListener(mediaPlayer -> { });
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!player.isPlaying()) {
            player.start();
        }
        return START_STICKY;
    }
    public void onDestroy() {
        //super.onDestroy();
        if (player.isPlaying()) {
            player.stop();
        }
        player.release();
    }
    // 为了和 Activity 交互，须要定义一个 Binder 对象
    public class AudioBinder extends Binder {
        // 返回 Service 对象Z
        public AudioService getService() {
            return AudioService.this;
        }
    }
    // 后退播放进度
    public void haveFun() {
        if (player.isPlaying() && player.getCurrentPosition() > 2500) {
            player.seekTo(player.getCurrentPosition() - 2500);
        }
    }
    public boolean play() {
        if (player.isPlaying()) {
            player.pause();
            return false;
        } else {
            player.start();
            return true;
        }
    }
    public boolean isPlaying() {
        return player.isPlaying();
    }
}