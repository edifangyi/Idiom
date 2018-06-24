package com.Idiom.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.media.tv.TvContract;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.Idiom.R;
import com.Idiom.adapter.PlayGameAdapter;
import com.Idiom.base.BaseActivity;
import com.Idiom.bean.GameBean;
import com.Idiom.bean.IdiomBean;
import com.Idiom.bean.SelectBean;
import com.Idiom.service.AudioService;
import com.Idiom.utils.DeviceUtils;
import com.Idiom.utils.LocalJsonResolutionUtils;
import com.Idiom.utils.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * ================================================
 * 作    者：FANGYI <87649669@qq.com>
 * 版    本：1.0.0
 * 日    期：2018/5/15
 * 说    明：
 * ================================================
 */
public class PlayGameActivity extends BaseActivity {


    private Toolbar toolbar;
    private ImageView ivPlay;
    private TextView tvReset;
    private TextView tvPoint;
    private TextView tvShare;
    private TextView tvPrompt;
    private android.support.v7.widget.RecyclerView mRecyclerView;

    private TextView tvCountdown;

    private int mCheckpoint = 9;//总关卡数
    private int index;//关卡变量

    private String mStrings;//生成展示格子列表前的临时变量

    private List<GameBean> mGameBeans = new ArrayList<>();//展示格子列表
    private List<SelectBean> mSelectList = new ArrayList<>();//选中格子的列表
    private ArrayList<String> mIdiomList;//已经被正确选中的成语

    private List<Integer> mRanList;//生成随机数列表

    private ArrayList<IdiomBean> mIdiomBeans;//关卡生成基本数据


    private AudioService audioService;//背景音
    private MediaPlayer clickPlayer;//点击音
    private MediaPlayer correctPlayer;//消除音

    private PlayGameAdapter mAdapter;


    private int maxCountdown = 60 * 3;
    private int countdown = 60 * 3;
    private Runnable mRunnable;

    private int point = 3;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_play_game;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();

        Intent mIntent = new Intent();
        mIntent.setClass(this, AudioService.class);
        bindService(mIntent, conn, Context.BIND_AUTO_CREATE);


        clickPlayer = MediaPlayer.create(this, R.raw.click);
        clickPlayer.setVolume(0.5f, 0.5f);
        correctPlayer = MediaPlayer.create(this, R.raw.correct);
        correctPlayer.setVolume(0.5f, 0.5f);


        //设置标题
        setSupportActionBar(toolbar);
        toolbar.setTitle("第1关");
        toolbar.setTitleTextColor(ContextCompat.getColor(mContext, R.color.brown));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(mContext, R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(view -> finish());


        point = 3;

        tvCountdown.setText(countdown + "秒");


        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (--countdown <= 0) {
                    tvCountdown.setText(countdown + "秒");
                    ToastUtil.showLong(mContext, "挑战失败");

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("挑战失败");
                    builder.setMessage("还想要玩吗？");
                    builder.setNegativeButton("否", (dialog, which) -> finish());
                    builder.setPositiveButton("是", (dialog, which) -> {
                        point = 3;
                        tvPoint.setText("提示：(" + point + ")");
                        getCheckpoint(index = 0);//挑战失败
                        toolbar.setTitle("第" + (index + 1) + "关");
                        reset();
                        mHandler.post(mRunnable);
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    tvCountdown.setText(countdown + "秒");
                    mHandler.postDelayed(this, 1000);
                }
            }
        };
        mHandler.postDelayed(mRunnable, 1000);

        //重置的点击事件
        tvReset.setOnClickListener(view -> {
            getCheckpoint(index);//重置
            reset();
            countdown++;
        });
        //提示的点击事件
        tvPoint.setOnClickListener(view -> {
            if (!mIdiomBeans.isEmpty() && point > 0) {
                point--;

                Random random = new Random();
                int ran = random.nextInt(mIdiomBeans.size());

                ToastUtil.showShort(mContext, "提示：" + mIdiomBeans.get(ran).getIdiom());
                tvPoint.setText("提示：(" + point + ")");
            } else {
                ToastUtil.showShort(mContext, "提示已经用完了呢！");
            }
        });
        //分享的点击事件
        tvShare.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.TEXT", "快来玩吧！成语消消乐~");
            startActivity(intent);
        });

        tvPrompt.setOnClickListener(view -> {
            Random random = new Random();
            int ran = random.nextInt(mIdiomBeans.size());
            tvPrompt.setText("提示：" + mIdiomBeans.get(ran).getResolve());
        });

        //播放器控制
        ivPlay.setOnClickListener(view -> ivPlay.setBackground(
                //控制播放器 并控制 按钮图标
                ContextCompat.getDrawable(mContext, audioService.play() ? R.drawable.ic_volume_up_black_24dp : R.drawable.ic_volume_off_black_24dp)));

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 7));

        int width = (DeviceUtils.getScreenWidth(mContext) - DeviceUtils.dp2px(mContext, 70)) / 7;//计算一个 item的宽度
        mAdapter = new PlayGameAdapter(mGameBeans, width);
        mRecyclerView.setAdapter(mAdapter);

        getCheckpoint(index = 0);//第一次
        reset();


        mAdapter.setOnItemClickListener((adapter, view, position) -> {

            if (mGameBeans.get(position).isSelect()) {//取消选中
                mGameBeans.get(position).setSelect(false);

                Iterator<SelectBean> list = mSelectList.iterator();
                while (list.hasNext()) {
                    SelectBean selectBean = list.next();
                    if (selectBean.getPosition() == position) {
                        list.remove();
                    }
                }

            } else {//选中

                if (audioService.isPlaying()) {
                    clickPlayer.start();
                }


                mGameBeans.get(position).setSelect(true);
                mSelectList.add(new SelectBean(position, mGameBeans.get(position).getString()));

                if (mSelectList.size() == 4) {//判断是否已经选择了四个字
                    String idiom = "";
                    for (SelectBean selectBean : mSelectList) {
                        idiom = idiom + selectBean.getString();
                    }


                    if (mIdiomList.contains(idiom)) {
                        for (SelectBean selectBean : mSelectList) {
                            mGameBeans.get(selectBean.getPosition()).setVisible(false);
                        }
                        Iterator<IdiomBean> list = mIdiomBeans.iterator();
                        while (list.hasNext()) {
                            IdiomBean idiomBean = list.next();
                            if (idiomBean.getIdiom().equals(idiom)) {
                                list.remove();
                            }
                        }
                        if (audioService.isPlaying()) {
                            correctPlayer.start();
                        }
                    } else {
                        for (GameBean gameBean : mGameBeans) {
                            gameBean.setSelect(false);
                        }
                        //震动
                        Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
                        vibrator.vibrate(500);
                    }
                    mSelectList.clear();
                }

                if (mIdiomBeans.isEmpty()) {

                    if (++index >= mCheckpoint) {
                        ToastUtil.showShort(mContext, "恭喜通关");
                        mHandler.removeCallbacks(mRunnable);

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("挑战成功");
                        builder.setMessage("还想要玩吗？");
                        builder.setNegativeButton("否", (dialog, which) -> finish());
                        builder.setPositiveButton("是", (dialog, which) -> {
                            point = 3;
                            tvPoint.setText("提示：(" + point + ")");
                            getCheckpoint(index = 0);//挑战成功
                            toolbar.setTitle("第" + (index + 1) + "关");
                            reset();
                            mHandler.post(mRunnable);
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        ToastUtil.showShort(mContext, "恭喜下一关");
                        toolbar.setTitle("第" + (index + 1) + "关");
                        getCheckpoint(index);//下一关
                        reset();

                        tvCountdown.setText(countdown + "秒");
                    }
                } else {
                    tvPrompt.setText("提示：" +
                            mIdiomBeans.get(mIdiomBeans.size() - 1).getResolve());
                }
            }

            mAdapter.notifyDataSetChanged();
        });

    }

    /**
     * 获取关卡数据
     *
     * @param index
     */
    private void getCheckpoint(int index) {
        String json = LocalJsonResolutionUtils.getJson(mContext, "idiom" + index + ".json");
        mIdiomBeans = new Gson().fromJson(json, new TypeToken<ArrayList<IdiomBean>>() {
        }.getType());

        mIdiomList = new ArrayList<>();
        for (IdiomBean idiomBean : mIdiomBeans) {
            mIdiomList.add(idiomBean.getIdiom());
        }

        countdown = maxCountdown - index * 20;

        tvPrompt.setText("提示：" + mIdiomBeans.get(mIdiomBeans.size() - 1).getResolve());

        tvPoint.setText("提示：(" + point + ")");
    }


    /**
     * 重置
     */
    private void reset() {
        mGameBeans.clear();
        mStrings = "";

        Random random = new Random();

        for (IdiomBean idiomBean : mIdiomBeans) {
            for (int i = 0; i < idiomBean.getIdiom().length(); i++) {
                mStrings = mStrings + idiomBean.getIdiom().charAt(i);
            }
        }

        mRanList = new ArrayList<>();

        int length = mStrings.length();
        for (int i = 0; i < length; i++) {
            mGameBeans.add(new GameBean(false, true, getIdiom(random, length)));
        }

        mAdapter.notifyDataSetChanged();
    }

    private String getIdiom(Random random, int max) {
        int ran = random.nextInt(max);
        if (mRanList.contains(ran)) {
            return getIdiom(random, max);
        } else {
            mRanList.add(ran);
            return String.valueOf(mStrings.charAt(ran));
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        tvReset = (TextView) findViewById(R.id.tv_reset);
        tvPoint = (TextView) findViewById(R.id.tv_point);
        tvShare = (TextView) findViewById(R.id.tv_share);
        tvPrompt = (TextView) findViewById(R.id.tv_prompt);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        tvCountdown = (TextView) findViewById(R.id.tv_countdown);
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
            ivPlay.setBackground(
                    //控制播放器 并控制 按钮图标
                    ContextCompat.getDrawable(mContext, audioService.isPlaying() ? R.drawable.ic_volume_up_black_24dp : R.drawable.ic_volume_off_black_24dp));
        }
    };
}
