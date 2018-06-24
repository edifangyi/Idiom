package com.Idiom.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.Idiom.R;
import com.Idiom.bean.GameBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * ================================================
 * 作    者：FANGYI <87649669@qq.com>
 * 版    本：1.0.0
 * 日    期：2018/5/15
 * 说    明：
 * ================================================
 */
public class PlayGameAdapter extends BaseQuickAdapter<GameBean, BaseViewHolder> {

    private int width = 0;

    public PlayGameAdapter(@Nullable List<GameBean> data, int width) {
        super(R.layout.play_game_item, data);
        this.width = width;
    }

    @Override
    protected void convert(BaseViewHolder helper, GameBean item) {

        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) helper.itemView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = width;
        helper.itemView.setLayoutParams(layoutParams);

        helper.setText(R.id.tv_string, item.getString());

        if (item.isSelect()) {
            helper.setBackgroundRes(R.id.tv_string, R.drawable.shape_play_game_item_yes);
        } else {
            helper.setBackgroundRes(R.id.tv_string, R.drawable.shape_play_game_item_no);
        }

        if (item.isVisible()) {
            helper.itemView.setVisibility(View.VISIBLE);
        } else {
            helper.itemView.setVisibility(View.INVISIBLE);
        }

    }
}
