package edu.uw.profile.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Kevin on 5/25/2016.
 * Based off TodoListProvider by Joel Ross
 */
public class ProfileProvider extends ContentProvider{
    private static final String TAG = "ProfileProvider";

    //Content Provider details
    private static final String AUTHORITY = "edu.uw.profile.provider";
    private static final String PROFILE_RESOURCE = "profiles";

    //URI details
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/"+PROFILE_RESOURCE);

    //database details
    private static final String DATABASE_NAME = "geoprofiler.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * The schema and contract for the underlying database.
     */
    public static class ProfileEntry implements BaseColumns {
        //class cannot be instantiated
        private ProfileEntry(){}

        public static final String TABLE_NAME = "profiles";
        public static final String COL_LAT = "lat";
        public static final String COL_LNG = "lng";
        public static final String COL_TITLE = "title";
        public static final String COL_SHAPE = "shape";
        public static final String COL_RADIUS = "radius";
        public static final String COL_MON = "mon";
        public static final String COL_TUES = "tues";
        public static final String COL_WED = "wed";
        public static final String COL_THUR = "thur";
        public static final String COL_FRI = "fri";
        public static final String COL_SAT = "sat";
        public static final String COL_SUN = "sun";
        public static final String COL_TIME_START = "time_start";
        public static final String COL_TIME_END = "time_end";
        public static final String COL_COLOR = "color";
        public static final String COL_MESSAGE = "message";
        public static final String COL_ACTIVE = "active";
        public static final String COL_TIME_CREATED = "created_at";
    }

    private static final UriMatcher sUriMatcher; //for handling Uri requests

    //integer values representing each supported resource Uri
    private static final int PROFILES_URI = 1; // /profiles
    private static final int PROFILES_NUM_URI = 2;// /profiles/:id

    static {
        //setup mapping between URIs and IDs
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, PROFILE_RESOURCE, PROFILES_URI);
        sUriMatcher.addURI(AUTHORITY, PROFILE_RESOURCE + "/#", PROFILES_NUM_URI);
    }


    /**
     * A class to help open, create, and update the database
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        //Days and 'active' are integers, 0 for off, 1 for on. Color is also integer
        //Shape: 0 for circle, 1 for square
        private static final String CREATE_PROFILES_TABLE =
                "CREATE TABLE " + ProfileEntry.TABLE_NAME + "(" +
                        ProfileEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ", "+
                        ProfileEntry.COL_TIME_CREATED + " INTEGER" + ","+
                        ProfileEntry.COL_LAT + " DOUBLE" + ","+
                        ProfileEntry.COL_LNG + " DOUBLE" + ","+
                        ProfileEntry.COL_TITLE + " TEXT" + ","+
                        ProfileEntry.COL_SHAPE + " INTEGER" + "," +
                        ProfileEntry.COL_RADIUS + " DOUBLE" + ","+
                        ProfileEntry.COL_MON + " INTEGER" + ","+
                        ProfileEntry.COL_TUES + " INTEGER" + ","+
                        ProfileEntry.COL_WED + " INTEGER" + ","+
                        ProfileEntry.COL_THUR + " INTEGER" + ","+
                        ProfileEntry.COL_FRI + " INTEGER" + ","+
                        ProfileEntry.COL_SAT + " INTEGER" + ","+
                        ProfileEntry.COL_SUN + " INTEGER" + ","+
                        ProfileEntry.COL_TIME_START + " INTEGER" + ","+
                        ProfileEntry.COL_TIME_END + " INTEGER" + ","+
                        ProfileEntry.COL_COLOR + " INTEGER" + ","+
                        ProfileEntry.COL_MESSAGE + " TEXT" + ","+
                        ProfileEntry.COL_ACTIVE + " INTEGER" +
                        ")";

        private static final String DROP_PROFILES_TABLE = "DROP TABLE IF EXISTS "+ProfileEntry.TABLE_NAME;

        public DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.v(TAG, "Creating profiles table");
            db.execSQL(CREATE_PROFILES_TABLE); //create table if needed
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_PROFILES_TABLE); //just drop and recreate table
            onCreate(db);
        }
    }

    private DatabaseHelper mDatabaseHelper;

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext()); //initialize the helper
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //build a query for us
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(ProfileEntry.TABLE_NAME);

        //projection mapping would go here if needed

        switch(sUriMatcher.match(uri)){
            case PROFILES_URI: //all profiles
                //no change
                break;
            case PROFILES_NUM_URI: //single profile
                builder.appendWhere(ProfileEntry._ID + "=" + uri.getPathSegments().get(1)); //restrict to those items
                //numeric data so not need to escape
            default:
                throw new IllegalArgumentException("Unknown URI "+uri);
        }

        //open the database
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        //now pass in the user arguments
        Cursor c = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //validate uri
        if(sUriMatcher.match(uri) != PROFILES_URI) {
            throw new IllegalArgumentException("Unknown URI "+uri);
        }

        //make sure all fields are set
        if(!values.containsKey(ProfileEntry.COL_TITLE)){
            values.put(ProfileEntry.COL_TITLE, "Untitled");
        }

        if(!values.containsKey(ProfileEntry.COL_SHAPE)){
            values.put(ProfileEntry.COL_SHAPE, 100.0);
        }

        if(!values.containsKey(ProfileEntry.COL_RADIUS)){
            values.put(ProfileEntry.COL_RADIUS, 0);
        }

        if(!values.containsKey(ProfileEntry.COL_MON)){
            values.put(ProfileEntry.COL_MON, 0);
        }
        if(!values.containsKey(ProfileEntry.COL_TUES)){
            values.put(ProfileEntry.COL_TUES, 0);
        }
        if(!values.containsKey(ProfileEntry.COL_WED)){
            values.put(ProfileEntry.COL_WED, 0);
        }
        if(!values.containsKey(ProfileEntry.COL_THUR)){
            values.put(ProfileEntry.COL_THUR, 0);
        }
        if(!values.containsKey(ProfileEntry.COL_FRI)){
            values.put(ProfileEntry.COL_FRI, 0);
        }
        if(!values.containsKey(ProfileEntry.COL_SAT)){
            values.put(ProfileEntry.COL_SAT, 0);
        }
        if(!values.containsKey(ProfileEntry.COL_SUN)){
            values.put(ProfileEntry.COL_SUN, 0);
        }

        if(!values.containsKey(ProfileEntry.COL_TIME_START)){
            values.put(ProfileEntry.COL_TIME_START, System.currentTimeMillis());
        }
        if(!values.containsKey(ProfileEntry.COL_TIME_END)){
            values.put(ProfileEntry.COL_TIME_END, System.currentTimeMillis());
        }

        if(!values.containsKey(ProfileEntry.COL_MESSAGE)){
            values.put(ProfileEntry.COL_MESSAGE, "I'm busy so here's an auto-reply message");
        }

        if(!values.containsKey(ProfileEntry.COL_ACTIVE)){
            values.put(ProfileEntry.COL_ACTIVE, 0);
        }

        //created now, no matter what
        values.put(ProfileEntry.COL_TIME_CREATED, System.currentTimeMillis());

        //open the database
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        long rowId = db.insert(ProfileEntry.TABLE_NAME, null, values);

        if (rowId > 0) { //if successful
            Uri noteUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri; //return the URI for the entry
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //open the database;
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        int count;
        switch (sUriMatcher.match(uri)) {
            case PROFILES_URI:
                count = db.delete(ProfileEntry.TABLE_NAME, selection, selectionArgs); //just pass in params
                break;
            case PROFILES_NUM_URI:
                String profileId = uri.getPathSegments().get(1);
                count = db.delete(ProfileEntry.TABLE_NAME, ProfileEntry._ID + "=" + profileId //select by id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs); //apply params
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //open the database;
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        int count;
        switch (sUriMatcher.match(uri)) {
            case PROFILES_URI:
                count = db.update(ProfileEntry.TABLE_NAME, values, selection, selectionArgs); //just pass in params
                break;
            case PROFILES_NUM_URI:
                String profileId = uri.getPathSegments().get(1);
                count = db.update(ProfileEntry.TABLE_NAME, values, ProfileEntry._ID + "=" + profileId //select by id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs); //apply params
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        //return cursor types, per http://developer.android.com/guide/topics/providers/content-provider-creating.html#TableMIMETypes
        switch(sUriMatcher.match(uri)){
            case PROFILES_URI:
                return "vnd.android.cursor.dir/"+AUTHORITY+"."+PROFILE_RESOURCE;
            case PROFILES_NUM_URI:
                return "vnd.android.cursor.item/"+AUTHORITY+"."+PROFILE_RESOURCE;
            default:
                throw new IllegalArgumentException("Unknown URI "+uri);
        }
    }
}
