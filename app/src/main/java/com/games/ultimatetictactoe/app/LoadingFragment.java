package com.games.ultimatetictactoe.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoadingFragment.LoadingFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoadingFragment extends Fragment {

    public static final String LOADINGFRAGMENTAUTHORITY = "com.games.ultimatetictactoe.app";
    public static final Uri DONELOADINGURI = Uri.parse("loading://"+LOADINGFRAGMENTAUTHORITY+"done");
    private LoadingFragmentInteractionListener mListener;
    private LoaderObserver loaderObserver;
    private static final String tokenRequestSentKey = "isTokenRequestSent";


    public static LoadingFragment newInstance() {
        LoadingFragment fragment = new LoadingFragment();

        return fragment;
    }

    public LoadingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();
        Activity activity = getActivity();
        Handler handler = new Handler(activity.getMainLooper());
        loaderObserver = new LoaderObserver(handler);
        activity.getContentResolver().registerContentObserver(DONELOADINGURI,false,loaderObserver);
        Intent registrationServiceIntent = new Intent(activity,RegistrationService.class);
        SharedPreferences preferences = activity.getSharedPreferences(activity.getPackageName()+"_preferences",
                Context.MODE_PRIVATE|Context.MODE_MULTI_PROCESS);

        SharedPreferences.Editor editor = preferences.edit();
        String token = preferences.getString(activity.getString(R.string.firebasetokenkey),null);
        boolean tokenRequestSent = preferences.getBoolean(tokenRequestSentKey,false);

        if(token == null || !tokenRequestSent){
            registrationServiceIntent.putExtra(RegistrationService.INTENT_EXTRA_ACCOUNT,((MainTicTacToeActivity)activity).getAccount());
            activity.startService(registrationServiceIntent);
            editor.putBoolean(tokenRequestSentKey,true);
            editor.apply();
        }
        else{

            SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor2 = sharedPreferences.edit();
            editor2.putBoolean(getString(R.string.first_time),false);
            editor2.apply();

            mListener.loadingFragmentInteraction();
        }
    }

    @Override
    public void onPause() {
        Activity activity = getActivity();
        activity.getContentResolver().unregisterContentObserver(loaderObserver);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (LoadingFragmentInteractionListener) activity;
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
    public interface LoadingFragmentInteractionListener {
        void loadingFragmentInteraction();
    }

    private class LoaderObserver extends ContentObserver{
        public LoaderObserver(Handler handler){
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Activity activity = (Activity)mListener;
            Intent registrationServiceIntent = new Intent(activity,RegistrationService.class);
            SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.first_time),false);
            editor.commit();

            activity.startService(registrationServiceIntent);
            mListener.loadingFragmentInteraction();
        }
    }

}
