package com.example.bookshelf;

import java.lang.ref.WeakReference;

import org.json.JSONArray;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
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
    SharedPreferences preferences = null;

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

        // create Image loader and inflater for list view load image from
        // Internet
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

        // create preferences
        preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        case R.id.menu_settings:
            Intent i = new Intent(this, Preferences.class);
            startActivity(i);
            break;

        }

        return true;
    }

    public void sendMsg2Engine(EngineAction engineAction) {
        Intent intent = new Intent(getApplicationContext(), Engine.class);
        intent.putExtra("MESSENGER", mMessenger);
        intent.putExtra("ACTION", engineAction);
        startService(intent);
    }

    public void updateBookList(JSONArray allBooksInfo, JSONArray userBook) {
        Log.d(LOG_TAG, "book lists send back from engine");
        // update two book lists
        mSectionsPagerAdapter.mAllBooksListSection.updateList(allBooksInfo);
        mSectionsPagerAdapter.mLoanBooksListSection.updateList(userBook);
    }

    public void showErrMsg() {
        Toast.makeText(getApplicationContext(), getString(R.string.error_msg),
                Toast.LENGTH_LONG).show();
    }

    private void handleMessage(Message msg) {
        Msg2Ui action = (Msg2Ui) msg.obj;
        action.excute(this);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        static final String LIST_TYPE = "ListType";

        ListSectionFragment mAllBooksListSection = null;
        ListSectionFragment mLoanBooksListSection = null;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(LOG_TAG, "Fragment getItem. position: " + position);

            Fragment fragment = null;

            // create all books list view
            if (position == 0) {
                mAllBooksListSection = new ListSectionFragment();
                Bundle args = new Bundle();
                args.putInt(LIST_TYPE, ListSectionFragment.ALL_BOOKS_LIST);
                mAllBooksListSection.setArguments(args);
                fragment = mAllBooksListSection;
            } else if (position == 1) { // create loan book list view
                mLoanBooksListSection = new ListSectionFragment();
                Bundle args = new Bundle();
                args.putInt(LIST_TYPE, ListSectionFragment.LOAN_BOOKS_LIST);
                mLoanBooksListSection.setArguments(args);
                fragment = mLoanBooksListSection;
            } else { // create test view
                fragment = new TestPageFragment();
                Bundle args = new Bundle();
                args.putInt(TestPageFragment.ARG_SECTION_NUMBER, position + 1);
                fragment.setArguments(args);
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
            android.support.v4.app.ListFragment implements OnItemClickListener {

        final static private int ALL_BOOKS_LIST = 0;
        final static private int LOAN_BOOKS_LIST = 1;
        private JSONArray mListContent = null;
        private int mListType = ALL_BOOKS_LIST;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            mListType = getArguments() != null ? getArguments().getInt(
                    SectionsPagerAdapter.LIST_TYPE) : ALL_BOOKS_LIST;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            Log.d(LOG_TAG, "ListSectionFragment onViewCreated");
            BaseAdapter listAdapter = null;

            if (mListType == ALL_BOOKS_LIST) {
                listAdapter = new AllBooksListAdapter(mListContent,
                        ((MainActivity) getActivity()).mInflater,
                        ((MainActivity) getActivity()).mImageLoader);
            } else {
                listAdapter = new LoanBooksListAdapter(mListContent,
                        ((MainActivity) getActivity()).mInflater,
                        ((MainActivity) getActivity()).mImageLoader);
            }

            setListAdapter(listAdapter);

            // handle item click event
            getListView().setOnItemClickListener(this);

            super.onViewCreated(view, savedInstanceState);
        }

        public void updateList(JSONArray listConent) {
            mListContent = listConent;
            LoanBooksListAdapter bookListAdapter = (LoanBooksListAdapter) getListAdapter();
            bookListAdapter.setData(mListContent);
            bookListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                final long id) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        String userName = ((MainActivity) getActivity()).preferences
                                .getString(getString(R.string.setting_key), "");

                        // Yes button clicked loan or return a book
                        EngineAction action = null;
                        if (mListType == ALL_BOOKS_LIST) {
                            action = new EngineAction(EngineAction.LOAN_A_BOOK,
                                    Long.toString(id), userName);
                        } else {
                            action = new EngineAction(
                                    EngineAction.RETURN_A_BOOK,
                                    Long.toString(id), userName);
                        }

                        ((MainActivity) getActivity()).sendMsg2Engine(action);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    view.getContext());
            String dailogString = null;
            if (mListType == ALL_BOOKS_LIST) {
                dailogString = getString(R.string.borrow_book_dialog_msg);
            } else {
                dailogString = getString(R.string.return_book_dialog_msg);
            }
            builder.setMessage(dailogString)
                    .setPositiveButton(getString(R.string.yes_btn_text),
                            dialogClickListener)
                    .setNegativeButton(getString(R.string.no_btn_text),
                            dialogClickListener).show();

        }

    }

}
