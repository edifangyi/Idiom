package com.Idiom.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.Idiom.R;
import com.Idiom.base.BaseActivity;
import com.Idiom.service.AudioService;
import com.Idiom.widget.MultiShapeView;

public class MainActivity extends BaseActivity {

    private MultiShapeView ivCircle;
    private Toolbar toolbar;
    private Button btnPlayGame;
    private Intent mIntent;
    private ImageView ivPlay;

    private AudioService audioService;//背景音乐服务

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void init(Bundle savedInstanceState) {
        initView();


        //设置标题
        toolbar.setTitle("成语消消乐");
        toolbar.setTitleTextColor(ContextCompat.getColor(mContext, R.color.brown));
        setSupportActionBar(toolbar);

        //设置头像
        ivCircle.setImageResource(R.mipmap.image_head);

        //设置按钮点击事件，执行开始游戏
        btnPlayGame.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, PlayGameActivity.class);
            startActivity(intent);
        });

        //播放器控制
        ivPlay.setOnClickListener(view -> ivPlay.setBackground(
                //控制播放器 并控制 按钮图标
                ContextCompat.getDrawable(mContext,
                        audioService.play() ? R.drawable.ic_volume_up_black_24dp : R.drawable.ic_volume_off_black_24dp)));

        mIntent = new Intent();
        mIntent.setClass(this, AudioService.class);
        startService(mIntent);
        bindService(mIntent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ivPlay.setBackground(
                //控制播放器 并控制 按钮图标
                ContextCompat.getDrawable(mContext,
                        audioService.isPlaying() ? R.drawable.ic_volume_up_black_24dp : R.drawable.ic_volume_off_black_24dp));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        stopService(mIntent);
    }

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            audioService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            // 这里我们实例化 audioService, 通过 binder 来实现
            audioService = ((AudioService.AudioBinder) binder).getService();

        }
    };

    /**
     * 初始化控件
     */
    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        ivCircle = findViewById(R.id.iv_circle);

        btnPlayGame = (Button) findViewById(R.id.btn_play_game);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
    }
}
