package edu.uw.psmith94.geo_profiler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import edu.uw.profile.provider.Profile;
import edu.uw.profile.provider.ProfileProvider;

/**
 * Created by Min on 5/30/2016.
 */
public class MySimpleCursorAdapter extends SimpleCursorAdapter {

    private Context mContext;
    private Context appContext;
    private int layout;
    private Cursor cr;
    private final LayoutInflater inflater;
    private boolean flag;

    public MySimpleCursorAdapter(Context context, int layout, Cursor c,
                                 String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.layout=layout;
        this.mContext = context;
        this.inflater=LayoutInflater.from(context);
        this.cr=c;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        final Context context1 = context;
        TextView title = (TextView)view.findViewById(R.id.list_title);
        TextView timeStart = (TextView)view.findViewById(R.id.list_time_start);
        TextView timeEnd = (TextView)view.findViewById(R.id.list_time_end);
        View colorBox = (View)view.findViewById(R.id.list_color_box);
        Switch swc = (Switch)view.findViewById(R.id.profile_toggle);

        int title_index = cursor.getColumnIndexOrThrow(Profile.TITLE);
        int timeStart_index = cursor.getColumnIndexOrThrow(Profile.TIME_START);
        int timeEnd_index = cursor.getColumnIndexOrThrow(Profile.TIME_END);
        int color_index = cursor.getColumnIndexOrThrow(Profile.COLOR);
        int active_index = cursor.getColumnIndex(Profile.ACTIVE);
        int id_index = cursor.getColumnIndex(Profile.ID);

        final int id = cursor.getInt(id_index);

        title.setText(cursor.getString(title_index));
        timeStart.setText(cursor.getString(timeStart_index));
        timeEnd.setText(cursor.getString(timeEnd_index));
        colorBox.setBackgroundColor(cursor.getInt(color_index));

        if (cursor.getInt(active_index) == 0) {
            Log.v("TAG", "false " + id);
            swc.setChecked(false);
        } else {
            Log.v("TAG", "true " + id);
            swc.setChecked(true);
        }

        final ContentValues mNewValue = new ContentValues();

        swc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Switch)v).isChecked()) {
                    mNewValue.put(ProfileProvider.ProfileEntry.COL_ACTIVE, 1);
                } else {
                    mNewValue.put(ProfileProvider.ProfileEntry.COL_ACTIVE, 0);
                }
                context1.getContentResolver().update(ProfileProvider.CONTENT_URI, mNewValue, "_id=" + id, null);
            }
        });

//        swc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(flag) {
//                    if (buttonView.isChecked()) {
//                        mNewValue.put(ProfileProvider.ProfileEntry.COL_ACTIVE, 1);
//                    } else {
//                        mNewValue.put(ProfileProvider.ProfileEntry.COL_ACTIVE, 0);
//                    }
//                    Log.v("AASDA", "" + id);
//                    context1.getContentResolver().update(ProfileProvider.CONTENT_URI, mNewValue, "_id=" + id, null);
//                }
//            }
//        });

    }
}
