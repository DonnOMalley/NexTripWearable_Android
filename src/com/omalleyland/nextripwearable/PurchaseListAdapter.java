package com.omalleyland.nextripwearable;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by D00M
 */

public class PurchaseListAdapter extends ArrayAdapter<PurchaseItem> {
    private final Activity context;

    private ArrayList<PurchaseItem> Purchases;

    public PurchaseListAdapter(Activity context,
                       ArrayList<PurchaseItem> Purchases) {
        super(context, R.layout.purchases_list_item, Purchases);
        this.context = context;
        this.Purchases = Purchases;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.purchases_list_item, null, true);

        TextView tvPurchaseName = (TextView)rowView.findViewById(R.id.tvPurchaseName);
        TextView tvPurchasePrice = (TextView)rowView.findViewById(R.id.tvPurchasePrice);
        TextView tvPurchaseDescription = (TextView)rowView.findViewById(R.id.tvPurchaseDescription);

        tvPurchaseName.setText(Purchases.get(position).getName());
        tvPurchasePrice.setText(Purchases.get(position).getPrice());
        tvPurchaseDescription.setText(Purchases.get(position).getDescription());

        return rowView;
    }
}
