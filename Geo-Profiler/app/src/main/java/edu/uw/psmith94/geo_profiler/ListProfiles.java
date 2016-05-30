package edu.uw.psmith94.geo_profiler;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import edu.uw.profile.provider.Profile;
import edu.uw.profile.provider.ProfileProvider;

/**
 * Created by vincentjoe on 5/21/16.
 */
public class ListProfiles extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.list_profiles);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_view_item, null,
                new String[]{Profile.TITLE, Profile.TIME_START, Profile.TIME_END},
                new int[]{R.id.list_title, R.id.list_time_start, R.id.list_time_end}, 0);

        ListView listView = (ListView)findViewById(R.id.profile_list);
        listView.setAdapter(adapter);

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
}