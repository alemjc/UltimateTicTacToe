package com.games.ultimatetictactoe.app;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


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
    private RelativeLayout gameTableLayout;
    private TableIndex bigTable[][];
    private enum PLAYER{PLAYER1,PLAYER2} // tile carries the player that played on the tile.
    private PLAYER currentPlayer;
    private String lastMove;



    /*
        gameChoice parameter will tell GameTable whether to continue an already started game or start a new game.
        possible values for gameChoice are: continue, new.

        gameName this will have the name of name iff gameChoice is filled out.

     */
    private String gameChoice;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameChoice = getArguments().getString(ARG_PARAM1);
            gameName = getArguments().getString(ARG_PARAM2);
        }

        lastMove = null;
        currentPlayer = PLAYER.PLAYER1;
        bigTable = new TableIndex[3][3];
        int count = 0;
        for(int i = 0; i < bigTable.length; i++){

            for(int j = 0; j < bigTable[i].length; j++){

                bigTable[i][j] = new TableIndex(Index.STATE.NONE);
                count++;
            }

        }
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
        //TODO: Check if this is a valid move done by the player. if this is not a valid move make a Toast and return.
        if(tile.getTag(TILEPLAYERSKEY) != null){ // some one already played this tile so can't play on a tile twice
            Toast.makeText((Activity)mListener,"Invalid move",Toast.LENGTH_LONG);
            return;
        }
        GridLayout parent = (GridLayout)tile.getParent();
        if(!parent.getTag().equals("t"+lastMove)){ // if the current table is not the table that was assigned base on last move.
            Toast.makeText((Activity)mListener,"Invalid move",Toast.LENGTH_LONG);
            return;
        }

        //Makes the move for the player.
        tile.setTag(TILEPLAYERSKEY, currentPlayer);
        lastMove = (String)tile.getTag(); // setting last move to be current move.

        TableIndex tableIndex [][] = new TableIndex[3][3];
        int index = 0;
        String tableCoordinate= ((String)parent.getTag()).substring(1);

        for(int i = 0; i < tableIndex.length; i++){

            for(int j = 0; j < tableIndex[i].length; j++){
                View tv = parent.getChildAt(index);
                switch ((PLAYER)tv.getTag(TILEPLAYERSKEY)){
                    case PLAYER1:
                        tableIndex[i][j] = new TableIndex(Index.STATE.PLAYER1);
                        break;
                    case PLAYER2:
                        tableIndex[i][j] = new TableIndex(Index.STATE.PLAYER2);
                        break;

                    default:
                        tableIndex[i][j] = new TableIndex(Index.STATE.NONE);
                }
            }

        }


        if(true)//TODO: Check if player won small table. Pass tableIndex to checker method.
        {
            int i = Integer.parseInt(tableCoordinate.charAt(0)+"");
            int j = Integer.parseInt(tableCoordinate.charAt(1)+"");
            switch (currentPlayer){
                case PLAYER1:
                    bigTable[i][j].setState(Index.STATE.PLAYER1);
                    break;
                case PLAYER2:
                    bigTable[i][j].setState(Index.STATE.PLAYER2);
                    break;
            }

            //TODO: if player won small table check if play won big table.
        }
        //TODO: If play didn't win small table check if this table is tied.
            //TODO if table is tied check if big table is also tied.


        switch(currentPlayer){
            case PLAYER1:
                currentPlayer = PLAYER.PLAYER2;

                break;
            case PLAYER2:
                currentPlayer = PLAYER.PLAYER1;
        }




    }

}
