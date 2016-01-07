package com.games.ultimatetictactoe.app;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameIntroFragment.OnGameIntroInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameIntroFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameIntroFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnGameIntroInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GameIntroFragment.
     */

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.newGame){
                mListener.onIntroInteraction(OnGameIntroInteractionListener.NEWGAME);
            }
            else if(v.getId() == R.id.continueAGame){
                mListener.onIntroInteraction(OnGameIntroInteractionListener.CONTINUE);
            }
            else if(v.getId() == R.id.gameInvitations){
                mListener.onIntroInteraction(OnGameIntroInteractionListener.INVITATIONS);
            }

        }
    };
    // TODO: Rename and change types and number of parameters
    public static GameIntroFragment newInstance(String param1, String param2) {
        GameIntroFragment fragment = new GameIntroFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public GameIntroFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        new GameInvitationsCounter().execute(new Object());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_game_intro, container, false);

        Button newGame = (Button) fragmentView.findViewById(R.id.newGame);
        newGame.setOnClickListener(onClickListener);

        Button continueGame = (Button) fragmentView.findViewById(R.id.continueAGame);
        continueGame.setOnClickListener(onClickListener);

        Button invitations = (Button) fragmentView.findViewById(R.id.gameInvitations);
        invitations.setOnClickListener(onClickListener);

        return fragmentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        try {
            mListener = (OnGameIntroInteractionListener) context;
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnGameIntroInteractionListener {
        // TODO: Update argument type and name
        String CONTINUE = "continue";
        String NEWGAME="new game";
        String INVITATIONS="invitations";
        void onIntroInteraction(String usersChoice);
    }


    private class GameInvitationsCounter extends AsyncTask<Object,Object,Boolean> {
        @Override
        protected Boolean doInBackground(Object... params) {
            Context c = getActivity();
            String gameNames[] = CPHandler.getGameNamesWithState(c,c.getContentResolver().
                    acquireContentProviderClient(DBManager.CONTENTURI),
                    getResources().getInteger(R.integer.gamestateawaitingacceptance));

            return (gameNames != null && gameNames.length > 0)? true:false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean){
                Toast.makeText(getActivity(), "You have new game invitations!", Toast.LENGTH_LONG);
            }
        }
    }

}
