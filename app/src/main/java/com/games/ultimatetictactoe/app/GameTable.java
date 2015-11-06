package com.games.ultimatetictactoe.app;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;


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
    private GridLayout nineByNineTable;
    private BigTableIndex bigTable[][];
    private enum PLAYER{PLAYER1,PLAYER2} // tile carries the player that played on the tile.
    private PLAYER currentPlayer;


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

        currentPlayer = PLAYER.PLAYER1;
        bigTable = new BigTableIndex[3][3];
        for(int i = 0; i < bigTable.length; i++){
            int startX = i;

            if(i > 0){
                startX = (bigTable[i-1][0]).getEndX()+1;
            }
            for(int j = 0; j < bigTable[i].length; j++){

                int startY = j;
                if(j > 0){
                    startY = (bigTable[i][j-1]).getEndY()+1;
                }

                bigTable[i][j] = new BigTableIndex(startX,startY, TableIndex.STATE.NONE);
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_game_table, container, false);
        nineByNineTable = (GridLayout) fragmentView.findViewById(R.id.tableGrid);
        int count = nineByNineTable.getChildCount();
        for(int i = 0; i < count; i++){
            nineByNineTable.getChildAt(i).setOnClickListener(tileListener);
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

        //Makes the move for the player.
        tile.setTag(TILEPLAYERSKEY, ""+currentPlayer);

        //TODO: Check if player won small table.
            //TODO: if player won small table check if play won big table.
        //TODO: If play didn't win small table check if this table is tied.
            //TODO if table is tied check if big table is also tied.


    }

}
