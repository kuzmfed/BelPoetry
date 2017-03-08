package by.fk.belpoetry;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static long backPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        ViewPager pager = (ViewPager) findViewById(R.id.pager_home);
        setupViewPager(pager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_home);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(pager);
        }

        if (false) {
            DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
            //databaseHelper.onUpgrade(databaseHelper.getWritableDatabase(), 1, 1);
            //databaseHelper.insertAuthors(getResources().getStringArray(R.array.author_names), getResources().getStringArray(R.array.author_short_names));
            databaseHelper.upgradeTable(DatabaseHelper.DATABASE_TABLE_SINGLE_POEMS);
            int authorIndex = 0;
            databaseHelper.insertData(getString(R.string.poem_titles), getString(R.string.poem_texts), getResources().getStringArray(R.array.author_names)[authorIndex]);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                new AboutDialog().show(getSupportFragmentManager(), "dialog_about");
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), R.string.exit_app_message, Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }

    private void setupViewPager(ViewPager pager) {
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new AuthorListFragment(), getString(R.string.belarussian_poets));
        pagerAdapter.addFragment(new SingleListFragment(), getString(R.string.single_poems));
        pagerAdapter.addFragment(new FavouriteListFragment(), getString(R.string.favourite));
        pager.setAdapter(pagerAdapter);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }
}
