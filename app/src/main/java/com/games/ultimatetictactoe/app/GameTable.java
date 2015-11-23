package com.games.ultimatetictactoe.app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String CONTINUE_GAME = "continue";
    private static final String NEW_GAME = "new";
    private static final int TILEPLAYERSKEY = 1;
    private boolean onCreateRead;
    private RelativeLayout gameTableLayout;
    private TableIndex bigTable[][];
    private enum PLAYER{PLAYER1,PLAYER2} // tile carries the player that played on the tile.
    private PLAYER currentPlayer;
    private String lastMove;
    private Handler observerHandler;
    private HandlerThread observerHandlerThread;
    private ContentObserver dbObserver;


    /*
            gameChoice parameter will tell GameTable whether to continue an already started game or start a new game.
            possible values for gameChoice are: continue, new.

            gameName this will have the name of name iff gameChoice is filled out.

         */
    private String opponent;
    private String gameName;

    private OnFragmentInteractionListener mListener;

    private View.OnClickListener tileListener = new
                        View.OnClickListener(){
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
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public GameTable() {
        // Required empty public constructor
    }

    public void setGameName(String gameName){
        this.gameName = gameName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onCreateRead = false;
        if (getArguments() != null) {
            opponent = getArguments().getString(ARG_PARAM1);
            gameName = getArguments().getString(ARG_PARAM2);
        }

        lastMove = null;
        currentPlayer = PLAYER.PLAYER1;
        bigTable = new TableIndex[3][3];
        //int count = 0;
        for(int i = 0; i < bigTable.length; i++){

            for(int j = 0; j < bigTable[i].length; j++){

                bigTable[i][j] = new TableIndex(Index.STATE.NONE);
                //count++;
            }

        }


        new DBReadAsyncTask().execute(new String[]{gameName,opponent});
        onCreateRead = true;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_game_table, container, false);
        gameTableLayout = (RelativeLayout) fragmentView.findViewById(R.id.gameTable);
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
        if(!onCreateRead) {
            new DBReadAsyncTask().execute(gameName);
        }
        else{
            onCreateRead = false;
        }
        startObserverThread();
        dbObserver = new DBObserver(observerHandler);
        getActivity().getContentResolver().registerContentObserver(Uri.parse(DBManager.CONTENTURI+"/"+DBManager.TABLENAME),
                                                                                    false,dbObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        new DBWriteAsyncTask().execute(gameName);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().getContentResolver().unregisterContentObserver(dbObserver);
        stopObserverThread();
    }


    private void startObserverThread(){
        observerHandlerThread = new HandlerThread("observerThread");
        observerHandlerThread.start();
        observerHandler = new Handler(observerHandlerThread.getLooper());
    }

    private void stopObserverThread(){
        if(observerHandler != null && observerHandlerThread != null){
            observerHandlerThread.quitSafely();
            observerHandler = null;
            observerHandlerThread = null;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
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

        if(tile.getTag(TILEPLAYERSKEY) != null){ // some one already played this tile so can't play on a tile twice
            Toast.makeText((Activity)mListener,"Invalid move",Toast.LENGTH_LONG);
            return;
        }
        GridLayout parent = (GridLayout)tile.getParent();
        if(!parent.getTag().equals("t"+lastMove)){ // if the current table is not the table that was assigned base on last move.
            Toast.makeText((Activity)mListener,"Invalid move",Toast.LENGTH_LONG);
            return;
        }
        String tableCoordinate= ((String)parent.getTag()).substring(1);

        Activity myActivity = GameTable.this.getActivity();
        //String asyncBundleSubjectKey = myActivity.getString(R.string.asyncBundlesubject);
        SharedPreferences preferences = myActivity.getPreferences(Context.MODE_PRIVATE);
        Bundle sendBundle = new Bundle();
        sendBundle.putString("to",preferences.getString(myActivity.getString(R.string.opponentsmailid),null));
        sendBundle.putString(myActivity.getString(R.string.asyncBundlesubject),myActivity.getString(R.string.asyncsendsubjecttype));
        String message = "game name: "+gameName+"\n";



        //Makes the move for the player.
        switch(currentPlayer){
            case PLAYER1:
                tile.setTag(TILEPLAYERSKEY, 0);
                break;
            case PLAYER2:
                tile.setTag(TILEPLAYERSKEY, 1);

                break;
        }

        new DBWriteAsyncTask().execute(new String[]{tableCoordinate,gameName});
        lastMove = (String)tile.getTag(); // setting last move to be current move.

        TableIndex tableIndex [][] = new TableIndex[3][3];
        int index = 0;

        String row = "";
        for(int i = 0; i < tableIndex.length; i++){

            for(int j = 0; j < tableIndex[i].length; j++){
                View tv = parent.getChildAt(index);
                int player = (Integer)tv.getTag(TILEPLAYERSKEY);
                row+=player;
                switch (player){
                    case 0:
                        tableIndex[i][j] = new TableIndex(Index.STATE.PLAYER1);
                        break;
                    case 1:
                        tableIndex[i][j] = new TableIndex(Index.STATE.PLAYER2);
                        break;

                    case -1:
                        tableIndex[i][j] = new TableIndex(Index.STATE.NONE);
                }
            }

        }
        int i = Integer.parseInt(tableCoordinate.charAt(0)+"");
        int j = Integer.parseInt(tableCoordinate.charAt(1)+"");
        message+="("+tableCoordinate+")"+"("+bigTable[i][j]+")"+row+"\n";




        if(true)//TODO: Check if player won small table. Pass tableIndex to checker method.
        {

            switch (currentPlayer){
                case PLAYER1:
                    bigTable[i][j].setState(Index.STATE.PLAYER1);
                    break;
                case PLAYER2:
                    bigTable[i][j].setState(Index.STATE.PLAYER2);
                    break;
            }

            //TODO: if player won small table check if play won big table.
            //if player won big table
            message+=this.getString(R.string.gamestatus)+" "+this.getString(R.string.gamestatuswon);
            //If player didn't win big table
            message+=this.getString(R.string.gamestatus)+" "+this.getString(R.string.gamestatuscontinue);

        }
        //TODO: If play didn't win small table check if this table is tied.
            //TODO if table is tied check if big table is also tied.


        Bundle messageBundle = new Bundle();
        messageBundle.putString("message",message);
        sendBundle.putBundle(myActivity.getString(R.string.asyncmessagebundle),messageBundle);
        myActivity.getContentResolver().requestSync(MainTicTacToeActivity.createSyncAccount(myActivity),MainTicTacToeActivity.AUTHORITY,sendBundle);


        switch(currentPlayer){
            case PLAYER1:
                currentPlayer = PLAYER.PLAYER2;

                break;
            case PLAYER2:
                currentPlayer = PLAYER.PLAYER1;
        }




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


            int bigTableChildCount = gameTableLayout.getChildCount();
            for(int i = 0; i < bigTableChildCount; i++){
                GridLayout gL = (GridLayout)gameTableLayout.getChildAt(i);
                String coordinatesTag = (String)gL.getTag();
                int x = coordinatesTag.charAt(0);
                int y = coordinatesTag.charAt(1);
                String row[] = strings[x][y].split(",");
                for(int j = 0; j < row.length; j++){
                    View v = gL.getChildAt(j);
                    int state = Integer.parseInt(row[j]);
                    if(state != -1)
                        v.setTag(TILEPLAYERSKEY,state);
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
            String [][]rows = new String[3][3];

            for(int i = 0; i < bigTable.length;i++){
                for(int j = 0; j < bigTable[i].length; j++){
                    int currentTableState = DBManager.CPHandler.getTableState(GameTable.this.getActivity(),i+""+j,params[0]);
                    if(currentTableState == 1){
                        bigTable[i][j].setState(Index.STATE.PLAYER2);
                    }
                    else if(currentTableState == 0){
                        bigTable[i][j].setState(Index.STATE.PLAYER1);
                    }
                    else{
                        bigTable[i][j].setState(Index.STATE.NONE);
                    }

                    rows[i][j] = DBManager.CPHandler.getUnParsedRow(GameTable.this.getActivity(),i+""+j,params[0]);

                }
            }
            return rows;
        }
    }


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
                    Integer p = (Integer)v.getTag(TILEPLAYERSKEY);
                    if(p != null)
                        row+=p+",";
                    else
                        row+="-1"+",";
                }
                String coordinate = (String)gl.getTag();
                int x = Integer.parseInt(coordinate.substring(0,1));
                int y = Integer.parseInt(coordinate.substring(1));
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

            if(params.length == 2){
                String coordinates = params[1];
                int i = Integer.parseInt(coordinates.charAt(0)+"");
                int j = Integer.parseInt(coordinates.charAt(1)+"");
                switch(bigTable[i][j].getState()){
                    case PLAYER1:
                        DBManager.CPHandler.updateTable(GameTable.this.getActivity(),dbObserver,coordinates,0,rows[i][j],params[0]);
                        break;
                    case PLAYER2:
                        DBManager.CPHandler.updateTable(GameTable.this.getActivity(),dbObserver,coordinates,1,rows[i][j],params[0]);
                        break;
                    case NONE:
                        DBManager.CPHandler.updateTable(GameTable.this.getActivity(),dbObserver,coordinates,-1,rows[i][j],params[0]);
                        break;

                }


            }

           else{
                for(int i = 0; i < bigTable.length; i++){
                    for(int j = 0; j < bigTable[i].length;j++){
                        String coordinates = ""+i+""+j;
                        switch(bigTable[i][j].getState()){
                            case PLAYER1:
                                //dbManager.insert(coordinates,0,rows[i][j],params[0]);
                                DBManager.CPHandler.insert(GameTable.this.getActivity(),dbObserver, coordinates, 0, rows[i][j], params[0],params[1]);
                                break;
                            case PLAYER2:
                                //dbManager.insert(coordinates,1,rows[i][j],params[0]);
                                DBManager.CPHandler.insert(GameTable.this.getActivity(),dbObserver,coordinates, 1, rows[i][j], params[0],params[1]);
                                break;

                            case NONE:
                                //dbManager.insert(coordinates,-1,rows[i][j],params[0]);
                                DBManager.CPHandler.insert(GameTable.this.getActivity(),dbObserver,coordinates, -1, rows[i][j], params[0],params[1]);
                                break;
                        }

                    }
                }

            }
            return null;
        }
    }

}
