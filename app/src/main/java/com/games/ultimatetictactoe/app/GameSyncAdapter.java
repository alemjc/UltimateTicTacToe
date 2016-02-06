package com.games.ultimatetictactoe.app;

import android.accounts.Account;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
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
    private static final int INTENT_REQUEST_CODE = 0x15;
    private static final int NOTIFICATION_ID = 0x1;

    public GameSyncAdapter(Context context, boolean autoInitialize) {
        this(context,autoInitialize,false);

    }

    public GameSyncAdapter(Context context,boolean autoInitialize, boolean allowParallelSyncs){
        super(context, autoInitialize,allowParallelSyncs);
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getPackageName()+"_preferences",
                Context.MODE_PRIVATE|Context.MODE_MULTI_PROCESS);
        contentResolver = context.getContentResolver();
    }

    private String getUsername(String from){
        String projection[] = {ContactsContract.Data.DISPLAY_NAME};
        String selection = ContactsContract.Data.MIMETYPE+"=?"+" AND "+ContactsContract.Data.DATA1+"=?";
        String args[] = {ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,from};
        Cursor cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,projection
                ,selection,args,null);
        String userName = null;
        if(cursor != null){
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                userName = cursor.getString(0);
            }
            cursor.close();
        }

        return userName;

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
                              SyncResult syncResult) {

        /*TODO: perform synchronization based on what triggered the syncadapter.
        * if the sync adapter was triggered by the gcm, then we expected to run by the protocol created for it.
        * if the sync adapter was triggered by some changes in the local database, then the sync adapter has to
        * send this changes to the opponent.*/
        Bundle message = null;
        String intent = extras.getString(context.getString(R.string.asyncbundleintent),"NONE");
        Notification.Builder notificationBuilder = new Notification.Builder(context);
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent gameIntent = new Intent(context,MainTicTacToeActivity.class);
        int messageID = sharedPreferences.getInt(MESSAGE_ID,0);
        String unParsedMessage = extras.getString(context.getString(R.string.asyncmessage));
        if(unParsedMessage == null){
            return;
        }
        String parsedMessage[] = unParsedMessage.split("\n");
        if(parsedMessage.length < 4){
            return;
        }

        message = new Bundle();
        message.putString("to",parsedMessage[0]);
        message.putString("fromNumber",parsedMessage[1]);
        message.putString("subject",parsedMessage[2]);
        message.putString("data",parsedMessage[3]);


        String subject = message.getString("subject","NONE");
        gameIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_xlauncher);

        notificationBuilder.setContentTitle(context.getString(R.string.app_name));
        notificationBuilder.setLargeIcon(bitmap).
                setSmallIcon(R.drawable.ic_stat_circlenotificationicon).
                setColor(context.getResources().getColor(R.color.red)).
                setContentIntent(PendingIntent.getActivity(context,INTENT_REQUEST_CODE,gameIntent,PendingIntent.FLAG_ONE_SHOT)).
                setVibrate(new long[]{100L,600L}).
                setAutoCancel(true);



        if (intent.equals(context.getString(R.string.asyncreceiveintent))) {


            String from = message.getString("fromNumber");
            String data = message.getString("data");

            if(subject.equals(context.getString(R.string.tokenrequest))){
                Log.d("async","getting token request");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(context.getString(R.string.firebasetokenkey),data);
                editor.commit();

                contentResolver.notifyChange(LoadingFragment.DONELOADINGURI,null);

            }
            else if(subject.equals(context.getString(R.string.gamerequest))){

                String userName = getUsername(from);

                if(userName == null){
                    userName = from;
                }

                String dataParams[] = data.split(";");


                if(dataParams.length == 0){
                    return;
                }

                String gameName = dataParams[0];
                Uri uri = Uri.parse(DBManager.CONTENTURI+"/"+ DBManager.DATABASENAME+"*"+"/"+"invitations");

                CPHandler.insertGameName(context,provider,gameName,userName,
                        context.getResources().getInteger(R.integer.gamestateawaitingacceptance),
                        context.getResources().getInteger(R.integer.myTurn));
                String initialRowStates = "-1,-1,-1,-1,-1,-1,-1,-1,-1";
                for(int i = 0; i < 3; i++){
                    for(int j = 0; j < 3; j++){
                        CPHandler.insert(context,provider,i+""+j,-1,9,initialRowStates,"",gameName);
                    }
                }
                notificationBuilder.setContentText(userName+" sent you an invitation to play!!");
                notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());
                context.getContentResolver().notifyChange(uri,null);
            }
            else if(subject.equals(context.getString(R.string.acceptgamerequest))){

                String userName = getUsername(from);

                if(userName == null){
                    userName = from;
                }

                String dataParams[] = data.split(";");


                if(dataParams.length == 0){
                    return;
                }


                String gameName = dataParams[0];

                CPHandler.updateGameState(context,provider,gameName,context.getResources().
                                                                                getInteger(R.integer.gamestateongoing));

                CPHandler.updateCurrentPLayer(context,provider,null,true,gameName,userName,
                        context.getResources().getInteger(R.integer.myTurn));

                notificationBuilder.setContentText(userName+" accepted your invitation to play!!");
                notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());
            }
            else if(subject.equals(context.getString(R.string.rejectgamerequest))){

                String userName = getUsername(from);

                if(userName == null){
                    userName = from;
                }

                String dataParams[] = data.split(";");

                if(dataParams.length == 0){
                    return;
                }

                String gameName = dataParams[0];
                CPHandler.removeGame(context,provider,gameName);

                notificationBuilder.setContentText(userName+" rejected your invitation to play!!");
                notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());
            }

            else if(subject.equals(context.getString(R.string.gamemove))){

                String userName = getUsername(from);

                if(userName == null){
                    userName = from;
                }

                String dataParams[] = data.split(";");


                if(dataParams.length < 2 || dataParams.length > 3) return;
                int tilesLeft;
                String gameName = dataParams[0];
                String gameState = dataParams[1];
                if(dataParams.length == 2){
                    int state = Integer.parseInt(gameState);
                    CPHandler.updateGameState(context,provider,gameName,state);
                    CPHandler.updateCurrentPLayer(context,provider,null,true,gameName,userName,context.getResources()
                            .getInteger(R.integer.myTurn));
                }
                else{
                    String composedMove = dataParams[2];

                    String move[] = composedMove.split("&");

                    if (move.length != 5) return;

                    String coordinates = move[0];
                    String tableState = move[1];
                    String lastMove = move[2];
                    String row = move[3];
                    tilesLeft = Integer.parseInt(move[4]);
                    CPHandler.updateGameState(context,provider,gameName,Integer.parseInt(gameState));
                    CPHandler.updateLastMove(context,provider,gameName,lastMove);
                    CPHandler.updateTable(context,provider, null,false, coordinates, Integer.parseInt(tableState), row, tilesLeft,gameName);
                    CPHandler.updateCurrentPLayer(context,provider,null,true,gameName,userName,context.getResources()
                            .getInteger(R.integer.myTurn));

                }

                notificationBuilder.setContentText(userName+" made a move!!");
                notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());
            }

        }
        else if(intent.equals(context.getString(R.string.asyncsendintent))){
            String projection[] = {ContactsContract.Data.DATA1};
            String args[] = {ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,message.getString("to")};
            String selection = ContactsContract.Data.MIMETYPE+"=?"+" AND "+ContactsContract.Contacts.DISPLAY_NAME+"=?";
            Cursor cursor = null;
            String data = message.getString("data");
            String dataParams[] = data.split(";");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

            if(dataParams.length == 0){
                return;
            }

            String gameName = dataParams[0];
            String userName = message.getString("to");

            if(subject.equals(context.getString(R.string.gamerequest))){

                CPHandler.insertGameName(context,provider,gameName,userName,
                        context.getResources().getInteger(R.integer.gamestateawaitingrequest),context.getResources().getInteger(R.integer.opponentsTurn));
                String initialRowStates = "-1,-1,-1,-1,-1,-1,-1,-1,-1";
                for(int i = 0; i < 3; i++){
                    for(int j = 0; j < 3; j++){
                        CPHandler.insert(context,provider,i+""+j,-1,9,initialRowStates,"",gameName);
                    }
                }

            }

            if(!subject.equals(context.getString(R.string.tokenrequest))) {
                cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, projection, selection, args, null);

                if (cursor == null || cursor.getCount() == 0) {
                    return;
                }
                cursor.moveToFirst();
                message.putString("to", cursor.getString(0));
                cursor.close();
            }

            if(subject.equals(context.getString(R.string.gamemove))){
                int tilesLeft;
                String gameState = dataParams[1];
                int state = Integer.parseInt(gameState);
                if(state != context.getResources().getInteger(R.integer.gamestatequit)){
                    StringBuffer buffer = new StringBuffer();
                    String composedMove = dataParams[2];
                    String move[] = composedMove.split("&");
                    if (move.length != 4) return;
                    buffer.append(dataParams[2].replace('m','o'));
                    buffer.append("&");
                    tilesLeft = CPHandler.getTileCountForTable(context,provider,move[0],gameName);
                    if(tilesLeft > 0){
                        tilesLeft--;
                    }

                    buffer.append(tilesLeft);

                    if(Integer.parseInt(gameState) == context.getResources().getInteger(R.integer.gamestatewon)){
                        message.putString("data",dataParams[0]+";"+
                                context.getResources().getInteger(R.integer.gamestatelose)+";"+buffer);
                    }
                    else{
                        message.putString("data",dataParams[0]+";"+dataParams[1]+";"+buffer);
                    }
                }

            }

            try {
                gcm.send(context.getString(R.string.defaultsenderid) + "@gcm.googleapis.com", messageID + "", message);
                editor.putInt(MESSAGE_ID,messageID+1);

                if(subject.equals(context.getString(R.string.gamemove))){
                    int tilesLeft;
                    String gameState = dataParams[1];
                    int state = Integer.parseInt(gameState);

                    if(state == context.getResources().getInteger(R.integer.gamestateongoing)){
                        String composedMove = dataParams[2];
                        String move[] = composedMove.split("&");
                        if (move.length != 4) return;
                        String coordinates = move[0];
                        int tableState = Integer.parseInt(move[1]);
                        String lastMove = move[2];
                        String opponentRow = move[3];
                        StringBuilder row = new StringBuilder();

                        tilesLeft = CPHandler.getTileCountForTable(context,provider,move[0],gameName);
                        if(tilesLeft > 0){
                            tilesLeft--;
                        }


                        if(tableState == context.getResources().getInteger(R.integer.myTurn)){
                            tableState = context.getResources().getInteger(R.integer.opponentsTurn);
                        }
                        else if(tableState == context.getResources().getInteger(R.integer.opponentsTurn)){
                            tableState = context.getResources().getInteger(R.integer.myTurn);
                        }



                        String tokens[] = opponentRow.split(",");

                        for(String token: tokens){
                            if(Integer.parseInt(token) == context.getResources().getInteger(R.integer.opponentsTurn)){
                                row.append(context.getResources().getInteger(R.integer.myTurn)+",");
                            }
                            else if(Integer.parseInt(token) == context.getResources().getInteger(R.integer.myTurn)){
                                row.append(context.getResources().getInteger(R.integer.opponentsTurn)+",");
                            }
                            else{
                                row.append(token+",");
                            }
                        }
                        row.deleteCharAt(row.length()-1);

                        CPHandler.updateGameState(context,provider,gameName,Integer.parseInt(gameState));
                        CPHandler.updateLastMove(context,provider,gameName,lastMove);
                        CPHandler.updateTable(context,provider, null,false, coordinates, tableState, row.toString(),tilesLeft, gameName);
                        CPHandler.updateCurrentPLayer(context,provider,null,false,gameName,userName,context.getResources()
                                .getInteger(R.integer.opponentsTurn));
                    }
                }
            }
            catch(IOException e){
                Log.d("asyncAdapter","could not send message");
            }

        }
    }
}
