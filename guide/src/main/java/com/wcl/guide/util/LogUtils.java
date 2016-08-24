package com.wcl.guide.util;

import android.util.Log;

/**
 *<p>Describe:日志工具类
 *<p>Author:王春龙
 *<p>CreateTime:2016/7/4
*/
public class LogUtils
{
    private static final String TAG = "Guide";
    private static boolean debug = true;

    public static void e(String msg)
    {
        if (debug)
            Log.e(TAG, msg);
    }

}
