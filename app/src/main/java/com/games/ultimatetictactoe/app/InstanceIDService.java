package com.games.ultimatetictactoe.app;

import android.content.Intent;
import android.os.IBinder;
import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by alemjc on 11/17/15.
 */
public class InstanceIDService extends InstanceIDListenerService {
    public InstanceIDService() {
        super();
    }

    @Override
    public void zzp(Intent intent) {
        super.zzp(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void zzah(boolean b) {
        super.zzah(b);
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Intent intent = new Intent(this,RegistrationService.class);
        startService(intent);
    }
}
