package com.app.shopbee.database;

/**
 * Created by sendilkumar on 27/06/15.
 */
import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.shopbee.model.Store;

public class DBController  extends SQLiteOpenHelper {

    public DBController(Context applicationcontext) {
        super(applicationcontext, "store.db", null, 1);
    }

    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {
        String query;
        query = "CREATE TABLE store ( storeName TEXT, locality TEXT, contactNo TEXT)";
        database.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS store ";
        database.execSQL(query);
        onCreate(database);
    }

    /**
     * Inserts User into SQLite DB
     *
     * @param queryValues
     */
    /*public void insertUser(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", queryValues.get("userId"));
        values.put("userName", queryValues.get("userName"));
        database.insert("users", null, values);
        database.close();
    }*/

    /**
     * Get list of Users from SQLite DB as Array List
     *
     * @return
     */
    public ArrayList<Store> getAllStores() {
        ArrayList<Store> storesList;
        storesList = new ArrayList<Store>();
        String selectQuery = "SELECT  * FROM store";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Store store = new Store();
                store.setStoreName(cursor.getString(0));
                store.setLocality(cursor.getString(1));
                store.setContactNo(cursor.getColumnName(2));
                storesList.add(store);
            } while (cursor.moveToNext());
        }
        database.close();
        return storesList;
    }


    public void insertStore(Store store) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("storeName", store.getStoreName());
        values.put("locality", store.getLocality());
        values.put("contactNo", store.getContactNo());
        database.insert("store", null, values);
        database.close();
    }

    public boolean isTableExists(String tableName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", tableName});
        if (!cursor.moveToFirst())
        {
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 1;
    }
}
