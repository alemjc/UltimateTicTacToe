package com.games.ultimatetictactoe.app;

import android.accounts.Account;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by alemjc on 11/17/15.
 */
public class RegistrationService extends IntentService {
    private Context context;
    public static final String INTENT_EXTRA_ACCOUNT ="account";
    public static final String INTENT_EXTRA_REFRESH_TOKEN = "refreshToken";
    public RegistrationService(){
        this(null);
    }
    public RegistrationService(String name) {
        super(name);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context = getApplicationContext();
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName()+"_preferences",
                Context.MODE_PRIVATE|Context.MODE_MULTI_PROCESS);

        String fireBaseToken = preferences.getString(context.getString(R.string.firebasetokenkey),null);
        String defaultSenderID = context.getString(R.string.gcm_defaultSenderId);
        InstanceID instanceID = InstanceID.getInstance(this);
        SharedPreferences.Editor editor = preferences.edit();
        String myTokenKey = context.getString(R.string.mygcmtoken);
        String token = preferences.getString(myTokenKey,null);
        boolean refreshToken = intent.getBooleanExtra(INTENT_EXTRA_REFRESH_TOKEN,false);

        Log.d("onHandleIntent","firebasetoken = "+fireBaseToken);

        if(fireBaseToken == null || token == null){
            try {
                token = instanceID.getToken(defaultSenderID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Bundle bundle = new Bundle();
                StringBuilder stringBuilder = new StringBuilder();
                Account account = intent.getParcelableExtra(INTENT_EXTRA_ACCOUNT);

                editor.putString(myTokenKey,token);
                editor.commit();
                bundle.putString(context.getString(R.string.asyncbundleintent),context.getString(R.string.asyncsendintent));

                stringBuilder.append(context.getString(R.string.defaultsenderid));
                stringBuilder.append("\n");
                stringBuilder.append(token);
                stringBuilder.append("\n");
                stringBuilder.append(context.getString(R.string.tokenrequest));
                stringBuilder.append("\n");
                stringBuilder.append(" ");

                bundle.putString(context.getString(R.string.asyncmessage),stringBuilder.toString());
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE,true);
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,true);

                ContentResolver.requestSync(account,MainTicTacToeActivity.AUTHORITY,bundle);

            }
            catch(IOException e){
                Log.d("RegistrationService","could not get sender token");
            }

        }
        else if(refreshToken && token != null && fireBaseToken != null){
            try{
                token = instanceID.getToken(defaultSenderID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                editor.putString(myTokenKey,token);
                editor.commit();

                sendRegistrationTokenToServer(token, fireBaseToken);

            }
            catch(IOException e){
                Log.d("RegistrationService","could not get sender token");
            }

        }

        else {
            sendRegistrationTokenToServer(token, fireBaseToken);
        }

    }



    private void sendRegistrationTokenToServer(final String token, String fireBaseToken) {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String phoneNumber = telephonyManager.getLine1Number();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        while(networkInfo == null || !networkInfo.isConnected()){
            try {
                Thread.sleep(30000);
            }
            catch(InterruptedException e){
                Log.d("sendRegistrationtoken","interrupted exception");
            }
        }

        final String time = TimeRequester.getTime(System.currentTimeMillis()+"");

        if (phoneNumber != null) {


            final Firebase fireBaseRef = new Firebase(context.getString(R.string.firebaseurl));
            fireBaseRef.authWithCustomToken(fireBaseToken, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    Log.d("sendRegistration","authenticated with database");
                    Firebase usersRef = fireBaseRef.child("users");

                    String number = phoneNumber;

                    number = number.replace("(","");
                    number = number.replace(")","");
                    number = number.replace("-","");
                    number = number.replace(" ","");
                    number = number.replace("+","");


                    if(phoneNumber.length() > 10){
                        number = phoneNumber.substring(phoneNumber.length()-10, phoneNumber.length());
                    }

                    User user = new User(token,time);
                    usersRef.child(number).setValue(user);
                    SharedPreferences preferences = context.getSharedPreferences(context.getPackageName()+"_preferences",
                            Context.MODE_PRIVATE|Context.MODE_MULTI_PROCESS);
                    SharedPreferences.Editor editor = preferences.edit();
                    String tokenSentKey = context.getString(R.string.tokenacquiredandSent);
                    editor.putBoolean(tokenSentKey,true);
                    editor.apply();
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    //TODO: might need to let user know that an error occured.
                    Log.d("sendRegistration","could not authenticate with database");
                }
            });

        }
    }

}
