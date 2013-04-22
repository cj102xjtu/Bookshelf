package com.example.bookshelf;

import java.lang.ref.WeakReference;

import org.json.JSONArray;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.fedorvlasov.lazylist.ImageLoader;

abstract class Msg2Engine {
    public abstract void excute(Engine engine);
}

public class MainActivity extends FragmentActivity {

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
    LayoutInflater mInflater;
    ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageLoader = new ImageLoader(getApplicationContext());

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // get books information
        EngineAction action = new EngineAction(EngineAction.GET_BOOKS_INFO);
        sendMsg2Engine(action);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
        case Engine.MSG_GET_BOOKS_INFO:
            // As part of the sample, tell the user what happened.
            Toast.makeText(getApplicationContext(),
                    "message send from service", Toast.LENGTH_SHORT).show();
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

        ListSectionFragment mListSection1 = null;
        ListSectionFragment mListSection2 = null;
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
            if (position == 2) {
                fragment = new TestPageFragment();
                Bundle args = new Bundle();
                args.putInt(TestPageFragment.ARG_SECTION_NUMBER, position + 1);
                fragment.setArguments(args);
            }else if(position == 0)
            {
                mListSection1 = new ListSectionFragment();
                fragment = mListSection1;
            }
            else {
                mListSection2 = new ListSectionFragment();
                fragment = mListSection2;
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
                return getString(R.string.title_section1);
            case 1:
                return getString(R.string.title_section2);
            case 2:
                return getString(R.string.title_section3);
            }
            return null;
        }
    }

    public static class ListSectionFragment extends
            android.support.v4.app.ListFragment {
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {

            BookListAdapter allBooksListAdapter = new BookListAdapter(null,
                    ((MainActivity) getActivity()).mInflater,
                    ((MainActivity) getActivity()).mImageLoader);

            setListAdapter(allBooksListAdapter);

            super.onViewCreated(view, savedInstanceState);
        }

    }

    public void sendMsg2Engine(EngineAction engineAction) {
        Intent intent = new Intent(getApplicationContext(), Engine.class);
        // Create a new Messenger for the communication back

        intent.putExtra("MESSENGER", mMessenger);

        intent.putExtra("ACTION", engineAction);
        startService(intent);

    }

    public void updateBookList(JSONArray allBooksInfo, JSONArray userBook) {
        BookListAdapter bookListAdapter = (BookListAdapter)mSectionsPagerAdapter.mListSection1.getListAdapter();
        bookListAdapter.setData(allBooksInfo);
        bookListAdapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "info send back from engine",
                Toast.LENGTH_SHORT).show();
    }

}
