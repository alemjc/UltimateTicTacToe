package com.games.ultimatetictactoe.app;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

/**
 * Created by alemjc on 11/17/15.
 */
public class RegistrationService extends IntentService {

    public RegistrationService(){
        this(null);
    }
    public RegistrationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences preferences = this.getSharedPreferences(MainTicTacToeActivity.class.getName(), Context.MODE_PRIVATE);
        Context context = getApplicationContext();
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

        }
        catch(java.io.IOException e){
            editor.putBoolean(tokenSentKey,false);
            editor.commit();
        }



    }


    private boolean sendRegistrationTokenToServer(String token){
        //TODO: send registration token to our database server.
        return false;
    }
}
