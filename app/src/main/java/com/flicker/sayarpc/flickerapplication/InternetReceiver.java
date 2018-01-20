package com.flicker.sayarpc.flickerapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by narendra on 13/06/16.
 * This class is for checking whether internet
 * is available or not,it will also check if there is
 * any network changes or not.
 */
public class InternetReceiver extends BroadcastReceiver {

    //public static InternetReceiverListener internetReceiverListener;
    Context context;
   static NetworkInfo activeNetworkInfo;
    public InternetReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        EventBus.getDefault().post(new MessageEvent(isConnected));

//        if (internetReceiverListener != null) {
//            internetReceiverListener.onNetworkConnectionChanged(isConnected);
//        }
    }

    /**
     * Function for checking Internet is available or not.
     * @return
     */
    public  static boolean isConnected() {

        ConnectivityManager
                cm = (ConnectivityManager) FlickerApplication.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

             activeNetworkInfo = cm.getActiveNetworkInfo();


        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

//    public interface InternetReceiverListener {
//        void onNetworkConnectionChanged(boolean isConnected);
//    }

}
