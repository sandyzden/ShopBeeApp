package com.app.shopbee.gcm;

/**
 * Created by sendilkumar on 13/06/15.
 */
import com.google.android.gcm.GCMBroadcastReceiver;

import android.content.Context;

public class GCMRedirectedBroadcastReceiver extends GCMBroadcastReceiver {

    @Override
    protected String getGCMIntentServiceClassName(Context context) {
        return GCMIntentService.class.getCanonicalName();
    }

}
