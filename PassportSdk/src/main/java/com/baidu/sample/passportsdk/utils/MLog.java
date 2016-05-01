package com.baidu.sample.passportsdk.utils;

import android.content.Context;
import android.util.Log;

/**
 * Created by liuzhenhui on 15/8/7.
 */
public class MLog {
    public static boolean isDebug = true;
    public static String packageName;

    public static void init(Context context) {
        packageName = context.getPackageName();
    }

    public static void d(String s) {
        if (!isDebug) {
            return;
        }
        Log.d("lzh", packageName + "======>" + s);
    }

    public static void e(String s) {
        if (!isDebug) {
            return;
        }
        Log.e("lzh", packageName + "======>" + s);
    }
}
