package com.mobiledevpro.remotelogcat;

import android.os.Build;
import android.text.TextUtils;

/**
 * App User info
 * <p>
 * Created by Dmitriy V. Chernysh on 25.09.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * https://fb.me/mobiledevpro/
 * <p>
 * #MobileDevPro
 */

public class UserInfoModel {

    private int androidApi;
    private String deviceModel;
    private String userName;

    public UserInfoModel(String userName) {
        this.androidApi = Build.VERSION.SDK_INT;
        this.deviceModel = getDeviceName();
        this.userName = userName;
    }

    UserInfoModel() {
        this.androidApi = Build.VERSION.SDK_INT;
        this.deviceModel = getDeviceName();
    }

    /**
     * For getting value form DB
     *
     * @param userInfo some data about user (divider - "|")
     */
    UserInfoModel parseUserInfo(String userInfo) {
        if (TextUtils.isEmpty(userInfo)) return this;
        //android api|
        String[] arrayInfo = userInfo.split(";");
        if (arrayInfo.length == 0) return this;

        if (arrayInfo.length > 0) this.androidApi = Integer.valueOf("0" + arrayInfo[0]);
        if (arrayInfo.length > 1) this.deviceModel = arrayInfo[1];
        if (arrayInfo.length > 2) this.userName = arrayInfo[2];

        return this;
    }

    String getAndroidApiTxt() {
        return "API " + androidApi;
    }

    String getDeviceModel() {
        return deviceModel;
    }

    String getUserName() {
        return userName;
    }

    String getString() {
        return androidApi +
                (!TextUtils.isEmpty(deviceModel) ? ";" + deviceModel : "") +
                (!TextUtils.isEmpty(userName) ? ";" + userName : "");
    }

    private String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model.toUpperCase();
        } else {
            return (manufacturer + " " + model).toUpperCase();
        }
    }
}
