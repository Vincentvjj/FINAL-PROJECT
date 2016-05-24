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
public class NewProfileActivity extends AppCompatActivity {

    private static TextView timeTxtFrom;
    private static TextView timeTxtTo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.new_profile);

        Button timePickerFrom = (Button)findViewById(R.id.timePickerFrom);
        Button timePickerTo = (Button)findViewById(R.id.timePickerTo);
        Button colorPicker = (Button)findViewById(R.id.colorPicker);
        timeTxtFrom = (TextView)findViewById(R.id.timePickedFrom);
        timeTxtTo = (TextView)findViewById(R.id.timePickedTo);


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
