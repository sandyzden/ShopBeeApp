package com.app.shopbee.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.app.shopbee.R;
import com.app.shopbee.adapter.StoreCardAdapter;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.google.android.gcm.GCMRegistrar;
import com.app.shopbee.database.DBController;

import com.app.shopbee.fragments.StoreMainFragment;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import java.util.HashMap;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ListView;
import android.app.AlarmManager;
import java.util.Calendar;

import com.app.shopbee.database.*;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.AsyncTask;
import static com.app.shopbee.util.LogUtils.*;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import android.support.v7.widget.RecyclerView;

import com.app.shopbee.http.ServiceHandler;
import android.support.v7.widget.LinearLayoutManager;

import com.app.shopbee.model.Store;
import com.app.shopbee.fragments.RecyclerViewFragment;
import com.app.shopbee.fragments.ScrollFragment;


public class ProfileActivity extends BaseActivity {

    private static final String TAG = makeLogTag(ProfileActivity.class);

    private static String url = "http://10.252.198.12:8080/StoreServer/getStoreByUser/sandy";

    //private DrawShadowFrameLayout mDrawShadowFrameLayout;
    private View mButterBar;

    private static final int MODE_EXPLORE = 0;
    private int mMode = MODE_EXPLORE;

    private MaterialViewPager mViewPager;

    private ProgressDialog pDialog;

    RecyclerView recList;

    // contacts JSONArray
    JSONArray stores = null;

    DrawerLayout mDrawerLayout;

    // Hashmap for ListView
    ArrayList<Store> storesList;

    Toolbar toolbar;

    DBController dbController = new DBController(this);

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);
        toolbar = mViewPager.getToolbar();

        mButterBar = findViewById(R.id.butter_bar);

        //MaterialPageViewer
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setHomeButtonEnabled(true);
            }

                toolbar.setNavigationIcon(R.drawable.ic_drawer);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                });

        }
        //MaterialViewPager
        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                switch (position % 4) {
                    //case 0:
                    //    return RecyclerViewFragment.newInstance();
                    case 1:
                        return RecyclerViewFragment.newInstance();
                    //case 2:
                    //    return WebViewFragment.newInstance();
                    default:
                        return ScrollFragment.newInstance();
                }
            }

            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position % 4) {
                    case 0:
                        return "BUSINESS";
                    case 1:
                        return "INVENTORY";
                    case 2:
                        return "TRANSACTIONS";
                    case 3:
                        return "REPORT";
                }
                return "";
            }
        });

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.MaterialViewPagerListener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.blue,
                                "http://cdn1.tnwcdn.com/wp-content/blogs.dir/1/files/2014/06/wallpaper_51.jpg");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.green,
                                "https://fs01.androidpit.info/a/63/0e/android-l-wallpapers-630ea6-h900.jpg");
                    case 2:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.cyan,
                                "http://www.droid-life.com/wp-content/uploads/2014/10/lollipop-wallpapers10.jpg");
                    case 3:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.red,
                                "http://www.tothemobile.com/wp-content/uploads/2014/07/original.jpg");
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        });

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        mViewPager.getViewPager().setCurrentItem(0);

     /*   recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        storesList = new ArrayList<Store>();*/

    }

    @Override
    public void onResume() {
        super.onResume();
        //checkShowStaleDataButterBar();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        // we only have a nav drawer if we are in top-level Explore mode.
        return mMode == MODE_EXPLORE ? NAVDRAWER_ITEM_STORE : NAVDRAWER_ITEM_INVALID;
    }

    // Options Menu (ActionBar Menu)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        // When Sync action button is clicked
        if (id == R.id.refresh) {
            // Transfer data from remote MySQL DB to SQLite on Android and perform Sync

            if(dbController.isTableExists("store"))
            {
                Toast.makeText(getApplicationContext(), "Loading from local data", Toast.LENGTH_LONG).show();
               // storesList = dbController.getAllStores();
               // recList.setAdapter(new StoreCardAdapter(storesList));
            }
            else {
                Toast.makeText(getApplicationContext(), "Loading from remote data", Toast.LENGTH_LONG).show();
                //new GetStore().execute();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}







