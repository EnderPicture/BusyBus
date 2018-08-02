package com.enderpicture.android.busybus;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OfflineActivity extends AppCompatActivity implements AsyncArchiveFetch.FetchUpdateInterface {

    RecyclerView recyclerView;

    Button update, get;
    ProgressBar progressBar;

    AsyncTask asyncArchiveFetch;

    Location mLocation;

    public static final int REQUEST_PERMISSION_LOCATION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new Adapter(this));

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setMax(100);

        update = findViewById(R.id.update_offline);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (asyncArchiveFetch != null) {
                    asyncArchiveFetch.cancel(true);
                    update.setText(R.string.update_offline);
                    progressBar.setProgress(0);
                    asyncArchiveFetch = null;
                } else {
                    asyncArchiveFetch = new AsyncArchiveFetch().execute(
                            new AsyncArchiveFetch.FetchObject(
                                    OfflineActivity.this,
                                    OfflineActivity.this));
                    update.setText(R.string.cancel);
                }
            }
        });

        get = findViewById(R.id.get_around_offline);
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setAdapter(new Adapter(OfflineActivity.this));
                Toast.makeText(OfflineActivity.this, "updated", Toast.LENGTH_SHORT).show();
            }
        });


    }

    /**
     * fetch
     *
     * @param value from 0 -> 100
     */
    @Override
    public void fetchUpdate(int value) {
        progressBar.setProgress(value);
        Toast.makeText(this, value + "% done", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recyclerView.setAdapter(new Adapter(this));
                } else {
                    Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public class Adapter extends RecyclerView.Adapter<OfflineActivity.Adapter.Holder> {

        ArrayList<Station> stations = new ArrayList<>();
        Context mContext;

        /**
         * creates a new adapter
         * @param context
         */
        public Adapter(Context context) {
            mContext = context;

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            assert locationManager != null;
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(OfflineActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
                Toast.makeText(mContext, "NO LOCATION PERMISSION", Toast.LENGTH_SHORT).show();
                return;
            }

            Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));

            mLocation = location;

            if (location == null) {
                Toast.makeText(mContext, "does not have location", Toast.LENGTH_SHORT).show();
                return;
            }

            // fetch offline data from database
            SQLiteGTFSHelper sqLiteGTFSHelper = new SQLiteGTFSHelper(OfflineActivity.this);
            SQLiteDatabase database = sqLiteGTFSHelper.getReadableDatabase();

            String[] columns = {
                    SQLiteGTFSHelper.STOPS_COL_STOP_LAT,
                    SQLiteGTFSHelper.STOPS_COL_STOP_LON,
                    SQLiteGTFSHelper.STOPS_COL_STOP_NAME,
                    SQLiteGTFSHelper.STOPS_COL_STOP_CODE};

            // the range of the the fetch
            double range = .005;
            String selection = SQLiteGTFSHelper.STOPS_COL_STOP_LAT + " > " + (location.getLatitude() - range) + " AND "
                    + SQLiteGTFSHelper.STOPS_COL_STOP_LAT + " < " + (location.getLatitude() + range) + " AND "
                    + SQLiteGTFSHelper.STOPS_COL_STOP_LON + " > " + (location.getLongitude() - range) + " AND "
                    + SQLiteGTFSHelper.STOPS_COL_STOP_LON + " < " + (location.getLongitude() + range);
            Cursor cursor = database.query(SQLiteGTFSHelper.STOPS, columns, selection, null, null, null, null);

            // go through all the results
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(SQLiteGTFSHelper.STOPS_COL_STOP_NAME));
                String code = cursor.getString(cursor.getColumnIndex(SQLiteGTFSHelper.STOPS_COL_STOP_CODE));
                String lat = cursor.getString(cursor.getColumnIndex(SQLiteGTFSHelper.STOPS_COL_STOP_LAT));
                String lon = cursor.getString(cursor.getColumnIndex(SQLiteGTFSHelper.STOPS_COL_STOP_LON));

                Location sLocation = new Location("point");
                sLocation.setLatitude(Float.parseFloat(lat));
                sLocation.setLongitude(Float.parseFloat(lon));

                stations.add(new Station(sLocation, name, code, location.distanceTo(sLocation)));
            }

            // sort the stations by distance
            Collections.sort(stations, new Comparator<Station>() {
                @Override
                public int compare(Station lhs, Station rhs) {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return (int) (lhs.mDistance - rhs.mDistance);
                }
            });

            database.close();
        }

        @NonNull
        @Override
        public OfflineActivity.Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.offline_item, parent, false);
            return new Holder(layout);
        }

        @Override
        public void onBindViewHolder(@NonNull OfflineActivity.Adapter.Holder holder, int position) {
            holder.setItem(stations.get(position));
        }

        @Override
        public int getItemCount() {
            return stations.size();
        }

        /**
         * station object
         */
        public class Station {
            final Location mLocation;
            final String mName;
            final String mCode;
            final float mDistance;

            public Station(Location location, String name, String code, float distance) {
                mLocation = location;
                mName = name;
                mCode = code;
                mDistance = distance;
            }
        }

        /**
         * holder for the adapter
         */
        public class Holder extends RecyclerView.ViewHolder {
            TextView textView;

            public Holder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textView);
            }

            public void setItem(Station station) {
                textView.setText(Html.fromHtml("<h2>" + station.mName + "</h2>" +
                        "<p>Stop code: " + station.mCode + "<br>" +
                        "Distance to you: " + station.mDistance + " meters<br>" +
                        "Latitude: " + mLocation.getLatitude() + "<br>" +
                        "Longitude: " + mLocation.getLongitude() + "</p>")
                );
            }
        }
    }
}
