package ir.zarg.serendipity.sutlog;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ir.zarg.serendipity.sutlog.fragments.mainFragments.infoFragment;
import ir.zarg.serendipity.sutlog.fragments.navigationDrawer.fragmentNavigationDrawer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(getWindow().getDecorView().getLayoutDirection()== View.LAYOUT_DIRECTION_LTR)
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        fragmentNavigationDrawer frag_nav_drawer = (fragmentNavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.navigation_fragment);
        frag_nav_drawer.setup((DrawerLayout) findViewById(R.id.drawer_layout),toolbar);


        if (findViewById(R.id.frag_container) != null){

            getSupportFragmentManager().beginTransaction().add(R.id.frag_container,new infoFragment()).commit();
        }


    }
}
