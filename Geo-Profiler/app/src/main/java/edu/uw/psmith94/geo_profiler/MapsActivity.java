package edu.uw.psmith94.geo_profiler;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import edu.uw.profile.provider.Profile;
import edu.uw.profile.provider.ProfileProvider;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener, LocationListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "MAP";
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private static Location curLoc;
    private Marker curLocMarker;
    private LatLng currentPoints;
    private boolean saved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.setRetainInstance(true);
        mapFragment.getMapAsync(this);
        getSupportActionBar();


        if(getIntent().getParcelableExtra("coordinates") != null) {
            currentPoints = getIntent().getParcelableExtra("coordinates");
        }

        if(mGoogleApiClient == null) {
            mGoogleApiClient =
                    new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
                marker.setPosition(latLng);
                Bundle args = new Bundle();
                args.putParcelable("latlng", latLng);
                Intent intent = new Intent(MapsActivity.this, NewProfileActivity.class);
                intent.putExtra("edu.uw.psmith94.bundle", args);
                startActivity(intent);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!marker.equals(curLocMarker)){
                    Intent intent = new Intent(MapsActivity.this, DetailActivity.class);
                    double lat = marker.getPosition().latitude;
                    double lng = marker.getPosition().longitude;
                    Bundle bundle = new Bundle();
                    bundle.putDouble("lat", lat);
                    bundle.putDouble("lng", lng);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                return true;
            }
        });
        String[] projection = new String[] {Profile.ID, Profile.LAT, Profile.LNG, Profile.TITLE,
                Profile.SHAPE, Profile.RADIUS, Profile.MON, Profile.TUE, Profile.WED,
                Profile.THU, Profile.FRI, Profile.SAT, Profile.SUN,
                Profile.TIME_START, Profile.TIME_END, Profile.COLOR, Profile.MESSAGE};
        Cursor cur = getContentResolver().query(ProfileProvider.CONTENT_URI, projection,
                null, null, null);

        while(cur.moveToNext()){
            double lat = cur.getDouble(cur.getColumnIndex("lat"));
            double lng = cur.getDouble(cur.getColumnIndex("lng"));
            LatLng latlng = new LatLng(lat, lng);
            int shape = cur.getInt(cur.getColumnIndex("shape"));
            double radius = cur.getDouble(cur.getColumnIndex("radius"));
            int color = cur.getInt(cur.getColumnIndex("color"));
            mMap.addMarker(new MarkerOptions().position(latlng));
            if(shape == 0){
                mMap.addCircle(new CircleOptions()
                        .center(latlng)
                        .radius(radius)
                        .fillColor((color & 0x00FFFFFF) | 0x40000000)
                        .strokeColor(color));
            } else{
                radius = radius / 111111;
                double left = lng - (radius/2);
                double right = lng + (radius/2);
                double top = lat + (radius/2);
                double bot = lat - (radius/2);
                mMap.addPolygon(new PolygonOptions()
                        .add(new LatLng(top, left), new LatLng(bot, left))
                        .add(new LatLng(bot, left), new LatLng(bot, right))
                        .add(new LatLng(bot, right), new LatLng(top, right))
                        .add(new LatLng(top, right), new LatLng(top, left))
                        .fillColor((color & 0x00FFFFFF) | 0x40000000)
                        .strokeColor(color));
            }
        }
        cur.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_list:
                Intent intent = new Intent(MapsActivity.this, ListProfiles.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final int REQUEST_CODE = 0;
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest request = locationRequest();

        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permission == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
        }
        else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
        }

        if(curLoc == null) {
            curLoc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } else {
            LatLng latlng = new LatLng(curLoc.getLatitude(), curLoc.getLongitude());
            curLocMarker = mMap.addMarker(new MarkerOptions().position(latlng)
                    .title("Current Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }

        if(currentPoints == null) {
            if(curLoc != null) {
                LatLng latlng = new LatLng(curLoc.getLatitude(), curLoc.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16.0f));
            }
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPoints));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPoints, 16.0f));
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            updateMap(location);
        }else{
            Log.v(TAG, "Got null location");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationRequest request = locationRequest();
                    int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This app requires Location Access to function.")
                            .setTitle("Closing app");
                    AlertDialog dialog = builder.create();
                    finish();
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //Updates map by moving camera and marker to new location
    public void updateMap(Location location){
        if(location != null) {
            if(curLocMarker != null) {
                curLocMarker.remove();
            }
            curLoc = location;
            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            curLocMarker = mMap.addMarker(new MarkerOptions().position(current)
                    .title("Current Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 16.0f));
        }else{
            Log.v(TAG, "Got null location");
        }
    }

    //Request to get location
    public LocationRequest locationRequest(){
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(10000);
        request.setFastestInterval(5000);
        return request;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[] {Profile.LAT, Profile.LNG, Profile.SHAPE,
                Profile.RADIUS, Profile.COLOR};
        CursorLoader loader = new CursorLoader(MapsActivity.this, ProfileProvider.CONTENT_URI,
                projection, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
