package com.games.ultimatetictactoe.app;

import android.accounts.Account;
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by alemjc on 11/15/15.
 */
public class GameSyncAdapter extends AbstractThreadedSyncAdapter{
    private ContentResolver contentResolver;
    private SharedPreferences sharedPreferences;
    private Context context;
    private static final String MESSAGE_ID="messageID";

    public GameSyncAdapter(Context context, boolean autoInitialize) {
        this(context,autoInitialize,false);

    }

    public GameSyncAdapter(Context context,boolean autoInitialize, boolean allowParallelSyncs){
        super(context, autoInitialize,allowParallelSyncs);
        this.context = context;
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
        String subject = extras.getString(context.getString(R.string.asyncBundlesubject),"NONE");
        int messageID = sharedPreferences.getInt(MESSAGE_ID,0);
        Bundle msgBundle = extras.getBundle(context.getString(R.string.asyncmessagebundle));
        String message = msgBundle.getString("message");
        String messageSplit[] = message.split("\n");
        String gameName = messageSplit[0];
        String purpose = messageSplit[1];


        if (subject.equals(context.getString(R.string.asyncreceivesubjecttype))) {


            String from = extras.getString("from");

            if(purpose.equals(context.getString(R.string.gamerequest))){
                String projection[] = {ContactsContract.Contacts.DISPLAY_NAME};
                String selection = ContactsContract.Data.MIMETYPE+"=?"+" AND "+ContactsContract.Data.DATA1+"=?";
                String args[] = {ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE,messageSplit[2]};
                Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,projection
                            ,selection,args,null);
                String userName = messageSplit[2];
                
                if(cursor != null){
                    if(cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        userName = cursor.getString(0);
                    }
                    cursor.close();
                }
                CPHandler.insertGameName(context,provider,gameName,from,userName,
                        context.getResources().getInteger(R.integer.gamestateawaitingacceptance),
                        context.getResources().getInteger(R.integer.myTurn));
                String initialRowStates = "-1,-1,-1,-1,-1,-1,-1,-1,-1";
                for(int i = 0; i < 3; i++){
                    for(int j = 0; j < 3; j++){
                        CPHandler.insert(context,provider,i+","+j,-1,initialRowStates,gameName);
                    }
                }
            }
            else if(purpose.equals(context.getString(R.string.acceptgamerequest))){
                CPHandler.updateGameState(context,provider,gameName,context.getResources().
                                                                                getInteger(R.integer.gamestateongoing));
                CPHandler.updateCurrentPLayer(context,provider,gameName,from,
                        context.getResources().getInteger(R.integer.myTurn));
            }
            else if(purpose.equals(context.getString(R.string.rejectgamerequest))){
                CPHandler.removeGame(context,provider,gameName);
            }

            else {
                if(messageSplit.length != 3) return;
                purpose = messageSplit[1];
                String gameState = purpose.split(" ")[1];
                String move[] = messageSplit[2].split("()");
                if (move.length != 3) return;
                String coordinates = move[0];
                String tableState = move[1];
                String row = move[2];
                CPHandler.updateGameState(context,provider,gameName,Integer.parseInt(gameState));
                CPHandler.updateTable(context,provider, null, coordinates, Integer.parseInt(tableState), row, gameName);
            }

        }
        else if(subject.equals(context.getString(R.string.asyncsendsubjecttype))){
            String to = extras.getString("to");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            
            
            if(purpose.equals(context.getString(R.string.gamerequest))){
                String userName = context.getString(R.string.asyncusername);
                CPHandler.insertGameName(context,provider,gameName,to,userName,
                        context.getResources().getInteger(R.integer.gamestateawaitingrequest),context.getResources().getInteger(R.integer.opponentsTurn));
                String initialRowStates = "-1,-1,-1,-1,-1,-1,-1,-1,-1";
                for(int i = 0; i < 3; i++){
                    for(int j = 0; j < 3; j++){
                        CPHandler.insert(context,null,i+","+j,-1,initialRowStates,gameName);
                    }
                }

                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String phoneNumber = telephonyManager.getLine1Number();

                if(phoneNumber == null){
                    Toast.makeText(context,"Some required features are not provided by device, aborting sending" +
                            " game play to opponent.",Toast.LENGTH_LONG);
                }
                
                message+="\n"+phoneNumber;
                msgBundle.putString("message",message);
            }
            


            try {
                gcm.send(to + "@gcm.googleapis.com", messageID + "", msgBundle);
            }
            catch(IOException e){
                Toast.makeText(context,"Could not send message",Toast.LENGTH_LONG);
            }

            editor.putInt(MESSAGE_ID,messageID+1);

        }
    }
}
