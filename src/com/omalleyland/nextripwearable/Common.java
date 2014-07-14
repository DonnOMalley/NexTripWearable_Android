package com.omalleyland.nextripwearable;

/**
 * Created by D00M
 */
public class Common {
    /* Application/Debug Constant */
    public static final String      APPLICATION_NAME                        = "NexTripWearable";
    public static final int         UNKNOWN                                 = -1;

    /* SQLite DB Constants */
    public static final int         DATABASE_VERSION                        = 2;
    public static final String      DATABASE_NAME                           = APPLICATION_NAME + "DB";

    /* SQLite Categories Table Constants */
    public static final String      tblFAVORITES                            = "Favorites";
    public static final String      colFAVORITES_IMAGE_ID                   = "ImageID";
    public static final int         colFAVORITES_IMAGE_ID_INDEX             = 0;
    public static final String      colFAVORITES_ROUTE_ID                   = "RouteID";
    public static final int         colFAVORITES_ROUTE_ID_INDEX             = 1;
    public static final String      colFAVORITES_ROUTE_DESCRIPTION          = "RouteDescription";
    public static final int         colFAVORITES_ROUTE_DESCRIPTION_INDEX    = 2;
    public static final String      colFAVORITES_DIRECTION_ID               = "DirectionID";
    public static final int         colFAVORITES_DIRECTION_ID_INDEX         = 3;
    public static final String      colFAVORITES_DIRECTION_TEXT             = "DirectionText";
    public static final int         colFAVORITES_DIRECTION_TEXT_INDEX       = 4;
    public static final String      colFAVORITES_STOP_ID                    = "StopID";
    public static final int         colFAVORITES_STOP_ID_INDEX              = 5;
    public static final String      colFAVORITES_STOP_DESCRIPTION           = "StopDescription";
    public static final int         colFAVORITES_STOP_DESCRIPTION_INDEX     = 6;


    public static final String[]    colFAVORITES_ALL                    = {colFAVORITES_IMAGE_ID,
                                                                            colFAVORITES_ROUTE_ID,
                                                                            colFAVORITES_ROUTE_DESCRIPTION,
                                                                            colFAVORITES_DIRECTION_ID,
                                                                            colFAVORITES_DIRECTION_TEXT,
                                                                            colFAVORITES_STOP_ID,
                                                                            colFAVORITES_STOP_DESCRIPTION};
    public static final String      CREATE_FAVORITE_TABLE               = "CREATE TABLE " + tblFAVORITES + "(" +
                                                                            colFAVORITES_IMAGE_ID + " integer NOT NULL, " +
                                                                            colFAVORITES_ROUTE_ID + " text NOT NULL, " +
                                                                            colFAVORITES_ROUTE_DESCRIPTION + " text, " +
                                                                            colFAVORITES_DIRECTION_ID + " text NOT NULL, " +
                                                                            colFAVORITES_DIRECTION_TEXT + " text, " +
                                                                            colFAVORITES_STOP_ID + " text NOT NULL," +
                                                                            colFAVORITES_STOP_DESCRIPTION + " text NULL," +
                                                                            "PRIMARY KEY(" + colFAVORITES_ROUTE_ID + "," +
                                                                                colFAVORITES_DIRECTION_ID + "," +
                                                                                colFAVORITES_STOP_ID + ")" +
                                                                            ");";
    public static final String      DROP_FAVORITE_TABLE                 = "DROP TABLE IF EXISTS " + tblFAVORITES;
}
