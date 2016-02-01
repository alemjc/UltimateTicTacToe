package com.games.ultimatetictactoe.app;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.games.ultimatetictactoe.app.content.GameContent;
import java.util.ArrayList;
import java.util.Calendar;

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

    private static final String ARG_PARAM1 = "param1";

    private Firebase fB;
    private ArrayList<String> otherUsers;
    public static String NEWGAME = "new game";
    public static String CONTINUE = "continue";

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


    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            String phoneNumber = telephonyManager.getLine1Number();

            if(phoneNumber == null){
                //TODO: show user an informative message.
                return;
            }
            DataSnapshot usersSnapShot = dataSnapshot.child("/users");


            int count = 0;
            for(DataSnapshot dataSnapshot1:usersSnapShot.getChildren()){
                if(!dataSnapshot1.getKey().equals(phoneNumber)){
                    otherUsers.add(dataSnapshot1.getKey());
                    count++;
                }
            }

            new ContactsAsyncTask().execute((String[])(otherUsers.toArray(new String[count])));

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            //TODO: show user an informative message.
        }
    };

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
        fB = new Firebase(getActivity().getString(R.string.fireBaseDB));
        otherUsers = new ArrayList<>();


    }

    @Override
    public void onResume() {
        super.onResume();
        GameContent.ITEMS.clear();
        GameContent.ITEM_MAP.clear();

        if (getArguments() != null) {

            usersChoice = getArguments().getString(ARG_PARAM1);

            if(usersChoice.equals(NEWGAME)){
                fB.addValueEventListener(valueEventListener);
            }
            else if(usersChoice.equals(CONTINUE)){
                new LocalAsyncTasks().execute(new Object());
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        View emptyView = view.findViewById(android.R.id.empty);
        //(mListView).setAdapter(mAdapter);
        mListView.setEmptyView(emptyView);


        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnGameListFragmentInteractionListener) activity;
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            if(usersChoice.equals(NEWGAME)){
                Activity mActivity = (Activity)mListener;
                TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                String phoneNumber = telephonyManager.getLine1Number();
                Bundle extra = new Bundle();
                StringBuilder message = new StringBuilder();
                extra.putString(mActivity.getString(R.string.asyncbundleintent),mActivity.getString(R.string.asyncsendintent));
                //message.putString("to",GameContent.ITEMS.get(position).userName);
                //message.putString("from",phoneNumber);
                //message.putString("subject",mActivity.getString(R.string.gamerequest));
                //message.putString("data",GameContent.ITEMS.get(position).gameName+"\n");
                message.append(GameContent.ITEMS.get(position).userName);
                message.append("\n");
                message.append(phoneNumber);
                message.append("\n");
                message.append(mActivity.getString(R.string.gamerequest));
                message.append("\n");
                message.append(GameContent.ITEMS.get(position).gameName);

                extra.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,true);
                extra.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE,true);
                extra.putString(mActivity.getString(R.string.asyncmessage),message.toString());
                mActivity.getContentResolver().requestSync(((MainTicTacToeActivity)mActivity).account,MainTicTacToeActivity.AUTHORITY,extra);
                Toast.makeText(mActivity,"Game invitation sent to recepient",Toast.LENGTH_LONG).show();
                mActivity.onBackPressed();
            }
            else {
                mListener.onGameListFragmentInteraction(GameContent.ITEMS.get(position).userName, GameContent.ITEMS.get(position).gameName);
            }
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
        void onGameListFragmentInteraction(String userName,String gameName);
    }

    private class ContactsAsyncTask extends AsyncTask<String,Object, Cursor>{
        private String SELECTION = ContactsContract.Data.MIMETYPE+ " = ?"+" AND "+ContactsContract.Data.DATA1 +" in ";
        private final String[] PROJECTION = {ContactsContract.Data.DATA1,ContactsContract.Data.DISPLAY_NAME};
        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);

            if(cursor == null || cursor.getCount() <= 0){
                if(cursor != null)
                    cursor.close();
                setEmptyText("List is empty");
                return;
            }

            cursor.moveToFirst();
            Log.d("","size: "+cursor.getCount());
            while(!cursor.isAfterLast()){
                Log.d("","WHAT HAPPENED!!!!!!!!!!!!!!!!!!!!!!!!");
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA1));
                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Calendar calendar = Calendar.getInstance();
                GameContent.addItem(new GameContent.GameItem(displayName,calendar.getTimeInMillis()+""));
                cursor.moveToNext();
            }

            mAdapter = new ArrayAdapter<>(getActivity(),
                    R.layout.simple_list, R.id.list_item, GameContent.ITEMS);
            mListView.setAdapter(mAdapter);

            cursor.close();


        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
        }

        private void completeSQL(int l){
            StringBuilder questionMarks = new StringBuilder();

            for(int i = 0; i < l; i++){
                if(i != l-1){
                    questionMarks.append("?,");
                }
                else{
                    questionMarks.append("?");
                }

            }
            Log.d("",questionMarks.toString());


            SELECTION+="("+questionMarks+")";

        }


        @Override
        protected Cursor doInBackground(String... params) {

            if(params == null){
                return null;
            }
            else if(params.length == 0){
                return null;
            }

            String sqlParams[] = new String[params.length+1];
            sqlParams[0] = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;

            for(int i = 1; i < sqlParams.length; i++){
                sqlParams[i] = params[i-1];
            }

            completeSQL(params.length);
            return getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI,PROJECTION,SELECTION,
                   sqlParams,null);
        }
    }



    private class LocalAsyncTasks extends AsyncTask<Object,Object,String[][]>{
        @Override
        protected String[][] doInBackground(Object... params) {
            Context c = (Context)mListener;
            int states[] = {c.getResources().getInteger(R.integer.gamestateongoing),c.getResources().getInteger(R.integer.gamestatelose),c.getResources().getInteger(R.integer.gamestatequit)};
            return CPHandler.getGameNamesWithOpponentsWithStates(c,c.getContentResolver().acquireContentProviderClient(DBManager.CONTENTURI)
                    ,states);
        }

        @Override
        protected void onPostExecute(String[][] strings) {
            super.onPostExecute(strings);
            if(strings != null && strings.length > 0) {
                for (int i = 0; i < strings.length; i++) {
                    GameContent.addItem(new GameContent.GameItem(strings[i][0], strings[i][1]));
                }
                mAdapter = new ArrayAdapter<>(getActivity(),
                        R.layout.simple_list, R.id.list_item, GameContent.ITEMS);
                mListView.setAdapter(mAdapter);
            }
            else{
                setEmptyText("List is empty");
            }


        }
    }

}
