package com.mobiledevpro.remotelogcat;

import java.util.ArrayList;

/**
 * Interface for DBHelper
 * <p>
 * Created by Dmitriy V. Chernysh on 23.09.17.
 * dmitriy.chernysh@gmail.com
 * <p>
 * https://instagr.am/mobiledevpro
 * https://github.com/dmitriy-chernysh
 * <p>
 * #MobileDevPro
 */

interface IDBHelper {

    /**
     * Select all log entries
     *
     * @return list of log entries
     */
    ArrayList<LogEntryModel> selectLogEntriesList();


    /**
     * Insert a new entry
     *
     * @param logEntry LogEntryModel
     * @return True -success
     */
    boolean insertLogEntry(LogEntryModel logEntry);

    /**
     * Delete log entry
     *
     * @param id id
     * @return True - success
     */
    boolean deleteLogEntry(int id);

    /**
     * Delete log entries
     *
     * @param ids ids
     * @return True - success
     */
    boolean deleteLogEntryList(int[] ids);

    /**
     * Update entries status
     *
     * @param ids ids
     * @return True - success
     */
    boolean updateEntriesStatus(int[] ids, boolean isSendingToServer);
}
