package com.games.ultimatetictactoe.app;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class UpdateTableService extends IntentService {

    /*actions for intent service*/
    public static final String ACTION_UPDATETABLE = "com.games.ultimatetictactoe.app.action.UPDATETABLE";
    public static final String ACTION_UPDATEPLAYER =  "com.games.ultimatetictactoe.app.acton.UPDATEPLAYER";
    public static final String ACTION_UPDATEGAMESTATE = "com.games.ultimatetictactoe.app.acton.UPDATEGAMESTATE";
    public static final String ACTION_REMOVEGAME = "com.games.ultimatetictactoe.app.action.REMOVEGAME";

    /*parameter keys for intent service*/
    public static final String EXTRA_ROW = "com.games.ultimatetictactoe.app.extra.ROW";
    public static final String EXTRA_COORDINATES = "com.games.ultimatetictactoe.app.extra.COORDINATES";
    public static final String EXTRA_GAMENAME = "com.games.ultimatetictactoe.app.extra.GAMENAME";
    public static final String EXTRA_TABLESTATE = "com.games.ultimatetictactoe.app.extra.TABLESTATE";
    public static final String EXTRA_OPPONENTID = "com.games.ultimatetictactoe.app.extra.OPPONENTID";
    public static final String EXTRA_CURRENTPLAYER = "com.games.ultimatetictactoe.app.extra.CURRENTPLAYER";
    public static final String EXTRA_GAMESTATE = "com.games.ultimatetictactoe.app.extra.GAMESTATE";
    public static final String EXTRA_LAST_MOVE = "com.games.ultimatetictactoe.app.extra.LASTMOVE";

    /**
     * Starts this service to perform action UpdateTable with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    private void startActionUpdateTable(Context context, String row,String lastMove, String coordinates, String gameName,
                                              int tableState) {
       /* CPHandler.updateTable(context,context.getContentResolver().acquireContentProviderClient(DBManager.CONTENTURI),
                null,false,coordinates,tableState,row,gameName);
        if(lastMove != null && lastMove.length() != 0){
            CPHandler.updateLastMove(context,context.getContentResolver().acquireContentProviderClient(DBManager.CONTENTURI)
                                    ,gameName,lastMove);
        }*/
    }

    private void startActionUpdatePlayer(Context context, String gameName, String opponentID, int currentPlayer){
        CPHandler.updateCurrentPLayer(context,context.getContentResolver().acquireContentProviderClient(DBManager.CONTENTURI),
                null,false,gameName,opponentID,currentPlayer);
    }

    private void startActionUpdateGameState(Context context,String gameName, int gameState){
        CPHandler.updateGameState(context, context.getContentResolver().acquireContentProviderClient(DBManager.CONTENTURI),
                gameName,gameState);
    }

    private void startActionRemoveGame(Context context, String gameName){
        CPHandler.removeGame(context,context.getContentResolver().acquireContentProviderClient(DBManager.CONTENTURI),gameName);
    }


    public UpdateTableService() {
        super("UpdateTableService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            String gameName = intent.getStringExtra(EXTRA_GAMENAME);

            switch(action){
                case ACTION_UPDATETABLE:
                    String row = intent.getStringExtra(EXTRA_ROW);
                    String coordinate = intent.getStringExtra(EXTRA_COORDINATES);
                    String lastMove = intent.getStringExtra(EXTRA_LAST_MOVE);

                    int tableState = intent.getIntExtra(EXTRA_TABLESTATE,-2);

                    if(row != null && coordinate != null && gameName != null && tableState != -2){
                        startActionUpdateTable(getApplicationContext(), row, lastMove, coordinate, gameName, tableState);
                    }

                    break;
                case ACTION_UPDATEPLAYER:
                    String opponentID = intent.getStringExtra(EXTRA_OPPONENTID);
                    int currentPlayer = intent.getIntExtra(EXTRA_CURRENTPLAYER,-2);

                    if(opponentID != null && currentPlayer != -2){
                        startActionUpdatePlayer(getApplicationContext(),gameName, opponentID, currentPlayer);
                    }

                    break;

                case ACTION_UPDATEGAMESTATE:

                    int gameState = intent.getIntExtra(EXTRA_GAMESTATE,-2);

                    if(gameName != null && gameState != -2){
                        startActionUpdateGameState(getApplicationContext(),gameName,gameState);
                    }
                    break;
                case ACTION_REMOVEGAME:
                    if(gameName != null){
                        startActionRemoveGame(getApplicationContext(),gameName);
                    }
            }

        }
    }

}
