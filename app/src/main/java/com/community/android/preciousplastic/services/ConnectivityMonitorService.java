package com.community.android.preciousplastic.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.community.android.preciousplastic.activities.BaseActivity;
import com.community.android.preciousplastic.utils.PPSession;

import java.io.IOException;

public class ConnectivityMonitorService extends IntentService {

    private static final String TAG = "CONNECTIVITY_MONITOR";

    private static final int CONNECT_RETRIES = 3;

    public enum OnlineStatus {
        CONNECTED,
        DISCONNECTED,
        DONE
    }

    public ConnectivityMonitorService() {
        super("ConnectivityMonitorService");
    }

    public ConnectivityMonitorService(String name) {
        super(name);
    }

    /**
     * Repeatedly checks whether there's an active connection, until a failure occurs..
     * @param workIntent irrelevant, not using any parameters.
     */
    @Override
    protected void onHandleIntent(Intent workIntent) {
        int secs = 1;
        int tries = 0;
        while (true) {
            final OnlineStatus onlineStatus = isOnline();
            if(onlineStatus == OnlineStatus.DISCONNECTED) {
                tries++;
                Log.w(TAG, String.format("onHandleIntent: no connection, attempted %s retries.", tries));
                if (tries == CONNECT_RETRIES) {
                    Log.w(TAG, "onHandleIntent: failed to connect, broadcasting failure.");
                    broadcastFailureToActivity();
                    return;
                }
            } else if (onlineStatus == OnlineStatus.CONNECTED) {
                try {
                    tries = 0;
                    // checks connectivity every "secs" seconds.
                    Thread.sleep(secs * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                // means that we're done checking (interrupted).
                return;
            }
        }
    }

    /**
     * notifies the current active activity of an internet failure.
     */
    private void broadcastFailureToActivity() {
        Intent intent = new Intent(PPSession.currentIntentKey);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Checks whether there is an online connection by pinging google.
     * @return true if there is a connection, else - false.
     */
    public static OnlineStatus isOnline() {
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
}