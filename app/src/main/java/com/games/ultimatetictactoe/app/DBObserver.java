package com.games.ultimatetictactoe.app;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

/**
 * Created by alemjc on 11/15/15.
 */
public class DBObserver extends ContentObserver {
    public DBObserver(Handler handler) {
        super(handler);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

    }

}
