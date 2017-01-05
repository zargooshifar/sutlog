package ir.zarg.serendipity.sutlog.fragments.mainFragments;


import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.firebase.FirebaseApp;
//import com.google.android.gms.appindexing.AndroidAppUri;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ir.zarg.serendipity.sutlog.classes.getinfo;
import ir.zarg.serendipity.sutlog.classes.auth;

import ir.zarg.serendipity.sutlog.R;
import ir.zarg.serendipity.sutlog.classes.ping;
/**
 * A simple {@link Fragment} subclass.
 */
public class infoFragment extends Fragment {
    public float angle  = 0;
    public String gUsername=null;
    public String gPassword=null;
    public String gResult=null;
    public String gUsage=null;
    public String gLimit=null;
    public String gRealName=null;
    public String result=null;
    public String update_time;
    private String bw_download = null;
    private String bw_upload = null;
    private String reset_date = null;
    public int limit=0;
    public int usage = 0;
    public int remainUsage=0;
    public TextView tvRemain;
    public TextView tvName;
    public TextView tvUsername;
    public TextView tvLimit;
    public TextView tvUpTime;
    private TextView tv_bw_download = null;
    private TextView tv_bw_upload = null;
    private TextView tv_reset_date = null;
    public RoundCornerProgressBar usageProgress;
    public ir.zarg.serendipity.sutlog.classes.getinfo getinfo = new getinfo();
    public ir.zarg.serendipity.sutlog.classes.auth auth = new auth();
    public ir.zarg.serendipity.sutlog.classes.ping ping = new ping();
    public Snackbar snackbar;
    public TextView tvStatus;
    public ImageView imStatus;
    public Boolean status;
    public Boolean change=true;
    private ProgressDialog dialog;
//    FloatingActionButton fab;
    public infoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View fragmentView =  inflater.inflate(R.layout.fragment_info, container, false);


        tvRemain = (TextView) fragmentView.findViewById(R.id.tvRemain);
        tvName = (TextView) fragmentView.findViewById(R.id.tvName);
        tvUsername = (TextView) fragmentView.findViewById(R.id.tvUsername);
        tvLimit = (TextView) fragmentView.findViewById(R.id.tvLimit);
        tvUpTime = (TextView) fragmentView.findViewById(R.id.up_time);
        usageProgress = (RoundCornerProgressBar) fragmentView.findViewById(R.id.usageProgress);
        snackbar = Snackbar.make(fragmentView, "خطا در اتصال به سرور", Snackbar.LENGTH_SHORT);
        tvStatus = (TextView) fragmentView.findViewById(R.id.tvStatus);
        imStatus = (ImageView) fragmentView.findViewById(R.id.ivStatus);

        tv_bw_download = (TextView) fragmentView.findViewById(R.id.tvSpeedDownload);
        tv_bw_upload = (TextView) fragmentView.findViewById(R.id.tvSpeedUpload);
        tv_reset_date = (TextView) fragmentView.findViewById(R.id.tvReset);



        async();


        return fragmentView;
    }


    public void refreshinfo(){
        final ir.zarg.serendipity.sutlog.classes.ping ping = new ping();
        final ir.zarg.serendipity.sutlog.classes.getinfo getinfo = new getinfo();
        final ir.zarg.serendipity.sutlog.classes.auth auth = new auth();

        int connectionAvalable = ping.respond((ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE));

        switch (connectionAvalable){

            case 0: { //case 0 --->>> has ping to server
                // try to auth user and get info
                Thread connect = new Thread(new Runnable() {
                    @Override
                    public void run() {


                        auth.authenticate(getActivity());
                        getinfo.getinfo();
                        result = getinfo.getResult();
                    }
                });
                connect.start();
                try {
                    connect.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    snackbar.setText("خطا در اتصال به سرور");
                    snackbar.show();
                }

                if (result.equals("SUCCESS")) {

                    getinfo.update(getActivity());
                    change=true;



                } else if (result.equals("FAILURE")) {
                    snackbar.setText("کلمه عبور عوض شده؟!");
                    snackbar.show();
                } else if (result.equals("")) {
                    snackbar.setText("پاسخی از سرور دریافت نشد!");
                    snackbar.show();
                }
                break;
            }
            case 1: {
                snackbar.setText("سرور در حال حاضر در دسترس نمی باشد!");
                snackbar.show();
                break;
            }
            case 2: {
                snackbar.setText("خطا در اتصال به سرور\nدوباره تلاش کنید");
                snackbar.show();
                break;
            }
            case 3: {
                snackbar.setText("لطفا اتصال به اینترنت خود را بررسی کنید");
                snackbar.show();
                break;
            }



        }

    }

    public void refreshScreen (){


        readinfo();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvUsername.setText(gUsername);
                tvName.setText(gRealName);
                tvLimit.setText(Integer.toString(limit));

                tv_bw_download.setText(bw_download);
                tv_bw_upload.setText(bw_upload);
                tv_reset_date.setText(reset_time());


                if (change){

                    if(status){
                        tvStatus.setText("آنلاین");
                        imStatus.setImageResource(android.R.drawable.presence_online);
                    }else{
                        tvStatus.setText("آفلاین");
                        imStatus.setImageResource(android.R.drawable.presence_offline);

                    }
                }else {
                    tvStatus.setText("نامعلوم");
                    imStatus.setImageResource(android.R.drawable.presence_offline);


                }



                usageProgress.setMax(limit);
                //color code
                final int zarib = (limit/31)+1;

                //animations

                if (change){
                    ValueAnimator tvAnimator = new ValueAnimator();
                    tvAnimator.setObjectValues(limit, remainUsage);
                    tvAnimator.setDuration(3000);
                    tvAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            tvRemain.setText("" + (int) animation.getAnimatedValue());
                            usageProgress.setProgress((int) animation.getAnimatedValue());
                            usageProgress.setProgressColor(Color.parseColor(getcolor((int) animation.getAnimatedValue()/zarib)));
                            change=false;

                        }
                    });
                    tvAnimator.start();
                }else{
                    tvRemain.setText(remainUsage);
                    usageProgress.setProgress(remainUsage);
                    usageProgress.setBackgroundColor(Color.parseColor(getcolor(remainUsage/zarib)));
                }
            }
        });
        tvUpTime.setText(time());
    }

    public String time(){
        String time_passed="none";
        String cur_time;
        long dif_in_mil=0;
        long dif_in_sec=0;
        SimpleDateFormat date_format = new SimpleDateFormat("HH:mm:ss yyyy/MM/dd");
        cur_time = date_format.format(new Date());

        try {
            Date past = date_format.parse(update_time);
            Date now = date_format.parse(cur_time);
            dif_in_mil = now.getTime() - past.getTime();
            dif_in_sec = dif_in_mil/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dif_in_sec < 3)
            time_passed = "همین الان آپدیت شده!";
        else if (dif_in_sec > 3 &&  dif_in_sec < 60)
            time_passed = String.format(" %d ثانیه پیش آپدیت شده!", dif_in_sec);
        else if (dif_in_sec > 60 &&  dif_in_sec < 3600)
            time_passed =String.format(" %d دقیقه پیش آپدیت شده!", dif_in_sec/60);
        else if (dif_in_sec > 3600)
            time_passed = String.format(" %d ساعت پیش آپدیت شده!", dif_in_sec/3600);
        return time_passed;
    }

    public String reset_time(){
        String reset_time="none";
        String cur_time;
        long dif_in_mil=0;
        long dif_in_sec=0;
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        cur_time = date_format.format(new Date());

        try {
            Date future = date_format.parse(reset_date);
            Date now = date_format.parse(cur_time);
            Calendar cal_f = Calendar.getInstance();

            cal_f.setTime(future);

            // new fix, hope this works
            while ((cal_f.getTime().getTime() - now.getTime()) < 0 ){
                cal_f.add(Calendar.MONTH,1);
            }


            dif_in_mil = cal_f.getTime().getTime() - now.getTime();
            dif_in_sec = dif_in_mil/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dif_in_sec < 3600)
            reset_time = String.format(" %d دقیقه دیگر!", dif_in_sec/60);
        else if ( dif_in_sec > 3600 && dif_in_sec < 86400)
            reset_time = String.format(" %d ساعت دیگر!", dif_in_sec/3600);
        else if ( dif_in_sec > 86400)
        reset_time = String.format(" %d روز دیگر!", dif_in_sec/86400);
            return reset_time;
    }




    public void readinfo(){
        SharedPreferences saved_info = this.getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        gUsername = saved_info.getString("username","");
        gRealName = saved_info.getString("name","");

        status = saved_info.getBoolean("status",false);
        update_time = saved_info.getString("update_time","11:11:11 1992/06/01");
        limit = saved_info.getInt("limit",0);
        usage = saved_info.getInt("usage",0);
        remainUsage = saved_info.getInt("usage_remain",0);

        bw_download = saved_info.getString("bw_download","نامعلوم");
        bw_upload = saved_info.getString("bw_upload","نامعلوم");
        reset_date = saved_info.getString("reset_date","11:11:11 1992/06/01");


        }

    public String getcolor(int x){
        String colorcode[] = new String[31];

        colorcode[0] = "#FF0000";
        colorcode[1] = "#FF1100";
        colorcode[2] = "#FF2200";
        colorcode[3] = "#FF3300";
        colorcode[4] = "#FF4400";
        colorcode[5] = "#FF5500";
        colorcode[6] = "#FF6600";
        colorcode[7] = "#FF7700";
        colorcode[8] = "#FF8800";
        colorcode[9] = "#FF9900";
        colorcode[10] = "#FFAA00";
        colorcode[11] = "#FFBB00";
        colorcode[12] = "#FFCC00";
        colorcode[13] = "#FFDD00";
        colorcode[14] = "#FFEE00";
        colorcode[15] = "#FFFF00";
        colorcode[16] = "#EEFF00";
        colorcode[17] = "#DDFF00";
        colorcode[18] = "#CCFF00";
        colorcode[19] = "#BBFF00";
        colorcode[20] = "#AAFF00";
        colorcode[21] = "#99FF00";
        colorcode[22] = "#88FF00";
        colorcode[23] = "#77FF00";
        colorcode[24] = "#66FF00";
        colorcode[25] = "#55FF00";
        colorcode[26] = "#44FF00";
        colorcode[27] = "#33FF00";
        colorcode[28] = "#22FF00";
        colorcode[29] = "#11FF00";
        colorcode[30] = "#00FF00";

        return colorcode[x];
    }






    public void async(){


        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(getActivity());
                dialog.setMessage("لطفا صبر کنید...");
                dialog.setCancelable(false);
                dialog.setIndeterminate(true);
                dialog.show();
                snackbar.dismiss();
                readinfo();
                tvUsername.setText(gUsername);
                tvName.setText(gRealName);
                tvLimit.setText(Integer.toString(limit));
                tvRemain.setText(Integer.toString(remainUsage));
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                refreshinfo();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                dialog.dismiss();
                refreshScreen();
                tvUpTime.setText(time());
            }
        }.execute();

    }




}
