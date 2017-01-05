package ir.zarg.serendipity.sutlog.fragments.mainFragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import ir.zarg.serendipity.sutlog.R;
import ir.zarg.serendipity.sutlog.classes.auth;
import ir.zarg.serendipity.sutlog.classes.getinfo;
import ir.zarg.serendipity.sutlog.classes.ping;

/**
 * A simple {@link Fragment} subclass.
 */
public class changepassFragment extends Fragment {

    private EditText etCPass;
    private EditText etNPass;
    private EditText etNPass2;
    private Button ok;
    private String cpass;
    private String npass;
    private String npass2;
    private Snackbar snackbar;
    private String result;
    private boolean respond;
    private ir.zarg.serendipity.sutlog.classes.getinfo getinfo = new getinfo();
    private ir.zarg.serendipity.sutlog.classes.auth auth = new auth();
    private ir.zarg.serendipity.sutlog.classes.ping ping = new ping();
    private ProgressDialog dialog;
    private String error;
    public changepassFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView =  inflater.inflate(R.layout.fragment_changepass, container, false);

        etCPass = (EditText) fragmentView.findViewById(R.id.etCurrentPass);
        etNPass = (EditText) fragmentView.findViewById(R.id.etNewPass);
        etNPass2 = (EditText) fragmentView.findViewById(R.id.etNewPass2);
        ok = (Button) fragmentView.findViewById(R.id.bOk);
        snackbar = Snackbar.make(fragmentView, "", Snackbar.LENGTH_LONG);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                async();
                if (respond){
                    clear_fields();
                }

            }
        });

        etNPass2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    async();
                    if (respond){
                        clear_fields();
                    }
                }
                return false;
            }
        });

        return fragmentView;
    }


    public void changePass(){
        SharedPreferences saved_cred = this.getActivity().getSharedPreferences("SAVE_CREDITS", Context.MODE_PRIVATE);
        String saved_cpass = saved_cred.getString("password","nopassfounded");
        cpass = etCPass.getText().toString();
        npass = etNPass.getText().toString();
        npass2 = etNPass2.getText().toString();
        if (saved_cpass.equals(cpass)){
            if (npass.equals(npass2)){
                respond = false;
                connectionforchangepass();


            }else {
                error = ("خطا: کلمه عبور با تکرار کلمه عبور مطابقت ندارد");
            }
        }else{
            error =  ("خطا: کلمه عبور فعلی وارد شده اشتباه است");
        }
    }

    public void connectionforchangepass(){

        int connectionAvalable = ping.respond((ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE));
        //check for connection and continue

        switch (connectionAvalable) {

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

                    connect = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Thread connection = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        SharedPreferences saved_data = getActivity().getSharedPreferences("SAVE_CREDITS", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = saved_data.edit();

                                        String gUsername = saved_data.getString("username", "");
                                        String gPassword = saved_data.getString("password", "");

                                        //Authentication par
                                        //Toast.makeText(this,"Getting Ready for Authentication", Toast.LENGTH_SHORT).show();


                                        URL url1 = new URL("http://172.16.16.1/IBSng/user/index.php?normal_username=" + gUsername + "&normal_password=" + gPassword);
                                        URL url2 = new URL("http://172.16.16.1/IBSng/user/change_pass.php?old_normal_password=" + cpass + "&new_normal_password1=" + npass + "&new_normal_password2=" + npass2);


                                        HttpURLConnection connection = (HttpURLConnection) url1.openConnection();

                                        connection.setRequestMethod("GET");
                                        connection.connect();
                                        connection.getResponseMessage();
                                        connection = (HttpURLConnection) url2.openConnection();
                                        connection.connect();
                                        connection.getResponseMessage();

                                        if (connection.getResponseCode() == 200){
                                            editor.putString("password",npass).commit();
                                            error = ("تغییر کلمه عبور با موفقیت انجام شد");
                                            respond = true;
                                        }


                                    } catch (MalformedURLException e) {
                                        error = ("خطا در اتصال به سرور");
                                        e.printStackTrace();
                                    } catch (UnsupportedEncodingException e) {
                                        error = ("خطا در اتصال به سرور");
                                        e.printStackTrace();
                                    } catch (ProtocolException e) {
                                        error = ("خطا در اتصال به سرور");
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        error = ("خطا در اتصال به سرور");
                                        e.printStackTrace();
                                    }


                                }
                            });
                            connection.start();
                            try {
                                connection.join();
                            } catch (InterruptedException e) {
                                error = ("خطا در اتصال به سرور");
                                e.printStackTrace();
                            }


                        }
                    });
                    connect.start();
                    try {
                        connect.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        error = ("خطا در اتصال به سرور");

                    }

                } else if (result.equals("FAILURE")) {
                    error = ("کلمه عبور موجود صحیح نمی باشد\nلطفا از اکانت خود خارج و \nمجددا در نرم افزار لاگین فرمایید");
                } else if (result.equals("")) {
                    error = ("پاسخی از سرور دریافت نشد!");
                }
                break;
            }
            case 1: {
                error = ("سرور در حال حاضر در دسترس نمی باشد!");
                break;
            }
            case 2: {
                error = ("خطا در اتصال به سرور\nدوباره تلاش کنید");
                break;
            }
            case 3: {
                error = ("لطفا اتصال به اینترنت خود را بررسی کنید");
                break;
            }

        }
    }

    public void clear_fields(){

        etCPass.setText("");
        etNPass.setText("");
        etNPass2.setText("");
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
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                changePass();
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
