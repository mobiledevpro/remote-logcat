package com.mobiledevpro.remotelogcat;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Contract for local database
 * <p>
 * Created by Dmitriy V. Chernysh on 23.09.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * https://instagr.am/mobiledevpro
 * https://github.com/dmitriy-chernysh
 * <p>
 * #MobileDevPro
 */

class DBContract {

    private DBContract() {
    }

    static void onCreate(SQLiteDatabase db) {
        db.execSQL(Table.SQL_CREATE_TABLE);
    }

    static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    /***
     * Query all entries
     *
     */
    static Cursor selectEntriesToSend(SQLiteDatabase db) {
        return db.query(
                Table.TABLE_NAME,
                Table.QUERY_PROJECTION,
                "CAST (" + Table.COLUMN_IS_SENDING + " as TEXT) = ?",
                new String[]{"0"},
                null,
                null,
                Table.COLUMN_DATETIME + " ASC",
                null
        );
    }


    /**
     * Check if entry exists by id
     */
    static boolean isEntryExists(SQLiteDatabase db, int logEntryId) {
        Cursor cursor;
        int rowCount = 0;

        cursor = db.query(
                Table.TABLE_NAME,
                new String[]{Table._ID},
                "CAST (" + Table._ID + " as TEXT) = ?",
                new String[]{String.valueOf(logEntryId)},
                null,
                null,
                null,
                null
        );

        try {
            rowCount = cursor.getCount();
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "DBContract.isEntryExists: EXCEPTION - " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return rowCount > 0;
    }

    static boolean updateEntriesStatus(SQLiteDatabase db, int[] ids, boolean isSending) {
        int result = 0;
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put(Table.COLUMN_IS_SENDING, isSending ? 1 : 0);

        try {
            for (int id : ids) {
                result = result + db.update(
                        Table.TABLE_NAME,
                        cv,
                        "CAST(" + Table._ID + " as TEXT) = ?",
                        new String[]{String.valueOf(id)}
                );
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "DBContract.updateEntriesStatus: exception - " + e.getMessage(), e);
            result = 0;
        } finally {
            db.endTransaction();
        }

        return result > 0;
    }

    /**
     * Insert a new log entry
     */
    static boolean insert(SQLiteDatabase db, LogEntryModel logEntry) {
        if (logEntry == null) return false;
        long insertedRowId = -1;

        ContentValues cv = new ContentValues();
        cv.put(Table.COLUMN_DATETIME, logEntry.getDateTime());
        cv.put(Table.COLUMN_LOG_LEVEL, logEntry.getLogLevel());
        cv.put(Table.COLUMN_LOG_TAG, logEntry.getLogTag());
        cv.put(Table.COLUMN_LOG_MSG, logEntry.getLogMsg());
        UserInfoModel userInfo = logEntry.getAppUserInfo();
        if (userInfo != null) {
            cv.put(Table.COLUMN_APP_USER, userInfo.getString());
        }

        AppInfoModel appInfo = logEntry.getAppInfo();
        if (appInfo != null) {
            cv.put(Table.COLUMN_APP_NAME, appInfo.getName());
            cv.put(Table.COLUMN_APP_VERSION, appInfo.getVersion());
            cv.put(Table.COLUMN_APP_BUILD, appInfo.getBuild());
        }

        cv.put(Table.COLUMN_IS_SENDING, 0);

        db.beginTransaction();
        try {
            insertedRowId = db.insert(
                    Table.TABLE_NAME,
                    null,
                    cv
            );
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "DBContract.insert: EXCEPTION - " + e.getLocalizedMessage(), e);
        } finally {
            db.endTransaction();
        }

        return insertedRowId > -1;
    }

    /**
     * Delete log entry by Id
     *
     * @param db         SQLiteDatabase
     * @param logEntryId Log entry id
     */
    static boolean delete(SQLiteDatabase db, int logEntryId) {
        long result = 0;
        db.beginTransaction();
        try {
            result = db.delete(
                    Table.TABLE_NAME,
                    "CAST (" + Table._ID + " as TEXT) =?",
                    new String[]{String.valueOf(logEntryId)}
            );

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "DBContract.delete: exception - " + e.getLocalizedMessage(), e);
        } finally {
            db.endTransaction();
        }

        return result > 0;
    }

    /**
     * Delete log entry by Id
     *
     * @param db          SQLiteDatabase
     * @param logEntryIds Log entry ids
     */
    static boolean delete(SQLiteDatabase db, int[] logEntryIds) {
        long result = 0;
        db.beginTransaction();
        try {
            for (int id : logEntryIds) {
                result = result + db.delete(
                        Table.TABLE_NAME,
                        "CAST (" + Table._ID + " as TEXT) =?",
                        new String[]{String.valueOf(id)}
                );
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "DBContract.delete: exception - " + e.getLocalizedMessage(), e);
        } finally {
            db.endTransaction();
        }

        return result > 0;
    }


    /**
     * Convert cursor data to LogEntry model
     *
     * @param cursor Cursor
     * @return LogEntryModel
     */
    static LogEntryModel getLogEntryFromCursor(Cursor cursor) {
        if (cursor == null) return null;
        return new LogEntryModel(
                cursor.getInt(cursor.getColumnIndex(Table._ID)),
                cursor.getLong(cursor.getColumnIndex(Table.COLUMN_DATETIME)),
                cursor.getInt(cursor.getColumnIndex(Table.COLUMN_LOG_LEVEL)),
                cursor.getString(cursor.getColumnIndex(Table.COLUMN_LOG_TAG)),
                cursor.getString(cursor.getColumnIndex(Table.COLUMN_LOG_MSG)),
                new AppInfoModel(
                        cursor.getString(cursor.getColumnIndex(Table.COLUMN_APP_NAME)),
                        cursor.getString(cursor.getColumnIndex(Table.COLUMN_APP_VERSION)),
                        cursor.getInt(cursor.getColumnIndex(Table.COLUMN_APP_BUILD))
                ),
                new UserInfoModel().parseUserInfo(cursor.getString(cursor.getColumnIndex(Table.COLUMN_APP_USER)))
        );
    }

    private static class Table implements BaseColumns {
        private static final String TABLE_NAME = "logs";
        private static final String COLUMN_DATETIME = "datetime"; //in milliseconds
        private static final String COLUMN_LOG_LEVEL = "log_level"; //see Constants class
        private static final String COLUMN_LOG_TAG = "log_tag";
        private static final String COLUMN_LOG_MSG = "log_msg";
        private static final String COLUMN_APP_NAME = "app_name";
        private static final String COLUMN_APP_VERSION = "app_version";
        private static final String COLUMN_APP_BUILD = "app_build";
        private static final String COLUMN_APP_USER = "app_user"; //some data about user (divider - ";")
        private static final String COLUMN_IS_SENDING = "is_sending"; //indicate that the entry in already is sending to server

        //table create sql
        private static final String SQL_CREATE_TABLE = "create table IF NOT EXISTS "
                + TABLE_NAME
                + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DATETIME + " INTEGER, "
                + COLUMN_LOG_LEVEL + " INTEGER, "
                + COLUMN_LOG_TAG + " TEXT, "
                + COLUMN_LOG_MSG + " TEXT, "
                + COLUMN_APP_NAME + " TEXT, "
                + COLUMN_APP_VERSION + " TEXT, "
                + COLUMN_APP_BUILD + " INTEGER, "
                + COLUMN_APP_USER + " TEXT, "
                + COLUMN_IS_SENDING + " INTEGER "
                + ");";

        private static final String[] QUERY_PROJECTION = {
                _ID,
                COLUMN_DATETIME,
                COLUMN_LOG_LEVEL,
                COLUMN_LOG_TAG,
                COLUMN_LOG_MSG,
                COLUMN_APP_NAME,
                COLUMN_APP_VERSION,
                COLUMN_APP_BUILD,
                COLUMN_APP_USER,
                COLUMN_IS_SENDING
        };
    }


}
