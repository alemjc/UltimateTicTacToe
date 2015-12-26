package com.games.ultimatetictactoe.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.games.ultimatetictactoe.app.content.GameContent;

/**
 * A fragment representing a list of Items.
 * <p>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class AcceptDeclineFragment extends Fragment implements AbsListView.OnItemClickListener {



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
    public static AcceptDeclineFragment newInstance() {
        AcceptDeclineFragment fragment = new AcceptDeclineFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AcceptDeclineFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_acceptdecline, container, false);


        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setEmptyView(view.findViewById(android.R.id.empty));
        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        new WaitingListAsyncFill().execute(new Object());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int pos = position;
        final Context context = getContext();
        if (null != context) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setMessage("Would you like to accept friend's invitation").
                    setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    GameContent.GameItem gameItem = (GameContent.GameItem)mAdapter.getItem(pos);
                                    String msgID = gameItem.id;
                                    String gameName = gameItem.gameName;

                                    GameContent.ITEMS.remove(pos);
                                    mAdapter = new ArrayAdapter<>(getActivity(),
                                            android.R.layout.simple_list_item_1, android.R.id.text1, GameContent.ITEMS);
                                    mListView.setAdapter(mAdapter);

                                    Intent intent = new Intent();
                                    intent.setAction(AcceptOrRejectRequestService.ACTION_ACCEPT_REQUEST);
                                    intent.putExtra(AcceptOrRejectRequestService.EXTRA_GAME_NAME,gameName);
                                    intent.putExtra(AcceptOrRejectRequestService.EXTRA_MSG_ID,msgID);
                                    (context).startService(intent);
                                }
                            }
                    ).setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    GameContent.GameItem gameItem = (GameContent.GameItem) mAdapter.getItem(pos);
                                    String msgID = gameItem.id;
                                    String gameName = gameItem.gameName;

                                    GameContent.ITEMS.remove(pos);
                                    mAdapter = new ArrayAdapter<>(getActivity(),
                                            android.R.layout.simple_list_item_1, android.R.id.text1, GameContent.ITEMS);
                                    mListView.setAdapter(mAdapter);

                                    Intent intent = new Intent();
                                    intent.setAction(AcceptOrRejectRequestService.ACTION_REJECT_REQUEST);
                                    intent.putExtra(AcceptOrRejectRequestService.EXTRA_GAME_NAME, gameName);
                                    intent.putExtra(AcceptOrRejectRequestService.EXTRA_MSG_ID, msgID);
                                    (context).startService(intent);
                                }
                    }).create().show();
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String id);
    }

    private class WaitingListAsyncFill extends AsyncTask<Object,Object,String[][]>{
        @Override
        protected String[][] doInBackground(Object... params) {
            Activity mActivity = getActivity();
            return CPHandler.getGameNamesWithOpponentsWithState(mActivity,mActivity.getContentResolver().
                    acquireContentProviderClient(DBManager.CONTENTURI),mActivity.getResources()
                                                                    .getInteger(R.integer.gamestateawaitingacceptance));
        }

        @Override
        protected void onPostExecute(String[][] strings) {
            super.onPostExecute(strings);
            if(strings != null || strings.length == 0) {
                for (int i = 0; i < strings.length; i++) {
                    GameContent.addItem(new GameContent.GameItem(strings[i][0], "", strings[i][1]));
                }

                // TODO: Change Adapter to display your content
                mAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, GameContent.ITEMS);
                mListView.setAdapter(mAdapter);
            }

            else{
                setEmptyText("List is empty");
            }


        }
    }

}
