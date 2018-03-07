package com.mobiledevpro.remotelogcat;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Class for data sending to server
 * <p>
 * Created by Dmitriy V. Chernysh on 23.09.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * https://fb.me/mobiledevpro/
 * <p>
 * #MobileDevPro
 */

class NetworkHelper extends AsyncTask<Void, Void, Boolean> {

    private static final String URL = "http://api.mobile-dev.pro/applog/api";
    private static final String METHOD = "POST";
    private static final int TIMEOUT = 5000; //ms

    private Context mContext;
    private String mToken;
    private ArrayList<LogEntryModel> mLogEntriesList;
    private int[] mEntriesIds;
    private NetworkConnectionReceiver mNetworkConnectionReceiver;

    NetworkHelper(Context context, String token, ArrayList<LogEntryModel> logEntriesList, NetworkConnectionReceiver networkConnectionReceiver) {
        mContext = context;
        mToken = token;
        mLogEntriesList = logEntriesList;
        mNetworkConnectionReceiver = networkConnectionReceiver;

        if (mLogEntriesList != null && !mLogEntriesList.isEmpty()) {
            mEntriesIds = new int[mLogEntriesList.size()];
            for (int i = 0, j = mLogEntriesList.size(); i < j; i++) {
                mEntriesIds[i] = mLogEntriesList.get(i).getId();
            }
        }
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        if (!Constants.isDeviceOnline(mContext)) {
            if (mNetworkConnectionReceiver != null) {
                mContext.registerReceiver(mNetworkConnectionReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            }
            return false;
        } else {
            try {
                if (mNetworkConnectionReceiver != null) {
                    mContext.unregisterReceiver(mNetworkConnectionReceiver);
                }
            } catch (IllegalArgumentException e) {
                //do nothing
            }
        }

        if (mLogEntriesList == null || mLogEntriesList.isEmpty()) return false;

        //change status for selected entries
        DBHelper.getInstance(mContext).updateEntriesStatus(mEntriesIds, true);

        //create json body
        JSONArray jsonArray = createRequestBody();
        if (jsonArray == null || jsonArray.length() == 0) return false;

        //send entries to server
        return sendRequest(jsonArray);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        DBHelper dbHelper = DBHelper.getInstance(mContext);
        if (aBoolean) {
            //remove entries from the local database
            dbHelper.deleteLogEntryList(mEntriesIds);
        } else {
            //change status for selected entries
            dbHelper.updateEntriesStatus(mEntriesIds, false);
        }
    }

    private JSONArray createRequestBody() {
        JSONArray jsonArray = new JSONArray();

        JSONObject jsonEntry;
        JSONObject jsonAppData;
        JSONObject jsonAppUserData;
        AppInfoModel appInfo;
        UserInfoModel appUserInfo;
        try {
            for (LogEntryModel logEntry : mLogEntriesList) {
                jsonEntry = new JSONObject();
                jsonEntry.put("datetime", logEntry.getDateTimeTxt());
                jsonEntry.put("loglevel", logEntry.getLogLevelTxt());
                jsonEntry.put("logtag", logEntry.getLogTag());
                jsonEntry.put("error", logEntry.getLogMsg());

                //app info
                jsonAppData = new JSONObject();
                appInfo = logEntry.getAppInfo();
                jsonAppData.put("name", appInfo != null ? appInfo.getName() : "");
                jsonAppData.put("version", appInfo != null ? appInfo.getVersion() + " (" + appInfo.getBuild() + ")" : "");
                jsonEntry.put("app", jsonAppData);

                //app user's info
                jsonAppUserData = new JSONObject();
                appUserInfo = logEntry.getAppUserInfo();
                jsonAppUserData.put("androidApi", appUserInfo.getAndroidApiTxt());
                jsonAppUserData.put("device", appUserInfo.getDeviceModel());
                jsonAppUserData.put("login", appUserInfo.getUserName());
                jsonEntry.put("appUser", jsonAppUserData);

                jsonArray.put(jsonEntry);
            }
        } catch (JSONException e) {
            Log.e(Constants.LOG_TAG, "NetworkHelper.createRequestBody: JSONException - " + e.getLocalizedMessage(), e);
        }

        return jsonArray;
    }

    private boolean sendRequest(JSONArray jsonArray) {
        try {
            URL url = new URL(URL + "?token=" + mToken);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(TIMEOUT);
            urlConnection.setReadTimeout(TIMEOUT);
            urlConnection.setRequestMethod(METHOD);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.connect();

            //write request
            OutputStreamWriter outputWriter = new OutputStreamWriter(urlConnection.getOutputStream());
            outputWriter.write(jsonArray.toString());
            outputWriter.close();

            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

            //get response
            int responseCode = urlConnection.getResponseCode();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }

            urlConnection.disconnect();

            return responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_ACCEPTED;
        } catch (MalformedURLException me) {
            Log.e(Constants.LOG_TAG, "NetworkHelper.sendRequest: MalformedInputException - " + me.getLocalizedMessage(), me);
        } catch (IOException ie) {
            Log.e(Constants.LOG_TAG, "NetworkHelper.sendRequest: IOException - " + ie.getLocalizedMessage(), ie);
        }
        return false;
    }
}
