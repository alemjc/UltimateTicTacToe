package com.games.ultimatetictactoe.app;

import android.accounts.Account;
import android.content.*;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by alemjc on 11/15/15.
 */
public class GameSyncAdapter extends AbstractThreadedSyncAdapter{
    private ContentResolver contentResolver;
    private SharedPreferences sharedPreferences;
    private static final String MESSAGE_ID="messageID";

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
        String subject = extras.getString(getContext().getString(R.string.asyncBundlesubject),"NONE");
        int messageid = sharedPreferences.getInt(MESSAGE_ID,0);
        Bundle msgbundle = extras.getBundle(getContext().getString(R.string.asyncmessagebundle));

        if(subject.equals(getContext().getString(R.string.gamerequest))){
            //TODO: what to do if a user makes a game request.
        }


        else if (subject.equals(getContext().getString(R.string.asyncreceivesubjecttype))) {

            String message = msgbundle.getString("message");
            String messageSplit[] = message.split("\n");
            String gameName = messageSplit[0];
            String purpose = messageSplit[1];
            String from = extras.getString("from");


            String move[] = purpose.split("()");
            if (move.length != 3) return;
            String coordinates = move[0];
            String state = move[1];
            String row = move[2];
            DBManager.CPHandler.updateTable(getContext(), null, coordinates, Integer.parseInt(state), row, gameName);


        }
        else if(subject.equals(getContext().getString(R.string.asyncsendsubjecttype))){
            String to = extras.getString("to");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getContext());
            try {
                gcm.send(to + "@gcm.googleapis.com", messageid + "", msgbundle);
            }
            catch(IOException e){
                Toast.makeText(getContext(),"Could not send message",Toast.LENGTH_LONG);
            }

            editor.putInt(MESSAGE_ID,messageid+1);

        }
    }
}
