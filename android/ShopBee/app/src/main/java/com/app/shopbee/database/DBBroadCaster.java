package com.app.shopbee.database;

/**
 * Created by sendilkumar on 27/06/15.
 */
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class DBBroadCaster extends BroadcastReceiver{

    static final String hostName = "192.168.1.2";
    static final String port = "8080";

    @Override
    public void onReceive(final Context context, Intent intent) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.get("http://192.168.1.3:8080/StoreServer/getStoreByUser/sandy",params ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if(obj.getInt("count") != 0)
                    {
                        final Intent intnt = new Intent(context, DBService.class);
                        intnt.putExtra("intntdata", "Unsynced Rows Count "+obj.getInt("count"));
                        context.startService(intnt);
                    }
                    else
                    {
                        Toast.makeText(context, "Sync not needed", Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                if(statusCode == 404){
                    Toast.makeText(context, "404", Toast.LENGTH_SHORT).show();
                }else if(statusCode == 500){
                    Toast.makeText(context, "500", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Error occured!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
