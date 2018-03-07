package com.mobiledevpro.remotelogcat;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;


/**
 * Class for saving logs and sending them to server
 * <p>
 * Created by Dmitriy V. Chernysh on 23.09.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * https://fb.me/mobiledevpro/
 * <p>
 * #MobileDevPro
 */

class LogManager {

    private DBHelper mDBHelper;
    private UserInfoModel mUserInfo;
    private AppInfoModel mAppInfo;
    private Context mContext;
    private String mRequestToken;
    private NetworkConnectionReceiver mNetworkConnectionReceiver;

    LogManager(Context appContext, String requestToken) {
        mDBHelper = DBHelper.getInstance(appContext);
        mContext = appContext;
        mRequestToken = requestToken;

        PackageManager manager = appContext.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(appContext.getPackageName(), 0);
            mAppInfo = new AppInfoModel(
                    info.packageName,
                    info.versionName,
                    info.versionCode
            );
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(Constants.LOG_TAG, "RemoteLog.init: NameNotFoundException - " + e.getLocalizedMessage(), e);
        }

        mNetworkConnectionReceiver = new NetworkConnectionReceiver();
    }

    void setUserInfo(UserInfoModel userInfo) {
        mUserInfo = userInfo;
    }

    void send(int logLevel, String logTag, String logMessage, Throwable tr) {
        switch (logLevel) {
            case Constants.LOG_LEVEL_DEBUG:
                if (tr == null) {
                    Log.d(logTag, logMessage);
                } else {
                    Log.d(logTag, logMessage, tr);
                }
                break;
            case Constants.LOG_LEVEL_ERROR:
                if (tr == null) {
                    Log.e(logTag, logMessage);
                } else {
                    Log.e(logTag, logMessage, tr);
                }
                break;
        }

        //create model
        LogEntryModel logEntryModel = createLogEntry(logLevel, logTag, logMessage);
        //save into db
        ArrayList<LogEntryModel> logEntriesList = insertEntryIntoDb(logEntryModel);
        //send saved entries to server
        if (logEntriesList != null && !logEntriesList.isEmpty()) {
            sendEntriesToServer(logEntriesList);
        }
    }

    void reSendLogs() {
        //  Log.d(Constants.LOG_TAG, "LogManager.reSendLogs(): ");
        ArrayList<LogEntryModel> logEntriesList = mDBHelper.selectLogEntriesList();
        //send saved entries to server
        if (logEntriesList != null && !logEntriesList.isEmpty()) {
            sendEntriesToServer(logEntriesList);
        }
    }

    private LogEntryModel createLogEntry(int logLevel, String logTag, String logMessage) {
        if (mUserInfo == null) mUserInfo = new UserInfoModel();
        return new LogEntryModel(
                new Date().getTime(),
                logLevel,
                logTag,
                logMessage,
                mAppInfo,
                mUserInfo
        );
    }

    private ArrayList<LogEntryModel> insertEntryIntoDb(LogEntryModel logEntryModel) {
        mDBHelper.insertLogEntry(logEntryModel);
        return mDBHelper.selectLogEntriesList();
    }

    private void sendEntriesToServer(ArrayList<LogEntryModel> logEntriesList) {
        AsyncTaskCompat.executeParallel(new NetworkHelper(mContext, mRequestToken, logEntriesList, mNetworkConnectionReceiver));
    }
}
