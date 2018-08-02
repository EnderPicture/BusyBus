package com.enderpicture.android.busybus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AsyncJsonFetch.FetchInterface {

    static final int FETCH_BUS = 1;
    static final int FETCH_STO = 2;
    static final int FETCH_EST = 3;
    static final int REQUEST_PERMISSION_LOCATION = 1;

    private GoogleMap mMap;

    private FloatingActionButton mFab;
    private DrawerLayout mDrawerLayout;
    private BottomSheetBehavior mBottomSheetBehavior;
    private LinearLayout mBottomSheet;

    private OnMarkerClick mOnMarkerClick;

    private ImageButton mFavStationButton;
    private Button mFavListButton;
    private Button mSettingsButton;
    private Button mTranslinkButton;
    private Button mOfflineButton;

    private ClusterManager<StationClusterItem> mStationClusterManager;
    private StationClusterRenderer mStationClusterRenderer;

    private SensorManager sensorManager;
    private boolean isBright = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(SettingsActivity.SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(SettingsActivity.THEME_KEY)) {

            String themeMode = sharedPreferences.getString(SettingsActivity.THEME_KEY, "");


            // if mode is at auto settings
            if (themeMode.equals(SettingsActivity.AUTO)) {
                autoTheme();
            } else if (themeMode.equals(SettingsActivity.LIGHT)) {
                isBright = true;
                afterSensedLight();
            } else if (themeMode.equals(SettingsActivity.DARK)) {
                isBright = false;
                afterSensedLight();
            }

        } else {
            autoTheme();
        }


    }

    /**
     * starts the auto theme process
     */
    protected void autoTheme() {
        // get sensor manager before view opens
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_LIGHT)
                    isBright = event.values[0] > SensorManager.LIGHT_FULLMOON;

                // unregister listener right after it gets the first event
                sensorManager.unregisterListener(this);

                // call "on create"
                afterSensedLight();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        // get sensor
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null)
            // get the sensor event the fastest possible time
            sensorManager.registerListener(sensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_FASTEST);
        else
            // if no sensor, just start it with no sensing of light
            afterSensedLight();
    }

    /**
     * determines what happens after the the sensor has or if not auto, right after on create
     */
    protected void afterSensedLight() {
        if (!isBright)
            setTheme(R.style.AppThemeDark);

        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        mDrawerLayout = findViewById(R.id.drawer);

        // bottom sheet stuff
        mBottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setHideable(true);

        bottomSheetHide();

        // action bar stuff
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);

        // set buttons
        NavigationView navigationView = findViewById(R.id.nav_view);
        mFavListButton = navigationView.getHeaderView(0).findViewById(R.id.fav_list);
        mFavListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, FavActivity.class);
                startActivity(i);
            }
        });

        mSettingsButton = navigationView.getHeaderView(0).findViewById(R.id.settings);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        mTranslinkButton = navigationView.getHeaderView(0).findViewById(R.id.translink);
        mTranslinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://m.translink.ca/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        mOfflineButton = navigationView.getHeaderView(0).findViewById(R.id.offline);
        mOfflineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, OfflineActivity.class);
                startActivity(i);
            }
        });

    }

    /**
     * hides the bottom sheet
     */
    public void bottomSheetHide() {
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        if (mMap != null) {
            // TODO set the padding
//            mMap.setPadding(100, 0, 0, 0);
        }
    }

    /**
     * reveals the bottom sheet
     */
    public void bottomSheetUnhide() {
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        if (mMap != null) {
            // TODO set the padding
//            mMap.setPadding(100, 0, 0, 0);
        }
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                mDrawerLayout.openDrawer(GravityCompat.START);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * @return image button
     */
    public ImageButton getmFavStationButton() {
        return mFavStationButton;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mOnMarkerClick = new OnMarkerClick(this);
        mMap.setOnMapClickListener(mOnMarkerClick);

        mFavStationButton = findViewById(R.id.fav_station);
        mFavStationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mOnMarkerClick.selectedStation != null) {
                    SQLiteFavHelper sqLiteFavHelper = new SQLiteFavHelper(MapsActivity.this);
                    SQLiteDatabase database = sqLiteFavHelper.getWritableDatabase();

                    String[] columns = {SQLiteFavHelper.COL_VALUE};
                    String selection = SQLiteFavHelper.COL_TYPE + "='" + SQLiteFavHelper.TYPE_STATION + "' AND " + SQLiteFavHelper.COL_VALUE + "='" + mOnMarkerClick.selectedStation + "'";
                    Cursor cursor = database.query(SQLiteFavHelper.TAB_NAME, columns, selection, null, null, null, null);

                    if (cursor.getCount() > 0) {
                        database.delete(SQLiteFavHelper.TAB_NAME, selection, null);
                        mFavStationButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    } else {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(SQLiteFavHelper.COL_TYPE, SQLiteFavHelper.TYPE_STATION);
                        contentValues.put(SQLiteFavHelper.COL_VALUE, mOnMarkerClick.selectedStation);
                        mFavStationButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                        database.insert(SQLiteFavHelper.TAB_NAME, null, contentValues);
                    }
                    database.close();

                }

            }
        });

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mStationClusterManager.clearItems();
                getStationsAround();

//                new AsyncArchiveFetch().execute(new AsyncArchiveFetch.FetchObject(MapsActivity.this));
            }
        });

        // theme
        if (isBright)
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_light_theme));
        else
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_dark_theme));

        // setup cluster
        mStationClusterManager = new ClusterManager<>(this, mMap);
        mStationClusterRenderer = new StationClusterRenderer(this, mMap, mStationClusterManager);
        mStationClusterManager.setRenderer(mStationClusterRenderer);


        mMap.setOnCameraIdleListener(mStationClusterManager);
        mMap.setOnMarkerClickListener(mStationClusterManager);
        mStationClusterManager.setOnClusterItemClickListener(mOnMarkerClick);

        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49.2662554, -123.0213313), 9.54f));


        tryStartingGPS();
    }


    /**
     * maps getter
     *
     * @return current google element of maps
     */
    public GoogleMap getmMap() {
        return mMap;
    }

    /**
     * tries to start the gps feature
     *
     * @return if it has succeeded or not
     */
    @SuppressLint("MissingPermission")
    public boolean tryStartingGPS() {
        if (gpsPermissionAvailable()) {
            mMap.setMyLocationEnabled(true);
            return true;
        } else {
            gpsPermissionFetch();
            return false;
        }
    }

    /**
     * gets this activity has location permission
     *
     * @return the mState of the permission
     */
    public boolean gpsPermissionAvailable() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * fetches for the location permission
     */
    public void gpsPermissionFetch() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSION_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tryStartingGPS();
                } else {
                    Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * parses json array
     *
     * @param jsonArray
     * @param id        the mode that you can chose before the fetch
     * @throws JSONException
     */
    private void afterHTTPFetch(JSONArray jsonArray, int id) throws JSONException {
        if (id == FETCH_BUS) {

            for (int c = 0; c < jsonArray.length(); c++) {
                JSONObject bus = jsonArray.getJSONObject(c);

                LatLng busLoc = new LatLng(bus.getDouble("Latitude"), bus.getDouble("Longitude"));
                String routeN = bus.get("RouteNo") + "";
                String time = "Last updated:" + bus.get("RecordedTime");


                Marker m = mMap.addMarker(new MarkerOptions().position(busLoc).title(routeN).snippet(time));
                m.setTag(bus);
            }

        }

        if (id == FETCH_STO) {


            for (int c = 0; c < jsonArray.length(); c++) {
                JSONObject stop = jsonArray.getJSONObject(c);

                LatLng stopLoc = new LatLng(stop.getDouble("Latitude"), stop.getDouble("Longitude"));


                mStationClusterManager.addItem(new StationClusterItem(stopLoc, stop));

//                Marker m = mMap.addMarker(new MarkerOptions().position(stopLoc).title(name).snippet(stopNo).anchor(0, 1).icon(BitmapDescriptorFactory.fromBitmap(createStationMarker(routes))));
//                m.setTag(stop);
            }

            mStationClusterManager.cluster();

        }

        if (id == FETCH_EST) {
            mOnMarkerClick.afterFetch(jsonArray);
        }

    }

    /**
     * gets the stations around the current location
     */
    public void getStationsAround() {

        if (gpsPermissionAvailable()) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            assert locationManager != null;
            @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));


            if (location != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.42f));


                String latitude = String.format(Locale.CANADA, "%.06f", location.getLatitude());
                String longitude = String.format(Locale.CANADA, "%.06f", location.getLongitude());

                httpFetchStart("http://api.translink.ca/rttiapi/v1/stops?apikey="+getString(R.string.translink_key)+"&lat=" + latitude + "&long=" + longitude + "&radius=750", FETCH_STO);
            } else {
                Toast.makeText(this, "location null", Toast.LENGTH_SHORT);
            }
        }
    }

    /**
     * starts the async task
     *
     * @param url the url to fetch
     * @param id  the mode you are using
     * @return
     */
    public AsyncTask httpFetchStart(String url, int id) {
        return new AsyncJsonFetch().execute(new AsyncJsonFetch.FetchObject(url, id, this));
    }

    /**
     * @return the bottom sheet view
     */
    public View getBottomSheetLayout() {
        return mBottomSheet;
    }

    /**
     * called after async json fetch
     *
     * @param fetchObject the fetch object called
     */
    @Override
    public void afterAsyncJsonFetch(AsyncJsonFetch.FetchObject fetchObject) {

        try {
            afterHTTPFetch(fetchObject.mJSONArray, fetchObject.mTaskCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
