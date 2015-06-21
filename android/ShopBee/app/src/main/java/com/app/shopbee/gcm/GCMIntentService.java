package com.app.shopbee.gcm;

/**
 * Created by sendilkumar on 13/06/15.
 */

import com.app.shopbee.Config;
//import com.app.shopbee.gcm.command.*;
import com.google.android.gcm.GCMBaseIntentService;
import com.app.shopbee.util.AccountUtils;

import android.content.Context;
import android.content.Intent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.app.shopbee.util.LogUtils.LOGD;
import static com.app.shopbee.util.LogUtils.LOGE;
import static com.app.shopbee.util.LogUtils.LOGI;
import static com.app.shopbee.util.LogUtils.LOGW;
import static com.app.shopbee.util.LogUtils.makeLogTag;


public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = makeLogTag("GCM");

    private static final Map<String, GCMCommand> MESSAGE_RECEIVERS;
    static {
        // Known messages and their GCM message receivers
        Map <String, GCMCommand> receivers = new HashMap<String, GCMCommand>();
        //receivers.put("sync_schedule", new SyncCommand());
        //receivers.put("sync_user", new SyncUserCommand());
        //receivers.put("notification", new NotificationCommand());
        MESSAGE_RECEIVERS = Collections.unmodifiableMap(receivers);
    }

    public GCMIntentService() {
        super(Config.GCM_SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String regId) {
        LOGI(TAG, "Device registered: regId=" + regId);
        ServerUtilities.register(context, regId, AccountUtils.getPlusProfileId(this));
    }

    @Override
    protected void onUnregistered(Context context, String regId) {
        LOGI(TAG, "Device unregistered");
        if (ServerUtilities.isRegisteredOnServer(context, AccountUtils.getPlusProfileId(this))) {
            ServerUtilities.unregister(context, regId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            LOGD(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        String action = intent.getStringExtra("action");
        String extraData = intent.getStringExtra("extraData");
        LOGD(TAG, "Got GCM message, action=" + action + ", extraData=" + extraData);

        if (action == null) {
            LOGE(TAG, "Message received without command action");
            return;
        }

        action = action.toLowerCase();
        GCMCommand command = MESSAGE_RECEIVERS.get(action);
        if (command == null) {
            LOGE(TAG, "Unknown command received: " + action);
        } else {
            command.execute(this, action, extraData);
        }

    }

    @Override
    public void onError(Context context, String errorId) {
        LOGE(TAG, "Received error: " + errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        LOGW(TAG, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }


}

