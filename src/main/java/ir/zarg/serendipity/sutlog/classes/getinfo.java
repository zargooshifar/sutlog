package ir.zarg.serendipity.sutlog.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.BoolRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


import ir.zarg.serendipity.sutlog.fragments.mainFragments.infoFragment;

public class getinfo {
    private String result = "";
    private String traffic_limit = "";
    private String traffic_usage = "";
    private String realName = null;
    private String reason = null;
    private String urlString = null;
    private String gUsername = null;
    private String response=null;
    private String bw_download = null;
    private String bw_upload = null;
    private String reset_date = null;
    private Boolean status=false;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;



    public getinfo(){
        String url = "http://172.16.16.1/IBSng/user/dialer/dialer_userinfo.php";
        this.urlString = url;
    }




    public void changepass(String nPass){
        String url = "http://172.16.16.1/IBSng/user/dialer/dialer_changepass.php?new_password="+nPass;
        this.urlString = url;
    }
    public String getResult(){
        return result;
    }

    public String getReason(){
        return reason;
    }

    public String getTraffic_limit(){
        return traffic_limit;
    }

    public String getTraffic_usage(){
        return traffic_usage;
    }

    public String getRealName(){
        return realName;
    }


    public String getUsername(){
        return gUsername;
    }



    public void update(Context activity){
        authenticate(activity);
        getinfo();
        if (result.equals("SUCCESS")){
            SharedPreferences saved_info = activity.getSharedPreferences("info", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = saved_info.edit();
            editor.putString("username",gUsername);
            editor.putString("name",realName);
            editor.putString("result",result);
            editor.putString("reason",reason);
            editor.putString("bw_download",bw_download);
            editor.putString("bw_upload",bw_upload);
            editor.putString("reset_date",reset_date);

            long usage = Long.parseLong(traffic_usage);
            usage = usage/1048576;
            int usagemb = Integer.parseInt(Long.toString(usage));
            long limit = Long.parseLong(traffic_limit);
            limit = limit/1048576;
            int limitmb = Integer.parseInt(Long.toString(limit));
            int remainUsage = (limitmb-usagemb);
            int data[] = new int[3];




            editor.putInt("limit",limitmb);
            editor.putInt("usage",usagemb);
            editor.putInt("usage_remain",remainUsage);
            Boolean state = response.indexOf("display") > 0 ;
            if (state){
                status = true;
            }else {
                status = false;
            }
            editor.putBoolean("status",status);

            String update_time = new SimpleDateFormat("HH:mm:ss yyyy/MM/dd").format(new Date());
            editor.putString("update_time",update_time);



            editor.commit();

        }




    }




    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        String endtag = null;
        String name = null;
        String value = null;
        String tag = null;

        try {
            event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                if(event == XmlPullParser.START_TAG) {
                    tag = myParser.getName();
                    //Log.d(TAG, "parseXMLAndStoreIt: ");
                    if (tag.equals("attribute")){
                        name = null;
                        value = null;
                    }


                } else if(event == XmlPullParser.TEXT) {
                    //System.out.println(tag + "  :  "  + myParser.getText());
                    if (tag.equals("name")){
                        name = myParser.getText();
                    }else if (tag.equals("value")){
                        value = myParser.getText();
                    }else if (tag.equals("result")){
                        result = myParser.getText();
                    }
                    tag = null;

                } else if(event == XmlPullParser.END_TAG) {
                    endtag = myParser.getName();
                    //System.out.println("endtag >>"  + endtag);

                    if (endtag.equals("attribute")) {
                        //System.out.println(name +"  :  " + value);

                        if (name.equals("name")){
                            realName = value;
                        }else if (name.equals("traffic_periodic_accounting_monthly_limit")){
                            traffic_limit = value;
                        }else if (name.equals("traffic_periodic_accounting_monthly_usage")){
                            traffic_usage = value;
                        }else if (name.equals("result")){
                            result = value;
                        }else if (name.equals("reason")){
                            reason = value;
                        }else if (name.equals("normal_username")){
                            gUsername = value;
                        }else if ( name.equals("bw_receive_rate")){
                            bw_download = value;
                        }else if (name.equals("bw_send_rate")){
                            bw_upload = value;
                        }else if(name.equals("traffic_periodic_accounting_monthly_reset")){
                            reset_date = value;
                        }


                        //conditions
                    }

                }


                event = myParser.next();
            }
            parsingComplete = false;
        }

        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getinfo(){
                Thread connection = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(urlString);
                            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                            conn.setReadTimeout(10000 /* milliseconds */);
                            conn.setConnectTimeout(15000 /* milliseconds */);
                            conn.setRequestMethod("GET");
                            conn.setDoInput(true);
                            conn.connect();

                            InputStream stream = conn.getInputStream();
                            xmlFactoryObject = XmlPullParserFactory.newInstance();
                            XmlPullParser myparser = xmlFactoryObject.newPullParser();

                            myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                            myparser.setInput(stream, null);

                            parseXMLAndStoreIt(myparser);

                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder total = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                total.append(line).append('\n');
                            }
                            response = total.toString();
                            stream.close();
                        }
                        catch (Exception e) {
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