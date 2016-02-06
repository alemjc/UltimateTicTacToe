package com.games.ultimatetictactoe.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.database.ContentObserver;
import android.graphics.drawable.AnimationDrawable;
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
import android.widget.ImageButton;
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
    private ImageView turnView;
    private ImageButton quitButton;



    private String opponentID;
    private String gameName;

    private OnFragmentInteractionListener mListener;

    private View.OnClickListener tileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateTable(v);
        }
    };

    private View.OnClickListener quitListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            //Activity activity = (Activity)mListener;
            quitAlertDialog();
            /*if(currentPlayer == activity.getResources().getInteger(R.integer.myTurn)){
                quitAlertDialog();
            }
            else{
                Toast.makeText(activity,"It is opponent's turn",Toast.LENGTH_LONG).show();
            }*/

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
        quitButton = (ImageButton)fragmentView.findViewById(R.id.quitButton);
        turnView = (ImageView)fragmentView.findViewById(R.id.turnView);

        quitButton.setOnClickListener(quitListener);
        quitButton.setEnabled(false);

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
        lastMove = "";
        new DBReadAsyncTask().execute((String[])null);
        startObserverThread();
        dbObserver = new Observer(observerHandler);
        Uri uri = Uri.parse(DBManager.CONTENTURI.toString()+"/"+DBManager.DATABASENAME+"/"+DBManager.GAMETABLENAME);
        getActivity().getContentResolver().registerContentObserver(uri, false, dbObserver);
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

    private void quitGame(){
        Bundle sendBundle; // bundle that contains message to send to opponent.
        StringBuilder stringBuilder;
        StringBuilder data;
        String phoneNumber;
        TelephonyManager telephonyManager;
        Activity myActivity = (Activity)mListener;
        Intent removeGameIntent = new Intent(myActivity,UpdateTableService.class);

        telephonyManager = (TelephonyManager)myActivity.getSystemService(Context.TELEPHONY_SERVICE);

        phoneNumber = telephonyManager.getLine1Number();
        sendBundle = new Bundle();
        data = new StringBuilder();
        stringBuilder = new StringBuilder();
        sendBundle.putString(myActivity.getString(R.string.asyncbundleintent),myActivity.getString(R.string.asyncsendintent));
        stringBuilder.append(opponentID);
        stringBuilder.append("\n");
        stringBuilder.append(phoneNumber);
        stringBuilder.append("\n");
        stringBuilder.append(myActivity.getString(R.string.gamemove));
        stringBuilder.append("\n");
        data.append(gameName);
        data.append(";");
        data.append(myActivity.getResources().getInteger(R.integer.gamestatequit));

        stringBuilder.append(data);
        sendBundle.putString(myActivity.getString(R.string.asyncmessage),stringBuilder.toString());
        sendBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,true);
        sendBundle.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE,true);

        ContentResolver.requestSync(((MainTicTacToeActivity)myActivity).getAccount(),
                MainTicTacToeActivity.AUTHORITY,sendBundle);

        removeGameIntent.setAction(UpdateTableService.ACTION_REMOVEGAME);
        removeGameIntent.putExtra(UpdateTableService.EXTRA_GAMENAME,gameName);
        myActivity.startService(removeGameIntent);
        mListener.onFragmentInteraction(null);

    }

    private void updateTable(View tile){
        Runnable runnable = null;
        GridLayout parent; // parent view.
        String tableCoordinate; // this is the coordinates of this small table.
        Bundle sendBundle; // bundle that contains message to send to opponent.
        TelephonyManager telephonyManager;
        String phoneNumber;
        StringBuilder data; // This will retain game data.
        StringBuilder message; //This is the message that will be send to the opponent.
        String tileCoordinate;

        final Activity myActivity = (Activity)mListener;

        if(currentPlayer == ((Activity)mListener).getResources().getInteger(R.integer.opponentsTurn)){
            Toast.makeText((Activity)mListener,"It is opponent's Turn",Toast.LENGTH_LONG).show();
            return;
        }

        if((Integer)tile.getTag(R.integer.tileplayerskey) != myActivity.getResources().getInteger(R.integer.none)){ // some one already played this tile so can't play on a tile twice
            return;
        }

        parent = (GridLayout)tile.getParent();
        if(lastMove.length() != 0 && !parent.getTag().equals("t"+lastMove.substring(1,lastMove.indexOf(',')))){ // if the current table is not the table that was assigned base on last move.
            Toast.makeText(myActivity,"Can't play in here",Toast.LENGTH_LONG).show();
            return;
        }

        tableCoordinate = ((String)parent.getTag()).substring(1);
        tileCoordinate = (String)tile.getTag();
        telephonyManager = (TelephonyManager) myActivity.getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = telephonyManager.getLine1Number();
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

        //Makes the move for the player.
        tile.setTag(R.integer.tileplayerskey, myActivity.getResources().getInteger(R.integer.myTurn));
        lastMove = "m"+tileCoordinate+','+tableCoordinate;
        //tile.setBackground(myActivity.getDrawable(R.drawable.mycolor));
        tile.setBackgroundResource(R.drawable.animatedmyturn);
        AnimationDrawable animationDrawable = (AnimationDrawable) tile.getBackground();

        animationDrawable.start();
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
                    opponentsRow.append(myActivity.getResources().getInteger(R.integer.opponentsTurn)+",");
                }
                else if(player == myActivity.getResources().getInteger(R.integer.opponentsTurn)){
                    opponentsRow.append(myActivity.getResources().getInteger(R.integer.myTurn)+",");
                }
                else{
                    opponentsRow.append(player+",");
                }
                tableIndex[i][j] = player;
                index++;
            }

        }

        row.deleteCharAt(row.length()-1);
        opponentsRow.deleteCharAt(opponentsRow.length()-1);
        int tileX = Integer.parseInt(tileCoordinate.charAt(0)+"");
        int tileY = Integer.parseInt(tileCoordinate.charAt(1)+"");
        int i = Integer.parseInt(tableCoordinate.charAt(0)+"");
        int j = Integer.parseInt(tableCoordinate.charAt(1)+"");


        if(bigTable[i][j] == -1 && checkIfWon(myActivity,tableIndex,tileX,tileY)) {
            bigTable[i][j] = myActivity.getResources().getInteger(R.integer.myTurn);
            if(checkIfWon(myActivity,bigTable,i,j)) {
                //if player won big table
                data.append(myActivity.getResources().getInteger(R.integer.gamestatewon));
                data.append(";");
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        showAlertDialog(R.layout.you_win_view);
                    }
                };

            }
            else if(checkIfTied(myActivity,bigTable)){
                // if big table tied
                data.append(myActivity.getResources().getInteger(R.integer.gamestatetied));
                data.append(";");
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        showAlertDialog(R.layout.game_tied_view);
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
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        showAlertDialog(R.layout.game_tied_view);
                    }
                };

            }
        }
        else{
            data.append(myActivity.getResources().getInteger(R.integer.gamestateongoing));
            data.append(";");
        }

        int tableState = -1;

        if(bigTable[i][j] == myActivity.getResources().getInteger(R.integer.myTurn)){
            tableState = myActivity.getResources().getInteger(R.integer.opponentsTurn);
        }
        else if(bigTable[i][j] == myActivity.getResources().getInteger(R.integer.opponentsTurn)){
            tableState = myActivity.getResources().getInteger(R.integer.myTurn);
        }

        data.append(tableCoordinate);
        data.append("&");
        data.append(tableState);
        data.append("&");
        data.append("m"+tileCoordinate+','+tableCoordinate);
        data.append("&");
        data.append(opponentsRow.toString());

        switch(currentPlayer){
            case 0:
                currentPlayer = getActivity().getResources().getInteger(R.integer.opponentsTurn);
                //updateTableIntent.putExtra(UpdateTableService.EXTRA_CURRENTPLAYER,currentPlayer);

                break;
            case 1:
                currentPlayer = getActivity().getResources().getInteger(R.integer.myTurn);
                //updateTableIntent.putExtra(UpdateTableService.EXTRA_CURRENTPLAYER,currentPlayer);
        }

        //myActivity.startService(updateTableIntent);

        if(bigTable[i][j] == -1 && lastMove.length() > 0)
            //parent.setBackgroundColor(myActivity.getResources().getColor(R.color.black));
            parent.setBackground(myActivity.getDrawable(R.drawable.littletablebackground));
        else if(bigTable[i][j] == myActivity.getResources().getInteger(R.integer.myTurn) && lastMove.length() > 0){
            //parent.setBackgroundColor(myActivity.getResources().getColor(R.color.mytablecolor));
            parent.setBackground(myActivity.getDrawable(R.drawable.mytablecolor));
        }
        else if(bigTable[i][j] == myActivity.getResources().getInteger(R.integer.opponentsTurn) && lastMove.length() > 0){
            //parent.setBackgroundColor(myActivity.getResources().getColor(R.color.opponenttablecolor));
            parent.setBackground(myActivity.getDrawable(R.drawable.opponenttablecolor));
        }

        turnView.setImageResource(R.drawable.opponentsturn);

        message.append(data.toString());
        sendBundle.putString(myActivity.getString(R.string.asyncmessage),message.toString());
        sendBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,true);
        sendBundle.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE,true);
        //sendBundle.putBundle(myActivity.getString(R.string.asyncmessagebundle), messageBundle);

        ContentResolver.requestSync(((MainTicTacToeActivity)myActivity).getAccount(),MainTicTacToeActivity.AUTHORITY,sendBundle);
        if(runnable != null){
            uiHandler.post(runnable);
        }


    }

    private void showAlertDialog(final int message){

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setView(message).
                setNegativeButton("Done", new AlertDialog.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Activity myActivity = (Activity)mListener;
                        Intent removeGameIntent = new Intent(myActivity,UpdateTableService.class);
                        removeGameIntent.setAction(UpdateTableService.ACTION_REMOVEGAME);
                        removeGameIntent.putExtra(UpdateTableService.EXTRA_GAMENAME,gameName);
                        myActivity.startService(removeGameIntent);
                        mListener.onFragmentInteraction(null);

                    }
                }).show();

    }

    private void quitAlertDialog(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setTitle("Are your sure you want to quit?").
                setMessage("You will lose if you quit").
                setNegativeButton("Yes",new AlertDialog.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        quitGame();
                    }
                }).setPositiveButton("No",new AlertDialog.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //User wants to keep playing to don't do anything.
                    }
        }).show();
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
        Runnable runnable;
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
                return;
            }
            Activity myActivity = (Activity)mListener;
            int bigTableChildCount = gameTableLayout.getChildCount();
            for(int i = 0; i < bigTableChildCount; i++){
                GridLayout gL = (GridLayout)gameTableLayout.getChildAt(i);
                String coordinatesTag = ((String)gL.getTag()).substring(1);
                int x = Integer.parseInt(coordinatesTag.charAt(0)+"");
                int y = Integer.parseInt(coordinatesTag.charAt(1)+"");
                String row[] = strings[x][y].split(",");


                for(int j = 0; j < row.length; j++){
                    ImageView v = (ImageView) gL.getChildAt(j);
                    int state = Integer.parseInt(row[j]);
                    // change view background based on the state
                    switch(state){
                        case 0:
                            //change background to O.

                            v.setImageDrawable(((Activity)mListener).getDrawable(R.drawable.myfullmove));
                            v.setTag(R.integer.tileplayerskey,myActivity.getResources().getInteger(R.integer.myTurn));
                            break;
                        case 1:
                            //change background to 1.
                            if(lastMove.length() != 0 && lastMove.charAt(0) == 'o' &&
                                    lastMove.substring(1,lastMove.indexOf(',')).equals(v.getTag()) &&
                                    lastMove.substring(lastMove.indexOf(',')+1).equals(""+x+y)){
                                v.setBackgroundResource(R.drawable.animatedopponentturn);
                                AnimationDrawable animationDrawable = (AnimationDrawable) v.getBackground();
                                animationDrawable.start();
                            }
                            else {
                                v.setImageDrawable(((Activity) mListener).getDrawable(R.drawable.opponentmovesecondstroke));
                            }

                            v.setTag(R.integer.tileplayerskey,myActivity.getResources().getInteger(R.integer.opponentsTurn));
                            break;
                        case -1:
                            //background is empty.
                            //v.setBackgroundColor(myActivity.getResources().getColor(R.color.white));
                            v.setImageDrawable(((Activity)mListener).getDrawable(R.drawable.emptytilebackground));
                            v.setTag(R.integer.tileplayerskey,myActivity.getResources().getInteger(R.integer.none));
                            break;
                    }
                }


                if(lastMove.length() > 0 && coordinatesTag.equals(lastMove.substring(1,lastMove.indexOf(','))) &&
                        currentPlayer == myActivity.getResources().getInteger(R.integer.myTurn)){
                    //gL.setBackgroundColor(myActivity.getResources().getColor(R.color.yellow));
                    gL.setBackground(myActivity.getDrawable(R.drawable.littletablebackgroundmove));
                }
                else{
                    if(bigTable[x][y] == myActivity.getResources().getInteger(R.integer.myTurn)){
                        //gL.setBackgroundColor(myActivity.getResources().getColor(R.color.mytablecolor));
                        gL.setBackground(myActivity.getDrawable(R.drawable.mytablecolor));
                    }
                    else if(bigTable[x][y] == myActivity.getResources().getInteger(R.integer.opponentsTurn)){
                        //gL.setBackgroundColor(myActivity.getResources().getColor(R.color.opponenttablecolor));
                        gL.setBackground(myActivity.getDrawable(R.drawable.opponenttablecolor));
                    }
                    else{
                        //gL.setBackgroundColor(myActivity.getResources().getColor(R.color.indigo));
                        gL.setBackground(myActivity.getDrawable(R.drawable.littletablebackground));
                    }

                }

            }

            if(currentPlayer == myActivity.getResources().getInteger(R.integer.myTurn)){
                turnView.setImageResource(R.drawable.yourturn);
            }
            else if(currentPlayer == myActivity.getResources().getInteger(R.integer.opponentsTurn)){
                turnView.setImageResource(R.drawable.opponentsturn);
            }

            if(runnable != null){
                Handler handler = new Handler(myActivity.getMainLooper());
                handler.post(runnable);
            }

            quitButton.setEnabled(true);

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
            Context c = getActivity();
            int tilesLeft = 0;
            ContentProviderClient providerClient = c.getContentResolver().
                    acquireContentProviderClient(DBManager.CONTENTURI);
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

            currentPlayer = CPHandler.getCurrentPlayer(c,providerClient,gameName,opponentID);
            lastMove = CPHandler.getLastMove(c,providerClient,gameName);
            if(lastMove.length()!= 0) {
                tilesLeft = CPHandler.getTileCountForTable(c, providerClient,
                        lastMove.substring(1,lastMove.indexOf(',')), gameName);
            }
            if(tilesLeft <= 0){
                lastMove = "";
            }
            Log.d("reading","lastmove: "+lastMove);

            int gameState = CPHandler.getGameState(c,providerClient,gameName);

            if(gameState == c.getResources().getInteger(R.integer.gamestatelose)){
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        showAlertDialog(R.layout.you_lose_view);
                    }
                };
            }
            else if(gameState == c.getResources().getInteger(R.integer.gamestatequit)){
                runnable = new Runnable(){
                    @Override
                    public void run() {
                        showAlertDialog(R.layout.opponent_quit_view);
                    }
                };
            }

            return rows;
        }
    }


    private class Observer extends ContentObserver{

        public Observer(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);

            new DBReadAsyncTask().execute((String[])null);


        }
    }

}
