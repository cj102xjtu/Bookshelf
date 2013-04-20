package com.example.bookshelf;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.R.string;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

interface Msg2Ui{
    public void excute(MainActivity activity);
}

public class Engine extends IntentService {

    /** Keeps track of the registered client. */
    Messenger mClient = null;
    /** Holds last value set by a client. */
    int mValue = 0;

    /**
     * Command to the service to register a client, receiving callbacks from the
     * service. The Message's replyTo field must be a Messenger of the client
     * where callbacks should be sent.
     */
    static final int MSG_REGISTER_CLIENT = 1;

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

    /**
     * Handler of incoming messages from clients.
     */
    static class IncomingHandler extends Handler {
        private final WeakReference<Engine> mEngine;

        IncomingHandler(Engine engine) {
            mEngine = new WeakReference<Engine>(engine);
        }

        @Override
        public void handleMessage(Message msg) {
            Engine engine = mEngine.get();
            if (engine != null) {
                engine.handleMsg(msg);
            } else {
                super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler(this));

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    /**
     * When binding to the service, we return an interface to our messenger for
     * sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    private void handleMsg(Message msg) {
        switch (msg.what) {
        case MSG_REGISTER_CLIENT:
            mClient = msg.replyTo;
            break;
        case MSG_UNREGISTER_CLIENT:
            mClient = null;
            break;
        case MSG_SET_VALUE:
            mValue = msg.arg1;
            testFunction(msg);
//            try {
//                mClient.send(Message.obtain(null, MSG_SET_VALUE, mValue, 0));
//                
//            } catch (RemoteException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
            break;
        default:
            break;
        }
    }

    public Engine() {
        super("Engine");
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub

    }
    
    private void testFunction(Message msg)
    {
        final ArrayList<String> testList = new ArrayList<String>();
        testList.add(String.valueOf(msg.arg1));
        testList.add(String.valueOf(msg.arg2));
        
        try {
            mClient.send(Message.obtain(null, MSG_SET_VALUE, new Msg2Ui() {
                
                @Override
                public void excute(MainActivity activity) {
                    activity.updateBookList(testList);
                    
                }
            }));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
