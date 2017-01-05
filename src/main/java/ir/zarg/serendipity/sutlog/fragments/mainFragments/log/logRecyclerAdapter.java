package ir.zarg.serendipity.sutlog.fragments.mainFragments.log;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.style.IconMarginSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import ir.zarg.serendipity.sutlog.R;
import ir.zarg.serendipity.sutlog.classes.FormatHelper;
import ir.zarg.serendipity.sutlog.classes.convert_date;

/**
 * Created by serendipity on 7/10/16.
 */

public class logRecyclerAdapter extends RecyclerView.Adapter<logRecyclerAdapter.logViewHolder>{
    private int day;
    private String month_name;
    private String hour;
    private String day_name;
    private LayoutInflater inflater;
    List<logDataFormat> data = Collections.emptyList();
    int day_of_the_year_now;
    int day_of_the_year_before;
    SharedPreferences saved_devices;


    public logRecyclerAdapter(Context context, List<logDataFormat> data){
        this.data = data;
        inflater = LayoutInflater.from(context);
        saved_devices = context.getSharedPreferences("devices", Context.MODE_PRIVATE);

    }

    @Override
    public logViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.log_row,parent,false);

        logViewHolder holder = new logViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(logViewHolder holder, int position) {
        logDataFormat current = data.get(position);
        if (current.new_date){
            holder.log_date_layout.setVisibility(View.VISIBLE);
            holder.tv_date.setText(date_parser(current.login_time));
        }else {
            holder.log_date_layout.setVisibility(View.GONE);
        }



        if (current.login_success.equals("t")){

            holder.log_true_state_layout.setVisibility(View.VISIBLE);
            holder.log_false_state_layout.setVisibility(View.GONE);

            holder.tv_time.setText(time_parser(current.login_time)+"");
            holder.log_state.setBackgroundColor(Color.GREEN);
            holder.tv_duration.setText(current.duration);
            holder.tv_download.setText(current.download);
            holder.tv_upload.setText(current.upload);

        }else if(current.login_success.equals("f")){


            holder.log_false_state_layout.setVisibility(View.VISIBLE);
            holder.log_true_state_layout.setVisibility(View.GONE);


            holder.tv_time.setText(time_parser(current.login_time)+"");
            holder.log_state.setBackgroundColor(Color.RED);

            holder.tv_kill_reason.setText(current.kill_reason);

        }
        if (saved_devices.getString(current.ip,"").equals("")){
            holder.tv_ip.setText(current.ip);
            holder.tv_mac.setText(current.mac);
            holder.device_name_layout.setVisibility(View.GONE);
            holder.log_ip_layout.setVisibility(View.VISIBLE);

        }else {
            String device_name = saved_devices.getString(current.ip,"");
            holder.device_name.setText(device_name);
            holder.log_ip_layout.setVisibility(View.GONE);
            holder.device_name_layout.setVisibility(View.VISIBLE);

        }



    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    class logViewHolder extends RecyclerView.ViewHolder{
        LinearLayout log_row_layout;
        TextView tv_download;
        TextView tv_upload;
        TextView tv_date;
        TextView tv_time;
        TextView tv_duration;
        TextView tv_ip;
        TextView tv_mac;
        TextView tv_log_mac;
        TextView tv_kill_reason;
        TextView tv_log_dur;
        TextView tv_log_dl;
        TextView tv_log_up;
        TextView tv_log_kill;
        ImageView log_state;

        RelativeLayout log_date_layout;
//        RoundCornerProgressBar log_state;
        LinearLayout log_true_state_layout;
        LinearLayout log_false_state_layout;
        LinearLayout device_name_layout;
        LinearLayout log_ip_layout;
        TextView device_name;

        public logViewHolder(View itemView) {
            super(itemView);
//            log_row_layout = (LinearLayout) itemView.findViewById(R.id.log_row_layout);
            tv_download = (TextView) itemView.findViewById(R.id.tv_download);
            tv_upload = (TextView) itemView.findViewById(R.id.tv_upload);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_duration = (TextView) itemView.findViewById(R.id.tv_duration);
            tv_ip = (TextView) itemView.findViewById(R.id.tv_ip);
            tv_mac = (TextView) itemView.findViewById(R.id.tv_mac);
//            tv_log_mac = (TextView) itemView.findViewById(R.id.tv_log_mac);
            tv_kill_reason = (TextView) itemView.findViewById(R.id.tv_kill);
//            tv_log_dur = (TextView) itemView.findViewById(R.id.tv_log_dur);
//            tv_log_dl = (TextView) itemView.findViewById(R.id.tv_log_dl);
//            tv_log_up = (TextView) itemView.findViewById(R.id.tv_log_up);
//            tv_log_kill = (TextView) itemView.findViewById(R.id.tv_log_kill);


            log_date_layout = (RelativeLayout) itemView.findViewById(R.id.log_date_layout);
//            log_state = (RoundCornerProgressBar) itemView.findViewById(R.id.log_state);
            log_true_state_layout = (LinearLayout) itemView.findViewById(R.id.log_true_state_layout);
            log_false_state_layout = (LinearLayout) itemView.findViewById(R.id.log_false_state_layout);
            log_state = (ImageView) itemView.findViewById(R.id.log_state);
            log_ip_layout = (LinearLayout) itemView.findViewById(R.id.log_ip_layout);
            device_name_layout = (LinearLayout) itemView.findViewById(R.id.device_name_layout);
            device_name =(TextView) itemView.findViewById(R.id.device_name);



        }


    }

    public String date_parser(String login_time){



        Date login_date = null;
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            login_date = date_format.parse(login_time);
            // login_date = date_format.parse("1371-10-11 13:02");


        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(login_date);

        day_of_the_year_now = cal.get(Calendar.DAY_OF_YEAR);

        int gday = cal.get(Calendar.DAY_OF_MONTH);
        int gmonth = cal.get(Calendar.MONTH);
        int gyear = cal.get(Calendar.YEAR);
        int gweek = cal.get(Calendar.DAY_OF_WEEK);


        int min = cal.get(Calendar.MINUTE);
        String min_fixed = (min < 10 ? "0" : "") + min;


        convert_date con = new convert_date() ;
        hour = cal.get(Calendar.HOUR_OF_DAY) + ":" + min_fixed;

        String result = con.gregorian_to_jalali(gyear,gmonth+1,gday,gweek-1) ;
        return result;

    }
    public String time_parser (String date){
        String result = "";

        Date time = null;
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            time = date_format.parse(date);
            // login_date = date_format.parse("1371-10-11 13:02");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);

        int time_hour = cal.get(Calendar.HOUR_OF_DAY);
        String time_hour_fixed = (time_hour < 10 ? "0" : "") + time_hour;
        int time_min = cal.get(Calendar.MINUTE);
        String time_min_fixed = (time_min < 10 ? "0" : "") + time_min;
        String time_result = FormatHelper.toPersianNumber(time_hour_fixed) + ":" + FormatHelper.toPersianNumber(time_min_fixed);




        return time_result;
    }




}
