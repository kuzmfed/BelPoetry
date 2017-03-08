package by.fk.belpoetry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PoemListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poem_list);
        setupActionBar();

        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        final String authorName = getIntent().getStringExtra(Keys.KEY_AUTHOR_NAME);
        String[] poemTitles = databaseHelper.getPoemTitlesByAuthor(authorName);

        setTitle(authorName);

        PoemListAdapter adapter = new PoemListAdapter(this, android.R.layout.simple_list_item_1, poemTitles);
        ListView listViewPoems = (ListView) findViewById(R.id.listViewPoems);

        if (listViewPoems != null) {
            listViewPoems.setAdapter(adapter);
            listViewPoems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String title;
                    String item = parent.getItemAtPosition(position).toString();

                    if (authorName.equalsIgnoreCase(Keys.KEY_FAVOURITE_POEM_AUTHOR)) {
                        int index = item.lastIndexOf('-');
                        String authorShortName = item.substring(index + 2);
                        String[] authorShortNames = getResources().getStringArray(R.array.author_short_names);
                        String authorFullName = null;

                        for (int i = 0; i < authorShortNames.length; i++) {
                            if (authorShortNames[i].equalsIgnoreCase(authorShortName)) {
                                authorFullName = getResources().getStringArray(R.array.author_names)[i];
                                break;
                            }
                        }

                        title = item.substring(0, index - 1) + '=' + authorFullName;
                    } else {
                        title = item + '=' + authorName;
                    }

                    Intent intent = new Intent(PoemListActivity.this, PoemActivity.class);
                    intent.putExtra(Keys.KEY_POEM_TITLE, title);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private class PoemListAdapter extends ArrayAdapter<String> {

        private Context mContext;
        private String[] mObjects;

        public PoemListAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);

            mContext = context;
            mObjects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.custom_list_item, parent, false);
            TextView textViewName = (TextView) row.findViewById(R.id.textViewName);
            ImageView imageViewIcon = (ImageView) row.findViewById(R.id.imageViewIcon);
            textViewName.setText(mObjects[position]);
            imageViewIcon.setImageResource(R.mipmap.ic_single);
            return row;
        }
    }
}
