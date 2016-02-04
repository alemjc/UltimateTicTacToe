package com.games.ultimatetictactoe.app;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.security.token.TokenGenerator;
import com.games.ultimatetictactoe.app.content.FirebaseCredentials;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alemjc on 11/17/15.
 */
public class RegistrationService extends IntentService {
    private Context context;
    public RegistrationService(){
        this(null);
    }
    public RegistrationService(String name) {
        super(name);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context = getApplicationContext();
        SharedPreferences preferences = RegistrationService.this.getSharedPreferences(MainTicTacToeActivity.
                class.getName(), Context.MODE_PRIVATE);
        String defaultSenderID = context.getString(R.string.gcm_defaultSenderId);
        InstanceID instanceID = InstanceID.getInstance(this);
        SharedPreferences.Editor editor = preferences.edit();
        String tokenSentKey = context.getString(R.string.tokenacquiredandSent);

        try{
            String token = instanceID.getToken(defaultSenderID, GoogleCloudMessaging.INSTANCE_ID_SCOPE,null);
            sendRegistrationTokenToServer(token);
        }
        catch(java.io.IOException e){
            editor.putBoolean(tokenSentKey,false);
            editor.commit();
        }




    }


    private boolean sendRegistrationTokenToServer(final String token){

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String phoneNumber = telephonyManager.getLine1Number();

        if(phoneNumber != null) {
            Map<String,Object> payload = new HashMap<>();
            TokenGenerator tokenGenerator = new TokenGenerator(FirebaseCredentials.SECRET);
            final Firebase fireBaseRef = new Firebase(FirebaseCredentials.URL);
            payload.put("uid",FirebaseCredentials.UID);
            String authToken = tokenGenerator.createToken(payload);
            fireBaseRef.authWithCustomToken(authToken, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {

                    SharedPreferences preferences = RegistrationService.this.getSharedPreferences(MainTicTacToeActivity.
                            class.getName(), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    String tokenSentKey = context.getString(R.string.tokenacquiredandSent);
                    String myTokenKey = context.getString(R.string.mygcmtoken);

                    Firebase usersRef = fireBaseRef.child("users");
                    usersRef.child(phoneNumber).setValue(token);

                    editor.putBoolean(tokenSentKey,true);
                    editor.putString(myTokenKey,token);
                    editor.commit();

                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    //TODO: Could not authenticate.
                    Log.d("authentication error","firebase authentication error");
                }
            });

            return true;
        }
        else {

            return false;
        }
    }
}
