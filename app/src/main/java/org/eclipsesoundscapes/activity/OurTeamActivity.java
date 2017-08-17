package org.eclipsesoundscapes.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import org.eclipsesoundscapes.adapters.PartnerTeamAdapter;

public class OurTeamActivity extends AppCompatActivity {

    private RecyclerView partnersRecyclerView;
    private PartnerTeamAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String[] teamList;
    private String[] teamDescription;
    private String[] teamTitle;
    private Integer[] teamImgs = {org.eclipsesoundscapes.R.drawable.henry_winter, org.eclipsesoundscapes.R.drawable.marykay_severino, org.eclipsesoundscapes.R.drawable.arlindo,
            org.eclipsesoundscapes.R.drawable.miles_gordon, org.eclipsesoundscapes.R.drawable.christina_migliore, org.eclipsesoundscapes.R.drawable.kristin_divona, org.eclipsesoundscapes.R.drawable.kelsey_perrett,
            org.eclipsesoundscapes.R.drawable.ic_default};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.eclipsesoundscapes.R.layout.activity_our_team);

        getSupportActionBar().setTitle(getString(org.eclipsesoundscapes.R.string.our_team));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        partnersRecyclerView = (RecyclerView) findViewById(org.eclipsesoundscapes.R.id.teams_recyclerview);
        teamList = getResources().getStringArray(org.eclipsesoundscapes.R.array.team);
        teamDescription = getResources().getStringArray(org.eclipsesoundscapes.R.array.team_bio);
        teamTitle = getResources().getStringArray(org.eclipsesoundscapes.R.array.team_title);

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
