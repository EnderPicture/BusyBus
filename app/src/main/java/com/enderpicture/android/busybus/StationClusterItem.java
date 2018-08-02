package com.enderpicture.android.busybus;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import org.json.JSONException;
import org.json.JSONObject;

public class StationClusterItem implements ClusterItem {

    final LatLng mPosition;
    final String mTitle;
    final String mSnippet;
    final JSONObject mInfo;

    public StationClusterItem(LatLng latLng, JSONObject info) {
        mPosition = latLng;
        mInfo = info;

        String name = "";
        String stopNo = "";

        try {
            name = mInfo.get("Name") + "";
            stopNo = mInfo.get("StopNo") + "";
        } catch (JSONException e) {
            e.printStackTrace();
        }


        mTitle = " ";
        mSnippet = " ";
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

}
