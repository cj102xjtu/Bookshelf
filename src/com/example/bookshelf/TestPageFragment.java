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

    private Button mAllBookButton = null;
    private Button mWarmUpTestButton = null;
    private Button mLoanBookButton = null;
    private Button mReturnBookButton = null;
    private EditText mTextFeild = null;

    public static final String ARG_SECTION_NUMBER = "section_number";

    public TestPageFragment() {
        Log.d(LOG_TAG, "Fragment constructor");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // create test view to test http request.
        View view = inflater.inflate(R.layout.test_page, container, false);
        mAllBookButton = (Button) view.findViewById(R.id.AllBookButton);
        mWarmUpTestButton = (Button) view.findViewById(R.id.WareUpTestButton);
        mLoanBookButton = (Button) view.findViewById(R.id.LoanBookButton);
        mReturnBookButton = (Button) view.findViewById(R.id.ReturnBookButton);
        mTextFeild = (EditText) view.findViewById(R.id.editText1);

        mAllBookButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Give it some value as an example.
                EngineAction action = new EngineAction(EngineAction.GET_BOOKS_INFO);
                
                ((MainActivity) getActivity()).sendMsg2Engine(action);

            }
        });
        
        mLoanBookButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                EngineAction action = new EngineAction(EngineAction.LOAN_A_BOOK, "2", "samme");
                ((MainActivity) getActivity()).sendMsg2Engine(action);
            }
        });
        
        mReturnBookButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                EngineAction action = new EngineAction(EngineAction.RETURN_A_BOOK, "2", "samme");
                ((MainActivity) getActivity()).sendMsg2Engine(action);
            }
        });

        mWarmUpTestButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                String str = oddNumbers(); 
                mTextFeild.setText(str);
            }
        });
        return view;

    }
    
    public boolean twoPowerNumber(long x)
    {
        return (x != 0) && ((x & (x - 1)) == 0);
    }
    
    public String revierseString (String str)
    {
        return new StringBuffer(str).reverse().toString();
    }
    
    public String repeatString(String str, int times)
    {
        return new String(new char[times]).replace("\0", str);
    }
    
    public String oddNumbers()
    {
        StringBuilder strBuilder = new StringBuilder();
        for(int i = 1; i <= 100; ++i)
        {
            if(i%2 != 0)
            {
                strBuilder.append(i);
                strBuilder.append(",");
            }
        }
        
        return strBuilder.toString();
    }
}