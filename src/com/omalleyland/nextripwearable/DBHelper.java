package com.omalleyland.nextripwearable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by D00M
 */
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, Common.DATABASE_NAME, null, Common.DATABASE_VERSION);
        Log.v(getClass().toString(), "DBHelper Constructor");
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.v(getClass().toString(), "Creating Favorite Table");
        database.execSQL(Common.CREATE_FAVORITE_TABLE);
        Log.v(getClass().toString(), "Tables Created");
    }

    // Method is called during an upgrade of the database,
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(getClass().toString(), "Upgrading database version " + Integer.toString(oldVersion) + " => " + Integer.toString(newVersion));
        Log.v(getClass().toString(), "Dropping Favorite Table");
        database.execSQL(Common.DROP_FAVORITE_TABLE);
        Log.v(getClass().toString(), "Tables Dropped");
        onCreate(database);
    }


}
