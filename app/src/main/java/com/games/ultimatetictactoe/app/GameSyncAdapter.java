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
        String message = msgbundle.getString("message");
        String messageSplit[] = message.split("\n");
        String gameName = messageSplit[0];
        String purpose = messageSplit[1];

        if (subject.equals(getContext().getString(R.string.asyncreceivesubjecttype))) {


            String from = extras.getString("from");

            if(purpose.equals(getContext().getString(R.string.gamerequest))){
                DBManager.CPHandler.insertGameName(getContext(),gameName,from,
                        getContext().getResources().getInteger(R.integer.gamestateawaitingacceptance),
                        getContext().getResources().getInteger(R.integer.myTurn));
                String initialRowStates = "-1,-1,-1,-1,-1,-1,-1,-1,-1";
                for(int i = 0; i < 3; i++){
                    for(int j = 0; j < 3; j++){
                        DBManager.CPHandler.insert(getContext(),null,i+","+j,-1,initialRowStates,gameName);
                    }
                }
            }
            else if(purpose.equals(getContext().getString(R.string.acceptgamerequest))){
                DBManager.CPHandler.updateGameState(getContext(),gameName,getContext().getResources().
                                                                                getInteger(R.integer.gamestateongoing));
                DBManager.CPHandler.updateCurrentPLayer(getContext(),gameName,from,
                        getContext().getResources().getInteger(R.integer.myTurn));
            }
            else if(purpose.equals(getContext().getString(R.string.rejectgamerequest))){
                DBManager.CPHandler.removeGame(getContext(),gameName);
            }

            else {

                String move[] = purpose.split("()");
                if (move.length != 3) return;
                String coordinates = move[0];
                String state = move[1];
                String row = move[2];
                DBManager.CPHandler.updateTable(getContext(), null, coordinates, Integer.parseInt(state), row, gameName);
            }

        }
        else if(subject.equals(getContext().getString(R.string.asyncsendsubjecttype))){
            String to = extras.getString("to");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getContext());
            if(purpose.equals(getContext().getString(R.string.gamerequest))){
                DBManager.CPHandler.insertGameName(getContext(),gameName,to,
                        getContext().getResources().getInteger(R.integer.gamestateawaitingrequest),getContext().getResources().getInteger(R.integer.opponentsTurn));
                String initialRowStates = "-1,-1,-1,-1,-1,-1,-1,-1,-1";
                for(int i = 0; i < 3; i++){
                    for(int j = 0; j < 3; j++){
                        DBManager.CPHandler.insert(getContext(),null,i+","+j,-1,initialRowStates,gameName);
                    }
                }
            }


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
