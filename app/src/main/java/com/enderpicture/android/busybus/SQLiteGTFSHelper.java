package com.enderpicture.android.busybus;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Hashtable;


@SuppressWarnings("WeakerAccess")
public class SQLiteGTFSHelper extends SQLiteOpenHelper {

    public static final String NAME = "GTFS";

    // always the first COL
    public static final String UID = "_id";

    public static final String AGENCY = "agency";
    public static final String AGENCY_COL_AGENCY_ID = "agency_id";
    public static final String AGENCY_COL_AGENCY_NAME = "agency_name";
    public static final String AGENCY_COL_AGENCY_URL = "agency_url";
    public static final String AGENCY_COL_AGENCY_TIMEZONE = "agency_timezone";
    public static final String AGENCY_COL_AGENCY_LANG = "agency_lang";

    public static final String CALENDAR = "calendar";
    public static final String CALENDAR_COL_SERVICE_ID = "service_id";
    public static final String CALENDAR_COL_MONDAY = "monday";
    public static final String CALENDAR_COL_TUESDAY = "tuesday";
    public static final String CALENDAR_COL_WEDNESDAY = "wednesday";
    public static final String CALENDAR_COL_THURSDAY = "thursday";
    public static final String CALENDAR_COL_FRIDAY = "friday";
    public static final String CALENDAR_COL_SATURDAY = "saturday";
    public static final String CALENDAR_COL_SUNDAY = "sunday";
    public static final String CALENDAR_COL_START_DATE = "start_date";
    public static final String CALENDAR_COL_END_DATE = "end_date";

    public static final String CALENDAR_DATES = "calendar_dates";
    public static final String CALENDAR_DATES_COL_SERVICE_ID = "service_id";
    public static final String CALENDAR_DATES_COL_DATE = "date";
    public static final String CALENDAR_DATES_COL_EXCEPTION_TYPE = "exception_type";

    public static final String FEED_INFO = "feed_info";
    public static final String FEED_INFO_COL_FEED_PUBLISHER_NAME = "feed_publisher_name";
    public static final String FEED_INFO_COL_FEED_PUBLISHER_URL = "feed_publisher_url";
    public static final String FEED_INFO_COL_FEED_LANG = "feed_lang";
    public static final String FEED_INFO_COL_FEED_START_DATE = "feed_start_date";
    public static final String FEED_INFO_COL_FEED_END_DATE = "feed_end_date";
    public static final String FEED_INFO_COL_FEED_VERSION = "feed_version";

    public static final String ROUTES = "routes";
    public static final String ROUTES_COL_ROUTE_ID = "route_id";
    public static final String ROUTES_COL_AGENCY_ID = "agency_id";
    public static final String ROUTES_COL_ROUTE_SHORT_NAME = "route_short_name";
    public static final String ROUTES_COL_ROUTE_LONG_NAME = "route_long_name";
    public static final String ROUTES_COL_ROUTE_DESC = "route_desc";
    public static final String ROUTES_COL_ROUTE_TYPE = "route_type";
    public static final String ROUTES_COL_ROUTE_URL = "route_url";
    public static final String ROUTES_COL_ROUTE_COLOR = "route_color";
    public static final String ROUTES_COL_ROUTE_TEXT_COLOR = "route_text_color";

    public static final String SHAPES = "shapes";
    public static final String SHAPES_COL_SHAPE_ID = "shape_id";
    public static final String SHAPES_COL_SHAPE_PT_LAT = "shape_pt_lat";
    public static final String SHAPES_COL_SHAPE_PT_LON = "shape_pt_lon";
    public static final String SHAPES_COL_SHAPE_PT_SEQUENCE = "shape_pt_sequence";
    public static final String SHAPES_COL_SHAPE_DIST_TRAVELED = "shape_dist_traveled";

    public static final String STOP_TIMES = "stop_times";
    public static final String STOP_TIMES_COL_TRIP_ID = "trip_id";
    public static final String STOP_TIMES_COL_ARRIVAL_TIME = "arrival_time";
    public static final String STOP_TIMES_COL_DEPARTURE_TIME = "departure_time";
    public static final String STOP_TIMES_COL_STOP_ID = "stop_id";
    public static final String STOP_TIMES_COL_STOP_SEQUENCE = "stop_sequence";
    public static final String STOP_TIMES_COL_STOP_HEADSIGN = "stop_headsign";
    public static final String STOP_TIMES_COL_PICKUP_TYPE = "pickup_type";
    public static final String STOP_TIMES_COL_DROP_OFF_TYPE = "drop_off_type";
    public static final String STOP_TIMES_COL_SHAPE_DIST_TRAVELED = "shape_dist_traveled";

    public static final String STOPS = "stops";
    public static final String STOPS_COL_STOP_ID = "stop_id";
    public static final String STOPS_COL_STOP_CODE = "stop_code";
    public static final String STOPS_COL_STOP_NAME = "stop_name";
    public static final String STOPS_COL_STOP_DESC = "stop_desc";
    public static final String STOPS_COL_STOP_LAT = "stop_lat";
    public static final String STOPS_COL_STOP_LON = "stop_lon";
    public static final String STOPS_COL_ZONE_ID = "zone_id";
    public static final String STOPS_COL_STOP_URL = "stop_url";
    public static final String STOPS_COL_LOCATION_TYPE = "location_type";
    public static final String STOPS_COL_PARENT_STATION = "parent_station";

    public static final String TRANSFERS = "transfers";
    public static final String TRANSFERS_COL_FROM_STOP_ID = "from_stop_id";
    public static final String TRANSFERS_COL_TO_STOP_ID = "to_stop_id";
    public static final String TRANSFERS_COL_TRANSFER_TYPE = "transfer_type";
    public static final String TRANSFERS_COL_MIN_TRANSFER_TIME = "min_transfer_time";

    public static final String TRIPS = "trips";
    public static final String TRIPS_COL_ROUTE_ID = "route_id";
    public static final String TRIPS_COL_SERVICE_ID = "service_id";
    public static final String TRIPS_COL_TRIP_ID = "trip_id";
    public static final String TRIPS_COL_TRIP_HEADSIGN = "trip_headsign";
    public static final String TRIPS_COL_TRIP_SHORT_NAME = "trip_short_name";
    public static final String TRIPS_COL_DIRECTION_ID = "direction_id";
    public static final String TRIPS_COL_BLOCK_ID = "block_id";
    public static final String TRIPS_COL_SHAPE_ID = "shape_id";
    public static final String TRIPS_COL_WHEELCHAIR_ACCESSIBLE = "wheelchair_accessible";
    public static final String TRIPS_COL_BIKES_ALLOWED = "bikes_allowed";

    public static final Hashtable<String, String[]> COL = new Hashtable<String, String[]>() {{
        put(AGENCY, new String[]{
                AGENCY_COL_AGENCY_ID,
                AGENCY_COL_AGENCY_NAME,
                AGENCY_COL_AGENCY_URL,
                AGENCY_COL_AGENCY_TIMEZONE,
                AGENCY_COL_AGENCY_LANG});
        put(CALENDAR, new String[]{
                CALENDAR_COL_SERVICE_ID,
                CALENDAR_COL_MONDAY,
                CALENDAR_COL_TUESDAY,
                CALENDAR_COL_WEDNESDAY,
                CALENDAR_COL_THURSDAY,
                CALENDAR_COL_FRIDAY,
                CALENDAR_COL_SATURDAY,
                CALENDAR_COL_SUNDAY,
                CALENDAR_COL_START_DATE,
                CALENDAR_COL_END_DATE});
        put(CALENDAR_DATES, new String[]{
                CALENDAR_DATES_COL_SERVICE_ID,
                CALENDAR_DATES_COL_DATE,
                CALENDAR_DATES_COL_EXCEPTION_TYPE});
        put(FEED_INFO, new String[]{
                FEED_INFO_COL_FEED_PUBLISHER_NAME,
                FEED_INFO_COL_FEED_PUBLISHER_URL,
                FEED_INFO_COL_FEED_LANG,
                FEED_INFO_COL_FEED_START_DATE,
                FEED_INFO_COL_FEED_END_DATE,
                FEED_INFO_COL_FEED_VERSION});
        put(ROUTES, new String[]{
                ROUTES_COL_ROUTE_ID,
                ROUTES_COL_AGENCY_ID,
                ROUTES_COL_ROUTE_SHORT_NAME,
                ROUTES_COL_ROUTE_LONG_NAME,
                ROUTES_COL_ROUTE_DESC,
                ROUTES_COL_ROUTE_TYPE,
                ROUTES_COL_ROUTE_URL,
                ROUTES_COL_ROUTE_COLOR,
                ROUTES_COL_ROUTE_TEXT_COLOR});
        put(SHAPES, new String[]{
                SHAPES_COL_SHAPE_ID,
                SHAPES_COL_SHAPE_PT_LAT,
                SHAPES_COL_SHAPE_PT_LON,
                SHAPES_COL_SHAPE_PT_SEQUENCE,
                SHAPES_COL_SHAPE_DIST_TRAVELED});
        put(STOP_TIMES, new String[]{
                STOP_TIMES_COL_TRIP_ID,
                STOP_TIMES_COL_ARRIVAL_TIME,
                STOP_TIMES_COL_DEPARTURE_TIME,
                STOP_TIMES_COL_STOP_ID,
                STOP_TIMES_COL_STOP_SEQUENCE,
                STOP_TIMES_COL_STOP_HEADSIGN,
                STOP_TIMES_COL_PICKUP_TYPE,
                STOP_TIMES_COL_DROP_OFF_TYPE,
                STOP_TIMES_COL_SHAPE_DIST_TRAVELED});
        put(STOPS, new String[]{
                STOPS_COL_STOP_ID,
                STOPS_COL_STOP_CODE,
                STOPS_COL_STOP_NAME,
                STOPS_COL_STOP_DESC,
                STOPS_COL_STOP_LAT,
                STOPS_COL_STOP_LON,
                STOPS_COL_ZONE_ID,
                STOPS_COL_STOP_URL,
                STOPS_COL_LOCATION_TYPE,
                STOPS_COL_PARENT_STATION});
        put(TRANSFERS, new String[]{
                TRANSFERS_COL_FROM_STOP_ID,
                TRANSFERS_COL_TO_STOP_ID,
                TRANSFERS_COL_TRANSFER_TYPE,
                TRANSFERS_COL_MIN_TRANSFER_TIME});
        put(TRIPS, new String[]{
                TRIPS_COL_ROUTE_ID,
                TRIPS_COL_SERVICE_ID,
                TRIPS_COL_TRIP_ID,
                TRIPS_COL_TRIP_HEADSIGN,
                TRIPS_COL_TRIP_SHORT_NAME,
                TRIPS_COL_DIRECTION_ID,
                TRIPS_COL_BLOCK_ID,
                TRIPS_COL_SHAPE_ID,
                TRIPS_COL_WHEELCHAIR_ACCESSIBLE,
                TRIPS_COL_BIKES_ALLOWED});
    }};


    public static final String AGENCY_CREATE = "CREATE TABLE " + AGENCY + " (" +
            UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            AGENCY_COL_AGENCY_ID + " TEXT, " +
            AGENCY_COL_AGENCY_NAME + " TEXT, " +
            AGENCY_COL_AGENCY_URL + " TEXT, " +
            AGENCY_COL_AGENCY_TIMEZONE + " TEXT, " +
            AGENCY_COL_AGENCY_LANG + " TEXT" + ");";
    public static final String CALENDAR_CREATE = "CREATE TABLE " + CALENDAR + " (" +
            UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CALENDAR_COL_SERVICE_ID + " INTEGER, " +
            CALENDAR_COL_MONDAY + " INTEGER, " +
            CALENDAR_COL_TUESDAY + " INTEGER, " +
            CALENDAR_COL_WEDNESDAY + " INTEGER, " +
            CALENDAR_COL_THURSDAY + " INTEGER, " +
            CALENDAR_COL_FRIDAY + " INTEGER, " +
            CALENDAR_COL_SATURDAY + " INTEGER, " +
            CALENDAR_COL_SUNDAY + " INTEGER, " +
            CALENDAR_COL_START_DATE + " DATETIME, " +
            CALENDAR_COL_END_DATE + " DATETIME" + ");";
    public static final String CALENDAR_DATES_CREATE = "CREATE TABLE " + CALENDAR_DATES + " (" +
            UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CALENDAR_DATES_COL_SERVICE_ID + " INTEGER, " +
            CALENDAR_DATES_COL_DATE + " INTEGER, " +
            CALENDAR_DATES_COL_EXCEPTION_TYPE + " INTEGER" + ");";
    public static final String FEED_INFO_CREATE = "CREATE TABLE " + FEED_INFO + " (" +
            UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FEED_INFO_COL_FEED_PUBLISHER_NAME + " TEXT, " +
            FEED_INFO_COL_FEED_PUBLISHER_URL + " TEXT, " +
            FEED_INFO_COL_FEED_LANG + " TEXT, " +
            FEED_INFO_COL_FEED_START_DATE + " DATETIME, " +
            FEED_INFO_COL_FEED_END_DATE + " DATETIME, " +
            FEED_INFO_COL_FEED_VERSION + " TEXT" + ");";
    public static final String ROUTES_CREATE = "CREATE TABLE " + ROUTES + " (" +
            UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ROUTES_COL_ROUTE_ID + " INTEGER, " +
            ROUTES_COL_AGENCY_ID + " TEXT, " +
            ROUTES_COL_ROUTE_SHORT_NAME + " TEXT, " +
            ROUTES_COL_ROUTE_LONG_NAME + " TEXT, " +
            ROUTES_COL_ROUTE_DESC + " TEXT, " +
            ROUTES_COL_ROUTE_TYPE + " INTEGER, " +
            ROUTES_COL_ROUTE_URL + " TEXT, " +
            ROUTES_COL_ROUTE_COLOR + " TEXT, " +
            ROUTES_COL_ROUTE_TEXT_COLOR + " TEXT" + ");";
    public static final String SHAPES_CREATE = "CREATE TABLE " + SHAPES + " (" +
            UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SHAPES_COL_SHAPE_ID + " INTEGER, " +
            SHAPES_COL_SHAPE_PT_LAT + " REAL, " +
            SHAPES_COL_SHAPE_PT_LON + " REAL, " +
            SHAPES_COL_SHAPE_PT_SEQUENCE + " INTEGER, " +
            SHAPES_COL_SHAPE_DIST_TRAVELED + " TEXT" + ");";
    public static final String STOP_TIMES_CREATE = "CREATE TABLE " + STOP_TIMES + " (" +
            UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            STOP_TIMES_COL_TRIP_ID + " INTEGER, " +
            STOP_TIMES_COL_ARRIVAL_TIME + " TEXT, " +
            STOP_TIMES_COL_DEPARTURE_TIME + " TEXT, " +
            STOP_TIMES_COL_STOP_ID + " INTEGER, " +
            STOP_TIMES_COL_STOP_SEQUENCE + " INTEGER, " +
            STOP_TIMES_COL_STOP_HEADSIGN + " TEXT, " +
            STOP_TIMES_COL_PICKUP_TYPE + " INTEGER, " +
            STOP_TIMES_COL_DROP_OFF_TYPE + " INTEGER, " +
            STOP_TIMES_COL_SHAPE_DIST_TRAVELED + " TEXT" + ");";
    public static final String STOPS_CREATE = "CREATE TABLE " + STOPS + " (" +
            UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            STOPS_COL_STOP_ID + " INTEGER, " +
            STOPS_COL_STOP_CODE + " INTEGER, " +
            STOPS_COL_STOP_NAME + " TEXT, " +
            STOPS_COL_STOP_DESC + " TEXT, " +
            STOPS_COL_STOP_LAT + " REAL, " +
            STOPS_COL_STOP_LON + " REAL, " +
            STOPS_COL_ZONE_ID + " TEXT, " +
            STOPS_COL_STOP_URL + " TEXT, " +
            STOPS_COL_LOCATION_TYPE + " INTEGER, " +
            STOPS_COL_PARENT_STATION + " TEXT" + ");";
    public static final String TRANSFERS_CREATE = "CREATE TABLE " + TRANSFERS + " (" +
            UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TRANSFERS_COL_FROM_STOP_ID + " INTEGER, " +
            TRANSFERS_COL_TO_STOP_ID + " INTEGER, " +
            TRANSFERS_COL_TRANSFER_TYPE + " INTEGER, " +
            TRANSFERS_COL_MIN_TRANSFER_TIME + " INTEGER" + ");";
    public static final String TRIPS_CREATE = "CREATE TABLE " + TRIPS + " (" +
            UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TRIPS_COL_ROUTE_ID + " INTEGER, " +
            TRIPS_COL_SERVICE_ID + " INTEGER, " +
            TRIPS_COL_TRIP_ID + " INTEGER, " +
            TRIPS_COL_TRIP_HEADSIGN + " TEXT, " +
            TRIPS_COL_TRIP_SHORT_NAME + " TEXT, " +
            TRIPS_COL_DIRECTION_ID + " INTEGER, " +
            TRIPS_COL_BLOCK_ID + " INTEGER, " +
            TRIPS_COL_SHAPE_ID + " INTEGER, " +
            TRIPS_COL_WHEELCHAIR_ACCESSIBLE + " INTEGER, " +
            TRIPS_COL_BIKES_ALLOWED + " INTEGER" + ");";

    public static final int VERSION = 1;

    public SQLiteGTFSHelper(Context context) {

        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(AGENCY_CREATE);
            db.execSQL(CALENDAR_CREATE);
            db.execSQL(CALENDAR_DATES_CREATE);
            db.execSQL(FEED_INFO_CREATE);
            db.execSQL(ROUTES_CREATE);
            db.execSQL(SHAPES_CREATE);
            db.execSQL(STOP_TIMES_CREATE);
            db.execSQL(STOPS_CREATE);
            db.execSQL(TRANSFERS_CREATE);
            db.execSQL(TRIPS_CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void clear(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + AGENCY);
        db.execSQL("DROP TABLE IF EXISTS " + CALENDAR);
        db.execSQL("DROP TABLE IF EXISTS " + CALENDAR_DATES);
        db.execSQL("DROP TABLE IF EXISTS " + FEED_INFO);
        db.execSQL("DROP TABLE IF EXISTS " + ROUTES);
        db.execSQL("DROP TABLE IF EXISTS " + SHAPES);
        db.execSQL("DROP TABLE IF EXISTS " + STOP_TIMES);
        db.execSQL("DROP TABLE IF EXISTS " + STOPS);
        db.execSQL("DROP TABLE IF EXISTS " + TRANSFERS);
        db.execSQL("DROP TABLE IF EXISTS " + TRIPS);
        onCreate(db);
    }
}
