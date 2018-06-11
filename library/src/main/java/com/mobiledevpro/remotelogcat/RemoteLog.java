package com.mobiledevpro.remotelogcat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

/**
 * Class for sending logs to remote server
 * <p>
 * Created by Dmitriy V. Chernysh on 23.09.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * https://instagr.am/mobiledevpro
 * https://github.com/dmitriy-chernysh
 * <p>
 * #MobileDevPro
 */

public class RemoteLog {

    private static String sToken;
    private static LogManager sLogManager;

    private RemoteLog() {
    }

    public static void init(Context context, @NonNull String token) {
        sToken = token;
        sLogManager = new LogManager(context, token);
    }

    public static void setUserInfo(UserInfoModel userInfo) {
        if (sLogManager == null) return;
        try {
            sLogManager.setUserInfo(userInfo);
        } catch (Exception e) {
            //do nothing
        }
    }

    public static void d(String tag, String msg) {
        if (isTokenEmpty()) return;
        if (sLogManager == null) return;
        try {
            sLogManager.send(Constants.LOG_LEVEL_DEBUG, tag, msg, null);
        } catch (Exception e) {
            //do nothing
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (isTokenEmpty()) return;
        if (sLogManager == null) return;
        try {
            sLogManager.send(Constants.LOG_LEVEL_DEBUG, tag, msg, tr);
        } catch (Exception e) {
            //do nothing
        }
    }

    public static void e(String tag, String msg) {
        if (isTokenEmpty()) return;
        if (sLogManager == null) return;
        try {
            sLogManager.send(Constants.LOG_LEVEL_ERROR, tag, msg, null);
        } catch (Exception e) {
            //do nothing
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (isTokenEmpty()) return;
        if (sLogManager == null) return;
        try {
            sLogManager.send(Constants.LOG_LEVEL_ERROR, tag, msg, tr);
        } catch (Exception e) {
            //do nothing
        }
    }

    static void resendLogs() {
        if (sLogManager == null) return;
        try {
            sLogManager.reSendLogs();
        } catch (Exception e) {
            //do nothing
        }
    }

    private static boolean isTokenEmpty() {
        boolean b = TextUtils.isEmpty(sToken);
        if (b)
            Log.e("RemoteLog", "Token is empty. Please, call RemoteLog.init([token here]) in onCreate() method of the main application class");
        return b;
    }
}
