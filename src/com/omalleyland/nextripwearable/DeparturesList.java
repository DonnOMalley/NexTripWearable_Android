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
public class DeparturesList extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] DepartureText;
    private final String[] StopDescription;

    public DeparturesList(Activity context,
                         String[] DepartureText, String[] RouteText) {
        super(context, R.layout.fragment_departures, DepartureText);
        this.context = context;
        this.DepartureText = DepartureText;
        this.StopDescription = RouteText;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.fragment_departures, null, true);
        ((TextView) rowView.findViewById(R.id.txt)).setText(DepartureText[position]);
        if(position == 0) {
	        ((TextView) rowView.findViewById(R.id.tvStopDescription)).setText(StopDescription[position]);
        }
        else {
        	((TextView) rowView.findViewById(R.id.txt)).setTextSize(24);
	        ((TextView) rowView.findViewById(R.id.tvStopDescription)).setVisibility(View.INVISIBLE);
	        ((TextView) rowView.findViewById(R.id.tvStopDescription)).setHeight(0);
        }
        return rowView;
    }
}
