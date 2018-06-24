package com.Idiom.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * ================================================
 * 作    者：FANGYI <87649669@qq.com>
 * 版    本：1.0.0
 * 日    期：2018/3/1
 * 权    限：
 * 说    明：
 * ================================================
 */
public class ToastUtil {

    private static Toast sToast;

    private static Toast initToast(Context context, CharSequence message,int duration) {
        if (sToast == null){
            sToast=Toast.makeText(context,message,duration);
        }else {
            sToast.setText(message);
            sToast.setDuration(duration);
        }
        return sToast;
    }

    /**
     * 短时间显示Toast
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message) {
        initToast(context,message,Toast.LENGTH_SHORT).show();
    }

    /**
     * 短时间显示Toast
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message) {
        initToast(context,message,Toast.LENGTH_LONG).show();
    }



}
