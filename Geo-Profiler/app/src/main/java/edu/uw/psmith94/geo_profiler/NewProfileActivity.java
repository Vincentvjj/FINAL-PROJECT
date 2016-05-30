package edu.uw.psmith94.geo_profiler;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.uw.profile.provider.ProfileProvider;
import es.dmoral.coloromatic.ColorOMaticDialog;
import es.dmoral.coloromatic.IndicatorMode;
import es.dmoral.coloromatic.OnColorSelectedListener;
import es.dmoral.coloromatic.colormode.ColorMode;

/**
 * Created by vincentjoe on 5/21/16.
 */
public class NewProfileActivity extends AppCompatActivity {

    private static final String TAG = "NEWPROFILE";
    private static TextView timeTxtFrom;
    private static TextView timeTxtTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.new_profile);

        Button timePickerFrom = (Button)findViewById(R.id.timePickerFrom);
        final Button timePickerTo = (Button)findViewById(R.id.timePickerTo);
        Button colorPicker = (Button)findViewById(R.id.colorPicker);
        Button saveBtn = (Button)findViewById(R.id.saveBtn);
        Button cancelBtn = (Button)findViewById(R.id.cancelBtn);

        final TextView title = (TextView) findViewById(R.id.title_edit);
        final Switch shape = (Switch) findViewById(R.id.shape_switch);
        final TextView radius = (TextView) findViewById(R.id.radius_edit);
        final CheckBox mon = (CheckBox) findViewById(R.id.mon);
        final CheckBox tue = (CheckBox) findViewById(R.id.tue);
        final CheckBox wed = (CheckBox) findViewById(R.id.wed);
        final CheckBox thu = (CheckBox) findViewById(R.id.thu);
        final CheckBox fri = (CheckBox) findViewById(R.id.fri);
        final CheckBox sat = (CheckBox) findViewById(R.id.sat);
        final CheckBox sun = (CheckBox) findViewById(R.id.sun);

        timeTxtFrom = (TextView)findViewById(R.id.timePickedFrom);
        timeTxtTo = (TextView)findViewById(R.id.timePickedTo);
        final LinearLayout edit_buttons = (LinearLayout)findViewById(R.id.edit_buttons);
        edit_buttons.setVisibility(LinearLayout.GONE);
        final View color = findViewById(R.id.color_box);
        color.setBackgroundColor((int) (Math.random() * -16777216));
        final TextView message = (TextView) findViewById(R.id.auto_reply_message);

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

        timePickerFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timeFragment = new TimePickerFragmentFrom();
                timeFragment.show(getSupportFragmentManager(), "time_picker");
            }
        });

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
                if (radius.getText().toString().isEmpty() || message.getText().toString().isEmpty()
                        || timeTxtFrom.getText().toString().isEmpty() || timeTxtTo.getText().toString().isEmpty()
                        || title.getText().toString().isEmpty()) {
                    Toast.makeText(NewProfileActivity.this, "please fill required fields", Toast.LENGTH_LONG).show();
                }
                else{

                    Bundle bundle = getIntent().getParcelableExtra("edu.uw.psmith94.bundle");
                    LatLng latlng = bundle.getParcelable("latlng");

                    ContentValues mNewValues = new ContentValues();
                    mNewValues.put(ProfileProvider.ProfileEntry.COL_LAT, latlng.latitude);
                    mNewValues.put(ProfileProvider.ProfileEntry.COL_LNG, latlng.longitude);
                    mNewValues.put(ProfileProvider.ProfileEntry.COL_TITLE, title.getText().toString());
                    mNewValues.put(ProfileProvider.ProfileEntry.COL_SHAPE, toInt(shape.isChecked()));
                    mNewValues.put(ProfileProvider.ProfileEntry.COL_RADIUS, radius.getText().toString());
                    mNewValues.put(ProfileProvider.ProfileEntry.COL_MON, toInt(mon.isChecked()));
                    mNewValues.put(ProfileProvider.ProfileEntry.COL_TUE, toInt(tue.isChecked()));
                    mNewValues.put(ProfileProvider.ProfileEntry.COL_WED, toInt(wed.isChecked()));
                    mNewValues.put(ProfileProvider.ProfileEntry.COL_THU, toInt(thu.isChecked()));
                    mNewValues.put(ProfileProvider.ProfileEntry.COL_FRI, toInt(fri.isChecked()));
                    mNewValues.put(ProfileProvider.ProfileEntry.COL_SAT, toInt(sat.isChecked()));
                    mNewValues.put(ProfileProvider.ProfileEntry.COL_SUN, toInt(sun.isChecked()));
                    mNewValues.put(ProfileProvider.ProfileEntry.COL_TIME_START, timeTxtFrom.getText().toString());
                    mNewValues.put(ProfileProvider.ProfileEntry.COL_TIME_END, timeTxtTo.getText().toString());
                    mNewValues.put(ProfileProvider.ProfileEntry.COL_COLOR, ((ColorDrawable) color.getBackground()).getColor());
                    mNewValues.put(ProfileProvider.ProfileEntry.COL_MESSAGE, message.getText().toString());

                    Uri mNewUri = getContentResolver().insert(
                            ProfileProvider.CONTENT_URI,
                            mNewValues
                    );
                    Toast.makeText(NewProfileActivity.this, "Saved!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(NewProfileActivity.this, MapsActivity.class);
                    intent.putExtra("edu.uw.psmith94.saved", true);
                    intent.putExtra("coordinates", latlng);
                    startActivity(intent);
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getParcelableExtra("edu.uw.psmith94.bundle");
                LatLng latlng = bundle.getParcelable("latlng");
                Intent intent = new Intent(NewProfileActivity.this, MapsActivity.class);
                intent.putExtra("edu.uw.psmith94.saved", false);
                intent.putExtra("coordinates", latlng);
                startActivity(intent);
            }
        });

        super.onCreate(savedInstanceState);
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
            SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
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
            SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
            Date date = new Date(0, 0, 0, hourOfDay, minute);
            timeTxtTo.setText(sdf.format(date));
        }
    }
}
