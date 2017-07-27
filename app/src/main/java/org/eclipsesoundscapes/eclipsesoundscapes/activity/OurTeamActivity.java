package org.eclipsesoundscapes.eclipsesoundscapes.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.accessibility.AccessibilityEvent;

import org.eclipsesoundscapes.eclipsesoundscapes.R;
import org.eclipsesoundscapes.eclipsesoundscapes.adapters.PartnerTeamAdapter;

public class OurTeamActivity extends AppCompatActivity {

    private RecyclerView partnersRecyclerView;
    private PartnerTeamAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String[] teamList;
    private String[] teamDescription;
    private String[] teamTitle;
    private Integer[] teamImgs = {R.drawable.henry_winter, R.drawable.marykay_severino, R.drawable.arlindo,
            R.drawable.miles_gordon, R.drawable.christina_migliore, R.drawable.kristin_divona, R.drawable.kelsey_perrett,
            R.drawable.ic_default};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_our_team);

        getSupportActionBar().setTitle(getString(R.string.our_team));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        partnersRecyclerView = (RecyclerView) findViewById(R.id.teams_recyclerview);
        teamList = getResources().getStringArray(R.array.team);
        teamDescription = getResources().getStringArray(R.array.team_bio);
        teamTitle = getResources().getStringArray(R.array.team_title);

        layoutManager = new LinearLayoutManager(this);
        adapter = new PartnerTeamAdapter(teamList, teamTitle, teamDescription, teamImgs, true);
        partnersRecyclerView.setLayoutManager(layoutManager);
        partnersRecyclerView.setAdapter(adapter);
        partnersRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
