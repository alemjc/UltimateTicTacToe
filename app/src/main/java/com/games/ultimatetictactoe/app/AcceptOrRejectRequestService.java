package com.games.ultimatetictactoe.app;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AcceptOrRejectRequestService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_ACCEPT_REQUEST = "com.games.ultimatetictactoe.app.action.ACCEPT";
    public static final String ACTION_REJECT_REQUEST = "com.games.ultimatetictactoe.app.action.DENY";

    // TODO: Rename parameters
    public static final String EXTRA_GAME_NAME = "com.games.ultimatetictactoe.app.extra.GAME_NAME";
    public static final String EXTRA_MSG_ID = "com.games.ultimatetictactoe.app.extra.MSG_ID";


    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    private static void startActionAcceptRequest(Context context, String gameName,String msgID) {
        DBManager.CPHandler.updateGameState(context,gameName,context.getResources().getInteger(R.integer.gamestateongoing));
        DBManager.CPHandler.updateCurrentPLayer(context,gameName,msgID,context.getResources().getInteger(R.integer.opponentsTurn));
        Bundle bundle = new Bundle();
        bundle.putString(context.getString(R.string.asyncBundlesubject),context.getString(R.string.asyncsendsubjecttype));
        bundle.putString("to",msgID);
        Bundle msgBundle = new Bundle();
        String message = gameName+"\n"+context.getString(R.string.acceptgamerequest);
        msgBundle.putString("message", message);
        bundle.putBundle(context.getString(R.string.asyncmessagebundle),msgBundle);
        context.getContentResolver().requestSync(MainTicTacToeActivity.createSyncAccount(context),MainTicTacToeActivity.AUTHORITY,bundle);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionRejectRequest(Context context, String gameName, String msgID) {
        DBManager.CPHandler.removeGame(context,gameName);
        Bundle bundle = new Bundle();
        bundle.putString(context.getString(R.string.asyncBundlesubject),context.getString(R.string.asyncsendsubjecttype));
        bundle.putString("to",msgID);
        Bundle msgBundle = new Bundle();
        String message = gameName+"\n"+context.getString(R.string.rejectgamerequest);
        msgBundle.putString("message", message);
        bundle.putBundle(context.getString(R.string.asyncmessagebundle),msgBundle);
        context.getContentResolver().requestSync(MainTicTacToeActivity.createSyncAccount(context),MainTicTacToeActivity.AUTHORITY,bundle);
    }

    public AcceptOrRejectRequestService() {
        super("AcceptOrRejectRequestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final String gameName = intent.getStringExtra(EXTRA_GAME_NAME);
            final String msgID = intent.getStringExtra(EXTRA_MSG_ID);
            if (ACTION_ACCEPT_REQUEST.equals(action)) {
                startActionAcceptRequest(getApplicationContext(), gameName,msgID);
            } else if (ACTION_REJECT_REQUEST.equals(action)) {
                startActionRejectRequest(getApplicationContext(), gameName, msgID);
            }
        }
    }

}
