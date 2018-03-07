package com.mobiledevpro.remotelogcat;

import android.app.Application;

/**
 * App
 * <p>
 * Created by Dmitriy V. Chernysh on 07.03.18.
 * <p>
 * https://fb.com/mobiledevpro/
 * https://github.com/dmitriy-chernysh
 * #MobileDevPro
 */

public class App extends Application {

    private static final String REMOTE_LOGCAT_TOKEN = "demo-token";

    @Override
    public void onCreate() {
        super.onCreate();

        RemoteLog.init(this, REMOTE_LOGCAT_TOKEN);
    }
}
