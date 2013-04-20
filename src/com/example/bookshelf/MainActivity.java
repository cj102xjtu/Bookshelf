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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
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

interface Msg2Engine
{
    public void excute(Engine engine);
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
    /** Messenger for communicating with service. */
    Messenger mService = null;
    /** Flag indicating whether we have called bind on the service. */
    boolean mIsBound;

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

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service. We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mService = new Messenger(service);
            Log.d(LOG_TAG, "connected with service");

            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                Message msg = Message.obtain(null, Engine.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);

                // Give it some value as an example.
                msg = Message.obtain(null, Engine.MSG_SET_VALUE,
                        this.hashCode(), 0);
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // As part of the sample, tell the user what happened.
            Toast.makeText(getApplicationContext(), "bind to service",
                    Toast.LENGTH_SHORT).show();

        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;

            // As part of the sample, tell the user what happened.
            Toast.makeText(getApplicationContext(), "unbind to service",
                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        // Establish a connection with the service. We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        bindService(new Intent(getApplicationContext(), Engine.class),
                mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null,
                            Engine.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                }
            }

            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

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
        doBindService();
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
                fragment = new TestPageFragment(mService);
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
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
    }

    public void updateBookList(ArrayList<String> avialibeBookList) {
        
        Toast.makeText(getApplicationContext(), "1: " + avialibeBookList.get(0) + " 2: " + avialibeBookList.get(1), Toast.LENGTH_LONG).show();
        
    }

}
