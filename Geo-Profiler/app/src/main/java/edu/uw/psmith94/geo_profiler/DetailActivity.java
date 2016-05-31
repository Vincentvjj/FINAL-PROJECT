package edu.uw.psmith94.geo_profiler;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.uw.profile.provider.Profile;
import edu.uw.profile.provider.ProfileProvider;
import es.dmoral.coloromatic.ColorOMaticDialog;
import es.dmoral.coloromatic.IndicatorMode;
import es.dmoral.coloromatic.OnColorSelectedListener;
import es.dmoral.coloromatic.colormode.ColorMode;

/**
 * Created by vincentjoe on 5/21/16.
 */
public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "DETAIL";
    private static TextView timeTxtFrom;
    private static TextView timeTxtTo;
    private int id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.new_profile);

        Bundle bundle = getIntent().getExtras();
        final double lat = bundle.getDouble("lat");
        final double lng = bundle.getDouble("lng");
        final LinearLayout buttons = (LinearLayout)findViewById(R.id.buttons);
        buttons.setVisibility(LinearLayout.GONE);


        getSupportLoaderManager().initLoader(0, null, this);

        String[] projection = new String[] {Profile.ID, Profile.LAT, Profile.LNG, Profile.TITLE,
                Profile.SHAPE, Profile.RADIUS, Profile.MON, Profile.TUE, Profile.WED,
                Profile.THU, Profile.FRI, Profile.SAT, Profile.SUN,
                Profile.TIME_START, Profile.TIME_END, Profile.COLOR, Profile.MESSAGE};
        Cursor cur = getContentResolver().query(ProfileProvider.CONTENT_URI, projection,
                null, null, null);
        //Gets id of correct profile
        while(cur.moveToNext()){
            if(cur.getDouble(cur.getColumnIndex("lat")) == lat &&
                    cur.getDouble(cur.getColumnIndex("lng")) == lng){
                id = cur.getInt(cur.getColumnIndex("_id"));
                break;
            }
        }

        Button timePickerFrom = (Button)findViewById(R.id.timePickerFrom);
        Button timePickerTo = (Button)findViewById(R.id.timePickerTo);
        Button colorPicker = (Button)findViewById(R.id.colorPicker);
        timeTxtFrom = (TextView)findViewById(R.id.timePickedFrom);
        timeTxtTo = (TextView)findViewById(R.id.timePickedTo);
        final EditText titleEdit = (EditText)findViewById(R.id.title_edit);
        final CheckBox sunCheck = (CheckBox)findViewById(R.id.sun);
        final CheckBox monCheck = (CheckBox)findViewById(R.id.mon);
        final CheckBox tueCheck = (CheckBox)findViewById(R.id.tue);
        final CheckBox wedCheck = (CheckBox)findViewById(R.id.wed);
        final CheckBox thuCheck = (CheckBox)findViewById(R.id.thu);
        final CheckBox friCheck = (CheckBox)findViewById(R.id.fri);
        final CheckBox satCheck = (CheckBox)findViewById(R.id.sat);
        final Switch shapeSwitch = (Switch)findViewById(R.id.shape_switch);
        final EditText radiusEdit = (EditText)findViewById(R.id.radius_edit);
        final EditText autoReply = (EditText)findViewById(R.id.auto_reply_message);

        //Sets all the parameters of profile
        timeTxtFrom.setText(cur.getString(cur.getColumnIndex("time_start")));
        timeTxtTo.setText(cur.getString(cur.getColumnIndex("time_end")));
        titleEdit.setText(cur.getString(cur.getColumnIndex("title")), TextView.BufferType.EDITABLE);
        radiusEdit.setText("" + cur.getDouble(cur.getColumnIndex("radius")), TextView.BufferType.EDITABLE);
        autoReply.setText(cur.getString(cur.getColumnIndex("message")), TextView.BufferType.EDITABLE);
        sunCheck.setChecked(toBool(cur.getInt(cur.getColumnIndex("sun"))));
        monCheck.setChecked(toBool(cur.getInt(cur.getColumnIndex("mon"))));
        tueCheck.setChecked(toBool(cur.getInt(cur.getColumnIndex("tue"))));
        wedCheck.setChecked(toBool(cur.getInt(cur.getColumnIndex("wed"))));
        thuCheck.setChecked(toBool(cur.getInt(cur.getColumnIndex("thu"))));
        friCheck.setChecked(toBool(cur.getInt(cur.getColumnIndex("fri"))));
        satCheck.setChecked(toBool(cur.getInt(cur.getColumnIndex("sat"))));
        shapeSwitch.setChecked(toBool(cur.getInt(cur.getColumnIndex("shape"))));
        final View box = findViewById(R.id.color_box);
        final int color = cur.getInt(cur.getColumnIndex("color"));
        box.setBackgroundColor(color);

        timePickerFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timeFragment = new TimePickerFragmentFrom();
                timeFragment.show(getSupportFragmentManager(), "time_picker");
            }
        });

        timePickerTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timeFragment = new TimePickerFragmentTo();
                timeFragment.show(getSupportFragmentManager(), "time_picker");
            }
        });

        colorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorOMaticDialog.Builder()
                        .colorMode(ColorMode.ARGB) // RGB, ARGB, HVS
                        .initialColor(color)
                        .indicatorMode(IndicatorMode.HEX) // HEX or DECIMAL; Note that using HSV with IndicatorMode.HEX is not recommended
                        .onColorSelected(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(@ColorInt int i) {
                                View box = findViewById(R.id.color_box);
                                box.setBackgroundColor(i);
                            }
                        })
                        .showColorIndicator(true) // Default false, choose to show text indicator showing the current color in HEX or DEC (see images) or not
                        .create()
                        .show(getSupportFragmentManager(), "ColorOMaticDialog");
            }
        });

        Button saveBtn = (Button)findViewById(R.id.edit_saveBtn);
        Button cancelBtn = (Button)findViewById(R.id.edit_cancelBtn);
        Button deleteBtn = (Button)findViewById(R.id.edit_delete);

        //Saves all values and updates database, returns to map screen
        saveBtn.setOnClickListener(new View.OnClickListener() {

            public int toInt(boolean value){
                if(value){
                    return 1;
                }else {
                    return 0;
                }
            }
            @Override
            public void onClick(View v) {
                ContentValues mNewValues = new ContentValues();
                mNewValues.put(ProfileProvider.ProfileEntry.COL_TITLE, titleEdit.getText().toString());
                mNewValues.put(ProfileProvider.ProfileEntry.COL_SHAPE, toInt(shapeSwitch.isChecked()));
                mNewValues.put(ProfileProvider.ProfileEntry.COL_RADIUS, radiusEdit.getText().toString());
                mNewValues.put(ProfileProvider.ProfileEntry.COL_MON, toInt(monCheck.isChecked()));
                mNewValues.put(ProfileProvider.ProfileEntry.COL_TUE, toInt(tueCheck.isChecked()));
                mNewValues.put(ProfileProvider.ProfileEntry.COL_WED, toInt(wedCheck.isChecked()));
                mNewValues.put(ProfileProvider.ProfileEntry.COL_THU, toInt(thuCheck.isChecked()));
                mNewValues.put(ProfileProvider.ProfileEntry.COL_FRI, toInt(friCheck.isChecked()));
                mNewValues.put(ProfileProvider.ProfileEntry.COL_SAT, toInt(satCheck.isChecked()));
                mNewValues.put(ProfileProvider.ProfileEntry.COL_SUN, toInt(sunCheck.isChecked()));
                mNewValues.put(ProfileProvider.ProfileEntry.COL_TIME_START, timeTxtFrom.getText().toString());
                mNewValues.put(ProfileProvider.ProfileEntry.COL_TIME_END, timeTxtTo.getText().toString());
                mNewValues.put(ProfileProvider.ProfileEntry.COL_COLOR, ((ColorDrawable)box.getBackground()).getColor());
                mNewValues.put(ProfileProvider.ProfileEntry.COL_MESSAGE, autoReply.getText().toString());

                getContentResolver().update(ProfileProvider.CONTENT_URI, mNewValues, "_id=" + id, null);

                Toast.makeText(DetailActivity.this, "Saved!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(DetailActivity.this, MapsActivity.class);
                intent.putExtra("edu.uw.psmith94.saved", true);
                startActivity(intent);
            }
        });

        //Returns to map screen
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, MapsActivity.class);
                intent.putExtra("edu.uw.psmith94.saved", false);
                startActivity(intent);
            }
        });

        //Deletes profile
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver cr = getContentResolver();
                cr.delete(ProfileProvider.CONTENT_URI, "_id=" + id, null);

                Intent intent = new Intent(DetailActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        super.onCreate(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[] {Profile.TITLE, Profile.SHAPE,
                Profile.RADIUS, Profile.MON, Profile.TUE, Profile.WED, Profile.THU, Profile.FRI,
                Profile.SAT, Profile.SUN, Profile.TIME_START, Profile.TIME_END, Profile.COLOR,
                Profile.MESSAGE};
        CursorLoader loader = new CursorLoader(DetailActivity.this, ProfileProvider.CONTENT_URI,
                projection, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private boolean toBool(int bool){
        if(bool == 0){
            return false;
        }else {
            return true;
        }
    }

    public static class TimePickerFragmentFrom extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date date = new Date(0, 0, 0, hourOfDay, minute);
            timeTxtFrom.setText(sdf.format(date));
        }
    }

    public static class TimePickerFragmentTo extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date date = new Date(0, 0, 0, hourOfDay, minute);
            timeTxtTo.setText(sdf.format(date));
        }
    }
}
