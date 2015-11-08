package com.games.ultimatetictactoe.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

/**
 * Created by alemjc on 11/8/15.
 * This is the DB table manager that will be used to save game data.
 */
public class DBManager{

    private static final String DATABASENAME = "ultimatetictactoe";
    private static final String TABLENAME="tablerepresentation";
    private static final int VERSION = 1;
    private static final String id="_id";
    private static final String TABLE_COORDINATES_COLUMN = "tablecoordinate";
    private static final String TABLE_STATE_COLUMN = "statecolumn";
    private static final String TABLE_ROW_COLUMN = "tablerow";
    private static final String GAME_NAME_COLUMN = "gamename";

    private SQLiteDatabase db;
    private SQLHelper sqlHelper;
    private static DBManager dbManager;


    private DBManager(Context c){
        sqlHelper = new SQLHelper(c);
        db = sqlHelper.getWritableDatabase();

    }

    public static DBManager getInstance(Context c){
        if(dbManager != null){
            return dbManager;
        }
        else{
            dbManager = new DBManager(c);
            return dbManager;
        }
    }

    private String convertToRow(TableIndex innerTable[][]){
        String row = "";

        for(int i = 0; i < innerTable.length; i++){
            for(int j = 0; j < innerTable[i].length; j++){
                row+=innerTable[i][j]+",";
            }
        }

        row = row.substring(0,row.length()-1);
        return row;
    }

    public long insert(String tableCoordinates,int tableState, TableIndex innerTable[][],String gameName){

        String row = convertToRow(innerTable);

        ContentValues contentValues = new ContentValues();
        contentValues.put(GAME_NAME_COLUMN,gameName);
        contentValues.put(TABLE_STATE_COLUMN,tableState);
        contentValues.put(TABLE_COORDINATES_COLUMN,tableCoordinates);
        contentValues.put(TABLE_ROW_COLUMN,row);

        return db.insert(TABLENAME,null,contentValues);

    }

    public void updateTable(String tableCoordinates,int tableState,TableIndex innerTable[][], String gameName){
        String row = convertToRow(innerTable);
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_STATE_COLUMN,tableState);
        contentValues.put(TABLE_ROW_COLUMN,row);

        db.update(TABLENAME,contentValues,TABLE_COORDINATES_COLUMN+" = ?"+
                                                    " AND "+GAME_NAME_COLUMN+" = ?",
                                                        new String[]{tableCoordinates,gameName});

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

    public int getTableState(String tableCoordinates, String gameName){
        Cursor c = db.rawQuery("SELECT "+TABLE_STATE_COLUMN+ " FROM "+TABLENAME+" WHERE "+TABLE_COORDINATES_COLUMN+" = ?"
                                                                                    +" AND "+GAME_NAME_COLUMN+"= ?",
                                    new String[]{tableCoordinates,gameName});
        c.moveToFirst();
        int state = c.getInt(0);
        c.close();
        return state;
    }

    public String getUnParsedRow(String tableCoordinates, String gameName){
        Cursor c = db.rawQuery("SELECT "+TABLE_ROW_COLUMN+  " FROM "+TABLENAME+" WHERE "+TABLE_COORDINATES_COLUMN+" = ?"
                                                                                          +" AND "+GAME_NAME_COLUMN+"= ?",
                                    new String[]{tableCoordinates,gameName});
        c.moveToFirst();
        String row = c.getString(0);
        c.close();

        return row;

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
}
