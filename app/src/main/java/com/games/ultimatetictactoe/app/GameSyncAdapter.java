package com.games.ultimatetictactoe.app;

import android.accounts.Account;
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
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
        Log.d("","here performing some sync");
        Bundle message = null;
        String intent = extras.getString(context.getString(R.string.asyncbundleintent),"NONE");
        int messageID = sharedPreferences.getInt(MESSAGE_ID,0);
        String unParsedMessage = extras.getString(context.getString(R.string.asyncmessage));
        if(unParsedMessage == null){
            return;
        }
        String parsedMessage[] = unParsedMessage.split("\n");
        if(parsedMessage.length < 4){
            return;
        }

        Log.d("message",unParsedMessage);
        message = new Bundle();
        message.putString("to",parsedMessage[0]);
        message.putString("fromNumber",parsedMessage[1]);
        message.putString("subject",parsedMessage[2]);
        message.putString("data",parsedMessage[3]);

        Log.d("messageBundle",message.toString());

        String subject = message.getString("subject","NONE");



        if (intent.equals(context.getString(R.string.asyncreceiveintent))) {


            String from = message.getString("fromNumber");
            String data = message.getString("data");
            String dataParams[] = data.split(";");
            Log.d("fromNumber",from);
            String projection[] = {ContactsContract.Data.DISPLAY_NAME};
            String selection = ContactsContract.Data.MIMETYPE+"=?"+" AND "+ContactsContract.Data.DATA1+"=?";
            String args[] = {ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,from};
            Cursor cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,projection
                    ,selection,args,null);
            String userName = from;
            if(cursor != null){
                if(cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    userName = cursor.getString(0);
                }
                cursor.close();
            }

            Log.d("username",userName);

            if(subject.equals(context.getString(R.string.gamerequest))){


                if(dataParams.length == 0){
                    return;
                }

                String gameName = dataParams[0];

                CPHandler.insertGameName(context,provider,gameName,userName,
                        context.getResources().getInteger(R.integer.gamestateawaitingacceptance),
                        context.getResources().getInteger(R.integer.myTurn));
                String initialRowStates = "-1,-1,-1,-1,-1,-1,-1,-1,-1";
                for(int i = 0; i < 3; i++){
                    for(int j = 0; j < 3; j++){
                        CPHandler.insert(context,provider,i+""+j,-1,initialRowStates,gameName);
                    }
                }
            }
            else if(subject.equals(context.getString(R.string.acceptgamerequest))){
                Log.d("syncadapter","receiving gamerequest acceptance");
                if(dataParams.length == 0){
                    return;
                }

                Log.d("syncadapter","dataParams passed validation");
                String gameName = dataParams[0];
                Log.d("syncadapter","gameName: "+gameName);
                int affectedRows = CPHandler.updateGameState(context,provider,gameName,context.getResources().
                                                                                getInteger(R.integer.gamestateongoing));
                Log.d("syncadapter","affectedRows: "+affectedRows);
                CPHandler.updateCurrentPLayer(context,provider,null,true,gameName,userName,
                        context.getResources().getInteger(R.integer.myTurn));
            }
            else if(subject.equals(context.getString(R.string.rejectgamerequest))){
                if(dataParams.length == 0){
                    return;
                }

                String gameName = dataParams[0];
                CPHandler.removeGame(context,provider,gameName);
            }

            else if(subject.equals(context.getString(R.string.gamemove))){

                if(dataParams.length != 3) return;
                String gameName = dataParams[0];
                String gameState = dataParams[1];
                String composedMove = dataParams[2];
                String move[] = composedMove.split("()");
                if (move.length != 3) return;
                String coordinates = move[0];
                String tableState = move[1];
                String row = move[2];
                CPHandler.updateGameState(context,provider,gameName,Integer.parseInt(gameState));
                CPHandler.updateTable(context,provider, null, coordinates, Integer.parseInt(tableState), row, gameName);
            }

        }
        else if(intent.equals(context.getString(R.string.asyncsendintent))){
            String projection[] = {ContactsContract.Data.DATA1};
            String args[] = {ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,message.getString("to")};
            String selection = ContactsContract.Data.MIMETYPE+"=?"+" AND "+ContactsContract.Contacts.DISPLAY_NAME+"=?";
            Cursor cursor = null;
            String data = message.getString("data");
            String dataParams[] = data.split("\n");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

            if(dataParams.length == 0){
                return;
            }

            String gameName = dataParams[0];
            

            if(subject.equals(context.getString(R.string.gamerequest))){
                String userName = message.getString("to");
                CPHandler.insertGameName(context,provider,gameName,userName,
                        context.getResources().getInteger(R.integer.gamestateawaitingrequest),context.getResources().getInteger(R.integer.opponentsTurn));
                String initialRowStates = "-1,-1,-1,-1,-1,-1,-1,-1,-1";
                for(int i = 0; i < 3; i++){
                    for(int j = 0; j < 3; j++){
                        CPHandler.insert(context,provider,i+""+j,-1,initialRowStates,gameName);
                    }
                }

            }

            cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,projection,selection,args,null);

            if(cursor == null || cursor.getCount() == 0){
                return;
            }
            cursor.moveToFirst();
            message.putString("to",cursor.getString(0));
            cursor.close();

            try {
                gcm.send(context.getString(R.string.defaultsenderid) + "@gcm.googleapis.com", messageID + "", message);
            }
            catch(IOException e){
                Toast.makeText(context,"Could not send message",Toast.LENGTH_LONG);
            }

            editor.putInt(MESSAGE_ID,messageID+1);

        }
    }
}
