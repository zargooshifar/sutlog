package ir.zarg.serendipity.sutlog.fragments.startFragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ir.zarg.serendipity.sutlog.R;
import ir.zarg.serendipity.sutlog.classes.auth;
import ir.zarg.serendipity.sutlog.classes.getinfo;
import ir.zarg.serendipity.sutlog.classes.ping;
import ir.zarg.serendipity.sutlog.MainActivity;


public class loginFragment extends Fragment {


    public loginFragment() {
        // Required empty public constructor
    }

    public String gUsername = null;
    public String gPassword = null;
    public EditText etUsername;
    public EditText etPassword;
    public String result = "";
    public HorizontalScrollView bg;
    public ir.zarg.serendipity.sutlog.classes.getinfo getinfo = new getinfo();
    public ir.zarg.serendipity.sutlog.classes.auth auth = new auth();
    public Snackbar snackbar;
    public ProgressDialog dialog;
    private String error;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_login, container, false);

        etUsername = (EditText) fragmentView.findViewById(R.id.etUsername);
        etPassword = (EditText) fragmentView.findViewById(R.id.etPassword);
        //    saveSwitch = (Switch) fragmentView.findViewById(R.id.saveSwitch);
        // get last stats and set
        SharedPreferences saved_data = this.getActivity().getSharedPreferences("SAVE_CREDITS", Context.MODE_PRIVATE);
        gUsername = saved_data.getString("username", null);
        gPassword = saved_data.getString("password", null);
        etUsername.setText(gUsername);
        etPassword.setText(gPassword);
        snackbar = Snackbar.make(fragmentView, "", Snackbar.LENGTH_LONG);



        Button doLogin = (Button) fragmentView.findViewById(R.id.bLogin);
        doLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                async();


            }
        });


        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    async();
                }
                return false;
            }
        });
        return fragmentView;
    }

    public void func_login() {
        //get editTexts texts...
        gUsername = etUsername.getText().toString();
        gPassword = etPassword.getText().toString();
        // check for ping to server
        SharedPreferences saved_data = this.getActivity().getSharedPreferences("SAVE_CREDITS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = saved_data.edit();
        editor.putString("username", gUsername);
        editor.putString("password", gPassword);
        editor.commit();
        ping ping = new ping();
        int connectionAvalable = ping.respond((ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE));
        //check for connection and continue

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
                    error = ("خطا در اتصال به سرور");
                }

                if (result.equals("SUCCESS")) {

                    editor.putString("username", gUsername);
                    editor.putString("password", gPassword);
                    editor.putBoolean("logined", true);
                    editor.commit();


                    Intent myintent = new Intent(this.getActivity(), MainActivity.class);
                    startActivity(myintent);
                    this.getActivity().finish();

                } else if (result.equals("FAILURE")) {
                    error =("نام کاربری یا کلمه عبور اشتباه می باشد");
                } else if (result.equals("")) {
                    error =("پاسخی از سرور دریافت نشد!");
                }
                break;
            }
            case 1: {
                error =("سرور در حال حاضر در دسترس نمی باشد!");
                break;
            }
            case 2: {
                error =("خطا در اتصال به سرور\nدوباره تلاش کنید");
                break;
            }
            case 3: {
                error =("لطفا اتصال به اینترنت خود را بررسی کنید");
                break;
            }



        }
    }

    public void async(){


        final AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(getActivity());
                dialog.setMessage("لطفا صبر کنید...");
                dialog.setCancelable(false);
                dialog.setIndeterminate(true);
                dialog.show();

            }

            @Override
            protected Object doInBackground(Object[] objects) {
                func_login();
                return null;
            }


            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                dialog.dismiss();
                if (snackbar.isShown()){
                    snackbar.setText(error);

                }else {
                    snackbar.setText(error).show();
                }
            }
        }.execute();


    }


}
