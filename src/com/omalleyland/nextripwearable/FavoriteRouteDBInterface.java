package com.omalleyland.nextripwearable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by D00M
 */
public class FavoriteRouteDBInterface {

    private final String className;
    private DBHelper dbHelper;

    public FavoriteRouteDBInterface(Context ctx){

        className = getClass().toString();
        Log.v(className, "FavoriteRouteDBInterface(Context) Constructor");
        dbHelper = new DBHelper(ctx);
    }

    public Route cursorToRoute(Cursor cursor) {

        Log.v(className, "Writing Cursor to Debit Object");

        Route FavoriteRoute = new Route();
        try {
            FavoriteRoute.setImageID(cursor.getInt(Common.colFAVORITES_IMAGE_ID_INDEX));
            FavoriteRoute.setRouteID(cursor.getString(Common.colFAVORITES_ROUTE_ID_INDEX));
            FavoriteRoute.setRouteDescription(cursor.getString(Common.colFAVORITES_ROUTE_DESCRIPTION_INDEX));
            FavoriteRoute.setDirectionID(cursor.getString(Common.colFAVORITES_DIRECTION_ID_INDEX));
            FavoriteRoute.setDirectionText(cursor.getString(Common.colFAVORITES_DIRECTION_TEXT_INDEX));
            FavoriteRoute.setStopID(cursor.getString(Common.colFAVORITES_STOP_ID_INDEX));
            FavoriteRoute.setStopDescription(cursor.getString(Common.colFAVORITES_STOP_DESCRIPTION_INDEX));
        }
        catch (Exception e) {
            Log.e(className, "FavoriteRouteDBInterface.cursorToRoute :: " + e.getMessage());
            FavoriteRoute = null;
        }

        return FavoriteRoute;
    }

    public long addFavorite(Route FavoriteRoute) {
        SQLiteDatabase db;
        NumberFormat numberFormat = DecimalFormat.getInstance();
        numberFormat.setMinimumIntegerDigits(1);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setRoundingMode(RoundingMode.DOWN);
        long insertId = -1;

        if(FavoriteRoute != null) {
            Log.d(className, "Adding Favorite Route To Database :: " +
                    "Image ID = " + Integer.toString(FavoriteRoute.getImageID()) +
                    ", Route ID = " + FavoriteRoute.getRouteID() +
                    ", Route Description = " + FavoriteRoute.getRouteDescription() +
                    ", Direction ID = " + FavoriteRoute.getDirectionID() +
                    ", Direction Text = " + FavoriteRoute.getDirectionText() +
                    ", Stop ID= " + FavoriteRoute.getStopID() +
                    ", Stop Description= " + FavoriteRoute.getStopDescription());
            try {
                db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(Common.colFAVORITES_IMAGE_ID, FavoriteRoute.getImageID());
                values.put(Common.colFAVORITES_ROUTE_ID, FavoriteRoute.getRouteID());
                values.put(Common.colFAVORITES_ROUTE_DESCRIPTION, FavoriteRoute.getRouteDescription());
                values.put(Common.colFAVORITES_DIRECTION_ID, FavoriteRoute.getDirectionID());
                values.put(Common.colFAVORITES_DIRECTION_TEXT, FavoriteRoute.getDirectionText());
                values.put(Common.colFAVORITES_STOP_ID, FavoriteRoute.getStopID());
                values.put(Common.colFAVORITES_STOP_DESCRIPTION, FavoriteRoute.getStopDescription());
                Log.v(className, "Inserting into Favorites Table");
                insertId = db.insert(Common.tblFAVORITES, null, values);
            }
            catch (Exception e) {
                Log.e(className, "Exception Adding Favorite :: " + e.getMessage());
            }

            dbHelper.close();
            Log.d(className, "insertID = " + Long.toString(insertId));
        }
        else {
            Log.e(className, "Unable to add 'NULL' Route to Favorites");
        }
        return insertId;
    }

    public int deleteFavorite(Route FavoriteRoute) {
        int result = -1;
        if(FavoriteRoute != null) {
            SQLiteDatabase db;
            Log.d(className, "Deleting Favorite From Database :: " +
                    "Image ID = " + Integer.toString(FavoriteRoute.getImageID()) +
                    ", Route ID = " + FavoriteRoute.getRouteID() +
                    ", Route Description = " + FavoriteRoute.getRouteDescription() +
                    ", Direction ID = " + FavoriteRoute.getDirectionID() +
                    ", Direction Text = " + FavoriteRoute.getDirectionText() +
                    ", Stop ID= " + FavoriteRoute.getStopID() +
                    ", Stop Description= " + FavoriteRoute.getStopDescription());
            try {
                db = dbHelper.getWritableDatabase();
                Log.v(className, "Performing Delete From Favorites Table");
                result = db.delete(Common.tblFAVORITES,
                                   Common.colFAVORITES_ROUTE_ID + "=? AND " +
                                           Common.colFAVORITES_DIRECTION_ID + "=? AND " +
                                           Common.colFAVORITES_STOP_ID + "=?",
                                   new String[] {
                                           FavoriteRoute.getRouteID(),
                                           FavoriteRoute.getDirectionID(),
                                           FavoriteRoute.getStopID()
                                   });
            }
            catch (Exception e) {
                Log.e(className, "Exception Removing Favorite :: " + e.getMessage());
            }
            dbHelper.close();
        }
        else {
            Log.e(className, "Unable to delete 'NULL' Route from Favorites");
        }
        return result;
    }

    public List<Route> getAllFavorites() {
        SQLiteDatabase db;
        List<Route> FavoriteLIst = new ArrayList<Route>();
        Route FavoriteRoute;
        Log.v(className, "Querying List of All Favorites");
        try {
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(Common.tblFAVORITES, Common.colFAVORITES_ALL ,null,null,null,null,null);
            Log.d(className, "Number of Favorite Records = " + Integer.toString(cursor.getCount()));
            if(cursor.moveToFirst()) {
                do {
                    FavoriteRoute = cursorToRoute(cursor);
                    FavoriteLIst.add(FavoriteRoute);
                    Log.d(className, "Favorite Record Returned :: " +
                            "Image ID = " + Integer.toString(FavoriteRoute.getImageID()) +
                            ", Route ID = " + FavoriteRoute.getRouteID() +
                            ", Route Description = " + FavoriteRoute.getRouteDescription() +
                            ", Direction ID = " + FavoriteRoute.getDirectionID() +
                            ", Direction Text = " + FavoriteRoute.getDirectionText() +
                            ", Stop ID= " + FavoriteRoute.getStopID() +
                            ", Stop Description= " + FavoriteRoute.getStopDescription());
                } while (cursor.moveToNext());
            }
            Log.d(className, "Favorite List Populated :: Size = " + Integer.toString(FavoriteLIst.size()));
        }
        catch (Exception e) {
            Log.e(className, "Exception Querying Favorite List:: " + e.getMessage());
        }
        dbHelper.close();
        return FavoriteLIst;
    }

}
