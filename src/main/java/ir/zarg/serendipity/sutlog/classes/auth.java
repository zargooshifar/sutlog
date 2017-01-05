package ir.zarg.serendipity.sutlog.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by yaghoub on 30/05/2016.
 */
public class auth {

    public void authenticate(Context activity)  {
        SharedPreferences saved_data = activity.getSharedPreferences("SAVE_CREDITS", Context.MODE_PRIVATE);
        final String gUsername = saved_data.getString("username","");
        final String gPassword = saved_data.getString("password","");

        Thread connection = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url;
                    //Authentication par
                    //Toast.makeText(this,"Getting Ready for Authentication", Toast.LENGTH_SHORT).show();

                    url = new URL("http://172.16.16.1/IBSng/user/dialer/dialer_auth.php");

                    String userPass = gUsername + ":" + gPassword;
                    byte[] upencode = userPass.getBytes("UTF-8");
                    String encoding = Base64.encodeToString(upencode,Base64.DEFAULT);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    //Toast.makeText(MainActivity.this,"Authentication...", Toast.LENGTH_SHORT).show();
                    connection.setRequestProperty("Authorization", "Basic " + encoding);
                    connection.connect();
                    connection.getResponseMessage();
                    //Toast.makeText(MainActivity.this,"Authentication Response: "+ connection.getResponseMessage(), Toast.LENGTH_SHORT).show();
                    //Getting username information
                    //Toast.makeText(MainActivity.this,"Getting Information...", Toast.LENGTH_SHORT).show();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        connection.start();
        try {
            connection.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }




}
