package com.enderpicture.android.busybus;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OnMarkerClick implements ClusterManager.OnClusterItemClickListener<StationClusterItem>, GoogleMap.OnMapClickListener {

    MapsActivity mMapsActivity;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    AsyncTask mAsyncTask;

    String selectedStation = null;

    public OnMarkerClick(MapsActivity mapsActivity) {
        mMapsActivity = mapsActivity;

        mRecyclerView = mMapsActivity.findViewById(R.id.bottom_sheet_recycler);

        mLayoutManager = new LinearLayoutManager(mMapsActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void afterFetch(JSONArray jsonArray) {
        mAdapter = new BottomSheetRecyclerAdapter(jsonArray);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
            mAsyncTask = null;
            selectedStation = null;
        }
        mMapsActivity.bottomSheetHide();
    }

    @Override
    public boolean onClusterItemClick(StationClusterItem stationClusterItem) {

        JSONObject markerTag = stationClusterItem.mInfo;

        TextView textView = mMapsActivity.getBottomSheetLayout().findViewById(R.id.bottom_sheet_title);

        try {
            textView.setText(markerTag.getString("Name"));
            selectedStation = markerTag.getString("StopNo");

            mAsyncTask = mMapsActivity.httpFetchStart(
                    "http://api.translink.ca/rttiapi/v1/stops/" +
                            markerTag.getString("StopNo") +
                            "/estimates?apikey="+mMapsActivity.getString(R.string.translink_key)+"&count=10",
                    MapsActivity.FETCH_EST);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (selectedStation != null) {
            SQLiteFavHelper sqLiteFavHelper = new SQLiteFavHelper(mMapsActivity);
            SQLiteDatabase database = sqLiteFavHelper.getReadableDatabase();

            String[] columns = {SQLiteFavHelper.COL_VALUE};
            String selection = SQLiteFavHelper.COL_TYPE + "='" + SQLiteFavHelper.TYPE_STATION + "' AND " + SQLiteFavHelper.COL_VALUE + "='" + selectedStation + "'";
            Cursor cursor = database.query(SQLiteFavHelper.TAB_NAME, columns, selection, null, null, null, null);

            if (cursor.getCount() > 0) {
                mMapsActivity.getmFavStationButton().setImageResource(R.drawable.ic_favorite_black_24dp);
            } else {
                mMapsActivity.getmFavStationButton().setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }
            database.close();
        }



//        mMapsActivity.getmMap().animateCamera(CameraUpdateFactory.newLatLng(stationClusterItem.mPosition));
        mMapsActivity.bottomSheetUnhide();
        return false;
    }
}
