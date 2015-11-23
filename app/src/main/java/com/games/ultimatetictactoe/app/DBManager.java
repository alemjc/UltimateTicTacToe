package com.games.ultimatetictactoe.app;

import android.content.*;
import android.database.ContentObserver;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by alemjc on 11/8/15.
 * This is the DB table manager that will be used to save game data.
 */
public class DBManager extends ContentProvider {

    public static final String DATABASENAME = "ultimatetictactoe";
    public static final String TABLENAME="tablerepresentation";
    public static final String GAMETABLENAME="gametable";
    public static final String GAMETABLE_OPPONENTS_COLUMN = "opponent";
    public static final String GAMETABLE_GAME_COLUMN = "gameName";
    private static final int VERSION = 1;
    private static final String id="_id";
    public static final String TABLE_COORDINATES_COLUMN = "tablecoordinate";
    public static final String TABLE_STATE_COLUMN = "statecolumn";
    public static final String TABLE_ROW_COLUMN = "tablerow";
    public static final String GAME_NAME_COLUMN = "gamename";
    public static final Uri CONTENTURI = Uri.parse("content://com.games.ultmatetictactoe.app.DBManager/"+DATABASENAME);
    private static final int TICTACTOETABLE = 1;
    private static final int TICTACTOEGAMENAME = 2;

    private SQLiteDatabase db;
    private SQLHelper sqlHelper;
    private static DBManager dbManager;

    private static final String AUTHORITY = "com.games.ultimatetictactoe.app.DBManager";
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        uriMatcher.addURI(AUTHORITY,DATABASENAME+"/"+TABLENAME,TICTACTOETABLE);
        uriMatcher.addURI(AUTHORITY,DATABASENAME+"/"+GAMETABLENAME,TICTACTOEGAMENAME);
        //uriMatcher.addURI(AUTHORITY,DATABASENAME+TABLENAME+"/"+TABLE_ROW_COLUMN,TICTACTOETABLEROW);
    }
    public DBManager(){
        super();
        sqlHelper = new SQLHelper(getContext());
        db = sqlHelper.getWritableDatabase();
    }



    /*private DBManager(Context c){
        super();
        sqlHelper = new SQLHelper(c);
        db = sqlHelper.getWritableDatabase();

    }*/

    /*public static DBManager getInstance(Context c){
        if(dbManager != null){
            return dbManager;
        }
        else{
            dbManager = new DBManager(c);
            return dbManager;
        }
    }*/

    /*private String convertToRow(TableIndex innerTable[][]){
        String row = "";

        for(int i = 0; i < innerTable.length; i++){
            for(int j = 0; j < innerTable[i].length; j++){
                row+=innerTable[i][j]+",";
            }
        }

        row = row.substring(0,row.length()-1);
        return row;
    }*/









    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        Uri returnedUri = null;
        switch(uriMatcher.match(uri)){
            case TICTACTOETABLE:
                db.insert(TABLENAME,null,contentValues);
                returnedUri = Uri.parse(CONTENTURI+"/"+TABLENAME);

                break;
            case TICTACTOEGAMENAME:
                db.insert(GAMETABLENAME,null,contentValues);
                returnedUri = Uri.parse(CONTENTURI+"/"+TABLENAME);

            default:


        }

        return returnedUri;


    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch(uriMatcher.match(uri)){
            case TICTACTOETABLE:
                return db.update(TABLENAME,values,selection,selectionArgs);

            default:
                return -1;
        }
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnC = null;
        switch(uriMatcher.match(uri)){
            case TICTACTOETABLE:
               returnC = db.query(TABLENAME,projection,selection,selectionArgs,null,null,null,null);
            break;
            case TICTACTOEGAMENAME:
                returnC = db.query(GAMETABLENAME,projection,selection,selectionArgs,null,null,null,null);
                break;
            default:

        }


        return returnC;

    }


    private class SQLHelper extends SQLiteOpenHelper {



        public SQLHelper(Context c) {
            this(c, DATABASENAME, null, VERSION);
        }

        public SQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public SQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }

        @Override
        public String getDatabaseName() {
            return super.getDatabaseName();
        }

        @Override
        public void setWriteAheadLoggingEnabled(boolean enabled) {
            super.setWriteAheadLoggingEnabled(enabled);
        }

        @Override
        public SQLiteDatabase getWritableDatabase() {
            return super.getWritableDatabase();
        }

        @Override
        public SQLiteDatabase getReadableDatabase() {
            return super.getReadableDatabase();
        }

        @Override
        public synchronized void close() {
            super.close();
        }

        @Override
        public void onConfigure(SQLiteDatabase db) {
            super.onConfigure(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            super.onDowngrade(db, oldVersion, newVersion);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS "+ TABLENAME);
            db.execSQL("DOP TABLE IF EXISTS "+ GAMETABLENAME);
            onCreate(db);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLENAME+ "( "+id+ " INTEGER INCREMENTS PRIMARY KEY, "+
                                                                    GAME_NAME_COLUMN+ " VARCHAR(100) "+
                                                                    TABLE_COORDINATES_COLUMN+" VARCHAR(2), "+
                                                                    TABLE_STATE_COLUMN+" INTEGER, "+
                                                                    TABLE_ROW_COLUMN+ "VARCHAR(17)); ");

            db.execSQL("CREATE TABLE IF NOT EXISTS "+GAMETABLENAME+"( "+id+" INTEGER INCREMENTS PRIMARY KEY, "+
                                                                        GAMETABLE_GAME_COLUMN+ " VARCHAR(100), "+
                                                                        GAMETABLE_OPPONENTS_COLUMN+" VARCHAR(400), "+
                                                                        "FOREIGN KEY ("+GAME_NAME_COLUMN+")"+" REFERENCES "+
                                                                        TABLENAME+"("+GAME_NAME_COLUMN+")"+ " ON DELETE CASCADE);");

        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);

        }

    }


    public static class CPHandler {

        private CPHandler(){};

        public static Uri[] insert(Context c,ContentObserver observer, String tableCoordinates,int tableState, String row,String gameName,String opponentsMailID ){
            ContentValues contentValues = new ContentValues();
            contentValues.put(GAME_NAME_COLUMN,gameName);
            contentValues.put(TABLE_STATE_COLUMN,tableState);
            contentValues.put(TABLE_COORDINATES_COLUMN,tableCoordinates);
            contentValues.put(TABLE_ROW_COLUMN,row);

            Uri uri = Uri.parse(CONTENTURI.toString()+"/"+TABLENAME);

            //return db.insert(TABLENAME,null,contentValues);
            Uri returnedUris[] = new Uri[2];
            returnedUris[0] = c.getContentResolver().insert(uri,contentValues);

            contentValues.clear();
            contentValues.put(GAMETABLE_GAME_COLUMN,gameName);
            contentValues.put(GAMETABLE_OPPONENTS_COLUMN,opponentsMailID);
            uri = Uri.parse(CONTENTURI.toString()+"/"+GAMETABLENAME);
            returnedUris[1] = c.getContentResolver().insert(uri,contentValues);

            //c.getContentResolver().notifyChange(returnedUri,observer);
            return returnedUris;

        }

        public static int updateTable(Context c,ContentObserver observer,String tableCoordinates,int tableState,String row, String gameName){

            ContentValues contentValues = new ContentValues();
            contentValues.put(TABLE_STATE_COLUMN,tableState);
            contentValues.put(TABLE_ROW_COLUMN,row);

            Uri uri = Uri.parse(CONTENTURI.toString()+"/"+TABLENAME);
            String selection = TABLE_COORDINATES_COLUMN+" = ?"+" AND "+GAME_NAME_COLUMN+" = ?";
            ContentResolver contentResolver = c.getContentResolver();
            int numUpdated = contentResolver.update(uri, contentValues, selection, new String[]{tableCoordinates, gameName});
            if(numUpdated > 0){
                //contentResolver.notifyChange(uri,observer);
            }

            return numUpdated;

        }

        public static String[] getSavedGames(Context c){
            ContentResolver contentResolver = c.getContentResolver();

            //Cursor c = db.rawQuery("SELECT "+GAME_NAME_COLUMN+" FROM "+TABLENAME+";",null);
            Uri contentUri = Uri.parse(CONTENTURI.toString()+"/"+TABLENAME);
            Cursor cursor = contentResolver.query(contentUri,new String[]{GAME_NAME_COLUMN}, null,null,null);
            cursor.moveToFirst();
            String [] gameNames = new String[cursor.getCount()];

            int count = 0;
            while(!cursor.isAfterLast()){
                gameNames[count] = c.getString(0);
                count++;
                cursor.moveToNext();
            }

            cursor.close();

            return gameNames;
        }

        public static String getOpponentsMailID(Context c, String gameName){

            Uri uri = Uri.parse(DBManager.CONTENTURI.toString()+"/"+GAMETABLENAME);

            Cursor cursor = c.getContentResolver().query(uri,new String[]{DBManager.GAMETABLE_OPPONENTS_COLUMN}
                            ,DBManager.GAMETABLE_GAME_COLUMN+" =?",new String[]{gameName},null);

            cursor.moveToFirst();
            String mailID = cursor.getString(0);


            return mailID;
        }



        public static int getTableState(Context c,String tableCoordinates, String gameName){
       /* Cursor c = db.rawQuery("SELECT "+TABLE_STATE_COLUMN+ " FROM "+TABLENAME+" WHERE "+TABLE_COORDINATES_COLUMN+" = ?"
                        +" AND "+GAME_NAME_COLUMN+"= ?",
                new String[]{tableCoordinates,gameName});*/
            Uri uri = Uri.parse(DBManager.CONTENTURI.toString()+"/"+DBManager.TABLENAME);

            Cursor cursor = c.getContentResolver().query(uri,new String[]{DBManager.TABLE_STATE_COLUMN},DBManager.TABLE_COORDINATES_COLUMN+" =?"+
                                                " AND "+GAME_NAME_COLUMN+"= ?",new String[]{tableCoordinates,gameName}, null);
            cursor.moveToFirst();
            int state = cursor.getInt(0);
            cursor.close();
            return state;
        }

        public static String getUnParsedRow(Context c,String tableCoordinates, String gameName){
            //Cursor c = db.rawQuery("SELECT "+TABLE_ROW_COLUMN+  " FROM "+TABLENAME+" WHERE "+TABLE_COORDINATES_COLUMN+" = ?"
            //               +" AND "+GAME_NAME_COLUMN+"= ?",
            //      new String[]{tableCoordinates,gameName});
            Uri uri = Uri.parse(DBManager.CONTENTURI.toString()+"/"+DBManager.TABLENAME);
            Cursor cursor = c.getContentResolver().query(uri,new String[]{TABLE_ROW_COLUMN},DBManager.TABLE_COORDINATES_COLUMN+" =?"+
                    " AND "+GAME_NAME_COLUMN+"= ?",new String[]{tableCoordinates,gameName}, null);

            cursor.moveToFirst();
            String row = c.getString(0);
            cursor.close();

            return row;

        }
    }
}
