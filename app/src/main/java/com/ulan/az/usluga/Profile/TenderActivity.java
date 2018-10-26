package com.ulan.az.usluga.Profile;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.ulan.az.usluga.R;
import com.ulan.az.usluga.forum.ForumCategoryFragment;
import com.ulan.az.usluga.helpers.ViewPagerAdapter;
import com.ulan.az.usluga.order.OrderFragment;
import com.ulan.az.usluga.service.ServicesFragment;

public class TenderActivity extends AppCompatActivity {
    ViewPager viewPager;
    TabLayout tabLayout;
    FragmentManager fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tender);
        fm = getSupportFragmentManager();
        getSupportActionBar().setTitle("Мои предложения");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(fm);


        adapter.addFragment(new TenderServiceFragment(), "Услуги");
        adapter.addFragment(new TenderOrderFragment(), "Задачи");
      //  adapter.addFragment(new ForumCategoryFragment(), "Форум");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
