package com.games.ultimatetictactoe.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.ConnectionResult;

import java.net.URI;


public class MainTicTacToeActivity extends FragmentActivity implements GameIntroFragment.OnGameIntroInteractionListener, ItemFragment.OnGameListFragmentInteractionListener {
    public static final String AUTHORITY = "com.games.ultimatetictactoe.app.DBManager";
    public static final String ACCOUNTTYPE = "authentication.com";
    public static final String ACCOUNT = "dummyAccount";
    private static final int GOOGLEAPPSERVICESREQUEST=0x001;
    private static final String LISTTOTABLE = "listtotable";
    private static final String INTROTOLIST = "introToList";
    private static final String INTROFRAGMENT = "intro";

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tic_tac_toe);
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int errorCode =  googleApiAvailability.isGooglePlayServicesAvailable(this);
        googleApiAvailability.getErrorDialog(this,errorCode,GOOGLEAPPSERVICESREQUEST);
        Intent registrationServiceIntent = new Intent(this,RegistrationService.class);
        startService(registrationServiceIntent);


        Account account = createSyncAccount(this);
        FragmentManager fragmentManager = getFragmentManager();
        GameIntroFragment gameIntroFragment = new GameIntroFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.space,gameIntroFragment,INTROFRAGMENT);
        fragmentTransaction.commit();




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GOOGLEAPPSERVICESREQUEST){
            //check google services again
            GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
            switch(googleApiAvailability.isGooglePlayServicesAvailable(this)){
                case ConnectionResult.SERVICE_INVALID:
                case ConnectionResult.SERVICE_MISSING:
                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                    finish();
                    break;
            }
        }
    }

    public static Account createSyncAccount(Context c){
        Account account = new Account(ACCOUNT,ACCOUNTTYPE);
        AccountManager accountManager = (AccountManager)c.getSystemService(Context.ACCOUNT_SERVICE);

        if(accountManager.addAccountExplicitly(account,null,null)){

            return account;
        }
        else{
            Log.d("createSyncAccount","account could not be created");
            return null;
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_tic_tac_toe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onGameListFragmentInteraction(String content) {
        String gameOpponent[] = content.split(" ");
        GameTable gameTable = GameTable.newInstance(gameOpponent[0],gameOpponent[1]);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(INTROTOLIST);
        if(fragment != null){
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(gameTable,LISTTOTABLE);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

    }

    @Override
    public void onIntroInteraction(String usersChoice) {
        if(usersChoice.equals(NEWGAME)){
            ItemFragment itemFragment = ItemFragment.newInstance(ItemFragment.NEWGAME);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(itemFragment,INTROTOLIST);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }
        else if(usersChoice.equals(CONTINUE)){
            ItemFragment itemFragment = ItemFragment.newInstance(ItemFragment.CONTINUE);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(itemFragment,INTROTOLIST);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();


        }
        else if(usersChoice.equals(INVITATIONS)){
            AcceptDeclineFragment acceptDeclineFragment = AcceptDeclineFragment.newInstance();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(acceptDeclineFragment,null);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }



}
