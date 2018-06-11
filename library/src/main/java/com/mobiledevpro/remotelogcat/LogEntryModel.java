package com.mobiledevpro.remotelogcat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Model for log entry
 * <p>
 * Created by Dmitriy V. Chernysh on 23.09.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * https://instagr.am/mobiledevpro
 * https://github.com/dmitriy-chernysh
 * <p>
 * #MobileDevPro
 */

class LogEntryModel {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    private int id;
    private long dateTime; //in ms
    private int logLevel;
    private String logTag;
    private String logMsg;
    private AppInfoModel appInfo;
    private UserInfoModel appUserInfo;

    /**
     * Constructor for getting entry from DB
     */
    LogEntryModel(int id, long dateTime, int logLevel, String logTag, String logMsg, AppInfoModel appInfo, UserInfoModel appUserInfo) {
        this.id = id;
        this.dateTime = dateTime;
        this.logLevel = logLevel;
        this.logTag = logTag;
        this.logMsg = logMsg;
        this.appInfo = appInfo;
        this.appUserInfo = appUserInfo;
    }

    /**
     * Constructor for creating a new entry
     */
    LogEntryModel(long dateTime, int logLevel, String logTag, String logMsg, AppInfoModel appInfo, UserInfoModel appUserInfo) {
        this.dateTime = dateTime;
        this.logLevel = logLevel;
        this.logTag = logTag;
        this.logMsg = logMsg;
        this.appInfo = appInfo;
        this.appUserInfo = appUserInfo;
    }

    int getId() {
        return id;
    }

    long getDateTime() {
        return dateTime;
    }

    String getDateTimeTxt() {
        return getStringFromDateTime(dateTime);
    }

    int getLogLevel() {
        return logLevel;
    }

    String getLogLevelTxt() {
        return Constants.getLogLevelTxt(logLevel);
    }

    String getLogTag() {
        return logTag;
    }

    String getLogMsg() {
        return logMsg;
    }

    AppInfoModel getAppInfo() {
        return appInfo;
    }

    UserInfoModel getAppUserInfo() {
        return appUserInfo;
    }


    /**
     * Convert Date to Date in string format
     *
     * @param dateTime Date in milliseconds
     * @return Date in string format
     */
    private String getStringFromDateTime(long dateTime) {
        if (dateTime == 0) return "";
        Date date = new Date(dateTime);
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        format.setTimeZone(Calendar.getInstance().getTimeZone());
        return format.format(date);
    }

}
