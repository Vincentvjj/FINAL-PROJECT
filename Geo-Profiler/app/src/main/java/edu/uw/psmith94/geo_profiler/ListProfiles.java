package edu.uw.psmith94.geo_profiler;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import edu.uw.profile.provider.Profile;
import edu.uw.profile.provider.ProfileProvider;

/**
 * Created by vincentjoe on 5/21/16.
 */
public class ListProfiles extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "List Profiles";

    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.list_profiles);
        final AdapterView listView = (AdapterView)findViewById(R.id.profile_list);

        String[] projection = new String[]{Profile.TITLE, Profile.TIME_START, Profile.TIME_END,
                Profile.COLOR, Profile.ACTIVE, Profile.ID};
        adapter = new MySimpleCursorAdapter(this,
                R.layout.list_view_item,
                null,
                projection,
                new int[]{R.id.list_title, R.id.list_time_start, R.id.list_time_end}, 0);

            listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "---------------------------");
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", cursor.getDouble(cursor.getColumnIndex("lat")));
                bundle.putDouble("lng", cursor.getDouble(cursor.getColumnIndex("lng")));
                Intent intent = new Intent(ListProfiles.this, DetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(0, null, this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{Profile.TITLE, Profile.TIME_START, Profile.TIME_END,
                Profile.COLOR, Profile.ACTIVE, Profile.ID, Profile.LAT, Profile.LNG};
        CursorLoader loader = new CursorLoader(ListProfiles.this, ProfileProvider.CONTENT_URI,
                projection, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}