package ir.zarg.serendipity.sutlog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;



import java.net.CookieHandler;
import java.net.CookieManager;

import ir.zarg.serendipity.sutlog.fragments.startFragments.loginFragment;

public class LoginActivity extends FragmentActivity {


    private boolean logined;


    SharedPreferences saved_data;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        saved_data = getSharedPreferences("SAVE_CREDITS", Context.MODE_PRIVATE);
        editor = saved_data.edit();

        logined = saved_data.getBoolean("logined", false);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new loginFragment()).commit();


        if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR)
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        // Add the fragment to the 'fragment_container' FrameLayout
        //  getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new splashFragment()).commit();
        // setup cookies
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        //check save data and replace fragment

        if (logined) {
            Intent myintent = new Intent(this, MainActivity.class);
            startActivity(myintent);
            finish();
        }
    }








}