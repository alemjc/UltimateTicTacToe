package com.games.ultimatetictactoe.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.ConnectionResult;



public class MainTicTacToeActivity extends FragmentActivity implements GameIntroFragment.OnGameIntroInteractionListener, ItemFragment.OnGameListFragmentInteractionListener,
GameTable.OnFragmentInteractionListener{
    public static final String AUTHORITY = "com.games.ultimatetictactoe.app.DB";
    public static final String ACCOUNTTYPE = "authentication.com";
    public static final String ACCOUNT = "dummyAccount";
    private static final int GOOGLEAPPSERVICESREQUEST=0x001;
    private static final String TOPFRAGMENT = "top";
    Account account;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("on super create","klk!!!!!!!!!!!!!!!!!!!");
        setContentView(R.layout.activity_main_tic_tac_toe);
        Firebase.setAndroidContext(this);
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int errorCode =  googleApiAvailability.isGooglePlayServicesAvailable(this);
        googleApiAvailability.getErrorDialog(this,errorCode,GOOGLEAPPSERVICESREQUEST);
        Intent registrationServiceIntent = new Intent(this,RegistrationService.class);
        startService(registrationServiceIntent);
        account = new Account(ACCOUNT,ACCOUNTTYPE);
        createSyncAccount(this);
        FragmentManager fragmentManager = getFragmentManager();
        GameIntroFragment gameIntroFragment = new GameIntroFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.space, gameIntroFragment, TOPFRAGMENT);
        fragmentTransaction.commit();
        Log.d("onCreate","got here");




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

    private void createSyncAccount(Context c){
        AccountManager accountManager = (AccountManager)c.getSystemService(Context.ACCOUNT_SERVICE);

        if(accountManager.addAccountExplicitly(account,null,null)){


        }
        else{
            Log.d("createSyncAccount","account could not be created");

        }



    }

    @Override
    public void onBackPressed() {

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(TOPFRAGMENT);
        if(!(fragment instanceof GameIntroFragment)){
            fragmentManager.popBackStack();
        }
        else {
            super.onBackPressed();
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
    public void onGameListFragmentInteraction(String userName,String gameName) {
        GameTable gameTable = GameTable.newInstance(userName,gameName);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.popBackStack();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.space,gameTable,TOPFRAGMENT);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.popBackStack();
    }

    @Override
    public void onIntroInteraction(String usersChoice) {
        if(usersChoice.equals(NEWGAME)){
            Log.d("onIntroInteraction","hey new game");
            ItemFragment itemFragment = ItemFragment.newInstance(ItemFragment.NEWGAME);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.space,itemFragment,TOPFRAGMENT);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }
        else if(usersChoice.equals(CONTINUE)){
            ItemFragment itemFragment = ItemFragment.newInstance(ItemFragment.CONTINUE);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.space,itemFragment,TOPFRAGMENT);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();


        }
        else if(usersChoice.equals(INVITATIONS)){
            AcceptDeclineFragment acceptDeclineFragment = AcceptDeclineFragment.newInstance();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.space,acceptDeclineFragment,TOPFRAGMENT);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }



}
