package com.app.shopbee.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
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


public class ProfileActivity extends BaseActivity {

    private static final String TAG = makeLogTag(ProfileActivity.class);

    private static String url = "http://10.252.198.12:8080/StoreServer/getStoreByUser/sandy";

    //private DrawShadowFrameLayout mDrawShadowFrameLayout;
    private View mButterBar;

    private static final int MODE_EXPLORE = 0;
    private int mMode = MODE_EXPLORE;

    private ProgressDialog pDialog;

    RecyclerView recList;

    // contacts JSONArray
    JSONArray stores = null;

    // Hashmap for ListView
    ArrayList<Store> storesList;

    DBController dbController = new DBController(this);

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = getActionBarToolbar();
        toolbar.setTitle("Profile Section");

        mButterBar = findViewById(R.id.butter_bar);

        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        storesList = new ArrayList<Store>();

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
                storesList = dbController.getAllStores();
                recList.setAdapter(new StoreCardAdapter(storesList));
            }
            else {
                Toast.makeText(getApplicationContext(), "Loading from remote data", Toast.LENGTH_LONG).show();
                new GetStore().execute();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetStore extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ProfileActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            if (jsonStr != null) {
                try {
                    stores = new JSONArray(jsonStr);
                    for (int i = 0; i < stores.length(); i++) {
                        JSONObject c = stores.getJSONObject(i);
                        String storeName = c.getString("storeName");
                        String locality = c.getString("locality");
                        String contactNumber = c.getString("contactNumber");

                        Store store = new Store();
                        store.setStoreName(storeName);
                        store.setLocality(locality);
                        store.setContactNo(contactNumber);

                        dbController.insertStore(store);

                        storesList.add(store);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            recList.setAdapter(new StoreCardAdapter(storesList));
        }
    }
}







