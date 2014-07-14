package com.omalleyland.nextripwearable;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by DOOM001 on 7/8/2014.
 */
public class FavoriteList extends ArrayAdapter<String> {
    private final Activity context;

    private Integer[] ImageIDs;
    private String[] RouteIDs;
    private String[] Descriptions;
    private String[] Directions;

    public FavoriteList(Activity context,
                       Integer[] ImageID, String[] RouteID, String[] RouteDescription, String[] Direction) {
        super(context, R.layout.fragment_favorites, RouteDescription);
        this.context        = context;
        this.ImageIDs       = ImageID;
        this.RouteIDs       = RouteID;
        this.Descriptions   = RouteDescription;
        this.Directions     = Direction;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.fragment_favorites, null, true);

        ImageView imgIcon = (ImageView)rowView.findViewById(R.id.imgFavIcon);
        TextView tvRouteID = (TextView)rowView.findViewById(R.id.tvFavRouteId);
        TextView tvDescription = (TextView)rowView.findViewById(R.id.tvFavRouteDescription);
        TextView tvDirection = (TextView)rowView.findViewById(R.id.tvFavDirection);

        imgIcon.setImageResource(ImageIDs[position]);
        tvRouteID.setText(RouteIDs[position]);
        tvDescription.setText(Descriptions[position]);
        tvDirection.setText(Directions[position]);

        return rowView;
    }
}

