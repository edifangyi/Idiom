package com.Idiom.bean;

/**
 * ================================================
 * 作    者：FANGYI <87649669@qq.com>
 * 版    本：1.0.0
 * 日    期：2018/5/15
 * 说    明：
 * ================================================
 */
public class SelectBean {
    private int position;
    private String string;

    public SelectBean(int position, String string) {
        this.position = position;
        this.string = string;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
