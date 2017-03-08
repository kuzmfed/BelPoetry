package by.fk.belpoetry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AuthorListFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AuthorListAdapter adapter = new AuthorListAdapter(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.author_names));
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_author_list, null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(getActivity(), PoemListActivity.class);
        intent.putExtra(Keys.KEY_AUTHOR_NAME, l.getItemAtPosition(position).toString());
        startActivity(intent);
    }

    private class AuthorListAdapter extends ArrayAdapter<String> {

        private Context mContext;
        private String[] mObjects;
        private final int[] mAuthorImages = {
                R.drawable.f00,
                R.drawable.f01,
                R.drawable.f02,
                R.drawable.f03,
                R.drawable.f04,
                R.drawable.f04_50,
                R.drawable.f04_51,
                R.drawable.f05,
                R.drawable.f06,
                R.drawable.f07,
                R.drawable.f08,
                R.drawable.f09,
                R.drawable.f10,
                R.drawable.f11,
                R.drawable.f12,
                R.drawable.f13,
                R.drawable.f14,
                R.drawable.f15,
                R.drawable.f16,
                R.drawable.f17,
                R.drawable.f18,
                R.drawable.f19,
                R.drawable.f20,
                R.drawable.f21,
                R.drawable.f22,
                R.drawable.f23,
                R.drawable.f23_00,
                R.drawable.f24,
                R.drawable.f25,
                R.drawable.f26,
                R.drawable.f27,
                R.drawable.f28,
                R.drawable.f29,
                R.drawable.f30,
                R.drawable.f30_00,
                R.drawable.f31
        };

        public AuthorListAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);

            mContext = context;
            mObjects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.custom_list_item, parent, false);
            TextView textViewName = (TextView) row.findViewById(R.id.textViewName);
            ImageView imageViewIcon = (ImageView) row.findViewById(R.id.imageViewIcon);
            textViewName.setText(mObjects[position]);
            imageViewIcon.setImageResource(mAuthorImages[position]);
            return row;
        }
    }
}
