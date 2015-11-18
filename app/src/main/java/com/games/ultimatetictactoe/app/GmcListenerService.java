package com.games.ultimatetictactoe.app;

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
        SharedPreferences preferences = context.getSharedPreferences(MainTicTacToeActivity.class.getCanonicalName(),
                                        Context.MODE_PRIVATE);
        String msg = data.getString("message");
        String msgdata[] = msg.split("\n");

        if(context.getString(R.string.gamerequest).equals(msgdata[1])){
            boolean gameStarted = preferences.getBoolean(context.getString(R.string.gamestarted),false);
            if(!gameStarted){
                String gameName = msgdata[0];
                //TODO: Tell user he has received a game request and have the user decide whether to accept or not.
                /* This might be through the use of a notification that will some how start some kind of receiver or
                    a notification that will start an activity based on the users decision, this might be to start the game
                     with an activity that says yes or no, if the user says yes then store the game name and the opponentid then send
                     the opponent an acceptance to the request with the game name as the topic,
                     but if the user says no then send a rejection to a request. the activity should take the opponentsenderid
                     and also the game name.
                 */


            }
        }
        else if(context.getString(R.string.acceptgamerequest).equals(msgdata[1])){
            String opponentsSerderIDKey = context.getString(R.string.opponentsmailid);

            if(preferences.getString(opponentsSerderIDKey,"none").equals(from)){
                SharedPreferences.Editor editor = preferences.edit();
                String gameStatusKey = context.getString(R.string.gamestatus);
                editor.putString(gameStatusKey,context.getString(R.string.gamestatuscontinue)).
                        commit();
            }

        }

        else if(msgdata[1].contains("Game Over")){
            //TODO: Notify user that opponent has won.
        }

    }
}
