package com.mobiledevpro.remotelogcat;

import android.os.AsyncTask;

/**
 * Helper for accessing features in {@link AsyncTask}
 * introduced after API level 4 in a backwards compatible fashion.
 *
 * This class has been removed from support library version 26+
 * <p>
 * Created by Dmitriy V. Chernysh on 07.03.18.
 * <p>
 * https://fb.com/mobiledevpro/
 * https://github.com/dmitriy-chernysh
 * #MobileDevPro
 */

class AsyncTaskCompat {
    /**
     * Executes the task with the specified parameters, allowing multiple tasks to run in parallel
     * on a pool of threads managed by {@link AsyncTask}.
     *
     * @param task   The {@link AsyncTask} to execute.
     * @param params The parameters of the task.
     * @return the instance of AsyncTask.
     */
    static <Params, Progress, Result> AsyncTask<Params, Progress, Result> executeParallel(
            AsyncTask<Params, Progress, Result> task, Params... params) {
        if (task == null) {
            throw new IllegalArgumentException("task can not be null");
        }

        // From API 11 onwards, we need to manually select the THREAD_POOL_EXECUTOR
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);

        return task;
    }
}
