package com.Idiom.bean;

/**
 * ================================================
 * 作    者：FANGYI <87649669@qq.com>
 * 版    本：1.0.0
 * 日    期：2018/5/15
 * 说    明：
 * ================================================
 */
public class GameBean {
    private boolean select;
    private boolean visible;
    private String string;


    public GameBean(boolean select, boolean visible, String string) {
        this.select = select;
        this.visible = visible;
        this.string = string;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
