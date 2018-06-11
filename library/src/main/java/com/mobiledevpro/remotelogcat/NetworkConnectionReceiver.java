package com.mobiledevpro.remotelogcat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * Broadcast receiver for network connection
 * <p>
 * Created by Dmitriy V. Chernysh on 03.10.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * https://instagr.am/mobiledevpro
 * https://github.com/dmitriy-chernysh
 * <p>
 * #MobileDevPro
 */

public class NetworkConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Log.d(Constants.LOG_TAG, "NetworkConnectionReceiver.onReceive(): " + intent);
        if (!intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) return;

        if (Constants.isDeviceOnline(context)) {
            RemoteLog.resendLogs();
        }
    }
}
