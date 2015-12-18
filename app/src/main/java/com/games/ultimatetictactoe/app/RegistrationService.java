package com.games.ultimatetictactoe.app;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

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
        context = getApplicationContext();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences preferences = this.getSharedPreferences(MainTicTacToeActivity.class.getName(), Context.MODE_PRIVATE);
        String defaultSenderID = context.getString(R.string.gcm_defaultSenderId);
        InstanceID instanceID = InstanceID.getInstance(this);
        SharedPreferences.Editor editor = preferences.edit();
        String tokenSentKey = context.getString(R.string.tokenAcquireAndSent);
        String myTokenKey = context.getString(R.string.mygcmtoken);
        try{
            String token = instanceID.getToken(defaultSenderID, GoogleCloudMessaging.INSTANCE_ID_SCOPE,null);
            if(sendRegistrationTokenToServer(token)){
                editor.putBoolean(tokenSentKey,true);
                editor.putString(myTokenKey,token);
                editor.commit();

            }

            else{
                Toast.makeText(context,"Unable to register to server",Toast.LENGTH_LONG);
            }



        }
        catch(java.io.IOException e){
            editor.putBoolean(tokenSentKey,false);
            editor.commit();
        }




    }


    private boolean sendRegistrationTokenToServer(String token){
        //TODO: send registration token to our database server.

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = telephonyManager.getLine1Number();

        if(phoneNumber != null) {
            Firebase firebaseRef = new Firebase(context.getString(R.string.fireBaseDB));
            Firebase usersRef = firebaseRef.child("users");
            usersRef.child(phoneNumber).setValue(token);
            return true;
        }
        else {

            return false;
        }
    }
}
