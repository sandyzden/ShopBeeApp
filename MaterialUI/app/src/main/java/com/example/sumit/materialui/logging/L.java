package com.example.sumit.materialui.logging;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by sumit on 08/05/2015.
 */
public class L {
    public static void m(String message) { Log.d("VIVZ", "" + message);}

    public static void t(Context context, String message) {
        Toast.makeText(context, message+"", Toast.LENGTH_SHORT).show();
    }
}
