package com.app.shopbee.util;

import android.content.Context;
import android.os.Build;
import android.view.View;

import com.app.shopbee.Config;

/**
 * Created by sendilkumar on 13/06/15.
 */
public class UIUtils {

    private static final long sAppLoadTime = System.currentTimeMillis();

    public static long getCurrentTime(final Context context) {
        if (Config.DEBUG) {
            return context.getSharedPreferences("mock_data", Context.MODE_PRIVATE)
                    .getLong("mock_current_time", System.currentTimeMillis())
                    + System.currentTimeMillis() - sAppLoadTime;
//            return ParserUtils.parseTime("2012-06-27T09:44:45.000-07:00")
//                    + System.currentTimeMillis() - sAppLoadTime;
        } else {
            return System.currentTimeMillis();
        }
    }

    public static void setAccessibilityIgnore(View view) {
        view.setClickable(false);
        view.setFocusable(false);
        view.setContentDescription("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        }
    }

}
