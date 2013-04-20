package com.example.bookshelf;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.Toast;

abstract class Msg2Engine 
{
    public abstract void excute(Engine engine);    
}

public class MainActivity extends FragmentActivity  {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    static private String LOG_TAG = "MainActivity";
    /*************************************************************************
     * code to handle communication with engine
     *************************************************************************/

    /**
     * Handler of incoming messages from service.
     */
    static class IncomingHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        IncomingHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            } else {
                super.handleMessage(msg);
            }
        } 
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler(this));


    /*************************************************************************
     * code to handle UI
     *************************************************************************/

    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        Log.d(LOG_TAG, "onCreate exit");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
        case Engine.MSG_SET_VALUE:
            // As part of the sample, tell the user what happened.
            Toast.makeText(getApplicationContext(), "message send from service",
                    Toast.LENGTH_SHORT).show();
            Msg2Ui action = (Msg2Ui) msg.obj;
            action.excute(this);
            break;
        default:
            break;
        }
    }
    

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(LOG_TAG, "Fragment getItem. position: " + position);
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment fragment = null;
            if (position == 0) {
                fragment = new TestPageFragment();
                Bundle args = new Bundle();
                args.putInt(TestPageFragment.ARG_SECTION_NUMBER, position + 1);
                fragment.setArguments(args);
            } else {
                fragment = new ListSectionFragment();
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
            case 0:
                return getString(R.string.title_section1).toUpperCase();
            case 1:
                return getString(R.string.title_section2).toUpperCase();
            case 2:
                return getString(R.string.title_section3).toUpperCase();
            }
            return null;
        }
    }

    public static class ListSectionFragment extends
            android.support.v4.app.ListFragment {
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {

            // test code
            List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < 10; i++) {
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("firstKey", "FirstKey" + i);
                data.put("secondKey", "SecondKey" + i);
                myData.add(data);
            }

            setListAdapter(new SimpleAdapter(getActivity(), myData,
                    android.R.layout.simple_list_item_2, new String[] {
                            "firstKey", "secondKey" }, new int[] {
                            android.R.id.text1, android.R.id.text2 }));

            super.onViewCreated(view, savedInstanceState);
        }

    }
    
    public void sendMsg2Engine(Message msg)
    {   
        Intent intent = new Intent(getApplicationContext(), Engine.class);
        // Create a new Messenger for the communication back

        intent.putExtra("MESSENGER", mMessenger);

        intent.putExtra("ACTION", Engine.MSG_REGISTER_CLIENT);
        startService(intent);
        
        
    }

    public void updateBookList(ArrayList<String> avialibeBookList) {
        
        Toast.makeText(getApplicationContext(), "1: " + avialibeBookList.get(0) + " 2: " + avialibeBookList.get(1), Toast.LENGTH_LONG).show();
        
    }

}
