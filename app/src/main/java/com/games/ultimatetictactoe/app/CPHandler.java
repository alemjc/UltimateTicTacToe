package com.games.ultimatetictactoe.app;

import android.content.*;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by alemjc on 12/15/15.
 */
public class CPHandler {

    private CPHandler(){}

    public static Uri insert(Context c, ContentProviderClient provider, String tableCoordinates, int tableState, String row, String gameName){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBManager.GAME_NAME_COLUMN,gameName);
        contentValues.put(DBManager.TABLE_STATE_COLUMN,tableState);
        contentValues.put(DBManager.TABLE_COORDINATES_COLUMN,tableCoordinates);
        contentValues.put(DBManager.TABLE_ROW_COLUMN,row);

        Uri uri = Uri.parse(DBManager.CONTENTURI.toString()+"/"+DBManager.DATABASENAME+"/"+DBManager.TABLENAME);

        //return db.insert(TABLENAME,null,contentValues);

        Uri returnedUri = null;

        try {
            provider.insert(uri, contentValues);
        }
        catch(RemoteException e){
            Toast.makeText(c,"Error in application. Could no complete request",Toast.LENGTH_LONG).show();
        }


        //c.getContentResolver().notifyChange(returnedUri,observer);
        return returnedUri;

    }


    public static Uri insertGameName(Context c, ContentProviderClient provider, String gameName,String userName, int state, int currentTurn){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBManager.GAMETABLE_GAME_COLUMN,gameName);
        contentValues.put(DBManager.GAMETABLE_STATE,state);
        contentValues.put(DBManager.GAMETABLE_CURRENTTURN_COLUMN,currentTurn);
        contentValues.put(DBManager.GAMETABLE_OPPONENTSUSERNAME_COLUMN,userName);
        Uri uri = Uri.parse(DBManager.CONTENTURI.toString()+"/"+DBManager.DATABASENAME+"/"+DBManager.GAMETABLENAME);
        Uri returnUri = null;
        try {
            returnUri = provider.insert(uri, contentValues);
        }
        catch(RemoteException e){
            Toast.makeText(c,"Error in application. Could no complete request",Toast.LENGTH_LONG).show();
        }
        return returnUri;
    }

    public static int getCurrentPlayer(Context c, ContentProviderClient provider, String gameName, String userName){
        int returnInt;

        Uri uri = Uri.parse(DBManager.CONTENTURI.toString()+"/"+DBManager.DATABASENAME+"/"+DBManager.GAMETABLENAME);
        Cursor cursor = null;
        try {
          cursor = provider.query(uri, new String[]{DBManager.GAMETABLE_CURRENTTURN_COLUMN}, DBManager.GAMETABLE_GAME_COLUMN + "= ?"
                            + " AND " + DBManager.GAMETABLE_OPPONENTSUSERNAME_COLUMN + " = ?", new String[]{gameName, userName},
                    null, null);
        }
        catch(RemoteException e){
            Toast.makeText(c,"Error in application. Could not complete request",Toast.LENGTH_LONG).show();
        }



        if(cursor == null){
            return -1;
        }
        cursor.moveToFirst();
        returnInt = cursor.getInt(0);
        cursor.close();

        return returnInt;
    }

    public static String[][] getGameNamesWithOpponentsWithState(Context c, ContentProviderClient provider, int state){

        String returnGames [][];
        Log.d("","puffff!!!!!!!!!!!!!!!!!!!!!!");

        Uri uri = Uri.parse(DBManager.CONTENTURI.toString()+"/"+DBManager.DATABASENAME+"/"+DBManager.GAMETABLENAME);
        Cursor cursor = null;

        try {
            cursor = provider.query(uri, new String[]{DBManager.GAMETABLE_GAME_COLUMN,DBManager.GAMETABLE_OPPONENTSUSERNAME_COLUMN}
                    , DBManager.GAMETABLE_STATE + " =? ", new String[]{state + ""}, null);
        }
        catch(RemoteException e){
            Toast.makeText(c,"Error in application. Could no complete request",Toast.LENGTH_LONG).show();
        }


        if(cursor == null){
            Log.d("","FU!!!!!!!!!!");
            return null;
        }
        cursor.moveToFirst();
        returnGames = new String[cursor.getCount()][2];
        int index = 0;
        while(!cursor.isAfterLast()){
            returnGames[index][0] = cursor.getString(cursor.getColumnIndex(DBManager.GAMETABLE_OPPONENTSUSERNAME_COLUMN));
            returnGames[index][1] = cursor.getString(cursor.getColumnIndex(DBManager.GAMETABLE_GAME_COLUMN));
            index++;
            cursor.moveToNext();
        }


        cursor.close();

        return returnGames;
    }

    public static int removeGame(Context c,ContentProviderClient provider, String gameName){
        Uri uri = Uri.parse(DBManager.CONTENTURI+"/"+DBManager.DATABASENAME+"/"+DBManager.GAMETABLENAME);
        int affectedRows = 0;
        try {
            provider.delete(uri, DBManager.GAMETABLE_GAME_COLUMN + " = ?", new String[]{gameName});
        }
        catch(RemoteException e){
            Toast.makeText(c,"Error in application. Could no complete request",Toast.LENGTH_LONG).show();
        }

        return affectedRows;
    }

    public static int updateCurrentPLayer(Context c, ContentProviderClient provider,ContentObserver contentObserver,
                                          boolean notify ,String gameName, String userName, int currentPlayer){

        Uri uri = Uri.parse(DBManager.CONTENTURI.toString()+"/"+DBManager.DATABASENAME+"/"+DBManager.GAMETABLENAME);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBManager.GAMETABLE_CURRENTTURN_COLUMN,currentPlayer);


        int affectedRows = 0;

        try {
            affectedRows = provider.update(uri, contentValues, DBManager.GAMETABLE_GAME_COLUMN + " = ?"
                            + " AND " + DBManager.GAMETABLE_OPPONENTSUSERNAME_COLUMN + " = ?",
                    new String[]{gameName, userName});

            Log.d("updateCurrentPlayer","updating users. Number of users affected is: "+affectedRows);
            Log.d("updateCurrentPlayer","current player will be: "+currentPlayer);
            Log.d("updateCurrentPlayer","userName is: "+userName);
            Log.d("updateCurrentPlayer","gameName: "+gameName);

            if(affectedRows == 1 && notify){
                c.getContentResolver().notifyChange(Uri.parse(DBManager.CONTENTURI+"/"+DBManager.DATABASENAME+"/"+
                        DBManager.GAMETABLE_CURRENTTURN_COLUMN),contentObserver);
            }
        }
        catch(RemoteException e){
            Toast.makeText(c,"Error in application. Could no complete request",Toast.LENGTH_LONG).show();
        }

        return affectedRows;
    }

    public static int updateGameState(Context c, ContentProviderClient provider, String gameName, int state){
        Uri uri = Uri.parse(DBManager.CONTENTURI+"/"+DBManager.DATABASENAME+"/"+DBManager.GAMETABLENAME);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBManager.GAMETABLE_STATE,state);
        int affectedRows = 0;

        try {
            affectedRows = provider.update(uri, contentValues, DBManager.GAMETABLE_GAME_COLUMN + " = ?", new String[]{gameName});
        }
        catch(RemoteException e){
            Toast.makeText(c,"Error in application. Could no complete request",Toast.LENGTH_LONG).show();
        }

        return affectedRows;
    }

    public static String[] getGameNamesWithState(Context c, ContentProviderClient provider, int state){
        Uri uri = Uri.parse(DBManager.CONTENTURI+"/"+DBManager.DATABASENAME+"/"+DBManager.GAMETABLENAME);

        Cursor cursor = null;

        try {
            provider.query(uri, new String[]{DBManager.GAMETABLE_GAME_COLUMN}, DBManager.GAMETABLE_STATE + " =?"
                    , new String[]{"" + state}, null);
        }
        catch(RemoteException e){
            Toast.makeText(c,"Error in application. Could no complete request",Toast.LENGTH_LONG).show();
        }

        if(cursor == null){
            return null;
        }
        cursor.moveToFirst();
        String[] gameNames = new String[cursor.getCount()];
        int count = 0;
        while(!cursor.isAfterLast()){
            gameNames[count] = cursor.getString(0);
            count++;
            cursor.moveToNext();
        }

        cursor.close();

        return gameNames;

    }


    public static int updateTable(Context c,ContentProviderClient provider,ContentObserver observer,String tableCoordinates,int tableState,String row, String gameName){

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBManager.TABLE_STATE_COLUMN,tableState);
        contentValues.put(DBManager.TABLE_ROW_COLUMN,row);

        Uri uri = Uri.parse(DBManager.CONTENTURI.toString()+"/"+DBManager.DATABASENAME+"/"+DBManager.TABLENAME);
        String selection = DBManager.TABLE_COORDINATES_COLUMN+" = ?"+" AND "+DBManager.GAME_NAME_COLUMN+" = ?";
        ContentResolver contentResolver = c.getContentResolver();
        int numUpdated = 0;

        try {
            numUpdated = provider.update(uri, contentValues, selection, new String[]{tableCoordinates, gameName});
            if (numUpdated > 0) {
                contentResolver.notifyChange(uri, observer);
            }

        }

        catch(RemoteException e){
            Toast.makeText(c,"Error in application. Could no complete request",Toast.LENGTH_LONG).show();
        }
        return numUpdated;

    }

    public static String[] getSavedGames(Context c, ContentProviderClient provider){


        //Cursor c = db.rawQuery("SELECT "+GAME_NAME_COLUMN+" FROM "+TABLENAME+";",null);
        Uri contentUri = Uri.parse(DBManager.CONTENTURI.toString()+"/"+DBManager.DATABASENAME+"/"+DBManager.GAMETABLENAME);
        Cursor cursor = null;

        try {
           cursor = provider.query(contentUri, new String[]{DBManager.GAMETABLE_GAME_COLUMN}, null, null, null);
        }
        catch(RemoteException e){
            Toast.makeText(c,"Error in application. Could no complete request",Toast.LENGTH_LONG).show();
        }

        if(cursor == null){
            return null;
        }
        cursor.moveToFirst();
        String [] gameNames = new String[cursor.getCount()];

        int count = 0;
        while(!cursor.isAfterLast()){
            gameNames[count] = cursor.getString(0);
            count++;
            cursor.moveToNext();
        }

        cursor.close();

        return gameNames;
    }

    public static String[][] getSavedGamesWithOpponents(Context c, ContentProviderClient provider){


        Uri contentUri = Uri.parse(DBManager.CONTENTURI.toString()+"/"+DBManager.DATABASENAME+"/"+DBManager.GAMETABLENAME);
        Cursor cursor = null;

        try {
           cursor = provider.query(contentUri, new String[]{DBManager.GAMETABLE_OPPONENTSUSERNAME_COLUMN, DBManager.GAMETABLE_GAME_COLUMN},
                    null, null, null);
        }
        catch(RemoteException e){

        }

        if(cursor == null){
            return null;
        }

        cursor.moveToFirst();
        String returnValues[][] = new String[cursor.getCount()][2];
        int index = 0;
        while(!cursor.isAfterLast()){
            returnValues[index][0] = cursor.getString(0);
            returnValues[index][1] = cursor.getString(1);
            index++;
            cursor.moveToNext();
        }
        cursor.close();

        return returnValues;
    }

    public static String getOpponentsUserName(Context c, ContentProviderClient provider, String gameName){

        Uri uri = Uri.parse(DBManager.CONTENTURI.toString()+"/"+DBManager.DATABASENAME+"/"+DBManager.GAMETABLENAME);

        Cursor cursor = null;

        try {
            cursor = provider.query(uri, new String[]{DBManager.GAMETABLE_OPPONENTSUSERNAME_COLUMN}
                    , DBManager.GAMETABLE_GAME_COLUMN + " =?", new String[]{gameName}, null);
        }
        catch(RemoteException e){
            Toast.makeText(c,"Error in application. Could no complete request",Toast.LENGTH_LONG).show();
        }

        if(cursor == null){
            return null;
        }

        cursor.moveToFirst();
        String mailID = cursor.getString(0);

        cursor.close();


        return mailID;
    }



    public static int getTableState(Context c, ContentProviderClient provider,String tableCoordinates, String gameName){
       /* Cursor c = db.rawQuery("SELECT "+TABLE_STATE_COLUMN+ " FROM "+TABLENAME+" WHERE "+TABLE_COORDINATES_COLUMN+" = ?"
                        +" AND "+GAME_NAME_COLUMN+"= ?",
                new String[]{tableCoordinates,gameName});*/
        Uri uri = Uri.parse(DBManager.CONTENTURI.toString()+"/"+DBManager.DATABASENAME+"/"+DBManager.TABLENAME);

        Cursor cursor = null;

        try {
            provider.query(uri, new String[]{DBManager.TABLE_STATE_COLUMN}, DBManager.TABLE_COORDINATES_COLUMN + " =?" +
                    " AND " + DBManager.GAME_NAME_COLUMN + "= ?", new String[]{tableCoordinates, gameName}, null);
        }
        catch(RemoteException e){
            Toast.makeText(c,"Error in application. Could no complete request",Toast.LENGTH_LONG).show();
        }

        if(cursor == null){
            return -1;
        }
        cursor.moveToFirst();
        int state = cursor.getInt(0);
        cursor.close();
        return state;
    }

    public static String getUnParsedRow(Context c, ContentProviderClient provider,String tableCoordinates, String gameName){
        //Cursor c = db.rawQuery("SELECT "+TABLE_ROW_COLUMN+  " FROM "+TABLENAME+" WHERE "+TABLE_COORDINATES_COLUMN+" = ?"
        //               +" AND "+GAME_NAME_COLUMN+"= ?",
        //      new String[]{tableCoordinates,gameName});
        Uri uri = Uri.parse(DBManager.CONTENTURI.toString()+"/"+DBManager.DATABASENAME+"/"+DBManager.TABLENAME);
        Cursor cursor = null;

        try {
           cursor = provider.query(uri, new String[]{DBManager.TABLE_ROW_COLUMN}, DBManager.TABLE_COORDINATES_COLUMN + " =?" +
                    " AND " + DBManager.GAME_NAME_COLUMN + "= ?", new String[]{tableCoordinates, gameName}, null);
        }
        catch(RemoteException e){
            Toast.makeText(c,"Error in application. Could no complete request",Toast.LENGTH_LONG).show();
        }

        if(cursor != null) {
            String row = null;
            cursor.moveToFirst();
            if(cursor.getCount()>0) {
                row = cursor.getString(0);
            }
            cursor.close();

            return row;
        }
        else {
            return null;
        }
    }
}