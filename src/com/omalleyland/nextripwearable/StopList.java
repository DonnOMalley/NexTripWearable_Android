package com.omalleyland.nextripwearable;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by D00M
 */
public class StopList extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] StopIDs;
    private final String[] StopTexts;
    public StopList(Activity context,
                         String[] StopIDs, String[] StopTexts) {
        super(context, R.layout.fragment_directions, StopIDs);
        this.context = context;
        this.StopIDs = StopIDs;
        this.StopTexts = StopTexts;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.fragment_directions, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        txtTitle.setText(StopTexts[position]);
        return rowView;
    }
}
