package com.games.ultimatetictactoe.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.database.ContentObserver;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Handler;
import android.os.HandlerThread;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameTable.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameTable#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameTable extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GAMENAME = "param1";
    private static final String ARG_OPPONENTID = "param2";
    private boolean onCreateRead;
    private GridLayout gameTableLayout;
    private int bigTable[][];
    private int currentPlayer;
    private String lastMove;
    private Handler observerHandler;
    private HandlerThread observerHandlerThread;
    private ContentObserver dbObserver;



    private String opponentID;
    private String gameName;

    private OnFragmentInteractionListener mListener;

    private View.OnClickListener tileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateTable(v);
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GameTable.
     */
    // TODO: Rename and change types and number of parameters
    public static GameTable newInstance(String param1, String param2) {
        GameTable fragment = new GameTable();
        Bundle args = new Bundle();
        args.putString(ARG_GAMENAME, param2);
        args.putString(ARG_OPPONENTID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public GameTable() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onCreateRead = false;
        if (getArguments() != null) {
            opponentID = getArguments().getString(ARG_OPPONENTID);
            gameName = getArguments().getString(ARG_GAMENAME);
        }

        lastMove = null;

        bigTable = new int[3][3];
        //int count = 0;
        for(int i = 0; i < bigTable.length; i++){

            for(int j = 0; j < bigTable[i].length; j++){

                bigTable[i][j] = getActivity().getResources().getInteger(R.integer.none);
                //count++;
            }

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_game_table, container, false);
        gameTableLayout = (GridLayout) fragmentView.findViewById(R.id.gameTable);
        int count = gameTableLayout.getChildCount();

        for(int i = 0; i < count; i++){
            GridLayout gL = (GridLayout)gameTableLayout.getChildAt(i);
            if(gL != null){
                int countY = gL.getChildCount();
                for(int j = 0; j < countY; j++){
                    View v = gL.getChildAt(j);
                    v.setOnClickListener(tileListener);
                }
            }
        }



        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new DBReadAsyncTask().execute(gameName);

        startObserverThread();
        dbObserver = new Observer(observerHandler);
        getActivity().getContentResolver().registerContentObserver(Uri.parse(DBManager.CONTENTURI+"/"+DBManager.TABLENAME),
                                                                                    true,dbObserver);
        getActivity().getContentResolver().registerContentObserver(Uri.parse(DBManager.CONTENTURI+"/"+DBManager.GAMETABLE_CURRENTTURN_COLUMN)
                ,false,dbObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        //new DBWriteAsyncTask().execute(gameName);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().getContentResolver().unregisterContentObserver(dbObserver);
        stopObserverThread();
    }


    private void startObserverThread(){
        //observerHandlerThread = new HandlerThread("observerThread");
        //observerHandlerThread.start();
        observerHandler = new Handler(getActivity().getMainLooper());
    }

    private void stopObserverThread(){
        if(observerHandler != null){
            //observerHandlerThread.quitSafely();
            observerHandler = null;
            //observerHandlerThread = null;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
         void onFragmentInteraction(Uri uri);
    }

    public void updateTable(View tile){
        Log.d("updateTable","about to go on validations!!!!!!!!!!!!!!!!!");
        Intent updateTableIntent; // intent to update db table.
        Runnable runnable = null;
        GridLayout parent; // parent view.
        String tableCoordinate; // this is the coordinates of this small table.
        Bundle sendBundle; // bundle that contains message to send to opponent.
        TelephonyManager telephonyManager;
        String phoneNumber;
        StringBuilder data; // This will retain game data.
        StringBuilder message; //This is the message that will be send to the opponent.
        final Activity myActivity = (Activity)mListener;

        if(currentPlayer == ((Activity)mListener).getResources().getInteger(R.integer.opponentsTurn)){
            Log.d("updateTable","it is opponentsTurn");
            Toast.makeText((Activity)mListener,"It is opponent's Turn",Toast.LENGTH_LONG).show();
            return;
        }

        if((Integer)tile.getTag(R.integer.tileplayerskey) != myActivity.getResources().getInteger(R.integer.none)){ // some one already played this tile so can't play on a tile twice
            Log.d("updateTable","for some reason the tile tag has a different value than none");
            Log.d("updateTable","tile tag: "+tile.getTag(R.integer.tileplayerskey));
            return;
        }
        Log.d("updateTable","passed validations!!!!!!!!!!!!!!!!!");
        parent = (GridLayout)tile.getParent();
        /*if(!parent.getTag().equals("t"+lastMove)){ // if the current table is not the table that was assigned base on last move.
            return;
        }*/

        tableCoordinate = ((String)parent.getTag()).substring(1);
        telephonyManager = (TelephonyManager) myActivity.getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = telephonyManager.getLine1Number();
        updateTableIntent = new Intent(myActivity,UpdateTableService.class);
        Handler uiHandler = new Handler(myActivity.getMainLooper());
        message = new StringBuilder();
        data = new StringBuilder();

        sendBundle = new Bundle();
        sendBundle.putString(myActivity.getString(R.string.asyncbundleintent),myActivity.getString(R.string.asyncsendintent));
        message.append(opponentID);
        message.append("\n");
        message.append(phoneNumber);
        message.append("\n");
        message.append(myActivity.getString(R.string.gamemove));
        message.append("\n");
        data.append(gameName);
        data.append(";");
        updateTableIntent.putExtra(UpdateTableService.EXTRA_GAMENAME,gameName);

        //Makes the move for the player.
        tile.setTag(R.integer.tileplayerskey, myActivity.getResources().getInteger(R.integer.myTurn));
        //tile.setBackground(myActivity.getDrawable(R.drawable.mycolor));
        ((ImageView)tile).setImageDrawable(myActivity.getDrawable(R.drawable.mycolor));
        //new DBWriteAsyncTask().execute(new String[]{tableCoordinate,gameName});//TODO: get rid of the async task and replace it with a service to write to the databse.


        int tableIndex [][] = new int[3][3];
        int index = 0;

        StringBuilder row = new StringBuilder();
        StringBuilder opponentsRow = new StringBuilder();

        for(int i = 0; i < tableIndex.length; i++){

            for(int j = 0; j < tableIndex[i].length; j++){
                View tv = parent.getChildAt(index);
                int player = (Integer)tv.getTag(R.integer.tileplayerskey);
                row.append(player+",");
                if(player == myActivity.getResources().getInteger(R.integer.myTurn)){
                    Log.d("updateTable","opponentsTurn "+myActivity.getResources().getInteger(R.integer.opponentsTurn));
                    opponentsRow.append(myActivity.getResources().getInteger(R.integer.opponentsTurn)+",");
                }
                else if(player == myActivity.getResources().getInteger(R.integer.opponentsTurn)){
                    opponentsRow.append(myActivity.getResources().getInteger(R.integer.myTurn)+",");
                }
                else{
                    Log.d("updateTable","empty: "+player);
                    opponentsRow.append(player+",");
                }
                tableIndex[i][j] = player;
                index++;
            }

        }

        row.deleteCharAt(row.length()-1);
        opponentsRow.deleteCharAt(opponentsRow.length()-1);
        int i = Integer.parseInt(tableCoordinate.charAt(0)+"");
        int j = Integer.parseInt(tableCoordinate.charAt(1)+"");


        if(checkIfWon(myActivity,tableIndex,i,j)) {
            bigTable[i][j] = myActivity.getResources().getInteger(R.integer.myTurn);
            if(checkIfWon(myActivity,bigTable,i,j)) {
                //if player won big table
                data.append(myActivity.getResources().getInteger(R.integer.gamestatewon));
                data.append(";");
                updateTableIntent.setAction(UpdateTableService.ACTION_UPDATEGAMESTATE);
                updateTableIntent.putExtra(UpdateTableService.EXTRA_GAMESTATE,myActivity.getResources().getInteger(R.integer.gamestatewon));
                myActivity.startService(updateTableIntent);
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        showAlertDialog("You Won!");
                    }
                };

            }
            else if(checkIfTied(myActivity,bigTable)){
                // if big table tied
                data.append(myActivity.getResources().getInteger(R.integer.gamestatetied));
                data.append(";");
                updateTableIntent.setAction(UpdateTableService.ACTION_UPDATEGAMESTATE);
                updateTableIntent.putExtra(UpdateTableService.EXTRA_GAMESTATE,myActivity.getResources().getInteger(R.integer.gamestatetied));
                myActivity.startService(updateTableIntent);
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        showAlertDialog("Game Tied");
                    }
                };

            }
            else {
                //If player didn't win big table
                data.append(myActivity.getResources().getInteger(R.integer.gamestateongoing));
                data.append(";");

            }

        }
        else if(checkIfTied(myActivity, tableIndex)){
            if(checkIfTied(myActivity,bigTable)){
                data.append(myActivity.getResources().getInteger(R.integer.gamestatetied));
                data.append(";");
                updateTableIntent.setAction(UpdateTableService.ACTION_UPDATEGAMESTATE);
                updateTableIntent.putExtra(UpdateTableService.EXTRA_GAMESTATE,myActivity.getResources().getInteger(R.integer.gamestatetied));
                myActivity.startService(updateTableIntent);

            }
        }


        data.append("(");
        data.append(tableCoordinate);
        data.append(")");
        data.append("(");
        data.append(bigTable[i][j]);
        data.append(")");
        data.append(opponentsRow.toString());

        Log.d("updateTable","data = "+data);
        updateTableIntent.setAction(UpdateTableService.ACTION_UPDATETABLE);
        updateTableIntent.putExtra(UpdateTableService.EXTRA_TABLESTATE,bigTable[i][j]);
        updateTableIntent.putExtra(UpdateTableService.EXTRA_COORDINATES,tableCoordinate);
        updateTableIntent.putExtra(UpdateTableService.EXTRA_ROW,row.toString());
        updateTableIntent.putExtra(UpdateTableService.EXTRA_GAMENAME,gameName);

        myActivity.startService(updateTableIntent);

        updateTableIntent.setAction(UpdateTableService.ACTION_UPDATEPLAYER);
        updateTableIntent.putExtra(UpdateTableService.EXTRA_OPPONENTID,opponentID);

        //ExecutorService executorService = Executors.newFixedThreadPool(1);


        switch(currentPlayer){
            case 0:
                currentPlayer = getActivity().getResources().getInteger(R.integer.opponentsTurn);
                updateTableIntent.putExtra(UpdateTableService.EXTRA_CURRENTPLAYER,currentPlayer);

                break;
            case 1:
                currentPlayer = getActivity().getResources().getInteger(R.integer.myTurn);
                updateTableIntent.putExtra(UpdateTableService.EXTRA_CURRENTPLAYER,currentPlayer);
        }

        myActivity.startService(updateTableIntent);



        message.append(data.toString());
        sendBundle.putString(myActivity.getString(R.string.asyncmessage),message.toString());
        sendBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,true);
        sendBundle.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE,true);
        //sendBundle.putBundle(myActivity.getString(R.string.asyncmessagebundle), messageBundle);
        Log.d("gametable$updatetable",sendBundle.toString());

        myActivity.getContentResolver().requestSync(((MainTicTacToeActivity)myActivity).account,MainTicTacToeActivity.AUTHORITY,sendBundle);
        if(runnable != null){
            uiHandler.post(runnable);
        }


    }

    private void showAlertDialog(String message){

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setMessage(message).
                setNegativeButton("Quit", new AlertDialog.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onFragmentInteraction(null);
                    }
                });

    }


    private boolean checkIfWon(Context c,int table[][],int x, int y){
        int player = c.getResources().getInteger(R.integer.myTurn);
        int opponent = c.getResources().getInteger(R.integer.opponentsTurn);
        if(GameChecker.checkRow(x,y,table,player,opponent) == GameChecker.STATE.PLAYER){
            return true;
        }
        else if(((x == 0 && y != 1) || (x==1 && y != 0) || (x == 2 && y != 1) || (x==1 && y !=2))
                && (GameChecker.checkDiagonal(x,y,table,player,opponent) == GameChecker.STATE.PLAYER)){
            return true;
        }
        else return GameChecker.checkColumn(x, y, table, player, opponent) == GameChecker.STATE.PLAYER;
    }

    private boolean checkIfTied(Context c,int table[][]){
        int player = c.getResources().getInteger(R.integer.myTurn);
        int opponent = c.getResources().getInteger(R.integer.opponentsTurn);


        for(int j = 0; j < table[0].length; j++){

            if(GameChecker.checkRow(0,j,table,player,opponent) != GameChecker.STATE.TIE){
                return false;
            }
            else if(GameChecker.checkDiagonal(0,j,table,player,opponent) != GameChecker.STATE.TIE){
                return false;
            }
            else if(GameChecker.checkColumn(0,j,table,player,opponent) != GameChecker.STATE.TIE){
                return false;
            }

        }

        return GameChecker.checkRow(1, 0, table, player, opponent) == GameChecker.STATE.TIE;

    }



    private class DBReadAsyncTask extends AsyncTask<String,Integer,String[][]>{
        public DBReadAsyncTask() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String[][] strings) {
            super.onPostExecute(strings);
            if(strings == null){
                Log.d("on readasynctask","strings is null");
                return;
            }
            Log.d("on readasynctask","strings length is: "+ strings.length);
            Activity myActivity = (Activity)mListener;
            int bigTableChildCount = gameTableLayout.getChildCount();
            for(int i = 0; i < bigTableChildCount; i++){
                GridLayout gL = (GridLayout)gameTableLayout.getChildAt(i);
                String coordinatesTag = ((String)gL.getTag()).substring(1);
                Log.d("on readasynctask","coordinatesTag: "+coordinatesTag);
                int x = Integer.parseInt(coordinatesTag.charAt(0)+"");
                int y = Integer.parseInt(coordinatesTag.charAt(1)+"");
                Log.d("on readasynctask","x = "+x+" "+y+"="+y);
                String row[] = strings[x][y].split(",");
                for(int j = 0; j < row.length; j++){
                    View v = gL.getChildAt(j);
                    int state = Integer.parseInt(row[j]);
                    // change view background based on the state
                    switch(state){
                        case 0:
                            //change background to O.
                            v.setBackground(((Activity)mListener).getDrawable(R.drawable.mycolor));
                            v.setTag(R.integer.tileplayerskey,myActivity.getResources().getInteger(R.integer.myTurn));
                            break;
                        case 1:
                            //change background to 1.
                            v.setBackground(((Activity)mListener).getDrawable(R.drawable.opponentscolor));
                            v.setTag(R.integer.tileplayerskey,myActivity.getResources().getInteger(R.integer.opponentsTurn));
                            break;
                        case -1:
                            //background is empty.
                            Log.d("filling none","hey!!!");
                            v.setBackground(((Activity)mListener).getDrawable(R.drawable.rszbluefabric));
                            v.setTag(R.integer.tileplayerskey,myActivity.getResources().getInteger(R.integer.none));
                            break;
                    }
                    //TODO: change picture of the view based on the state.
                }

            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onCancelled(String[][] strings) {
            super.onCancelled(strings);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected String[][] doInBackground(String... params) {

            String gameName = params[0];

            String [][]rows = new String[3][3];
            Context c = getActivity();
            for(int i = 0; i < bigTable.length;i++){
                for(int j = 0; j < bigTable[i].length; j++){

                    int currentTableState = CPHandler.getTableState(c,c.getContentResolver().
                            acquireContentProviderClient(DBManager.CONTENTURI),i+""+j,gameName);
                    bigTable[i][j] = currentTableState;
                    rows[i][j] = CPHandler.getUnParsedRow(c,c.getContentResolver().
                            acquireContentProviderClient(DBManager.CONTENTURI)
                            ,i+""+j,gameName);
                    if(rows[i][j] == null){
                        return null;
                    }

                }
            }

            currentPlayer = CPHandler.getCurrentPlayer(c,c.getContentResolver().
                    acquireContentProviderClient(DBManager.CONTENTURI),gameName,opponentID);
            Log.d("readasynctask","currentUpdate: "+currentPlayer);
            return rows;
        }
    }


    //Not being used.
    private class DBWriteAsyncTask extends AsyncTask<String,Integer,Object> {
        String rows[][];


        public DBWriteAsyncTask() {
            super();
            rows = new String[3][3];
            for(int i = 0; i < rows.length; i++){
                for(int j = 0; j< rows[i].length; j++){
                    rows[i][j] = "";
                }
            }
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            int childCount = gameTableLayout.getChildCount();

            for(int i = 0; i < childCount; i++){
                GridLayout gl = (GridLayout)gameTableLayout.getChildAt(i);
                int cc = gl.getChildCount();
                String row = "";
                for(int j = 0; j < cc; j++){
                    View v = gl.getChildAt(j);
                    Integer p = (Integer)v.getTag(R.integer.tileplayerskey);
                    if(p != null)
                        row+=p+",";
                    else
                        row+="-1"+",";
                }
                String coordinate = (String)gl.getTag();
                int x = Integer.parseInt(coordinate.substring(1,2));
                int y = Integer.parseInt(coordinate.substring(2));
                rows[x][y] = row;

            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }

        @Override
        protected void onCancelled(Object o) {
            super.onCancelled(o);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }


        @Override
        protected Object doInBackground(String... params) {
            //DBManager dbManager = GameTable.this.getActivity().getContentResolver();
            Context c = getActivity();
            if(params.length == 2){
                String coordinates = params[1];
                int i = Integer.parseInt(coordinates.charAt(0)+"");
                int j = Integer.parseInt(coordinates.charAt(1)+"");

                CPHandler.updateTable(c,c.getContentResolver().acquireContentProviderClient(DBManager.CONTENTURI)
                        ,dbObserver,coordinates,bigTable[i][j],rows[i][j],params[0]);



            }

            return null;
        }
    }

    private class Observer extends ContentObserver{
        private final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        private final int UPDATETABLE = 1;
        private final int UPDATECURRENTUSER = 2;

        public Observer(Handler handler) {
            super(handler);
            uriMatcher.addURI(DBManager.AUTHORITY,DBManager.DATABASENAME+"/"+DBManager.TABLENAME,UPDATETABLE);
            uriMatcher.addURI(DBManager.AUTHORITY,DBManager.DATABASENAME+"/"+DBManager.GAMETABLE_CURRENTTURN_COLUMN,UPDATECURRENTUSER);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if(uriMatcher.match(uri) == UPDATETABLE) {
                new DBReadAsyncTask().execute(gameName);
            }
            else if(uriMatcher.match(uri) == UPDATECURRENTUSER){
                new Thread( new Runnable(){
                    @Override
                    public void run() {
                        Activity activity = (Activity)mListener;
                        ContentResolver contentResolver = activity.getContentResolver();
                        ContentProviderClient contentProviderClient = contentResolver.acquireContentProviderClient(DBManager.CONTENTURI);
                        currentPlayer = CPHandler.getCurrentPlayer(getActivity(),contentProviderClient,gameName,opponentID);
                    }
                }).start();
            }
        }
    }

}
