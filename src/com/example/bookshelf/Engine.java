package com.example.bookshelf;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.R.string;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

interface Msg2Ui{
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
    static final int MSG_HTTP_OPERATION = 1;


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
        int action = intent.getIntExtra("ACTION", MSG_HTTP_OPERATION);
        if(action == MSG_REGISTER_CLIENT)
        {
            Log.d(LOG_TAG, "do get all books");
//            HttpHandler.getAllBooks();
            Bundle extras = intent.getExtras();
            final ArrayList<String> testList = new ArrayList<String>();
            testList.add("this is arg1");
            testList.add("this is arg2");
            if (extras != null) {
              Messenger messenger = (Messenger) extras.get("MESSENGER");
              Message msg = Message.obtain(null, MSG_SET_VALUE, new Msg2Ui() {
                
                @Override
                public void excute(MainActivity activity) {
                    activity.updateBookList(testList);
                    
                }
            });
              try {
                  messenger.send(msg);
              } catch (android.os.RemoteException e1) {
                Log.w(getClass().getName(), "Exception sending message", e1);
              }
        }}
    }

    
    public void getbooks()
    {
        HttpHandler.getAllBooks();
    }
}
