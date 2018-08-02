package com.enderpicture.android.busybus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteFavHelper extends SQLiteOpenHelper {

    public static final String NAME = "FAV";

    public static final String UID = "_id";

    public static final int VERSION = 1;

    public static final String TAB_NAME = "fav";
    public static final String COL_TYPE = "type";
    public static final String COL_VALUE = "value";

    public static final String TYPE_STATION = "station";
    public static final String TYPE_BUSROUTE = "route";

    public static final String CREATE = "CREATE TABLE " + TAB_NAME + " (" +
            UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TYPE + " TEXT, " +
            COL_VALUE + " TEXT" + ");";

    public SQLiteFavHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
