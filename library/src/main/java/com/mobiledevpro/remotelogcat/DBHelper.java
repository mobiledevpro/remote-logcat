package com.mobiledevpro.remotelogcat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * SQLite database helper
 * <p>
 * Created by Dmitriy V. Chernysh on 23.09.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * https://fb.me/mobiledevpro/
 * <p>
 * #MobileDevPro
 */

class DBHelper extends SQLiteOpenHelper implements IDBHelper {
    private static final String DB_NAME = "logs";
    private static final int DB_VERSION = 2;

    private static DBHelper sDBHelperInstance;

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Get DBHelper instance
     *
     * @return DBHelper instance
     */
    static synchronized DBHelper getInstance(Context appContext) {
        if (sDBHelperInstance == null) {
            sDBHelperInstance = new DBHelper(appContext);
        }
        return sDBHelperInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DBContract.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DBContract.onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Select all log entries
     *
     * @return list of log entries
     */
    @Override
    public ArrayList<LogEntryModel> selectLogEntriesList() {
        ArrayList<LogEntryModel> list = new ArrayList<>();
        LogEntryModel logEntry;

        Cursor cursor = DBContract.selectEntriesToSend(getReadableDatabase());

        try {
            if (cursor.moveToFirst()) {
                do {
                    logEntry = DBContract.getLogEntryFromCursor(cursor);
                    list.add(logEntry);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "DBHelper.selectLogEntriesList: EXCEPTION - " + e.getLocalizedMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return list;
    }

    @Override
    public boolean insertLogEntry(LogEntryModel logEntry) {
        if (!DBContract.isEntryExists(getReadableDatabase(), logEntry.getId())) {
            return DBContract.insert(getWritableDatabase(), logEntry);
        }

        return false;
    }

    @Override
    public boolean deleteLogEntry(int id) {
        return DBContract.delete(getWritableDatabase(), id);
    }

    @Override
    public boolean deleteLogEntryList(int[] ids) {
        return DBContract.delete(getWritableDatabase(), ids);
    }

    @Override
    public boolean updateEntriesStatus(int[] ids, boolean isSendingToServer) {
        return DBContract.updateEntriesStatus(getWritableDatabase(), ids, isSendingToServer);
    }
}
