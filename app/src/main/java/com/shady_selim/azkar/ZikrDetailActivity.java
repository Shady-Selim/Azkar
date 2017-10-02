package com.shady_selim.azkar;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ZikrDetailActivity extends AppCompatActivity {
    @BindView(R.id.detail_toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zikr_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle(getIntent().getStringExtra("name"));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            toolbar.setTitle(getIntent().getStringExtra("name"));
            Bundle arguments = new Bundle();
            arguments.putString("name", getIntent().getStringExtra("name"));
            arguments.putParcelable("azkar", getIntent().getParcelableExtra("azkar"));
            ZikrDetailFragment fragment = new ZikrDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.zikr_detail_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
