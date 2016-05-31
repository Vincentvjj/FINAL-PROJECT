package edu.uw.psmith94.geo_profiler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import edu.uw.profile.provider.Profile;
import edu.uw.profile.provider.ProfileProvider;


/**
 * Created by Min on 5/28/2016.
 */

public class Receiver extends BroadcastReceiver {

    LocationManager mLocationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {

            SmsMessage[] sms = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            String author = sms[0].getOriginatingAddress();

            Calendar c = Calendar.getInstance();
            int today = c.get(Calendar.DAY_OF_WEEK);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);

            String dayContent = "";

            if(today == 1) {
                dayContent = Profile.SUN;
            } else if (today == 2) {
                dayContent = Profile.MON;
            } else if (today == 3) {
                dayContent = Profile.TUE;
            }else if (today == 4) {
                dayContent = Profile.WED;
            }else if (today == 5) {
                dayContent = Profile.THU;
            } else if (today == 6) {
                dayContent = Profile.FRI;
            } else if (today == 7) {
                dayContent = Profile.SUN;
            }

            String[] projection = new String[]{Profile.ID, Profile.LAT, Profile.LNG, Profile.SHAPE, Profile.MESSAGE,
                    Profile.RADIUS, Profile.TIME_END, Profile.TIME_START, dayContent, Profile.ACTIVE};

            String whereClause = Profile.ACTIVE + "= 1" + " AND " + dayContent + " = 1";

            Cursor cur = context.getContentResolver().query(ProfileProvider.CONTENT_URI, projection,
                    whereClause, null, null);

            
            Location lo = getLastKnownLocation(context);
            if(lo == null) {
                Toast.makeText(context, "Please turn on location to auto-reply", Toast.LENGTH_LONG).show();
            } else {
                while(cur.moveToNext()){
                    String[] timeArrStart = cur.getString(cur.getColumnIndex(Profile.TIME_START)).split(":");
                    String[] timeArrEnd = cur.getString(cur.getColumnIndex(Profile.TIME_END)).split(":");

                    int timeStart= Integer.parseInt(timeArrStart[0]) * 24 + (Integer.parseInt(timeArrStart[1]));
                    int timeEnd = Integer.parseInt(timeArrEnd[0]) * 24 + Integer.parseInt(timeArrEnd[1]) ;
                    int timeNow = hour * 24 + min;

                    Double lat = cur.getDouble(cur.getColumnIndex(Profile.LAT));
                    Double lng = cur.getDouble(cur.getColumnIndex(Profile.LNG));


                    if(timeStart <= timeNow && timeEnd >= timeNow) {

//                    check coordinates now
                        float[] distance = new float[2];


                        if (cur.getInt(cur.getColumnIndex(Profile.SHAPE)) == 0) {
                            Location.distanceBetween(lo.getLatitude(), lo.getLongitude(), lat, lng, distance);

                            Log.v("TAG", distance[0] + "");
                            Log.v("TAG", cur.getInt(cur.getColumnIndex(Profile.RADIUS)) + "");
                            if (distance[0] <= cur.getInt(cur.getColumnIndex(Profile.RADIUS))) {
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(author, null, cur.getString(cur.getColumnIndex(Profile.MESSAGE))
                                        , null, null);
                            }
                        } else {
                            double radius = cur.getInt(cur.getColumnIndex(Profile.RADIUS));
                            double latRadius = radius / 111111;
                            double lonRadius = radius / 75114;
                            double left = lng - (lonRadius/2);
                            double right = lng + (lonRadius/2);
                            double top = lat + (latRadius/2);
                            double bot = lat - (latRadius/2);
                            if((lo.getLongitude() >= left && lo.getLongitude() <= right)
                                    && (lo.getLatitude() >= bot && lo.getLatitude() <= top)) {
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(author, null, cur.getString(cur.getColumnIndex(Profile.MESSAGE))
                                        , null, null);
                            }
                        }
                    }

                }
                cur.close();

            }


        }
    }


    private Location getLastKnownLocation(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }


}


