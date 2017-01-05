package ir.zarg.serendipity.sutlog.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

/**
 * Created by Arash on 30/05/2016.
 */
public class ping {


    public static int respond(final ConnectivityManager cm) {

        final int[] returnValue = new int[1];

        Thread connection = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnected()) {
                    try {
                        URL url = new URL("http://172.16.16.1/IBSng/user/");
                        HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
                        //urlc.setRequestProperty("User-Agent", "test");
                        urlc.setRequestProperty("Connection", "close");
                        urlc.setConnectTimeout(3000); // mTimeout is in seconds
                        urlc.connect();
                        if (urlc.getResponseCode() == 200) {
                            returnValue[0]=  0;
                        } else {
                            returnValue[0] =  1;
                        }
                    } catch (IOException e) {
                        Log.i("warning", "Error checking internet connection", e);
                        returnValue[0] =  2;
                    }
                }else {
                    returnValue[0] = 3;
                }
            }
        });
        connection.start();
        try {
            connection.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return returnValue[0];
    }


}
