package by.fk.belpoetry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FavouriteListFragment extends ListFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        String[] favouritePoems = databaseHelper.getPoemTitlesByAuthor(Keys.KEY_FAVOURITE_POEM_AUTHOR);
        FavouriteListAdapter adapter = new FavouriteListAdapter(getContext(), android.R.layout.simple_list_item_1, favouritePoems);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourite_list, null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String item = l.getItemAtPosition(position).toString();
        int index = item.lastIndexOf('-');
        String title = item.substring(0, index - 1);
        String authorShortName = item.substring(index + 2);
        String[] authorShortNames = getResources().getStringArray(R.array.author_short_names);
        String authorFullName = authorShortName;

        for (int i = 0; i < authorShortNames.length; i++) {
            if (authorShortNames[i].equalsIgnoreCase(authorShortName)) {
                authorFullName = getResources().getStringArray(R.array.author_names)[i];
                break;
            }
        }

        Intent intent = new Intent(getActivity(), PoemActivity.class);
        intent.putExtra(Keys.KEY_POEM_TITLE, title + '=' + authorFullName);
        startActivity(intent);
    }

    private class FavouriteListAdapter extends ArrayAdapter<String> {

        private Context mContex;
        private String[] mStrings;

        public FavouriteListAdapter(Context context, int textViewResourceId, String[] strings) {
            super(context, textViewResourceId, strings);

            mContex = context;
            mStrings = strings;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContex.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.custom_list_item, parent, false);
            TextView textViewName = (TextView) row.findViewById(R.id.textViewName);
            ImageView imageViewIcon = (ImageView) row.findViewById(R.id.imageViewIcon);
            textViewName.setText(mStrings[position]);
            imageViewIcon.setImageResource(R.mipmap.ic_single);
            return row;
        }
    }
}
