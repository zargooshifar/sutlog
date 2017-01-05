package ir.zarg.serendipity.sutlog.fragments.mainFragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import ir.zarg.serendipity.sutlog.R;
import ir.zarg.serendipity.sutlog.classes.ObjectSerializer;
import ir.zarg.serendipity.sutlog.fragments.mainFragments.log.logDataFormat;
import ir.zarg.serendipity.sutlog.fragments.mainFragments.log.logRecyclerAdapter;
import ir.zarg.serendipity.sutlog.fragments.mainFragments.log.logReports;



public class logFragment extends Fragment {

    private RecyclerView log_recycler_view;
    private logRecyclerAdapter log_adapter;
    public ProgressDialog dialog;
    private ArrayList<logDataFormat> data;
    SharedPreferences saved_devices;

    AlertDialog.Builder inputDialog;
    public logFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_log, container, false);
        final SharedPreferences saved_info = this.getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = saved_info.edit();
        saved_devices = this.getActivity().getSharedPreferences("devices", Context.MODE_PRIVATE);

        log_recycler_view = (RecyclerView) fragmentView.findViewById(R.id.rv_reports);

        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(getActivity());
                dialog.setMessage("در حال دریافت اطلاعات از سرور\nلطفا صبر کنید...");
                dialog.setCancelable(false);
                dialog.setIndeterminate(true);
                dialog.show();

            }

            @Override
            protected Object doInBackground(Object[] objects) {
                data = new ArrayList<>();
                data = logReports.getReports();
                if (data.size() != 0){

                    try {
                        editor.putString("log", ObjectSerializer.serialize(data)).commit();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if (data.size() == 0){

                    try {
                        data = (ArrayList<logDataFormat>) ObjectSerializer.deserialize(saved_info.getString("log",""));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("data",data.size()+"");


                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                log_adapter = new logRecyclerAdapter(getActivity(), data);
                log_recycler_view.setAdapter(log_adapter);
                log_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
                dialog.dismiss();

            }


        }.execute();


        log_recycler_view.addOnItemTouchListener(new logFragment.log_recycler_listener(getActivity(), log_recycler_view, new logFragment.log_recycler_listener.click_listener() {
            @Override
            public void onClick(View view, int position) {

                //on click
            }

            @Override
            public void onLongClick(View view, int position) {
                logDataFormat current = data.get(position);
                if (saved_devices.getString(current.ip,"").equals("")){
                    makeInputDialog(current.ip);
                    inputDialog.show();
                }else{
                    String device_name = saved_devices.getString(current.ip,"");
                    makeDeleteDialog(device_name,current.ip);
                    inputDialog.show();
                }



            }
        }));


        return fragmentView;
    }







    static class log_recycler_listener implements RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private logFragment.log_recycler_listener.click_listener clickListener;
        public log_recycler_listener(Context context, final RecyclerView recyclerView, final logFragment.log_recycler_listener.click_listener clicklistener){

            this.clickListener = clicklistener;

            gestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){



                @Override
                public boolean onSingleTapUp(MotionEvent e) {



                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(),e.getY());

                    if (child !=null && clicklistener !=null){

                        clicklistener.onLongClick(child,recyclerView.getChildAdapterPosition(child));
                    }
                }
            });

        }
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(),e.getY());

            if (child !=null && clickListener !=null && gestureDetector.onTouchEvent(e)){

                clickListener.onClick(child,rv.getChildAdapterPosition(child));

            }



            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

        public static interface click_listener{
            public void onClick(View view, int position);
            public void onLongClick(View view, int position);

        }
    }
    public void makeInputDialog(final String ip){

        inputDialog = new AlertDialog.Builder(getActivity());
        inputDialog.setTitle("");
        inputDialog.setMessage(String.format("نام گذاری آی پی آدرس %s \n توجه کنید که آی پی آدرس هر چند مدت یک بار عوض می شود. برای دقت بیشتر باید از آدرس mac استفاده شود که برای هر دستگاه اختصاصی است. ولی متاسفانه به علت مشکل سرور دانشگاه آدرس mac فعلا قابل دسترسی نمی باشد. امیدواریم که خدمات ماشینی دانشگاه این مشکل را حل کرده و در آپدیت بعدی از آدرس mac استفاده شود.", ip));

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("نام دستگاه");

        input.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        inputDialog.setView(input);

        inputDialog.setPositiveButton("خب!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = saved_devices.edit();
                String device_name = input.getText().toString();
                if (!device_name.equals("")){
                    editor.putString(ip,device_name).commit();
                    log_recycler_view.setAdapter(log_adapter);

                }

            }
        });

        inputDialog.setNegativeButton("بستن", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });


    }
    public void makeDeleteDialog(String device_name, final String ip){

        inputDialog = new AlertDialog.Builder(getActivity());
        inputDialog.setTitle("");
        inputDialog.setMessage( String.format("حدف نام «%S» با آی پی آدرس «%s»", device_name,ip));




        inputDialog.setPositiveButton("خب!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = saved_devices.edit();
                editor.putString(ip,"").commit();
                log_recycler_view.setAdapter(log_adapter);

            }
        });

        inputDialog.setNegativeButton("بستن", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
    }
}
