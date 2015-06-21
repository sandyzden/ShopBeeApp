package com.app.shopbee.activities;

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
import com.google.android.gcm.GCMRegistrar;

import static com.app.shopbee.util.LogUtils.*;

public class ProfileActivity extends BaseActivity {

    private static final String TAG = makeLogTag(ProfileActivity.class);

    //private DrawShadowFrameLayout mDrawShadowFrameLayout;
    private View mButterBar;

    private static final int MODE_EXPLORE = 0;
    private int mMode = MODE_EXPLORE;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = getActionBarToolbar();
        toolbar.setTitle("Profile Section");

        mButterBar = findViewById(R.id.butter_bar);
       // mDrawShadowFrameLayout = (DrawShadowFrameLayout) findViewById(R.id.main_content);
       // registerHideableHeaderView(mButterBar);
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




}
