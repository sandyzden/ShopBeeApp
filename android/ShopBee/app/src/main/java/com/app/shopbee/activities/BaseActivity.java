package com.app.shopbee.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.app.shopbee.R;
import com.app.shopbee.util.LoginAndAuthHelper;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.app.shopbee.Config;

import com.app.shopbee.util.PrefUtils;
import com.app.shopbee.util.AccountUtils;
import com.app.shopbee.gcm.ServerUtilities;
import com.google.android.gms.auth.GoogleAuthUtil;

import static com.app.shopbee.util.LogUtils.LOGD;
import static com.app.shopbee.util.LogUtils.LOGE;
import static com.app.shopbee.util.LogUtils.LOGI;
import static com.app.shopbee.util.LogUtils.LOGW;
import static com.app.shopbee.util.LogUtils.makeLogTag;
import com.app.shopbee.ui.MultiSwipeRefreshLayout;
import com.app.shopbee.ui.ScrimInsetsScrollView;
import com.app.shopbee.util.UIUtils;
import com.app.shopbee.util.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by sendilkumar on 13/06/15.
 */
abstract class BaseActivity extends ActionBarActivity implements
        LoginAndAuthHelper.Callbacks,
        SharedPreferences.OnSharedPreferenceChangeListener,
        MultiSwipeRefreshLayout.CanChildScrollUpCallback {

        private static final String TAG = makeLogTag(BaseActivity.class);

        private AsyncTask<Void, Void, Void> mGCMRegisterTask;

        private LoginAndAuthHelper mLoginAndAuthHelper;

        private SwipeRefreshLayout mSwipeRefreshLayout;

        private boolean mManualSyncRequest;

        private DrawerLayout mDrawerLayout;

        private ImageView mExpandAccountBoxIndicator;
        private LinearLayout mAccountListContainer;
        private ViewGroup mDrawerItemsListContainer;

        private static final int ACCOUNT_BOX_EXPAND_ANIM_DURATION = 200;
        private static final int HEADER_HIDE_ANIM_DURATION = 300;

        private boolean mActionBarAutoHideEnabled = false;

        private ObjectAnimator mStatusBarColorAnimator;

        private ArrayList<View> mHideableHeaderViews = new ArrayList<View>();

        private ArrayList<Integer> mNavDrawerItems = new ArrayList<Integer>();

        private View[] mNavDrawerItemViews = null;

        private int mProgressBarTopWhenActionBarShown;

        // Primary toolbar and drawer toggle
        private Toolbar mActionBarToolbar;

        private static final TypeEvaluator ARGB_EVALUATOR = new ArgbEvaluator();

        // A Runnable that we should execute when the navigation drawer finishes its closing animation
        private Runnable mDeferredOnDrawerClosedRunnable;

        private boolean mAccountBoxExpanded = false;

        private Handler mHandler;

        private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
        private static final int MAIN_CONTENT_FADEIN_DURATION = 250;


        private static final int NAVDRAWER_LAUNCH_DELAY = 250;


        protected static final int NAVDRAWER_ITEM_INVALID = -1;
        protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;
        protected static final int NAVDRAWER_ITEM_SEPARATOR_SPECIAL = -3;

        protected static final int NAVDRAWER_ITEM_MY_PROFILE = 0;
        protected static final int NAVDRAWER_ITEM_STORE = 1;
        protected static final int NAVDRAWER_ITEM_INVENTORY = 2;
        protected static final int NAVDRAWER_ITEM_PRODUCTS = 3;
        protected static final int NAVDRAWER_ITEM_SETTINGS = 4;
        protected static final int NAVDRAWER_ITEM_SIGN_IN = 5;

        private static final int[] NAVDRAWER_TITLE_RES_ID = new int[]{
                R.string.navdrawer_item_my_profile,
                R.string.navdrawer_item_store,
                R.string.navdrawer_item_inventory,
                R.string.navdrawer_item_products,
                R.string.navdrawer_item_settings,
                R.string.navdrawer_item_sign_in

        };

        // icons for navdrawer items (indices must correspond to above array)
        private static final int[] NAVDRAWER_ICON_RES_ID = new int[] {
                R.drawable.ic_drawer_my_schedule,  // My Schedule
                R.drawable.ic_drawer_explore,  // Explore
                R.drawable.ic_drawer_map, // Map
                R.drawable.ic_drawer_social, // Social
                R.drawable.ic_drawer_settings,
                0// Sign in


        };


        private boolean mActionBarShown = true;

        private int mNormalStatusBarColor;

        private ImageLoader mImageLoader;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                PrefUtils.init(this);

                // Check if the EULA has been accepted; if not, show it.
                if (!PrefUtils.isTosAccepted(this)) {
                        Intent intent = new Intent(this, WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                }

                mImageLoader = new ImageLoader(this);
                mHandler = new Handler();

                if (savedInstanceState == null) {
                        registerGCMClient();
                }

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                sp.registerOnSharedPreferenceChangeListener(this);

                ActionBar ab = getSupportActionBar();
                if (ab != null) {
                        ab.setDisplayHomeAsUpEnabled(true);
                }

        } // end oncreate

        private void registerGCMClient() {
                GCMRegistrar.checkDevice(this);
                GCMRegistrar.checkManifest(this);

                final String regId = GCMRegistrar.getRegistrationId(this);

                if (TextUtils.isEmpty(regId)) {
                        // Automatically registers application on startup.
                        GCMRegistrar.register(this, Config.GCM_SENDER_ID);

                } else {
                        // Get the correct GCM key for the user. GCM key is a somewhat non-standard
                        // approach we use in this app. For more about this, check GCM.TXT.
                        final String gcmKey = AccountUtils.hasActiveAccount(this) ?
                                AccountUtils.getGcmKey(this, AccountUtils.getActiveAccountName(this)) : null;
                        // Device is already registered on GCM, needs to check if it is
                        // registered on our server as well.
                        if (ServerUtilities.isRegisteredOnServer(this, gcmKey)) {
                                // Skips registration.
                                LOGI(TAG, "Already registered on the GCM server with right GCM key.");
                        } else {
                                // Try to register again, but not in the UI thread.
                                // It's also necessary to cancel the thread onDestroy(),
                                // hence the use of AsyncTask instead of a raw thread.
                                mGCMRegisterTask = new AsyncTask<Void, Void, Void>() {
                                        @Override
                                        protected Void doInBackground(Void... params) {
                                                LOGI(TAG, "Registering on the GCM server with GCM key: "
                                                        + AccountUtils.sanitizeGcmKey(gcmKey));
                                                boolean registered = ServerUtilities.register(BaseActivity.this,
                                                        regId, gcmKey);
                                                // At this point all attempts to register with the app
                                                // server failed, so we need to unregister the device
                                                // from GCM - the app will try to register again when
                                                // it is restarted. Note that GCM will send an
                                                // unregistered callback upon completion, but
                                                // GCMIntentService.onUnregistered() will ignore it.
                                                if (!registered) {
                                                        LOGI(TAG, "GCM registration failed.");
                                                        GCMRegistrar.unregister(BaseActivity.this);

                                                } else {
                                                        LOGI(TAG, "GCM registration successful.");

                                                }
                                                return null;
                                        }
                                };
                        }
                }
        }

        @Override
        protected void onPostCreate(Bundle savedInstanceState) {
                super.onPostCreate(savedInstanceState);
                setupNavDrawer();
                setupAccountBox();
                trySetupSwipeRefresh();

                View mainContent = findViewById(R.id.main_content);
                if (mainContent != null) {
                        mainContent.setAlpha(0);
                        mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
                } else {
                        LOGW(TAG, "No view with ID main_content to fade in.");
                }
        }

        @Override
        protected void onDestroy() {
                super.onDestroy();

                if (mGCMRegisterTask != null) {
                        LOGD(TAG, "Cancelling GCM registration task.");
                        mGCMRegisterTask.cancel(true);
                }

                try {
                        GCMRegistrar.onDestroy(this);
                } catch (Exception e) {
                        LOGW(TAG, "C2DM unregistration error", e);
                }

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                sp.unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onAuthSuccess(String accountName, boolean newlyAuthenticated) {
                Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                LOGD(TAG, "onAuthSuccess, account " + accountName + ", newlyAuthenticated=" + newlyAuthenticated);

                //refreshAccountDependantData();

                if (newlyAuthenticated) {
                        LOGD(TAG, "Enabling auto sync on content provider for account " + accountName);
                       // SyncHelper.updateSyncInterval(this, account);
                       // SyncHelper.requestManualSync(account);
                }

                setupAccountBox();
                populateNavDrawer();
                registerGCMClient();
        }

        @Override
        public void onAuthFailure(String accountName) {
                LOGD(TAG, "Auth failed for account " + accountName);
                //refreshAccountDependantData();
        }

       @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                if (mLoginAndAuthHelper == null || !mLoginAndAuthHelper.onActivityResult(requestCode,
                        resultCode, data)) {
                        super.onActivityResult(requestCode, resultCode, data);
                }
        }

        @Override
        public void onStop() {
                LOGD(TAG, "onStop");
                super.onStop();
                if (mLoginAndAuthHelper != null) {
                        mLoginAndAuthHelper.stop();
                }
        }

        @Override
        public void onStart() {
                LOGD(TAG, "onStart");
                super.onStart();

                // Perform one-time bootstrap setup, if needed

                startLoginProcess();
        }

        @Override
        public void onPlusInfoLoaded(String accountName) {
               setupAccountBox();
               populateNavDrawer();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
               /* if (key.equals(PrefUtils.PREF_ATTENDEE_AT_VENUE)) {
                        LOGD(TAG, "Attendee at venue preference changed, repopulating nav drawer and menu.");
                        populateNavDrawer();
                        invalidateOptionsMenu();*/
                }

        @Override
        public boolean canSwipeRefreshChildScrollUp() {
                return false;
        }

        private void trySetupSwipeRefresh() {
                mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
                if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setColorSchemeResources(
                                R.color.refresh_progress_1,
                                R.color.refresh_progress_2,
                                R.color.refresh_progress_3);
                        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                @Override
                                public void onRefresh() {
                                        requestDataRefresh();
                                }
                        });

                        if (mSwipeRefreshLayout instanceof MultiSwipeRefreshLayout) {
                                MultiSwipeRefreshLayout mswrl = (MultiSwipeRefreshLayout) mSwipeRefreshLayout;
                                mswrl.setCanChildScrollUpCallback(this);
                        }
                }
        }

        protected void requestDataRefresh() {
                Account activeAccount = AccountUtils.getActiveAccount(this);
                //ContentResolver contentResolver = getContentResolver();
                //if (contentResolver.isSyncActive(activeAccount,"com.app.shopbee")) {
                        LOGD(TAG, "Ignoring manual sync request because a sync is already in progress.");
                  //      return;
                //}
                mManualSyncRequest = true;
                LOGD(TAG, "Requesting manual data refresh.");
                //SyncHelper.requestManualSync(activeAccount);
        }

        private void setupNavDrawer() {
                // What nav drawer item should be selected?
                int selfItem = getSelfNavDrawerItem();

                mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (mDrawerLayout == null) {
                        return;
                }
                mDrawerLayout.setStatusBarBackgroundColor(
                        getResources().getColor(R.color.theme_primary_dark));


                ScrimInsetsScrollView navDrawer = (ScrimInsetsScrollView)
                        mDrawerLayout.findViewById(R.id.navdrawer);
                if (selfItem == NAVDRAWER_ITEM_INVALID) {
                        // do not show a nav drawer
                        if (navDrawer != null) {
                                ((ViewGroup) navDrawer.getParent()).removeView(navDrawer);
                        }
                        mDrawerLayout = null;
                        return;
                }

                if (navDrawer != null) {
                        final View chosenAccountContentView = findViewById(R.id.chosen_account_content_view);
                        final View chosenAccountView = findViewById(R.id.chosen_account_view);
                        final int navDrawerChosenAccountHeight = getResources().getDimensionPixelSize(
                                R.dimen.navdrawer_chosen_account_height);
                        navDrawer.setOnInsetsCallback(new ScrimInsetsScrollView.OnInsetsCallback() {
                                @Override
                                public void onInsetsChanged(Rect insets) {
                                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)
                                                chosenAccountContentView.getLayoutParams();
                                        lp.topMargin = insets.top;
                                        chosenAccountContentView.setLayoutParams(lp);

                                        ViewGroup.LayoutParams lp2 = chosenAccountView.getLayoutParams();
                                        lp2.height = navDrawerChosenAccountHeight + insets.top;
                                        chosenAccountView.setLayoutParams(lp2);
                                }
                        });
                }

                if (mActionBarToolbar != null) {
                        mActionBarToolbar.setNavigationIcon(R.drawable.ic_drawer);

                        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                        mDrawerLayout.openDrawer(GravityCompat.START);
                                }
                        });
                }

                mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                                // run deferred action, if we have one
                                if (mDeferredOnDrawerClosedRunnable != null) {
                                        mDeferredOnDrawerClosedRunnable.run();
                                        mDeferredOnDrawerClosedRunnable = null;
                                }
                                if (mAccountBoxExpanded) {
                                        mAccountBoxExpanded = false;
                                        setupAccountBoxToggle();
                                }
                                onNavDrawerStateChanged(false, false);
                        }

                        @Override
                        public void onDrawerOpened(View drawerView) {
                                onNavDrawerStateChanged(true, false);
                        }

                        @Override
                        public void onDrawerStateChanged(int newState) {
                                onNavDrawerStateChanged(isNavDrawerOpen(), newState != DrawerLayout.STATE_IDLE);
                        }

                        @Override
                        public void onDrawerSlide(View drawerView, float slideOffset) {
                                onNavDrawerSlide(slideOffset);
                        }
                });

                mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

                // populate the nav drawer with the correct items
                populateNavDrawer();

                // When the user runs the app for the first time, we want to land them with the
                // navigation drawer open. But just the first time.
                if (!PrefUtils.isWelcomeDone(this)) {
                        // first run of the app starts with the nav drawer open
                        PrefUtils.markWelcomeDone(this);
                        mDrawerLayout.openDrawer(GravityCompat.START);

                }
        }

        protected int getSelfNavDrawerItem() {
                return NAVDRAWER_ITEM_INVALID;
        }

        private void setupAccountBoxToggle() {
                int selfItem = getSelfNavDrawerItem();
                if (mDrawerLayout == null || selfItem == NAVDRAWER_ITEM_INVALID) {
                        // this Activity does not have a nav drawer
                        return;
                }
                mExpandAccountBoxIndicator.setImageResource(mAccountBoxExpanded
                        ? R.drawable.ic_drawer_accounts_collapse
                        : R.drawable.ic_drawer_accounts_expand);
                int hideTranslateY = -mAccountListContainer.getHeight() / 4; // last 25% of animation
                if (mAccountBoxExpanded && mAccountListContainer.getTranslationY() == 0) {
                        // initial setup
                        mAccountListContainer.setAlpha(0);
                        mAccountListContainer.setTranslationY(hideTranslateY);
                }

                AnimatorSet set = new AnimatorSet();
                set.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                                mDrawerItemsListContainer.setVisibility(mAccountBoxExpanded
                                        ? View.INVISIBLE : View.VISIBLE);
                                mAccountListContainer.setVisibility(mAccountBoxExpanded
                                        ? View.VISIBLE : View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                                onAnimationEnd(animation);
                        }
                });

                if (mAccountBoxExpanded) {
                        mAccountListContainer.setVisibility(View.VISIBLE);
                        AnimatorSet subSet = new AnimatorSet();
                        subSet.playTogether(
                                ObjectAnimator.ofFloat(mAccountListContainer, View.ALPHA, 1)
                                        .setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION),
                                ObjectAnimator.ofFloat(mAccountListContainer, View.TRANSLATION_Y, 0)
                                        .setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION));
                        set.playSequentially(
                                ObjectAnimator.ofFloat(mDrawerItemsListContainer, View.ALPHA, 0)
                                        .setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION),
                                subSet);
                        set.start();
                } else {
                        mDrawerItemsListContainer.setVisibility(View.VISIBLE);
                        AnimatorSet subSet = new AnimatorSet();
                        subSet.playTogether(
                                ObjectAnimator.ofFloat(mAccountListContainer, View.ALPHA, 0)
                                        .setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION),
                                ObjectAnimator.ofFloat(mAccountListContainer, View.TRANSLATION_Y,
                                        hideTranslateY)
                                        .setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION));
                        set.playSequentially(
                                subSet,
                                ObjectAnimator.ofFloat(mDrawerItemsListContainer, View.ALPHA, 1)
                                        .setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION));
                        set.start();
                }

                set.start();
        }

        protected void onNavDrawerStateChanged(boolean isOpen, boolean isAnimating) {
                if (mActionBarAutoHideEnabled && isOpen) {
                        autoShowOrHideActionBar(true);
                }
        }

        protected void autoShowOrHideActionBar(boolean show) {
                if (show == mActionBarShown) {
                        return;
                }

                mActionBarShown = show;
                onActionBarAutoShowOrHide(show);
        }


        protected void onActionBarAutoShowOrHide(boolean shown) {
                if (mStatusBarColorAnimator != null) {
                        mStatusBarColorAnimator.cancel();
                }
                mStatusBarColorAnimator = ObjectAnimator.ofInt(
                        (mDrawerLayout != null) ? mDrawerLayout : null,
                        (mDrawerLayout != null) ? "statusBarBackgroundColor" : "statusBarColor",
                        shown ? Color.BLACK : mNormalStatusBarColor,
                        shown ? mNormalStatusBarColor : Color.BLACK)
                        .setDuration(250);
                if (mDrawerLayout != null) {
                        mStatusBarColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        ViewCompat.postInvalidateOnAnimation(mDrawerLayout);
                                }
                        });
                }
                mStatusBarColorAnimator.setEvaluator(ARGB_EVALUATOR);
                mStatusBarColorAnimator.start();

                updateSwipeRefreshProgressBarTop();

                for (View view : mHideableHeaderViews) {
                        if (shown) {
                                view.animate()
                                        .translationY(0)
                                        .alpha(1)
                                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                                        .setInterpolator(new DecelerateInterpolator());
                        } else {
                                view.animate()
                                        .translationY(-view.getBottom())
                                        .alpha(0)
                                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                                        .setInterpolator(new DecelerateInterpolator());
                        }
                }
        }

        private void updateSwipeRefreshProgressBarTop() {
                if (mSwipeRefreshLayout == null) {
                        return;
                }

                int progressBarStartMargin = getResources().getDimensionPixelSize(
                        R.dimen.swipe_refresh_progress_bar_start_margin);
                int progressBarEndMargin = getResources().getDimensionPixelSize(
                        R.dimen.swipe_refresh_progress_bar_end_margin);
                int top = mActionBarShown ? mProgressBarTopWhenActionBarShown : 0;
                mSwipeRefreshLayout.setProgressViewOffset(false,
                        top + progressBarStartMargin, top + progressBarEndMargin);
        }

        protected boolean isNavDrawerOpen() {
                return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
        }

        protected void onNavDrawerSlide(float offset) {}

        private void populateNavDrawer() {

                mNavDrawerItems.clear();

                // decide which items will appear in the nav drawer
                if (AccountUtils.hasActiveAccount(this)) {
                        // Only logged-in users can save sessions, so if there is no active account,
                        // there is no My Schedule
                        mNavDrawerItems.add(NAVDRAWER_ITEM_MY_PROFILE);
                } else {
                        // If no active account, show Sign In
                        mNavDrawerItems.add(NAVDRAWER_ITEM_SIGN_IN);
                }

                mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);
                // Explore is always shown
                mNavDrawerItems.add(NAVDRAWER_ITEM_STORE);

                mNavDrawerItems.add(NAVDRAWER_ITEM_INVENTORY);

                // If the attendee is on-site, show Map on the nav drawer

                mNavDrawerItems.add(NAVDRAWER_ITEM_PRODUCTS);


                mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR_SPECIAL);
                mNavDrawerItems.add(NAVDRAWER_ITEM_SETTINGS);

                createNavDrawerItems();
        }

        private void createNavDrawerItems() {
                mDrawerItemsListContainer = (ViewGroup) findViewById(R.id.navdrawer_items_list);
                if (mDrawerItemsListContainer == null) {
                        return;
                }

                mNavDrawerItemViews = new View[mNavDrawerItems.size()];
                mDrawerItemsListContainer.removeAllViews();
                int i = 0;
                for (int itemId : mNavDrawerItems) {
                        mNavDrawerItemViews[i] = makeNavDrawerItem(itemId, mDrawerItemsListContainer);
                        mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
                        ++i;
                }
        }

        private View makeNavDrawerItem(final int itemId, ViewGroup container) {
                boolean selected = getSelfNavDrawerItem() == itemId;
                int layoutToInflate = 0;
                if (itemId == NAVDRAWER_ITEM_SEPARATOR) {
                        layoutToInflate = R.layout.navdrawer_separator;
                } else if (itemId == NAVDRAWER_ITEM_SEPARATOR_SPECIAL) {
                        layoutToInflate = R.layout.navdrawer_separator;
                } else {
                        layoutToInflate = R.layout.navdrawer_item;
                }
                View view = getLayoutInflater().inflate(layoutToInflate, container, false);

                if (isSeparator(itemId)) {
                        // we are done
                        UIUtils.setAccessibilityIgnore(view);
                        return view;
                }

                ImageView iconView = (ImageView) view.findViewById(R.id.icon);
                TextView titleView = (TextView) view.findViewById(R.id.title);
                int iconId = itemId >= 0 && itemId < NAVDRAWER_ICON_RES_ID.length ?
                        NAVDRAWER_ICON_RES_ID[itemId] : 0;
                int titleId = itemId >= 0 && itemId < NAVDRAWER_TITLE_RES_ID.length ?
                        NAVDRAWER_TITLE_RES_ID[itemId] : 0;

                // set icon and text
                iconView.setVisibility(iconId > 0 ? View.VISIBLE : View.GONE);
                if (iconId > 0) {
                        iconView.setImageResource(iconId);
                }
                titleView.setText(getString(titleId));

                formatNavDrawerItem(view, itemId, selected);

                view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                onNavDrawerItemClicked(itemId);
                        }
                });

                return view;
        }

        private void formatNavDrawerItem(View view, int itemId, boolean selected) {
                if (isSeparator(itemId)) {
                        // not applicable
                        return;
                }

                ImageView iconView = (ImageView) view.findViewById(R.id.icon);
                TextView titleView = (TextView) view.findViewById(R.id.title);

                if (selected) {
                        view.setBackgroundResource(R.drawable.selected_navdrawer_item_background);
                }

                // configure its appearance according to whether or not it's selected
                titleView.setTextColor(selected ?
                        getResources().getColor(R.color.navdrawer_text_color_selected) :
                        getResources().getColor(R.color.navdrawer_text_color));
                iconView.setColorFilter(selected ?
                        getResources().getColor(R.color.navdrawer_icon_tint_selected) :
                        getResources().getColor(R.color.navdrawer_icon_tint));
        }

        private boolean isSeparator(int itemId) {
                return itemId == NAVDRAWER_ITEM_SEPARATOR || itemId == NAVDRAWER_ITEM_SEPARATOR_SPECIAL;
        }

        private void onNavDrawerItemClicked(final int itemId) {
                if (itemId == getSelfNavDrawerItem()) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        return;
                }

                if (isSpecialItem(itemId)) {
                        goToNavDrawerItem(itemId);
                } else {
                        // launch the target Activity after a short delay, to allow the close animation to play
                        mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                        goToNavDrawerItem(itemId);
                                }
                        }, NAVDRAWER_LAUNCH_DELAY);

                        // change the active item on the list so the user can see the item changed
                        setSelectedNavDrawerItem(itemId);
                        // fade out the main content
                        View mainContent = findViewById(R.id.main_content);
                        if (mainContent != null) {
                                mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
                        }
                }

                mDrawerLayout.closeDrawer(GravityCompat.START);
        }

        private boolean isSpecialItem(int itemId) {
                return itemId == NAVDRAWER_ITEM_SETTINGS;
        }

        private void goToNavDrawerItem(int item) {
                Intent intent;
                switch (item) {
                        case NAVDRAWER_ITEM_MY_PROFILE:
                                //intent = new Intent(this, MyScheduleActivity.class);
                                //startActivity(intent);
                                //finish();
                                break;
                        case NAVDRAWER_ITEM_STORE:
                                //intent = new Intent(this, BrowseSessionsActivity.class);
                               // startActivity(intent);
                               // finish();
                                break;
                        case NAVDRAWER_ITEM_INVENTORY:
                               // intent = new Intent(this, UIUtils.getMapActivityClass(this));
                               // startActivity(intent);
                              //  finish();
                                break;
                        case NAVDRAWER_ITEM_PRODUCTS:
                              //  intent = new Intent(this, SocialActivity.class);
                             //   startActivity(intent);
                             //   finish();
                                break;
                        case NAVDRAWER_ITEM_SIGN_IN:
                                signInOrCreateAnAccount();
                                break;
                        case NAVDRAWER_ITEM_SETTINGS:
                              //  intent = new Intent(this, SettingsActivity.class);
                              //  startActivity(intent);
                                break;

                }
        }

        private void signInOrCreateAnAccount() {
                //Get list of accounts on device.
                AccountManager am = AccountManager.get(BaseActivity.this);
                Account[] accountArray = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                if (accountArray.length == 0) {
                        //Send the user to the "Add Account" page.
                        Intent intent = new Intent(Settings.ACTION_ADD_ACCOUNT);
                        intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[] {"com.google"});
                        startActivity(intent);
                } else {
                        //Try to log the user in with the first account on the device.
                        startLoginProcess();
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                }
        }

        private void startLoginProcess() {
                LOGD(TAG, "Starting login process.");
                if (!AccountUtils.hasActiveAccount(this)) {
                        LOGD(TAG, "No active account, attempting to pick a default.");
                        String defaultAccount = getDefaultAccount();
                        if (defaultAccount == null) {
                                LOGE(TAG, "Failed to pick default account (no accounts). Failing.");
                                complainMustHaveGoogleAccount();
                                return;
                        }
                        LOGD(TAG, "Default to: " + defaultAccount);
                        AccountUtils.setActiveAccount(this, defaultAccount);
                }

                if (!AccountUtils.hasActiveAccount(this)) {
                        LOGD(TAG, "Can't proceed with login -- no account chosen.");
                        return;
                } else {
                        LOGD(TAG, "Chosen account: " + AccountUtils.getActiveAccountName(this));
                }

                String accountName = AccountUtils.getActiveAccountName(this);
                LOGD(TAG, "Chosen account: " + AccountUtils.getActiveAccountName(this));

                if (mLoginAndAuthHelper != null && mLoginAndAuthHelper.getAccountName().equals(accountName)) {
                        LOGD(TAG, "Helper already set up; simply starting it.");
                        mLoginAndAuthHelper.start();
                        return;
                }

                LOGD(TAG, "Starting login process with account " + accountName);

                if (mLoginAndAuthHelper != null) {
                        LOGD(TAG, "Tearing down old Helper, was " + mLoginAndAuthHelper.getAccountName());
                        if (mLoginAndAuthHelper.isStarted()) {
                                LOGD(TAG, "Stopping old Helper");
                                mLoginAndAuthHelper.stop();
                        }
                        mLoginAndAuthHelper = null;
                }

                LOGD(TAG, "Creating and starting new Helper with account: " + accountName);
                mLoginAndAuthHelper = new LoginAndAuthHelper(this, this, accountName);
                mLoginAndAuthHelper.start();
        }

        private String getDefaultAccount() {
                // Choose first account on device.
                LOGD(TAG, "Choosing default account (first account on device)");
                AccountManager am = AccountManager.get(this);
                Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                if (accounts.length == 0) {
                        // No Google accounts on device.
                        LOGW(TAG, "No Google accounts on device; not setting default account.");
                        return null;
                }

                LOGD(TAG, "Default account is: " + accounts[0].name);
                return accounts[0].name;
        }


        private void complainMustHaveGoogleAccount() {
                LOGD(TAG, "Complaining about missing Google account.");
                new AlertDialog.Builder(this)
                        .setTitle(R.string.google_account_required_title)
                        .setMessage(R.string.google_account_required_message)
                        .setPositiveButton(R.string.add_account, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                        promptAddAccount();
                                }
                        })
                        .setNegativeButton(R.string.not_now, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                }
                        })
                        .show();
        }

        private void promptAddAccount() {
                Intent intent = new Intent(Settings.ACTION_ADD_ACCOUNT);
                intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[]{"com.google"});
                startActivity(intent);
                finish();
        }

        private void setSelectedNavDrawerItem(int itemId) {
                if (mNavDrawerItemViews != null) {
                        for (int i = 0; i < mNavDrawerItemViews.length; i++) {
                                if (i < mNavDrawerItems.size()) {
                                        int thisItemId = mNavDrawerItems.get(i);
                                        formatNavDrawerItem(mNavDrawerItemViews[i], thisItemId, itemId == thisItemId);
                                }
                        }
                }
        }

        private void setupAccountBox() {
                mAccountListContainer = (LinearLayout) findViewById(R.id.account_list);

                if (mAccountListContainer == null) {
                        //This activity does not have an account box
                        return;
                }

                final View chosenAccountView = findViewById(R.id.chosen_account_view);
                Account chosenAccount = AccountUtils.getActiveAccount(this);
                if (chosenAccount == null) {
                        // No account logged in; hide account box
                        chosenAccountView.setVisibility(View.GONE);
                        mAccountListContainer.setVisibility(View.GONE);
                        return;
                } else {
                        chosenAccountView.setVisibility(View.VISIBLE);
                        mAccountListContainer.setVisibility(View.INVISIBLE);
                }

                AccountManager am = AccountManager.get(this);
                Account[] accountArray = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                List<Account> accounts = new ArrayList<Account>(Arrays.asList(accountArray));
                accounts.remove(chosenAccount);

                ImageView coverImageView = (ImageView) chosenAccountView.findViewById(R.id.profile_cover_image);
                ImageView profileImageView = (ImageView) chosenAccountView.findViewById(R.id.profile_image);
                TextView nameTextView = (TextView) chosenAccountView.findViewById(R.id.profile_name_text);
                TextView email = (TextView) chosenAccountView.findViewById(R.id.profile_email_text);
                mExpandAccountBoxIndicator = (ImageView) findViewById(R.id.expand_account_box_indicator);

                String name = AccountUtils.getPlusName(this);
                if (name == null) {
                        nameTextView.setVisibility(View.GONE);
                } else {
                        nameTextView.setVisibility(View.VISIBLE);
                        nameTextView.setText(name);
                }

                String imageUrl = AccountUtils.getPlusImageUrl(this);
                if (imageUrl != null) {
                        mImageLoader.loadImage(imageUrl, profileImageView);
                }

                String coverImageUrl = AccountUtils.getPlusCoverUrl(this);
                if (coverImageUrl != null) {
                        mImageLoader.loadImage(coverImageUrl, coverImageView);
                } else {
                        coverImageView.setImageResource(R.drawable.default_cover);
                }

                email.setText(chosenAccount.name);

                if (accounts.isEmpty()) {
                        // There's only one account on the device, so no need for a switcher.
                        mExpandAccountBoxIndicator.setVisibility(View.GONE);
                        mAccountListContainer.setVisibility(View.GONE);
                        chosenAccountView.setEnabled(false);
                        return;
                }

                chosenAccountView.setEnabled(true);

                mExpandAccountBoxIndicator.setVisibility(View.VISIBLE);
                chosenAccountView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                mAccountBoxExpanded = !mAccountBoxExpanded;
                                setupAccountBoxToggle();
                        }
                });
                setupAccountBoxToggle();

                populateAccountList(accounts);
        }


        private void populateAccountList(List<Account> accounts) {
                mAccountListContainer.removeAllViews();

                LayoutInflater layoutInflater = LayoutInflater.from(this);
                for (Account account : accounts) {
                        View itemView = layoutInflater.inflate(R.layout.list_item_account,
                                mAccountListContainer, false);
                        ((TextView) itemView.findViewById(R.id.profile_email_text))
                                .setText(account.name);
                        final String accountName = account.name;
                        String imageUrl = AccountUtils.getPlusImageUrl(this, accountName);
                        if (!TextUtils.isEmpty(imageUrl)) {
                                mImageLoader.loadImage(imageUrl,
                                        (ImageView) itemView.findViewById(R.id.profile_image));
                        }
                        itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                        ConnectivityManager cm = (ConnectivityManager)
                                                getSystemService(CONNECTIVITY_SERVICE);
                                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                                        if (activeNetwork == null || !activeNetwork.isConnected()) {
                                                // if there's no network, don't try to change the selected account
                                                Toast.makeText(BaseActivity.this, R.string.no_connection_cant_login,
                                                        Toast.LENGTH_SHORT).show();
                                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                                return;
                                        } else {
                                                LOGD(TAG, "User requested switch to account: " + accountName);
                                                AccountUtils.setActiveAccount(BaseActivity.this, accountName);
                                                onAccountChangeRequested();
                                                startLoginProcess();
                                                mAccountBoxExpanded = false;
                                                setupAccountBoxToggle();
                                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                                setupAccountBox();
                                        }
                                }
                        });
                        mAccountListContainer.addView(itemView);
                }
        }


        protected void onAccountChangeRequested() {
                // override if you want to be notified when another account has been selected account has changed
        }

        protected Toolbar getActionBarToolbar() {
                if (mActionBarToolbar == null) {
                        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
                        if (mActionBarToolbar != null) {
                                setSupportActionBar(mActionBarToolbar);
                        }
                }
                return mActionBarToolbar;
        }

      @Override
        public void setContentView(int layoutResID) {
                super.setContentView(layoutResID);
                getActionBarToolbar();
        }

} //end class

