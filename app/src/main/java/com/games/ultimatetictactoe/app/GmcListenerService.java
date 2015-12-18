package com.games.ultimatetictactoe.app;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by alemjc on 11/17/15.
 */
public class GmcListenerService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        Context context = getApplicationContext();
        ContentResolver contextResolver = context.getContentResolver();
        String asyncBundleSubjectKey = context.getString(R.string.asyncBundlesubject);
        Bundle receive = new Bundle();
        receive.putString(asyncBundleSubjectKey,context.getString(R.string.asyncreceivesubjecttype));
        receive.putString("from",from);
        receive.putBundle(context.getString(R.string.asyncmessagebundle),data);

        contextResolver.requestSync(MainTicTacToeActivity.createSyncAccount(context),MainTicTacToeActivity.AUTHORITY,receive);

    }
}
