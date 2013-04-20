package com.example.bookshelf;

import android.os.Bundle;
import android.os.Message;
import android.os.MessageQueue;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import com.example.bookshelf.Msg2Engine;
import android.widget.EditText;

public class TestPageFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this fragment.
     */
    static private String LOG_TAG = "TestPageFragment";

    private Button AllBookButton = null;
    private Button LentBookButton = null;
    private Button LoanBookButton = null;
    private Button ReturnBookButton = null;
    private EditText TextFeild = null;
    private Messenger mService = null;

    public static final String ARG_SECTION_NUMBER = "section_number";

    public TestPageFragment(Messenger service) {
        Log.d(LOG_TAG, "Fragment constructor");
        mService = service;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // create test view to test http request.
        View view = inflater.inflate(R.layout.test_page, container, false);
        AllBookButton = (Button) view.findViewById(R.id.AllBookButton);
        LentBookButton = (Button) view.findViewById(R.id.LentBookButton);
        LoanBookButton = (Button) view.findViewById(R.id.LoanBookButton);
        ReturnBookButton = (Button) view.findViewById(R.id.ReturnBookButton);
        TextFeild = (EditText) view.findViewById(R.id.editText1);

        AllBookButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Give it some value as an example.
                Message msg = Message.obtain(null, Engine.MSG_SET_VALUE,
                        this.hashCode(), 3);
                ((MainActivity) getActivity()).sendMsg2Engine(msg);

            }
        });

        return view;

    }
}