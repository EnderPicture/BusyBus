package com.enderpicture.android.busybus;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.json.JSONException;


public class StationClusterRenderer extends DefaultClusterRenderer {

    private Context mContext;

    public StationClusterRenderer(Context context, GoogleMap map, ClusterManager clusterManager) {
        super(context, map, clusterManager);

        mContext = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterItem item, MarkerOptions markerOptions) {
        try {
            StationClusterItem station = (StationClusterItem) item;
            String routes = station.mInfo.getString("Routes");
            String stationID = station.mInfo.getString("StopNo");

            SQLiteFavHelper sqLiteFavHelper = new SQLiteFavHelper(mContext);
            SQLiteDatabase database = sqLiteFavHelper.getReadableDatabase();

            String[] columns = {SQLiteFavHelper.COL_VALUE};
            String selection = SQLiteFavHelper.COL_TYPE + "='" + SQLiteFavHelper.TYPE_STATION + "' AND " + SQLiteFavHelper.COL_VALUE + "='" + stationID + "'";
            Cursor cursor = database.query(SQLiteFavHelper.TAB_NAME, columns, selection, null, null, null, null);

            String s = "";

            if (cursor.getCount() > 0) {
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createStationMarker(routes, true)));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createStationMarker(routes, false)));
            }
            database.close();

            markerOptions.anchor(0, 1);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        super.onBeforeClusterItemRendered(item, markerOptions);
    }


    private Bitmap createStationMarker(String name, boolean fav) {

        // grab the view
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View markerLayout = layoutInflater.inflate(R.layout.station_marker, null);
        ConstraintLayout root = markerLayout.findViewById(R.id.root);

        if (fav)
            root.setBackgroundResource(R.drawable.bus_station_background_fav);

        // find text
        TextView markerRating = markerLayout.findViewById(R.id.marker_text);

        // set text to name
        markerRating.setText(name);

        markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

        final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerLayout.draw(canvas);
        return bitmap;
    }
}
