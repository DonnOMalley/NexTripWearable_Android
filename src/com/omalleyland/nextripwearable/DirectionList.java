package com.omalleyland.nextripwearable;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by D00M on 7/7/14.
 */
public class DirectionList extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] DirectionIDs;
    private final String[] DirectionTexts;
    public DirectionList(Activity context,
                     String[] DirectionIDs, String[] DirectionTexts) {
        super(context, R.layout.fragment_directions, DirectionTexts);
        this.context = context;
        this.DirectionIDs = DirectionIDs;
        this.DirectionTexts = DirectionTexts;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.fragment_directions, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        txtTitle.setText(DirectionTexts[position]);
        return rowView;
    }
}
