package com.games.ultimatetictactoe.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by alemjc on 11/15/15.
 */
public class SyncAdapterService extends Service {
    private static final Object syncObject = new Object();
    private GameSyncAdapter syncAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized(syncObject){
            syncAdapter = new GameSyncAdapter(getApplicationContext(),true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();

    }
}

