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
 *
 * Registration service that takes care of registering the device with the gcm server and the app server in order to
 * receive and send notifications.
 */
public class RegistrationService extends IntentService {
    private Context context;
    public static final String INTENT_EXTRA_ACCOUNT ="account";
    public static final String INTENT_EXTRA_REFRESH_TOKEN = "refreshToken";
    public static final String INTENT_EXTRA_REQUEST_FIREBASE_TOKEN = "requestFirebasetoken";
    private SharedPreferences preferences;
    private String registrationTime; // Although this is a private field for the class. it will only be used in lines
                                    // 108-178.
    public RegistrationService(){
        this(null);
    }
    public RegistrationService(String name) {
        super(name);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context = getApplicationContext();
        preferences = context.getSharedPreferences(context.getPackageName()+"_preferences",
                Context.MODE_PRIVATE|Context.MODE_MULTI_PROCESS);
        String fireBaseToken = preferences.getString(context.getString(R.string.firebasetokenkey),null);
        String defaultSenderID = context.getString(R.string.gcm_defaultSenderId);
        InstanceID instanceID = InstanceID.getInstance(this);
        SharedPreferences.Editor editor = preferences.edit();
        String myTokenKey = context.getString(R.string.mygcmtoken);
        String token = preferences.getString(myTokenKey,null);
        boolean refreshToken = intent.getBooleanExtra(INTENT_EXTRA_REFRESH_TOKEN,false);
        boolean requestFireBaseToken = intent.getBooleanExtra(INTENT_EXTRA_REQUEST_FIREBASE_TOKEN,false);
        Account account = intent.getParcelableExtra(INTENT_EXTRA_ACCOUNT);

        Log.d("onHandleIntent","firebasetoken = "+fireBaseToken);

        if((fireBaseToken == null || requestFireBaseToken ) || token == null){
            try {
                token = instanceID.getToken(defaultSenderID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                requestFirebaseToken(myTokenKey,token,account);

            }
            catch(IOException e){
                Log.d("RegistrationService","could not get sender token");
            }

        }
        else if(refreshToken){
            try{
                token = instanceID.getToken(defaultSenderID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                editor.putString(myTokenKey,token);
                editor.commit();

                sendRegistrationTokenToServer(token, fireBaseToken, account);

            }
            catch(IOException e){
                Log.d("RegistrationService","could not get sender token");
            }

        }

        else {
            sendRegistrationTokenToServer(token, fireBaseToken, account);
        }

    }

    private void requestFirebaseToken(String tokenKey,String token, Account account){
        Bundle bundle = new Bundle();
        StringBuilder stringBuilder = new StringBuilder();
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(tokenKey,token);
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



    private void sendRegistrationTokenToServer(final String token, String fireBaseToken, final Account account) {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String phoneNumber = telephonyManager.getLine1Number();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        registrationTime = preferences.getString(context.getString(R.string.registrationtime),null);

        if(registrationTime == null) {
            Log.d("sendRegistration", "checking network");
            while (networkInfo == null || !networkInfo.isConnected()) {
                try {
                    Thread.sleep(5000);
                    networkInfo = connectivityManager.getActiveNetworkInfo();
                } catch (InterruptedException e) {
                    Log.d("sendRegistrationtoken", "interrupted exception");
                }
            }
            Log.d("sendRegistration", "getting timestamp");
            registrationTime = TimeRequester.getTime(System.currentTimeMillis() + "");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(context.getString(R.string.registrationtime), registrationTime);
            editor.commit();
        }

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

                    // We can assume that registrationTime is not equals to null, because of the above lines 113-129.
                    User user = new User(token,registrationTime);
                    usersRef.child(number).setValue(user);

                    SharedPreferences.Editor editor = preferences.edit();
                    String tokenSentKey = context.getString(R.string.tokenacquiredandSent);
                    editor.putBoolean(tokenSentKey,true);
                    editor.apply();
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    //TODO: might need to let user know that an error occured.
                    Log.d("sendRegistration","could not authenticate with database");
                    String myTokenKey = context.getString(R.string.mygcmtoken);
                    requestFirebaseToken(myTokenKey,token,account);
                }
            });

        }
    }

}
