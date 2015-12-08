package com.games.ultimatetictactoe.app;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;


import com.games.ultimatetictactoe.app.dummy.GameContent;

/**
 * A fragment representing a list of Items.
 * <p>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnGameListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    public static String NEWGAME = "new game";
    public static String CONTINUE = "continue";


    // TODO: Rename and change types of parameters
    private String usersChoice;


    private OnGameListFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static ItemFragment newInstance(String param1) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            usersChoice = getArguments().getString(ARG_PARAM1);
            if(usersChoice.equals(NEWGAME)){

            }
            else if(usersChoice.equals(CONTINUE)){
                new localAsyncTask().execute(new Object());
            }

        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        //(mListView).setAdapter(mAdapter);
        setEmptyText("List is empty");

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnGameListFragmentInteractionListener) context;
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            if(usersChoice.equals(NEWGAME)){
                Activity mActivity = (Activity)mListener;
                Bundle extra = new Bundle();
                extra.putString("to",GameContent.ITEMS.get(position).id);
                extra.putString(mActivity.getString(R.string.asyncBundlesubject),mActivity.getString(R.string.asyncsendsubjecttype));
                Bundle msgBundle = new Bundle();
                msgBundle.putString("message",GameContent.ITEMS.get(position).gameName+"\n"+mActivity.getString(R.string.gamerequest));
                extra.putBundle(mActivity.getString(R.string.asyncmessagebundle),msgBundle);
                mActivity.getContentResolver().requestSync(MainTicTacToeActivity.createSyncAccount(mActivity),MainTicTacToeActivity.AUTHORITY,extra);
            }
            mListener.onGameListFragmentInteraction(GameContent.ITEMS.get(position).id+" "+
                                                    GameContent.ITEMS.get(position).gameName);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
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
    public interface OnGameListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onGameListFragmentInteraction(String content);
    }

    private class localAsyncTask extends AsyncTask<Object,Object,String[][]>{
        @Override
        protected String[][] doInBackground(Object... params) {
            Context c = (Context)mListener;
            return DBManager.CPHandler.getGameNamesWithOpponentsWithState(c,c.getResources().getInteger(R.integer.gamestateongoing));
        }

        @Override
        protected void onPostExecute(String[][] strings) {
            super.onPostExecute(strings);

            for(int i = 0; i < strings.length; i++){
                GameContent.addItem(new GameContent.GameItem(strings[i][0],"",strings[i][1]));
            }

            mAdapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, GameContent.ITEMS);
            mListView.setAdapter(mAdapter);
        }
    }

}
