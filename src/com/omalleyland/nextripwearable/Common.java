package com.omalleyland.nextripwearable;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ListView;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by D00M
 */
public class Common {
    /* Application/Debug Constant */
    public static final String      APPLICATION_NAME                        = "NexTripWearable";
    public static final int         UNKNOWN                                 = -1;

    /* SQLite DB Constants */
    public static final int         DATABASE_VERSION                        = 4;
    public static final String      DATABASE_NAME                           = APPLICATION_NAME + "DB";

    /* SQLite Categories Table Constants */
    public static final String      tblFAVORITES                            = "Favorites";
    public static final String      colFAVORITES_IMAGE_ID                   = "ImageID";
    public static final int         colFAVORITES_IMAGE_ID_INDEX             = 0;
    public static final String      colFAVORITES_ROUTE_ID                   = "RouteID";
    public static final int         colFAVORITES_ROUTE_ID_INDEX             = 1;
    public static final String      colFAVORITES_ROUTE_DESCRIPTION          = "RouteDescription";
    public static final int         colFAVORITES_ROUTE_DESCRIPTION_INDEX    = 2;
    public static final String      colFAVORITES_DIRECTION_ID               = "DirectionID";
    public static final int         colFAVORITES_DIRECTION_ID_INDEX         = 3;
    public static final String      colFAVORITES_DIRECTION_TEXT             = "DirectionText";
    public static final int         colFAVORITES_DIRECTION_TEXT_INDEX       = 4;
    public static final String      colFAVORITES_STOP_ID                    = "StopID";
    public static final int         colFAVORITES_STOP_ID_INDEX              = 5;
    public static final String      colFAVORITES_STOP_DESCRIPTION           = "StopDescription";
    public static final int         colFAVORITES_STOP_DESCRIPTION_INDEX     = 6;
    public static final String      colFAVORITES_TYPE                       = "FavoriteType";
    public static final int         colFAVORITES_TYPE_INDEX                 = 7;


    public static final String[]    colFAVORITES_ALL                    = {colFAVORITES_IMAGE_ID,
                                                                            colFAVORITES_ROUTE_ID,
                                                                            colFAVORITES_ROUTE_DESCRIPTION,
                                                                            colFAVORITES_DIRECTION_ID,
                                                                            colFAVORITES_DIRECTION_TEXT,
                                                                            colFAVORITES_STOP_ID,
                                                                            colFAVORITES_STOP_DESCRIPTION,
                                                                            colFAVORITES_TYPE};

    public static final String      CREATE_FAVORITE_TABLE               = "CREATE TABLE " + tblFAVORITES + "(" +
                                                                            colFAVORITES_IMAGE_ID + " integer NOT NULL, " +
                                                                            colFAVORITES_ROUTE_ID + " text NOT NULL, " +
                                                                            colFAVORITES_ROUTE_DESCRIPTION + " text, " +
                                                                            colFAVORITES_DIRECTION_ID + " text NOT NULL, " +
                                                                            colFAVORITES_DIRECTION_TEXT + " text, " +
                                                                            colFAVORITES_STOP_ID + " text NOT NULL," +
                                                                            colFAVORITES_STOP_DESCRIPTION + " text NULL," +
                                                                            colFAVORITES_TYPE + " integer NOT NULL," +
                                                                            "PRIMARY KEY(" + colFAVORITES_ROUTE_ID + "," +
                                                                                colFAVORITES_DIRECTION_ID + "," +
                                                                                colFAVORITES_STOP_ID + ")" +
                                                                            ");";

    public static final String DROP_FAVORITE_TABLE                      = "DROP TABLE IF EXISTS " + tblFAVORITES;

    public static int STOP_TYPE_ROUTE                                   = 0;
    public static int STOP_TYPE_STOP_ID                                 = 1;
    public static String ROUTE_FRAGMENT_TAG                             = "ROUTE_FRAGMENT";
    public static String SEARCH_STOP_FRAGMENT_TAG                       = "SEARCH_STOP_FRAGMENT";
    public static String FAVORITE_FRAGMENT_TAG                          = "FAVORITE_FRAGMENT";
    public static int PURCHASES_REQUEST_CODE                            = 1001;
    public static int IN_APP_INTENT_CODE                                = 1002;
    public static String PREMIUM_KEY_PURCHASE_ID                        = "premiumkey";
    public static String REMOVE_ADS_PURCHASE_ID                         = "removeads";
    public static String SAVE_FAVORITES_PURCHASE_ID                     = "savefavorites";

    //Global Variables for quick reference of InApp Purchases
    public static boolean PremiumMode                                   = false;
    public static boolean ShowAds                                       = true;
    public static boolean EnableFavorites                               = false;

    //Local Private Variables
    private static Context Ctx;
    private static IInAppBillingService InAppPurchaseService;
    private static ArrayList<PurchaseItem> PurchaseList;
    private static IPurchasesResult Caller;

    public static void LoadPreferences() {

        PremiumMode = Ctx.getSharedPreferences(APPLICATION_NAME, Context.MODE_PRIVATE).getBoolean(PREMIUM_KEY_PURCHASE_ID, false);
        ShowAds = Ctx.getSharedPreferences(Common.APPLICATION_NAME, Context.MODE_PRIVATE).getBoolean(REMOVE_ADS_PURCHASE_ID, true);
        EnableFavorites = Ctx.getSharedPreferences(Common.APPLICATION_NAME, Context.MODE_PRIVATE).getBoolean(SAVE_FAVORITES_PURCHASE_ID, false);

        Log.d("NexTripWearable", "Preferences = " + Boolean.toString(PremiumMode) + " : " + Boolean.toString(ShowAds) + " : " + Boolean.toString(EnableFavorites));
    }

    public static ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Common.InAppPurchaseService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            Common.InAppPurchaseService = IInAppBillingService.Stub.asInterface(service);

            BuildPurchaseList(null, null);
        }
    };

    public static void InitInAppPurchasing(Context ctx, IPurchasesResult caller) {
        Ctx = ctx;
        PurchaseList = new ArrayList<PurchaseItem>();
        Caller = caller;
        Ctx.bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"), mServiceConn, Context.BIND_AUTO_CREATE);
    }

    public static void CleanupInAppPurchasing() {
        Ctx.unbindService(mServiceConn);
    }

    public static boolean CanPurchase(int SelectedPosition) {
        return !PurchaseList.get(SelectedPosition).getPrice().equals("Purchased");
    }

    public static Bundle getBuyIntent(String PackageName, int SelectedPosition) {
        Bundle bundle = null;

        try {
            bundle = InAppPurchaseService.getBuyIntent(3, PackageName, PurchaseList.get(SelectedPosition).getProductID(), "inapp", "");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private static ArrayList<String> LoadPurchasedItems() {
        ArrayList<String> PurchasedItems = new ArrayList<String>();

        //Check for owned items
        try {
            Bundle ownedItems = InAppPurchaseService.getPurchases(3, "com.omalleyland.nextripwearable", "inapp", null);

            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");

                for(String purchaseData: purchaseDataList) {
                    JSONObject object = new JSONObject(purchaseData);
                    String sku = object.getString("productId");
                    Log.d("NexTripWearable", "ADDING PURCHASE :: " + sku);
                    PurchasedItems.add(sku);

                    if(sku.equals(Common.PREMIUM_KEY_PURCHASE_ID)) {
                        Common.PremiumMode = true;
                        Ctx.getSharedPreferences(Common.APPLICATION_NAME, Context.MODE_PRIVATE).edit().putBoolean(Common.PREMIUM_KEY_PURCHASE_ID, true).commit();
                    }
                    else if(sku.equals(Common.REMOVE_ADS_PURCHASE_ID)) {
                        Ctx.getSharedPreferences(Common.APPLICATION_NAME, Context.MODE_PRIVATE).edit().putBoolean(Common.REMOVE_ADS_PURCHASE_ID, false).commit();
                        Common.ShowAds = false;
                    }
                    else if(sku.equals(Common.SAVE_FAVORITES_PURCHASE_ID)) {
                        Ctx.getSharedPreferences(Common.APPLICATION_NAME, Context.MODE_PRIVATE).edit().putBoolean(Common.SAVE_FAVORITES_PURCHASE_ID, true).commit();
                        Common.EnableFavorites = true;
                    }

                }
            }


        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return PurchasedItems;
    }


    public static void BuildPurchaseList(Activity activity, ListView lvPurchases) {

        PurchaseList = new ArrayList<PurchaseItem>();

        ArrayList<String> PurchasedItems = LoadPurchasedItems();

        ArrayList<String> skuList = new ArrayList<String> ();
        skuList.add(PREMIUM_KEY_PURCHASE_ID);
        skuList.add(REMOVE_ADS_PURCHASE_ID);
        skuList.add(SAVE_FAVORITES_PURCHASE_ID);

        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

        try {
            Bundle skuDetails = InAppPurchaseService.getSkuDetails(3, "com.omalleyland.nextripwearable", "inapp", querySkus);

            int response = skuDetails.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                for (String thisResponse : responseList) {
                    JSONObject object = new JSONObject(thisResponse);

                    if(
                        (object.getString("productId").equals(PREMIUM_KEY_PURCHASE_ID) && !PurchasedItems.contains(REMOVE_ADS_PURCHASE_ID) && !PurchasedItems.contains(SAVE_FAVORITES_PURCHASE_ID)) ||
                        ((object.getString("productId").equals(REMOVE_ADS_PURCHASE_ID) || object.getString("productId").equals(SAVE_FAVORITES_PURCHASE_ID)) && !PurchasedItems.contains(PREMIUM_KEY_PURCHASE_ID)) ||
                        (!object.getString("productId").equals(PREMIUM_KEY_PURCHASE_ID) && !object.getString("productId").equals(REMOVE_ADS_PURCHASE_ID) && !object.getString("productId").equals(SAVE_FAVORITES_PURCHASE_ID))
                      ) {
                        PurchaseItem purchaseItem = new PurchaseItem();
                        purchaseItem.setName(object.getString("title").replace("(NexTrip Companion)", ""));
                        purchaseItem.setProductID(object.getString("productId"));

                        Log.d("NexTripWearable", "CHECKING PURCHASE :: " + purchaseItem.toString());
                        if(PurchasedItems.contains(purchaseItem.toString())) {
                            purchaseItem.setPrice("Purchased");
                        }
                        else {
                            purchaseItem.setPrice(object.getString("price"));
                        }
                        purchaseItem.setDescription(object.getString("description"));
                        PurchaseList.add(purchaseItem);
                    }
                }
            }

            if((activity != null) && (lvPurchases != null)) {
                PurchaseListAdapter adapter = new PurchaseListAdapter(activity, PurchaseList);
                lvPurchases.setAdapter(adapter);
            }
            if(Caller != null) Caller.ProcessPurchases();

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public interface IPurchasesResult {
        public void ProcessPurchases();
    }
}
