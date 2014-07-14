package com.omalleyland.nextripwearable;

/**
 * Created by D00M on 7/6/14.
 */
public class FavoriteRoute {
    private int ID;
    private int ImageID;
    private String RouteID;
    private String DirectionID;
    private String DirectionText;
    private String HttpParameters;

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

    public String getHttpParameters() {
        return HttpParameters;
    }

    public void setHttpParameters(String httpParameters) {
        HttpParameters = httpParameters;
    }

    public FavoriteRoute() {

    }

    public FavoriteRoute(int ID, int ImageID, String RouteID, String DirectionID, String DirectionText, String HttpParameters) {

    }


}
