package edu.uw.psmith94.geo_profiler;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.Calendar;

import edu.uw.profile.provider.Profile;
import edu.uw.profile.provider.ProfileProvider;

public class MyService extends IntentService
{
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    Intent intentNew;

    public MyService() {
        super("MyService");
    }

    private class LocationListener implements android.location.LocationListener{
        Location mLastLocation;
        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }
        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);

            if(intentNew!= null && mLastLocation != null) {
                SmsMessage[] sms = Telephony.Sms.Intents.getMessagesFromIntent(intentNew);
                String author = sms[0].getOriginatingAddress();

                Calendar c = Calendar.getInstance();
                int today = c.get(Calendar.DAY_OF_WEEK);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int min = c.get(Calendar.MINUTE);

                String dayContent;

                switch (today) {
                    case 1:
                        dayContent = Profile.SUN;
                    case 2:
                        dayContent = Profile.MON;
                    case 3:
                        dayContent = Profile.TUES;
                    case 4:
                        dayContent = Profile.WED;
                    case 5:
                        dayContent = Profile.THUR;
                    case 6:
                        dayContent = Profile.FRI;
                    case 7:
                        dayContent = Profile.SAT;
                    default:
                        dayContent = Profile.MON;
                }

                String[] projection = new String[]{Profile.ID, Profile.LAT, Profile.LNG, Profile.SHAPE, Profile.MESSAGE,
                        Profile.RADIUS, Profile.TIME_END, Profile.TIME_START, dayContent, Profile.ACTIVE};

//        String whereClause = Profile.ACTIVE + "= 1" + " AND " + dayContent + " = 1";

                Cursor cur = getContentResolver().query(ProfileProvider.CONTENT_URI, projection,
                        null, null, null);

                while(cur.moveToNext()){
                    Log.v("TAG", cur.getString(cur.getColumnIndex(Profile.MESSAGE)));

                }

                stopSelf();
            }


        }
        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        intentNew = intent.getParcelableExtra("intent");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }
    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

    }
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}