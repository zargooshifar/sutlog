package ir.zarg.serendipity.sutlog.fragments.navigationDrawer;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ir.zarg.serendipity.sutlog.AdvActivity;
import ir.zarg.serendipity.sutlog.LoginActivity;
import ir.zarg.serendipity.sutlog.R;
import ir.zarg.serendipity.sutlog.fragments.mainFragments.changepassFragment;
import ir.zarg.serendipity.sutlog.fragments.mainFragments.infoFragment;
import ir.zarg.serendipity.sutlog.fragments.mainFragments.logFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentNavigationDrawer extends Fragment {


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private navRecyclerAdapter nav_adapter;
    private RecyclerView recyclerView;

    public fragmentNavigationDrawer() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);



        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.naw_recycle_view);
        nav_adapter = new navRecyclerAdapter(getActivity(), getData());
        recyclerView.setAdapter(nav_adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new recycler_touch_listener(getActivity(), recyclerView, new recycler_touch_listener.click_listener() {
            @Override
            public void onClick(View view, int position) {

                switch (position){
                    case 0: getFragmentManager().beginTransaction().replace(R.id.frag_container,new infoFragment()).commit();

                        break;

                    case 1: getFragmentManager().beginTransaction().replace(R.id.frag_container,new logFragment()).commit();
                        break;

                    case 2: getFragmentManager().beginTransaction().replace(R.id.frag_container,new changepassFragment()).commit();
                        break;
                    case 3:
                            getActivity().startActivity(new Intent(getActivity(), AdvActivity.class));
                        break;

                    case 4 : logout();
                        break;

                }


                mDrawerLayout.closeDrawers();

            }

            @Override
            public void onLongClick(View view, int position) {
                switch (position){
                    case 0:
                        Toast.makeText(getActivity(),"طراح و برنامه نویس:\nیعقوب زرگوشی فر",Toast.LENGTH_LONG).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 1:
                        Toast.makeText(getActivity(),"شماره تماس:\n0936 958 1483\n0930 574 3625",Toast.LENGTH_LONG).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 2:
                        Toast.makeText(getActivity(),"Email: Zargooshifar@outlook.com\nWebsite: zarg.ir",Toast.LENGTH_LONG).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 3:
                        Toast.makeText(getActivity(),"Telegram @ yaghoup\nInstagram @ yaghoup",Toast.LENGTH_LONG).show();
                        mDrawerLayout.closeDrawers();
                        break;
                }


            }
        }));





        return fragmentView;
    }

    public static List<navDataFormat> getData() {
        List<navDataFormat> data = new ArrayList<>();

        int[] icons = {R.drawable.account, R.drawable.file_chart, R.drawable.key, R.drawable.account ,R.drawable.logout};
        String[] titles = {"اطلاعات حساب", "گزارشات", "تغییر کلمه عبور","معرفی اپلیکیشن دانشجو" ,"خروج از اکانت"};

        for (int i = 0; i < titles.length; i++) {

            navDataFormat current = new navDataFormat();
            current.naw_text = titles[i];
            current.naw_img_id = icons[i];
            data.add(current);

        }
        return data;

    }


    public void setup(DrawerLayout drawer_layout, Toolbar toolbar) {

        mDrawerLayout = drawer_layout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {




            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }


            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }






        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });



    }


    static class recycler_touch_listener implements RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private click_listener clickListener;
        public recycler_touch_listener(Context context, final RecyclerView recyclerView, final click_listener clicklistener){

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

    public void logout(){

        SharedPreferences saved_data = this.getActivity().getSharedPreferences("SAVE_CREDITS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = saved_data.edit();
        editor.putString("username","").commit();
        editor.putString("password","").commit();
        editor.putBoolean("logined",false).commit();

        Intent myintent = new Intent(this.getActivity(), LoginActivity.class);
        startActivity(myintent);
        this.getActivity().finish();
    }
}
