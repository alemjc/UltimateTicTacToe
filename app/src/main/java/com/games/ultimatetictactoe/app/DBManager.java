package com.games.ultimatetictactoe.app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
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
    private static final int VERSION = 1;
    private static final String id="_id";
    public static final String TABLE_COORDINATES_COLUMN = "tablecoordinate";
    public static final String TABLE_STATE_COLUMN = "statecolumn";
    public static final String TABLE_ROW_COLUMN = "tablerow";
    public static final String GAME_NAME_COLUMN = "gamename";
    public static final Uri CONTENTURI = Uri.parse("content://com.games.ultmatetictactoe.app.DBManager/"+DATABASENAME);
    private static final int TICTACTOETABLE = 1;
    private static final int UPDATETABLETATEROW = 6;
    private static final int TICTACTOETABLETATE = 7;
    private static final int TICTACTOETABLEROW = 8;
    private static final int UPDATETABLE = 2;
    private static final int GETSAVEDGAMES = 3;
    private static final int GETTABLESTATE = 4;
    private static final int GETUNPARSEDROWS = 5;

    private SQLiteDatabase db;
    private SQLHelper sqlHelper;
    private static DBManager dbManager;

    private static final String AUTHORITY = "com.games.ultimatetictactoe.app.DBManager";
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        uriMatcher.addURI(AUTHORITY,DATABASENAME+"/"+TABLENAME,TICTACTOETABLE);
        //uriMatcher.addURI(AUTHORITY,DATABASENAME+TABLENAME+"/"+TABLE_STATE_COLUMN,TICTACTOETABLETATE);
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



    public int updateTable(String tableCoordinates,int tableState,String row, String gameName){

        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_STATE_COLUMN,tableState);
        contentValues.put(TABLE_ROW_COLUMN,row);

        Uri uri = Uri.parse(CONTENTURI.toString());
        String selection = TABLE_COORDINATES_COLUMN+" = ?"+" AND "+GAME_NAME_COLUMN+" = ?";

        return update(uri,contentValues,selection, new String[]{tableCoordinates,gameName});

    }

    public String[] getSavedGames(){

        Cursor c = db.rawQuery("SELECT "+GAME_NAME_COLUMN+" FROM "+TABLENAME+";",null);
        c.moveToFirst();
        String [] gameNames = new String[c.getCount()];

        int count = 0;
        while(!c.isAfterLast()){
            gameNames[count] = c.getString(0);
            count++;
            c.moveToNext();
        }

        c.close();

        return gameNames;
    }



    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        switch(uriMatcher.match(uri)){
            case TICTACTOETABLE:
                db.insert(TABLENAME,null,contentValues);
                return Uri.parse(CONTENTURI+"/"+TABLENAME);

            default:
                return null;

        }


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

        switch(uriMatcher.match(uri)){
            case TICTACTOETABLE:
               return db.query(TABLENAME,projection,selection,selectionArgs,null,null,null,null);
            default:
                return null;
        }



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
            onCreate(db);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLENAME+ "( "+id+ "INTEGER INCREMENTS PRIMARY KEY, "+
                                                                    GAME_NAME_COLUMN+ " VARCHAR(100) "+
                                                                    TABLE_COORDINATES_COLUMN+" VARCHAR(2), "+
                                                                    TABLE_STATE_COLUMN+" INTEGER, "+
                                                                    TABLE_ROW_COLUMN+ "VARCHAR(17)); ");

        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);

        }

    }


    public static class CPHandler {

        private CPHandler(){};

        public static Uri insert(Context c, String tableCoordinates,int tableState, String row,String gameName){
            ContentValues contentValues = new ContentValues();
            contentValues.put(GAME_NAME_COLUMN,gameName);
            contentValues.put(TABLE_STATE_COLUMN,tableState);
            contentValues.put(TABLE_COORDINATES_COLUMN,tableCoordinates);
            contentValues.put(TABLE_ROW_COLUMN,row);

            Uri uri = Uri.parse(CONTENTURI.toString()+"/"+DBManager.TABLENAME);

            //return db.insert(TABLENAME,null,contentValues);
            return c.getContentResolver().insert(uri,contentValues);

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
