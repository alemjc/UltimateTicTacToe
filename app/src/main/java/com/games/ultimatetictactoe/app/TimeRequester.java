package com.games.ultimatetictactoe.app;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by alemjc on 2/6/16.
 */
public class TimeRequester {
    private static final String SERVERURL = "http://polar-springs-79566.herokuapp.com/time";

    private TimeRequester(){

    }

    public static String getTime(String time){
        InputStream is = null;
        BufferedReader bufferedReader = null;

        try{
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(new HttpGet(SERVERURL));
            is = httpResponse.getEntity().getContent();
            bufferedReader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            return jsonObject.getString("time");


        }
        catch(JSONException e){
            Log.d("getTime","json object creation failed");
        }
        catch(IOException e){
            Log.d("getTime", "couldn't get time from server");
        }
        finally {
            try {
                if(bufferedReader != null) {
                    bufferedReader.close();
                }

                if(is != null){
                    is.close();
                }
            }
            catch(IOException e){
                Log.d("async getGameName","io exception closing buffer");
            }
        }

        return time;
    }
}
