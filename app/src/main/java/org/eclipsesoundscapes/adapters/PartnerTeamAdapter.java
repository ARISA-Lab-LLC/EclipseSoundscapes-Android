package org.eclipsesoundscapes.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter used for both partner and team listings
 */

public class PartnerTeamAdapter extends RecyclerView.Adapter<PartnerTeamAdapter.PartnerTeamViewHolder> {

    String[] partnersTeams;
    String[] descriptions;
    String[] extra; // partner link or team member title
    Integer[] images;
    public boolean isTeam;

    @Override
    public PartnerTeamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(org.eclipsesoundscapes.R.layout.item_partner, parent, false);

        return new PartnerTeamViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PartnerTeamViewHolder holder, int position) {
        holder.partner_name.setText(partnersTeams[position]);
        holder.extra_detail.setText(extra[position]);
        holder.partner_description.setText(descriptions[position]);
        holder.partnerLogo.setImageResource(images[position]);

        // hyperlink
        if (!isTeam)
            Linkify.addLinks(holder.extra_detail, Linkify.ALL);
    }


    @Override
    public int getItemCount() {
        return partnersTeams.length;
    }

    static class PartnerTeamViewHolder extends RecyclerView.ViewHolder {
        ImageView partnerLogo;
        TextView partner_name;
        TextView extra_detail;
        TextView partner_description;


        PartnerTeamViewHolder(View view) {
            super(view);
            partnerLogo = (ImageView) view.findViewById(org.eclipsesoundscapes.R.id.partner_logo);
            partner_name = (TextView) view.findViewById(org.eclipsesoundscapes.R.id.partner_name);
            partner_description = (TextView) view.findViewById(org.eclipsesoundscapes.R.id.partner_description);
            extra_detail = (TextView) view.findViewById(org.eclipsesoundscapes.R.id.extra_detail);

        }
    }

    public PartnerTeamAdapter(String[] partnersTeams, String extra[], String[] descriptions,  Integer[] images, boolean isTeam) {
        this.partnersTeams = partnersTeams;
        this.extra = extra;
        this.descriptions = descriptions;
        this.images = images;
        this.isTeam = isTeam;
    }
}
