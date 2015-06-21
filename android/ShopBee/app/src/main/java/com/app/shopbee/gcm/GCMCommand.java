package com.app.shopbee.gcm;

/**
 * Created by sendilkumar on 13/06/15.
 */
import android.content.Context;

public abstract class GCMCommand {
    public abstract void execute(Context context, String type, String extraData);
}
