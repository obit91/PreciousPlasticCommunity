package com.example.android.preciousplastic.connection;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

public class ConnectivityMonitor extends AsyncTask<Void, Integer, Boolean> {

    private final String TAG = "CONNECTIVITY_MONITOR";

    private OnTaskCompleted listener;

    public ConnectivityMonitor(OnTaskCompleted listener) {
        this.listener = listener;
    }

    /**
     * Repeatedly checks whether there's an active connection, until a failure occurs..
     * @param params
     * @return false when there is no active connection.
     */
    @Override
    protected Boolean doInBackground(Void... params) {
        while (true) {
            final OnlineStatus onlineStatus = isOnline();
            if(onlineStatus == OnlineStatus.DISCONNECTED) {
                return false;
            } else if (onlineStatus == OnlineStatus.CONNECTED) {
                try {
                    // checks connectivity every 2 seconds.
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                // means that we're done checking (interrupted).
                return true;
            }
        }
    }

    /**
     * Delegates false when there is a connectivity issue.
     * @param result a boolean indicating that there is no connectivity.
     */
    protected void onPostExecute(Boolean result) {
        listener.onTaskCompleted(result);
    }

    /**
     * Checks whether there is an online connection by pinging google.
     * @return true if there is a connection, else - false.
     */
    private OnlineStatus isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            if (exitValue == 0) {
                return OnlineStatus.CONNECTED;
            } else {
                return OnlineStatus.DISCONNECTED;
            }

        } catch (IOException e) {
            Log.e(TAG, "isOnline: got an IOException - " + e.getMessage());
        } catch (InterruptedException e) {
            Log.d(TAG, "isOnline: got interrupted.");
            return OnlineStatus.DONE;
        }
        return OnlineStatus.DISCONNECTED;
    }

    private enum OnlineStatus {
        CONNECTED,
        DISCONNECTED,
        DONE
    }
}