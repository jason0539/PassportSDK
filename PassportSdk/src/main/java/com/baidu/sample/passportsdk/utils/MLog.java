package com.baidu.sample.passportsdk.utils;

import com.baidu.sample.passportsdk.AccountManager;

import android.util.Log;

/**
 * Created by liuzhenhui on 15/8/7.
 */
public class MLog {
    public static boolean isDebug = true;

    public static void d(String s) {
        if (!isDebug) {
            return;
        }
        Log.d("lzh", AccountManager.getInstance().getContext().getPackageName() + "======>" + s);
    }

    public static void e(String s) {
        if (!isDebug) {
            return;
        }
        Log.e("lzh", AccountManager.getInstance().getContext().getPackageName() + "======>" + s);
    }
}
