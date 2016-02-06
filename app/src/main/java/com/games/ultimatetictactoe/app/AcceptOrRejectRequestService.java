package com.games.ultimatetictactoe.app;

import android.accounts.Account;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;

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
    public static final String EXTRA_ACCOUNT = "com.games.ultimatetictactoe.app.extra.ACCOUNT";


    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    private static void startActionAcceptRequest(Context context, String gameName,String userName, Account account) {
        CPHandler.updateGameState(context,context.getContentResolver().
                acquireContentProviderClient(DBManager.CONTENTURI),gameName,context.getResources().getInteger(R.integer.gamestateongoing));
        CPHandler.updateCurrentPLayer(context,context.getContentResolver().
                acquireContentProviderClient(DBManager.CONTENTURI),null,true,gameName,userName,context.getResources().getInteger(R.integer.opponentsTurn));
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = telephonyManager.getLine1Number();
        StringBuilder message = new StringBuilder();
        Bundle bundle = new Bundle();
        bundle.putString(context.getString(R.string.asyncbundleintent),context.getString(R.string.asyncsendintent));

        message.append(userName);
        message.append("\n");
        message.append(phoneNumber);
        message.append("\n");
        message.append(context.getString(R.string.acceptgamerequest));
        message.append("\n");
        message.append(gameName);

        bundle.putString(context.getString(R.string.asyncmessage),message.toString());
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE,true);
        ContentResolver.requestSync(account,MainTicTacToeActivity.AUTHORITY,bundle);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionRejectRequest(Context context, String gameName, String userName, Account account) {
        CPHandler.removeGame(context,context.getContentResolver().
                acquireContentProviderClient(DBManager.CONTENTURI),gameName);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = telephonyManager.getLine1Number();
        StringBuilder message = new StringBuilder();
        Bundle bundle = new Bundle();
        bundle.putString(context.getString(R.string.asyncbundleintent),context.getString(R.string.asyncsendintent));

        message.append(userName);
        message.append("\n");
        message.append(phoneNumber);
        message.append("\n");
        message.append(context.getString(R.string.rejectgamerequest));
        message.append("\n");
        message.append(gameName);

        bundle.putString(context.getString(R.string.asyncmessage),message.toString());
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE,true);
        context.getContentResolver().requestSync(account,MainTicTacToeActivity.AUTHORITY,bundle);
    }

    public AcceptOrRejectRequestService() {
        super("AcceptOrRejectRequestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final String gameName = intent.getStringExtra(EXTRA_GAME_NAME);
            final String userName = intent.getStringExtra(EXTRA_MSG_ID);
            final Account account = intent.getParcelableExtra(EXTRA_ACCOUNT);
            if (ACTION_ACCEPT_REQUEST.equals(action)) {
                startActionAcceptRequest(getApplicationContext(), gameName,userName,account);
            } else if (ACTION_REJECT_REQUEST.equals(action)) {
                startActionRejectRequest(getApplicationContext(), gameName, userName,account);
            }
        }
    }

}
