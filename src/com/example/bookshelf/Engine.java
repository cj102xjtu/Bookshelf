package com.example.bookshelf;

import org.json.JSONArray;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

interface Msg2Ui {
    public void excute(MainActivity activity);
}

public class Engine extends IntentService {

    static final String LOG_TAG = "Engine";

    HttpHandler mHttpHandler = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * When binding to the service, we return an interface to our messenger for
     * sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Engine() {
        super("Engine");
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "onHandleIntent");
        Bundle extras = intent.getExtras();
        if (extras != null) {
            EngineAction engineAction = (EngineAction) extras.get("ACTION");
            Messenger messenger = (Messenger) extras.get("MESSENGER");

            // do the action
            if (engineAction != null) {
                engineAction.excute(this, messenger);
            }


        }
    }

    public void getBooksInfo(Messenger messager) {
        // get user name from setting
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userName = preferences.getString(getString(R.string.setting_key), "");
        Log.d(LOG_TAG, "user name is: " + userName);
        // get info from server
        final JSONArray allBooks = HttpHandler.getAllBooks();
        final JSONArray usersBooks = HttpHandler.getUsersBook(userName);

        // update UI
        // send message back to UI
        try {
            messager.send(Message.obtain(null, 0, new Msg2Ui() {

                @Override
                public void excute(MainActivity activity) {
                    activity.updateBookList(allBooks, usersBooks);
                }
            }));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void loanOrReturnBook(String bookId, String userId, boolean loanBook, Messenger messager) {
        boolean result = false;
        if (bookId.length() != 0 && userId.length() != 0) {
            // post request to server
            result = HttpHandler.loanOrReturnBook(bookId, userId, loanBook);
        } else {
            Log.e(LOG_TAG, "bookId or userId is empty. bookId = " + bookId
                    + ". userId = " + userId);
        }

        // update UI
        if (result == true) {
            getBooksInfo(messager);
        } else {
            // pop up error message in UI
            try {
                messager.send(Message.obtain(null, 0, new Msg2Ui() {

                    @Override
                    public void excute(MainActivity activity) {
                        activity.showErrMsg();
                    }
                }));
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
}
