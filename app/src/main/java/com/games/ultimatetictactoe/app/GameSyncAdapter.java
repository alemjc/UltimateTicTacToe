package com.games.ultimatetictactoe.app;

import android.accounts.Account;
import android.content.*;
import android.os.Bundle;

/**
 * Created by alemjc on 11/15/15.
 */
public class GameSyncAdapter extends AbstractThreadedSyncAdapter{
    private ContentResolver contentResolver;
    private SharedPreferences sharedPreferences;

    public GameSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        sharedPreferences = context.getSharedPreferences(MainTicTacToeActivity.class.getName(),Context.MODE_PRIVATE);
        contentResolver = context.getContentResolver();
    }

    public GameSyncAdapter(Context context,boolean autoInitialize, boolean allowParallelSyncs){
        super(context, autoInitialize,allowParallelSyncs);
        sharedPreferences = context.getSharedPreferences(MainTicTacToeActivity.class.getName(),Context.MODE_PRIVATE);
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
                              SyncResult syncResult) {

        /*TODO: perform synchronization based on what triggered the syncadapter.
        * if the sync adapter was triggered by the gcm, then we expected to run by the protocol created for it.
        * if the sync adapter was triggered by some changes in the local database, then the sync adapter has to
        * send this changes to the opponent.*/
    }
}
