package com.omalleyland.nextripwearable;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by D00M
 */
public class ResultsList extends ArrayAdapter<String> {
    private final Activity context;

    private String[] Routes;
    private String[] Departures;
    private String[] Directions;
    private String[] Descriptions;

    public ResultsList(Activity context,
                         String[] Routes, String[] Departures, String[] Directions, String[] Descriptions) {
        super(context, R.layout.fragment_results, Descriptions);
        this.context = context;
        this.Routes = Routes;
        this.Departures = Departures;
        this.Directions = Directions;
        this.Descriptions = Descriptions;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.fragment_results, null, true);

        TextView tvRoutes = (TextView)rowView.findViewById(R.id.tvStopRoute);
        TextView tvDepartures = (TextView)rowView.findViewById(R.id.tvStopDeparture);
        TextView tvDirections = (TextView)rowView.findViewById(R.id.tvStopDirection);
        TextView tvDescriptions = (TextView)rowView.findViewById(R.id.tvStopDescription);

        Log.d("NexTripWearable", "Position = " + position);
        Log.d("NexTripWearable", "Routes Length = " + Routes.length);
        Log.d("NexTripWearable", "Routes[position] = " + Routes[position]);
        Log.d("NexTripWearable", "Departures[position] = " + Departures[position]);
        Log.d("NexTripWearable", "Directions[position] = " + Directions[position]);
        Log.d("NexTripWearable", "Descriptions[position] = " + Descriptions[position]);
        tvRoutes.setText(Routes[position]);
        tvDepartures.setText(Departures[position]);
        tvDirections.setText(Directions[position]);
        tvDescriptions.setText(Descriptions[position]);

        return rowView;
    }
}
