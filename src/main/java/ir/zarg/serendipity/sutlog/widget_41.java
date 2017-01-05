package ir.zarg.serendipity.sutlog;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import ir.zarg.serendipity.sutlog.classes.getinfo;
import ir.zarg.serendipity.sutlog.classes.login;

/**
 * Implementation of App Widget functionality.
 */
public class widget_41 extends AppWidgetProvider {


    private static final String REFRESH_CLICKED = "automaticWidgetRefreshButtonClick";
    private static final String LOGIN_CLICKED = "automaticWidgetLoginButtonClick";
    private boolean status;
    private boolean logined;
    private boolean premium;
    private String update_time;
    private int limit;
    private int usage;
    private int remainUsage;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_41);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);


    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews remoteViews;
        ComponentName sutlogWidget;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_41);
        sutlogWidget = new ComponentName(context, widget_41.class);
        remoteViews.setOnClickPendingIntent(R.id.widget_refresh_but,getPendingSelfIntent(context,REFRESH_CLICKED));
        remoteViews.setOnClickPendingIntent(R.id.widget_login_but, getPendingSelfIntent(context, LOGIN_CLICKED));
        getinfo info = new getinfo();
        info.update(context);
        readinfo(context);

            if (logined){
                info.update(context);
                readinfo(context);
                Log.d("widget", "widget updated?!");
                remoteViews.setTextViewText(R.id.widget_tv, remainUsage + " MB");
                remoteViews.setTextViewText(R.id.widget_tv_update,time());
                remoteViews.setProgressBar(R.id.widget_progressbar, limit, remainUsage / 2, false);
                remoteViews.setViewVisibility(R.id.widget_login_but, View.INVISIBLE);

            }else {

                remoteViews.setTextViewText(R.id.widget_tv, "");
                remoteViews.setTextViewText(R.id.widget_tv_update,"لطفا به اکانت خود در اپلیکیشن وارد شوید");
                remoteViews.setProgressBar(R.id.widget_progressbar, 100,  0, true);
                remoteViews.setViewVisibility(R.id.widget_login_but, View.INVISIBLE);
            }



        appWidgetManager.updateAppWidget(sutlogWidget, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        RemoteViews remoteViews;
        ComponentName watchWidget;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_41);
        watchWidget = new ComponentName(context, widget_41.class);



        if (LOGIN_CLICKED.equals(intent.getAction())){
            login login = new login();
            login.async(context);
        }

        if (REFRESH_CLICKED.equals(intent.getAction())) {
            getinfo info = new getinfo();
            info.update(context);
            readinfo(context);



                if (logined){
                    info.update(context);
                    readinfo(context);
                    Log.d("widget", "widget updated?!");
                    remoteViews.setTextViewText(R.id.widget_tv, remainUsage + " MB");
                    remoteViews.setTextViewText(R.id.widget_tv_update,time());
                    remoteViews.setProgressBar(R.id.widget_progressbar, limit, remainUsage / 2, false);
                    remoteViews.setViewVisibility(R.id.widget_login_but, View.INVISIBLE);

                }else {

                    remoteViews.setTextViewText(R.id.widget_tv, "");
                    remoteViews.setTextViewText(R.id.widget_tv_update,"لطفا به اکانت خود در اپلیکیشن وارد شوید");
                    remoteViews.setProgressBar(R.id.widget_progressbar, 100,  0, true);
                    remoteViews.setViewVisibility(R.id.widget_login_but, View.INVISIBLE);
                }



            appWidgetManager.updateAppWidget(watchWidget, remoteViews);


        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void readinfo(Context context) {
        SharedPreferences saved_credits = context.getSharedPreferences("SAVE_CREDITS", Context.MODE_PRIVATE);
        SharedPreferences saved_info = context.getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences saved_setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);

        logined = saved_credits.getBoolean("logined",false);
        status = saved_info.getBoolean("status", false);
        update_time = saved_info.getString("update_time", "11:11:11 1992/06/01");
        limit = saved_info.getInt("limit", 0);
        usage = saved_info.getInt("usage", 0);
        remainUsage = saved_info.getInt("usage_remain", 0);



    }

    public String time() {
        String time_passed = "none";
        String cur_time;
        long dif_in_mil = 0;
        long dif_in_sec = 0;
        SimpleDateFormat date_format = new SimpleDateFormat("HH:mm:ss yyyy/MM/dd");
        cur_time = date_format.format(new Date());

        try {
            Date past = date_format.parse(update_time);
            Date now = date_format.parse(cur_time);
            dif_in_mil = now.getTime() - past.getTime();
            dif_in_sec = dif_in_mil / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dif_in_sec < 3)
            time_passed = "همین الان آپدیت شده!";
        else if (dif_in_sec > 3 && dif_in_sec < 60)
            time_passed = String.format(" %d ثانیه پیش آپدیت شده!", dif_in_sec);
        else if (dif_in_sec > 60 && dif_in_sec < 3600)
            time_passed = String.format(" %d دقیقه پیش آپدیت شده!", dif_in_sec / 60);
        else if (dif_in_sec > 3600)
            time_passed = String.format(" %d ساعت پیش آپدیت شده!", dif_in_sec / 3600);
        return time_passed;
    }

}

