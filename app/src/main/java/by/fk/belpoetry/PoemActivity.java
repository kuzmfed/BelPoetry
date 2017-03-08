package by.fk.belpoetry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PoemActivity extends AppCompatActivity {

    private DatabaseHelper mDatabaseHelper;
    private String mAuthorName;
    private String mPoemTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poem);
        setupActionBar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mDatabaseHelper.isFavourite(mPoemTitle, mAuthorName)) {
                        mDatabaseHelper.insertFavourite(mPoemTitle, mAuthorName);
                        Toast.makeText(getBaseContext(), R.string.added_to_favourite, Toast.LENGTH_SHORT).show();
                    } else {
                        mDatabaseHelper.deleteFavourite(mPoemTitle, mAuthorName);
                        Toast.makeText(getBaseContext(), R.string.deleted_from_favourite, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        mDatabaseHelper = new DatabaseHelper(getApplicationContext());

        String title = getIntent().getStringExtra(Keys.KEY_POEM_TITLE);
        int index = title.indexOf('=');
        mPoemTitle = title.substring(0, index);
        mAuthorName = title.substring(index + 1);

        String poemText = mDatabaseHelper.getText(mPoemTitle, mAuthorName);
        String authorShortName = mDatabaseHelper.getAuthorShortName(mAuthorName);
        if (authorShortName == null) {
            setTitle(mAuthorName);
        } else {
            setTitle(authorShortName);
        }

        TextView textViewTitle = (TextView) findViewById(R.id.textViewPoemTitle);
        TextView textViewPoem = (TextView) findViewById(R.id.textViewPoem);
        if (textViewTitle != null) {
            textViewTitle.setText(mPoemTitle);
        }
        if (textViewPoem != null) {
            textViewPoem.setText(poemText);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        TextView textViewTitle = (TextView) findViewById(R.id.textViewPoemTitle);
        TextView textViewPoem = (TextView) findViewById(R.id.textViewPoem);

        String fontSizeString = preferences.getString(getString(R.string.key_text_size), "" + ReadingPreferenceActivity.DEFAULT_FONT_SIZE);
        int fontSize;

        try {
            fontSize = Integer.parseInt(fontSizeString);
            if (fontSize < 16 && fontSize > 32) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            fontSize = ReadingPreferenceActivity.DEFAULT_FONT_SIZE;
            Toast.makeText(getApplicationContext(), R.string.error_message_font_size, Toast.LENGTH_SHORT).show();
        }

        if (textViewTitle != null) {
            textViewTitle.setTextSize(fontSize + 2);
        }

        if (textViewPoem != null) {
            textViewPoem.setTextSize(fontSize);
        }

        String fontColor = preferences.getString(getString(R.string.key_text_color), ReadingPreferenceActivity.DEFAULT_FONT_COLOR);
        String[] fontColors = getResources().getStringArray(R.array.font_colors);
        int color = 0;

        for (int i = 0; i < fontColors.length; i++) {
            if (fontColor.equals(fontColors[i])) {
                color = i;
            }
        }

        if (textViewTitle != null && textViewPoem != null) {
            switch (color) {
                case 1:
                    textViewTitle.setTextColor(getResources().getColor(R.color.white));
                    textViewPoem.setTextColor(getResources().getColor(R.color.white));
                    break;
                case 2:
                    textViewTitle.setTextColor(getResources().getColor(R.color.red));
                    textViewPoem.setTextColor(getResources().getColor(R.color.red));
                    break;
                case 3:
                    textViewTitle.setTextColor(getResources().getColor(R.color.orange));
                    textViewPoem.setTextColor(getResources().getColor(R.color.orange));
                    break;
                case 4:
                    textViewTitle.setTextColor(getResources().getColor(R.color.yellow));
                    textViewPoem.setTextColor(getResources().getColor(R.color.yellow));
                    break;
                case 5:
                    textViewTitle.setTextColor(getResources().getColor(R.color.green));
                    textViewPoem.setTextColor(getResources().getColor(R.color.green));
                    break;
                case 6:
                    textViewTitle.setTextColor(getResources().getColor(R.color.light_blue));
                    textViewPoem.setTextColor(getResources().getColor(R.color.light_blue));
                    break;
                case 7:
                    textViewTitle.setTextColor(getResources().getColor(R.color.blue));
                    textViewPoem.setTextColor(getResources().getColor(R.color.blue));
                    break;
                case 8:
                    textViewTitle.setTextColor(getResources().getColor(R.color.violet));
                    textViewPoem.setTextColor(getResources().getColor(R.color.violet));
                    break;
                case 9:
                    textViewTitle.setTextColor(getResources().getColor(R.color.black));
                    textViewPoem.setTextColor(getResources().getColor(R.color.black));
                    break;
            }
        }

        boolean readingMode = preferences.getBoolean(getString(R.string.key_night), false);
        android.support.design.widget.CoordinatorLayout poemActivityLayout = (android.support.design.widget.CoordinatorLayout) findViewById(R.id.activity_poem_layout);

        if (!readingMode) {
            if (textViewTitle != null) {
                textViewTitle.setTextColor(getResources().getColor(R.color.poem_day_text_color));
            }

            if (textViewPoem != null) {
                textViewPoem.setTextColor(getResources().getColor(R.color.poem_day_text_color));
            }

            if (poemActivityLayout != null) {
                poemActivityLayout.setBackgroundColor(getResources().getColor(R.color.poem_background));
            }
        } else {
            if (textViewTitle != null) {
                textViewTitle.setTextColor(getResources().getColor(R.color.poem_night_text_color));
            }

            if (textViewPoem != null) {
                textViewPoem.setTextColor(getResources().getColor(R.color.poem_night_text_color));
            }

            if (poemActivityLayout != null) {
                poemActivityLayout.setBackgroundColor(getResources().getColor(R.color.poem_night_backround));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_poem, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home_screen:
                startActivity(new Intent(this, HomeActivity.class));
                break;
            case R.id.action_settigs:
                startActivity(new Intent(this, ReadingPreferenceActivity.class));
                break;
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

    /*private String trimText(String text) {
        text = text.replace("==", "=");
        if (text.startsWith("=")) {
            text = text.substring(1);
        }
        if (text.endsWith("=")) {
            text = text.substring(0, text.length() - 1);
        }

        return text;
        return null;
    }*/
}
