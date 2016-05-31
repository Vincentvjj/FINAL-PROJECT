package edu.uw.psmith94.geo_profiler;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import edu.uw.profile.provider.Profile;

/**
 * Created by Min on 5/30/2016.
 */
public class MySimpleCursorAdapter extends SimpleCursorAdapter {

    private Context mContext;
    private Context appContext;
    private int layout;
    private Cursor cr;
    private final LayoutInflater inflater;

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
        TextView title = (TextView)view.findViewById(R.id.list_title);
        TextView timeStart = (TextView)view.findViewById(R.id.list_time_start);
        TextView timeEnd = (TextView)view.findViewById(R.id.list_time_end);
        View colorBox = (View)view.findViewById(R.id.list_color_box);

        int title_index = cursor.getColumnIndexOrThrow(Profile.TITLE);
        int timeStart_index = cursor.getColumnIndexOrThrow(Profile.TIME_START);
        int timeEnd_index = cursor.getColumnIndexOrThrow(Profile.TIME_END);
        int color_index = cursor.getColumnIndexOrThrow(Profile.COLOR);

        title.setText(cursor.getString(title_index));
        timeStart.setText(cursor.getString(timeStart_index));
        timeEnd.setText(cursor.getString(timeEnd_index));
        colorBox.setBackgroundColor(cursor.getInt(color_index));

    }
}
