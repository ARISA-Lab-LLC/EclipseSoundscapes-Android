package org.eclipsesoundscapes.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see [http://www.gnu.org/licenses/].
  * */


/**
 * @author Joel Goncalves
 *
 * Adapter for linear recyclerview in OurTeamAcitivty and OurPartnersAcitivty
 * @see org.eclipsesoundscapes.activity.OurPartnersActivity
 * @see org.eclipsesoundscapes.activity.OurTeamActivity
 *
 */

public class PartnerTeamAdapter extends RecyclerView.Adapter<PartnerTeamAdapter.PartnerTeamViewHolder> {

    private String[] partnersTeams;
    private String[] descriptions;
    private String[] extra; // partner link or team member title
    private Integer[] images;
    private boolean isTeam;

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
