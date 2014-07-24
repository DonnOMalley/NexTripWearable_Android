package com.omalleyland.nextripwearable;

import android.content.Context;
import android.content.Intent;

/**
 * Created by D00M on 7/6/14.
 */
public class Route {
    private int ImageID;
    private String RouteID;
    private String RouteDescription;
    private String DirectionID;
    private String DirectionText;
    private String StopID;
    private String StopDescription;
    private int Type;

    public int getImageID() {
        return ImageID;
    }

    public void setImageID(int imageID) {
        ImageID = imageID;
    }

    public String getRouteID() {
        return RouteID;
    }

    public void setRouteID(String routeID) {
        RouteID = routeID;
    }

    public String getRouteDescription() {
        return RouteDescription;
    }

    public void setRouteDescription(String routeDescription) {
        RouteDescription = routeDescription;
    }

    public String getDirectionID() {
        return DirectionID;
    }

    public void setDirectionID(String directionID) {
        DirectionID = directionID;
    }

    public String getDirectionText() {
        return DirectionText;
    }

    public void setDirectionText(String directionText) {
        DirectionText = directionText;
    }

    public String getStopID() {
        return StopID;
    }

    public void setStopID(String stopID) {
        StopID = stopID;
    }

    public String getStopDescription() {
        return StopDescription;
    }

    public void setStopDescription(String stopDescription) {
        StopDescription = stopDescription;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    private void InitializeDefaults() {
        this.ImageID = -1;
        this.RouteID = "";
        this.RouteDescription = "";
        this.DirectionID = "";
        this.DirectionText = "";
        this.StopID = "";
        this.StopDescription = "";
        this.Type = Common.STOP_TYPE_ROUTE;
    }
    public Route() {
        InitializeDefaults();
    }

    public Route(int ImageID, String RouteID, String RouteDescription, String DirectionID, String DirectionText, String StopID, String StopDescription, int Type) {
        this.ImageID = ImageID;
        this.RouteID = RouteID;
        this.RouteDescription = RouteDescription;
        this.DirectionID = DirectionID;
        this.DirectionText = DirectionText;
        this.StopID = StopID;
        this.StopDescription = StopDescription;
        this.Type = Type;
    }

    public Intent BuildIntent(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);

        intent.putExtra("ImageID", this.ImageID);
        intent.putExtra("RouteID", this.RouteID);
        intent.putExtra("RouteDescription", this.RouteDescription);
        intent.putExtra("DirectionID", this.DirectionID);
        intent.putExtra("DirectionText", this.DirectionText);
        intent.putExtra("StopID", this.StopID);
        intent.putExtra("StopDescription", this.StopDescription);
        intent.putExtra("Type", this.Type);

        return intent;
    }

    public void LoadFromIntent(Intent intent) {
        this.ImageID = intent.getIntExtra("ImageID", -1);
        this.RouteID = intent.getStringExtra("RouteID");
        this.RouteDescription = intent.getStringExtra("RouteDescription");
        this.DirectionID = intent.getStringExtra("DirectionID");
        this.DirectionText = intent.getStringExtra("DirectionText");
        this.StopID = intent.getStringExtra("StopID");
        this.StopDescription = intent.getStringExtra("StopDescription");
        this.Type = intent.getIntExtra("Type", Common.STOP_TYPE_ROUTE);
    }
}
