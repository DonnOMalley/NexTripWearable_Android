package com.omalleyland.nextripwearable;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MyPurchases extends Activity {

    private ListView lvPurchaseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_purchases);
        lvPurchaseList = (ListView)findViewById(R.id.lvMyPurchases);

        lvPurchaseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(Common.CanPurchase(position)) {
                    //prompt to purchase item
                    Bundle buyIntentBundle = Common.getBuyIntent(getPackageName(), position);
                    if (buyIntentBundle != null) {
                        try {
                            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

                            startIntentSenderForResult(pendingIntent.getIntentSender(), Common.IN_APP_INTENT_CODE, new Intent(), 0,0,0);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Item Already Purchased", Toast.LENGTH_LONG).show();
                }
            }
        });

        Common.BuildPurchaseList(this, lvPurchaseList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Common.IN_APP_INTENT_CODE) {
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

            if (requestCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");

                    if(sku.equals(Common.PREMIUM_KEY_PURCHASE_ID)) {
                        getSharedPreferences(Common.APPLICATION_NAME, Context.MODE_PRIVATE).edit().putBoolean(Common.PREMIUM_KEY_PURCHASE_ID, true).commit();
                        Common.PremiumMode = true;
                    }
                    else if(sku.equals(Common.REMOVE_ADS_PURCHASE_ID)) {
                        getSharedPreferences(Common.APPLICATION_NAME, Context.MODE_PRIVATE).edit().putBoolean(Common.REMOVE_ADS_PURCHASE_ID, false).commit();
                        Common.ShowAds = false;
                    }
                    else if(sku.equals(Common.SAVE_FAVORITES_PURCHASE_ID)) {
                        getSharedPreferences(Common.APPLICATION_NAME, Context.MODE_PRIVATE).edit().putBoolean(Common.SAVE_FAVORITES_PURCHASE_ID, true).commit();
                        Common.EnableFavorites = true;
                    }

                    Common.BuildPurchaseList(this, lvPurchaseList);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
