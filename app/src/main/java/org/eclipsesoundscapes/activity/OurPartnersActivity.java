package org.eclipsesoundscapes.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.adapters.PartnerTeamAdapter;

public class OurPartnersActivity extends AppCompatActivity {

    private RecyclerView partnersRecyclerView;
    private PartnerTeamAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String[] partnerList;
    private String[] partnerDescription;
    private String[] partnerLink;
    private Integer[] partner_logo = {R.drawable.nasa, R.drawable.national_parkers_logo, R.drawable.smithsonian_logo,
                                        R.drawable.ncam_logo, R.drawable.eclipsemob_textlogo_large_reverse,
                                        R.drawable.logo_scifri, R.drawable.ngcp_theme_logo, R.drawable.nso_logo_200_a, R.drawable.byu};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_our_partners);

        getSupportActionBar().setTitle(getString(R.string.our_partners));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        partnersRecyclerView = (RecyclerView) findViewById(R.id.partners_recyclerview);
        partnerList = getResources().getStringArray(R.array.partners);
        partnerDescription = getResources().getStringArray(R.array.partner_description);
        partnerLink = getResources().getStringArray(R.array.partner_links);

        layoutManager = new LinearLayoutManager(this);
        adapter = new PartnerTeamAdapter(partnerList, partnerLink, partnerDescription, partner_logo, false);
        partnersRecyclerView.setLayoutManager(layoutManager);
        partnersRecyclerView.setNestedScrollingEnabled(false);
        partnersRecyclerView.setAdapter(adapter);

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
