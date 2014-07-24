package com.omalleyland.nextripwearable;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * Created by D00M
 */
public class ImageList extends ArrayAdapter<String>{
    private final Activity context;
    private final String[] routeId;
    private final String[] route;
    private final Integer[] imageId;
    public ImageList(Activity context,
                     String[] routeId, String[] route, Integer[] imageId) {
        super(context, R.layout.route_list_item, route);
        this.context = context;
        this.routeId = routeId;
        this.route = route;
        this.imageId = imageId;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.route_list_item, null, true);
        TextView txtRouteID = (TextView)rowView.findViewById(R.id.tvRouteFragRouteId);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.tvRouteFragDescription);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imgRouteFragIcon);
        txtRouteID.setText(routeId[position]);
        txtTitle.setText(route[position]);
        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}
