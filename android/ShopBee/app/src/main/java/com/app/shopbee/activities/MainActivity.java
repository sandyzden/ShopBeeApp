package com.app.shopbee.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.Intent;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import android.support.v4.widget.DrawerLayout;

import com.app.shopbee.R;
import com.app.shopbee.model.GoogleUser;
import com.app.shopbee.fragments.FragmentDrawer;

public class MainActivity extends ActionBarActivity {



    private String userName;
    private String emailId;
    private String profilePicUrl;
    private Toolbar mToolbar;
    private GoogleUser googleUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(readFromPreferences(this,"userName",null)==null)
        {
            Intent welcomeIntent = new Intent(getApplicationContext(),WelcomeActivity.class);
            startActivity(welcomeIntent);
        }
        else
        {
            googleUser = new GoogleUser();
            googleUser.setUserName(readFromPreferences(this,"userName",null));
            googleUser.setEmailId(readFromPreferences(this, "emailId", null));
            googleUser.setProfilePic(readFromPreferences(this,"profilePicUrl",null));
        }

        FragmentDrawer drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        if(googleUser != null)
        drawerFragment.setUp(R.id.fragment_navigation_drawer,(DrawerLayout)findViewById(R.id.drawer_layout),mToolbar,googleUser);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getString(preferenceName, defaultValue);
    }
}
