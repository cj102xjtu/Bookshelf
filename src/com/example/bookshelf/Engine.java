package com.example.bookshelf;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

interface Msg2Ui {
    public void excute(MainActivity activity);
}

public class Engine extends IntentService {

    static final String LOG_TAG = "Engine";

    HttpHandler mHttpHandler = null;
    /** Holds last value set by a client. */
    int mValue = 0;

    /**
     * Command to the service to register a client, receiving callbacks from the
     * service. The Message's replyTo field must be a Messenger of the client
     * where callbacks should be sent.
     */
    static final int MSG_REGISTER_CLIENT = 4;

    /**
     * Command to the service to unregister a client, ot stop receiving
     * callbacks from the service. The Message's replyTo field must be a
     * Messenger of the client as previously given with MSG_REGISTER_CLIENT.
     */
    static final int MSG_UNREGISTER_CLIENT = 2;

    /**
     * Command to service to set a new value. This can be sent to the service to
     * supply a new value, and will be sent by the service to any registered
     * clients with the new value.
     */
    static final int MSG_SET_VALUE = 3;
    static final int MSG_GET_BOOKS_INFO = 1;
    static final int MSG_LOAN_A_BOOK = 2;
    static final int MSG_RETURN_A_BOOK = 3;

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
        if(extras != null)
        {
            EngineAction engineAction = (EngineAction)extras.get("ACTION");
            Messenger messenger = (Messenger) extras.get("MESSENGER");
            
            // do the action
            if(engineAction != null)
            {
                engineAction.excute(this);
            }
            
            
        }
    }

    public void getBooksInfo() {
        // get info from server
        HttpHandler.getAllBooks();
        HttpHandler.getUsersBook("samme");

        // update UI
    }

    public void loanBook(String bookId, String userId) {
        if (bookId.length() != 0 && userId.length() != 0) {
            // post request to server
            HttpHandler.loanOrReturnBook(bookId, userId, true);
        } else {
            Log.e(LOG_TAG, "bookId or userId is empty. bookId = " + bookId
                    + ". userId = " + userId);
        }
    }

    public void returnBook(String bookId, String userId) {
        if (bookId.length() != 0 && userId.length() != 0) {
            // post request to server
            HttpHandler.loanOrReturnBook(bookId, userId, false);
        } else {
            Log.e(LOG_TAG, "bookId or userId is empty. bookId = " + bookId
                    + ". userId = " + userId);
        }
    }
}
