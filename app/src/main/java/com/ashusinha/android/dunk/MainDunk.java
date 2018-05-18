package com.ashusinha.android.dunk;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainDunk extends AppCompatActivity {

    private ViewPager mViewPager;
    private MainDunk.SectionsPagerAdapter mSectionsPagerAdapter;
    private BottomNavigationView mBottomNavigationView;
    private MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mSectionsPagerAdapter = new MainDunk.SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mBottomNavigationView = findViewById(R.id.navigation);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_global:
                                mViewPager.setCurrentItem(0);
                                break;
                            case R.id.navigation_personal:
                                mViewPager.setCurrentItem(1);
                                break;
                            case R.id.navigation_upload:
                                mViewPager.setCurrentItem(2);
                                break;
                        }
                        return false;
                    }
                });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    mBottomNavigationView.getMenu().getItem(0).setChecked(false);
                }

                mBottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = mBottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        /*Button logoutButton = findViewById(R.id.action_logout);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = Academics.this.getSharedPreferences("com.vyommaitreya.android.scienceup", Context.MODE_PRIVATE);
                sharedPref.edit().putString("username", null).commit();
                sharedPref.edit().putString("password", null).commit();

                startActivity(new Intent(Academics.this, Login.class));
                finish();
            }
        });*/
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.grades, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            SharedPreferences sharedPref = Academics.this.getSharedPreferences("com.vyommaitreya.android.scienceup", Context.MODE_PRIVATE);
            sharedPref.edit().putString("username", null).commit();
            sharedPref.edit().putString("password", null).commit();

            startActivity(new Intent(Academics.this, Login.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new Global();
                case 1:
                    return new Personal();
                case 2:
                    return new Upload();
                default:
                    return null;
            }
        }


        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
