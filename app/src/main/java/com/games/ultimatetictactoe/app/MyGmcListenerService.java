package com.games.ultimatetictactoe.app;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by alemjc on 11/17/15.
 * Listener service that will read messages received from gcm. it will then prepare a bundle to send to the sync adapter,
 * so that the sync adapter can process the data.
 */
public class MyGmcListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        Account account;
        Context context = getApplicationContext();
        ContentResolver contentResolver = context.getContentResolver();
        Bundle parsedBundle = new Bundle();
        StringBuilder message = new StringBuilder();
        String asyncBundleSubjectKey = context.getString(R.string.asyncbundleintent);

        if(!data.containsKey("to") || !data.containsKey("data") || !data.containsKey("fromNumber") ||
                !data.containsKey("subject")){
            Log.d("GmcListenerService","data not well formatted.");
            return;
        }
        Log.d("onMessageReceived",data.toString());
        message.append(data.getString("to"));
        message.append("\n");
        message.append(data.getString("fromNumber"));
        message.append("\n");
        message.append(data.getString("subject"));
        message.append("\n");
        message.append(data.getString("data"));

        parsedBundle.putString(asyncBundleSubjectKey,context.getString(R.string.asyncreceiveintent));
        parsedBundle.putString(context.getString(R.string.asyncmessage),message.toString());
        parsedBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,true);
        parsedBundle.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE,true);
        //receive.putBundle(context.getString(R.string.asyncmessagebundle),data);
        account = new Account(MainTicTacToeActivity.ACCOUNT,MainTicTacToeActivity.ACCOUNTTYPE);
        contentResolver.requestSync(account,MainTicTacToeActivity.AUTHORITY,parsedBundle);

    }


}
