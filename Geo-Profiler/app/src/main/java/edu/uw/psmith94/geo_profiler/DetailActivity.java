package edu.uw.psmith94.geo_profiler;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import es.dmoral.coloromatic.ColorOMaticDialog;
import es.dmoral.coloromatic.IndicatorMode;
import es.dmoral.coloromatic.OnColorSelectedListener;
import es.dmoral.coloromatic.colormode.ColorMode;

/**
 * Created by vincentjoe on 5/21/16.
 */
public class DetailActivity extends AppCompatActivity{
    private static TextView timeTxtFrom;
    private static TextView timeTxtTo;

    private String profileName = "School";
    private int radius = 50;
    private String hexCode = "";
    private String autoReplyMssg = "I'm at School I'll Get Back to You after Class!";
    private String startTime = "08:30";
    private String endTime = "14:30";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.new_profile);

        Button timePickerFrom = (Button)findViewById(R.id.timePickerFrom);
        Button timePickerTo = (Button)findViewById(R.id.timePickerTo);
        Button colorPicker = (Button)findViewById(R.id.colorPicker);
        timeTxtFrom = (TextView)findViewById(R.id.timePickedFrom);
        timeTxtTo = (TextView)findViewById(R.id.timePickedTo);
        EditText titleEdit = (EditText)findViewById(R.id.title_edit);
        CheckBox sunCheck = (CheckBox)findViewById(R.id.sun);
        CheckBox monCheck = (CheckBox)findViewById(R.id.mon);
        CheckBox tueCheck = (CheckBox)findViewById(R.id.tue);
        CheckBox wedCheck = (CheckBox)findViewById(R.id.wed);
        CheckBox thuCheck = (CheckBox)findViewById(R.id.thurs);
        CheckBox friCheck = (CheckBox)findViewById(R.id.fri);
        CheckBox satCheck = (CheckBox)findViewById(R.id.sat);
        EditText radiusEdit = (EditText)findViewById(R.id.radius_edit);
        EditText autoReply = (EditText)findViewById(R.id.auto_reply_message);

        timeTxtFrom.setText(startTime);
        timeTxtTo.setText(endTime);
        titleEdit.setText(profileName, TextView.BufferType.EDITABLE);
        radiusEdit.setText("" + radius, TextView.BufferType.EDITABLE);
        autoReply.setText(autoReplyMssg, TextView.BufferType.EDITABLE);
        sunCheck.setChecked(false);
        monCheck.setChecked(true);
        tueCheck.setChecked(true);
        wedCheck.setChecked(true);
        thuCheck.setChecked(true);
        friCheck.setChecked(true);
        satCheck.setChecked(false);



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
                                //change something here
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


        colorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorOMaticDialog.Builder()
                        .colorMode(ColorMode.ARGB) // RGB, ARGB, HVS
                        .indicatorMode(IndicatorMode.HEX) // HEX or DECIMAL; Note that using HSV with IndicatorMode.HEX is not recommended
                        .onColorSelected(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(@ColorInt int i) {
                                //change something here
                            }
                        })
                        .showColorIndicator(true) // Default false, choose to show text indicator showing the current color in HEX or DEC (see images) or not
                        .create()
                        .show(getSupportFragmentManager(), "ColorOMaticDialog");
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


            timeTxtFrom.setText(Integer.toString(hourOfDay) + ":" + Integer.toString(minute));
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


            timeTxtTo.setText(Integer.toString(hourOfDay) + ":" + Integer.toString(minute));
        }
    }
}
