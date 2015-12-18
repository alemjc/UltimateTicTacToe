package com.games.ultimatetictactoe.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by alemjc on 11/15/15.
 */
public class AuthenticatorService extends Service {

    private Authenticator authenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        authenticator = new Authenticator(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
