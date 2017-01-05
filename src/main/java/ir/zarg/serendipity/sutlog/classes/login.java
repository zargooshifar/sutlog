package ir.zarg.serendipity.sutlog.classes;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by Mehrad on 02/08/2016.
 */

public class login {
    Context context;




    public void connect(){

    }

    public void async(final Context cont){
        this.context = cont;
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Object doInBackground(Object[] objects) {
                connect();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                Toast.makeText(context,"لاگین شدید",Toast.LENGTH_SHORT).show();
            }
        }.execute();

    }

}


