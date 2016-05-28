//package edu.uw.psmith94.geo_profiler;
//
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.location.Location;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.provider.Settings;
//import android.provider.Telephony;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.TaskStackBuilder;
//import android.telephony.SmsManager;
//import android.telephony.SmsMessage;
//import android.util.Log;
//
//
///**
// * Created by Min on 5/28/2016.
// */
//
//public class Receiver extends BroadcastReceiver {
//
//    private static final String AUTHORITY = "edu.uw.profile.provider";
//
//
//    public static final String TAG = "ReceiveMessage";
//    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
//    public static final String ACTION_SMS_SENT = "edu.uw.notsetdemo.ACTION_SMS_SENT";
//    public static final int SMS_NOTIFY_CODE = 3;
//
//    private static final int SMS_SEND_CODE = 1;
//
//    private String num;
//    private String body;
//
//    private Location curLoc;
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        final Bundle bundle = intent.getExtras();
//        if(intent.getAction().equals(SMS_RECEIVED)) {
//            Log.v(TAG, "received");
//            {
//                SmsMessage message = Telephony.Sms.Intents.getMessagesFromIntent(intent)[0];
//            }
//            try {
//                Object[] pdus = (Object[]) bundle.get("pdus");
//
//                for (int i = 0; i < pdus.length; i++) {
//                    SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[i]);
//                    num = message.getDisplayOriginatingAddress();
//                    body = message.getMessageBody();
//
//                    notify(context, message);
//                }
//
//                //TODO: make sure checkbox, time, location, is matched
//
//
//                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//                if (preferences.getBoolean("pref_auto", false)) {
//                    SmsManager smsManager = SmsManager.getDefault();
//                    String response = preferences.getString("pref_edit_text", "Response").toString();
//                    if (response == "") {
//                        response = "default";
//                    }
//
//                    Intent smsIntent = new Intent(ACTION_SMS_SENT);
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, SMS_SEND_CODE, smsIntent, 0);
//                    smsManager.sendTextMessage(
//                            num,
//                            null,
//                            response,
//                            pendingIntent,
//                            null
//                    );
//                }
//
//            } catch (Exception e) {
//
//            }
//        }
//    }
//
//    private static final int NOTIFY_CODE = 0;
//
//    public void notify(Context context, SmsMessage message){
//        Log.v(TAG, "Notify button pressed");
//
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
//                .setContentTitle(num)
//                .setSmallIcon(R.drawable.ic_notification)
//                .setContentText(body);
//
//        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
//        builder.setVibrate(new long[]{0, 500, 500, 500});
//        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
//
//
//        Intent intent = new Intent(context, MapsActivity.class);
//
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addNextIntent(intent);
//        PendingIntent pendingIntent = stackBuilder.getPendingIntent(SMS_NOTIFY_CODE, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        builder.setContentIntent(pendingIntent); //what to happen when clicked
//
//        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(NOTIFY_CODE, builder.build());
//    }
//}
//
//
