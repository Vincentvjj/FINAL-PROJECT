package edu.uw.psmith94.geo_profiler;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.list_profiles);
        final AdapterView listView = (AdapterView)findViewById(R.id.profile_list);

        String[] projection = new String[]{Profile.TITLE, Profile.TIME_START, Profile.TIME_END,
            Profile.ACTIVE, Profile.ID};
        adapter = new SimpleCursorAdapter(this,
                R.layout.list_layout,
                null,
                projection,
                new int[]{R.id.listItem, R.id.profile_toggle2}, 0);
        listView.setAdapter(adapter);
        getSupportLoaderManager().initLoader(0, null, this);

        super.onCreate(savedInstanceState);


//        Button editButton = (Button)findViewById(R.id.edit_button);
//
//        editButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ListProfiles.this, DetailActivity.class);
//                startActivity(intent);
//            }
//        });
//    }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{Profile.TITLE, Profile.TIME_START, Profile.TIME_END,
                Profile.ACTIVE, Profile.ID};
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