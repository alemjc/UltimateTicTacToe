package com.games.ultimatetictactoe.app;

import android.content.*;
import android.database.ContentObserver;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Created by alemjc on 11/8/15.
 * This is the DB table manager that will be used to save game data.
 */
public class DBManager extends ContentProvider {

    public static final String DATABASENAME = "ultimatetictactoe";

    // GAME Name information
    public static final String GAMETABLENAME="gametable";
    public static final String GAMETABLE_OPPONENTSID_COLUMN = "opponent";
    public static final String GAMETABLE_OPPONENTSUSERNAME_COLUMN = "opponentUsername";
    public static final String GAMETABLE_STATE = "gameState";
    public static final String GAMETABLE_GAME_COLUMN = "gameName";
    public static final String GAMETABLE_CURRENTTURN_COLUMN = "currentTurn";
    private static final int VERSION = 1;
    private static final String id="_id";

    //Game table columns
    public static final String TABLENAME="tablerepresentation";
    public static final String TABLE_COORDINATES_COLUMN = "tablecoordinate";
    public static final String TABLE_STATE_COLUMN = "statecolumn";
    public static final String TABLE_ROW_COLUMN = "tablerow";
    public static final String GAME_NAME_COLUMN = "gamename";
    private static final String AUTHORITY = "com.games.ultimatetictactoe.app.DB";
    public static final Uri CONTENTURI = Uri.parse("content://"+AUTHORITY);
    public static final int TICTACTOETABLE = 1;
    public static final int TICTACTOEGAMENAME = 2;



    private SQLiteDatabase db;
    private SQLHelper sqlHelper;


    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        uriMatcher.addURI(AUTHORITY,DATABASENAME+"/"+TABLENAME,TICTACTOETABLE);
        uriMatcher.addURI(AUTHORITY,DATABASENAME+"/"+GAMETABLENAME,TICTACTOEGAMENAME);
    }
    public DBManager(){
        super();

    }


    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        db = sqlHelper.getWritableDatabase();
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
        db = sqlHelper.getWritableDatabase();
        int affectedRows = 0;
        switch(uriMatcher.match(uri)){
            case TICTACTOETABLE:
                //TODO: Not implemented yet.
                break;
            case TICTACTOEGAMENAME:
                affectedRows = db.delete(GAMETABLENAME,selection,selectionArgs);
                break;
        }


        return affectedRows;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        db = sqlHelper.getWritableDatabase();
        int affectedRows = 0;
        switch(uriMatcher.match(uri)){
            case TICTACTOETABLE:
                affectedRows = db.update(TABLENAME,values,selection,selectionArgs);
                break;
            case TICTACTOEGAMENAME:
                affectedRows = db.update(GAMETABLENAME,values,selection,selectionArgs);
        }


        return affectedRows;
    }

    @Override
    public boolean onCreate() {
        Log.d("dbManager","entered");
        if(getContext() == null){
            Log.d("dbManager","context is null");
        }
        sqlHelper = new SQLHelper(getContext());
        if(sqlHelper == null){
            Log.d("dbManager","sqlHelper is null");
        }
        return true;
    }






    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnC = null;
        db = sqlHelper.getWritableDatabase();
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
            this(context, name, factory, version,null);
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
                                                                    GAME_NAME_COLUMN+ " VARCHAR(100), "+
                                                                    TABLE_COORDINATES_COLUMN+" VARCHAR(2), "+
                                                                    TABLE_STATE_COLUMN+" INTEGER, "+
                                                                    TABLE_ROW_COLUMN+ " VARCHAR(17)); ");

            db.execSQL("CREATE TABLE IF NOT EXISTS "+GAMETABLENAME+"( "+id+" INTEGER INCREMENTS PRIMARY KEY, "+
                                                                        GAMETABLE_GAME_COLUMN+ " VARCHAR(100), "+
                                                                        GAMETABLE_OPPONENTSID_COLUMN +" VARCHAR(400), "+
                                                                        GAMETABLE_OPPONENTSUSERNAME_COLUMN+" VARCHAR(400), "+
                                                                        GAMETABLE_STATE+" INTEGER, "+
                                                                        GAMETABLE_CURRENTTURN_COLUMN+" INTEGER, "+
                                                                        "FOREIGN KEY ("+GAME_NAME_COLUMN+")"+" REFERENCES "+
                                                                        TABLENAME+"("+GAME_NAME_COLUMN+")"+ " ON DELETE CASCADE);");

        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);

        }

    }



}
