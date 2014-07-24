package com.omalleyland.nextripwearable;

/**
 * Created by D00M on 7/6/14.
 */
public class FavoriteRoute {
    private int ID;
    private int ImageID;
    private String RouteID;
    private String RouteDescription;
    private String DirectionID;
    private String DirectionText;
    private String StopID;
    private String StopDescription;
    private int StopType;

    public String getRouteDescription() {
        return RouteDescription;
    }

    public void setRouteDescription(String routeDescription) {
        RouteDescription = routeDescription;
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

    public int getStopType() {
        return StopType;
    }

    public void setStopType(int stopType) {
        StopType = stopType;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

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

    public FavoriteRoute() {

    }

    public FavoriteRoute(int ID, int ImageID, String RouteID, String DirectionID, String DirectionText, String HttpParameters) {

    }


}
