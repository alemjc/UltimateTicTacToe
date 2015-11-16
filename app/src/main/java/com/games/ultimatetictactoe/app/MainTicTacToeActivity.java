package com.games.ultimatetictactoe.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainTicTacToeActivity extends FragmentActivity {
    public static final String AUTHORITY = "com.games.ultimatetictactoe.app.DBManager";
    public static final String ACCOUNTTYPE = "authentication.com";
    public static final String ACCOUNT = "dummyAccount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tic_tac_toe);
        Account account = createSyncAccount(this);

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
}
