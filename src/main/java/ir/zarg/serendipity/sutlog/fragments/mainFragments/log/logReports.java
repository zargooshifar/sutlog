package ir.zarg.serendipity.sutlog.fragments.mainFragments.log;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import ir.zarg.serendipity.sutlog.classes.convert_date;

public class logReports {



    public static int repNum= 300;
    public static int pageNum=1;
    public int row=0;
    public String mac="";
    public String kill_reason="";
    public String login_time = "";
    public String logout_time = "";
    public String bytes_in = "";
    public String bytes_out = "";
    public String success = "";
    public String duration = "";
    public String ip = "";
    static int day_of_the_year_before;

    private  static String urlString = "http://172.16.16.1/IBSng/user/connection_log.php?show_reports=1&rpp="+repNum+"&page="+pageNum+"&kill=show__details_kill_reason&mac=show__details_mac&ip=show__details_station_ip&Bytes_OUT=show__bytes_out&Bytes_IN=show__bytes_in&Login_Time=show__login_time_formatted&Logout_Time=show__logout_time_formatted&Duration=show__duration_seconds&order_by=login_time&desc=on&view_options=8&Successful=show__successful&successful=All&tab1_selected=Conditions";
    ;


    private static XmlPullParserFactory xmlFactoryObject;
    public static volatile boolean parsingComplete = true;






    public static ArrayList<logDataFormat> getReports() {

        final ArrayList<logDataFormat> reports = new ArrayList<>();


        final int[] event = new int[1];
        final String[] endtag = {null};
        final String[] tag = {null};


        Thread connection = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream stream = conn.getInputStream();


                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myParser = xmlFactoryObject.newPullParser();
                    myParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myParser.setInput(stream, null);
                    int row = 0;
                    String mac = "";
                    String kill_reason = "";
                    String login_time = "";
                    String logout_time = "";
                    String bytes_in = "";
                    String bytes_out = "";
                    String success = "";
                    String duration = "";
                    String ip = "";
                    event[0] = myParser.getEventType();
                    while (event[0] != XmlPullParser.END_DOCUMENT) {
                        if (event[0] == XmlPullParser.START_TAG) {
                            tag[0] = myParser.getName();

                            if (tag[0].equals("elements")) {
                                //azinja shoro mishe
                            }
                        } else if (event[0] == XmlPullParser.TEXT) {

                            if (tag[0].equals("kill_reason")) {
                                kill_reason = myParser.getText();

                            } else if (tag[0].equals("mac")) {
                                mac = myParser.getText();
                            } else if (tag[0].equals("station_ip")) {
                                ip = myParser.getText();
                            } else if (tag[0].equals("bytes_out")) {
                                bytes_out = myParser.getText();
                            } else if (tag[0].equals("bytes_in")) {
                                bytes_in = myParser.getText();
                            } else if (tag[0].equals("login_time_formatted")) {
                                login_time = myParser.getText();
                            } else if (tag[0].equals("logout_time_formatted")) {
                                logout_time = myParser.getText();
                            } else if (tag[0].equals("duration_seconds")) {
                                duration = myParser.getText();
                            } else if (tag[0].equals("successful")) {
                                success = myParser.getText();
                            }
                            tag[0] = null;
                        } else if (event[0] == XmlPullParser.END_TAG) {
                            endtag[0] = myParser.getName();


                            if (endtag[0].equals("elements")) {

                                logDataFormat current = new logDataFormat();
                                current.login_success = success;
                                current.login_time = login_time;
                                current.logout_time = logout_time;
                                current.duration = duration;
                                current.download = bytes_in;
                                current.upload = bytes_out;
                                current.ip = ip;
                                current.mac = mac;
                                current.kill_reason = kill_reason;
                                if (check_date(login_time)){
                                    current.new_date = true;
                                }else{
                                    current.new_date = false;
                                }

                                reports.add(current);


                            }

                        }


                        event[0] = myParser.next();
                    }
                    parsingComplete = false;


                } catch (Exception e) {
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

        return reports;
    }


    public static Boolean check_date(String login_time){

        Boolean change = true;
        int now = get_day(login_time);

        if (now != day_of_the_year_before) {
            change = true;

        }else if (now == day_of_the_year_before){
            change =  false;
        }
        day_of_the_year_before = now;
        return change;
    }

    public static int get_day(String login_time){

        Date login_date = null;
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            login_date = date_format.parse(login_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(login_date);

        int day_of_the_year = cal.get(Calendar.DAY_OF_YEAR);

        return day_of_the_year;

    }

}