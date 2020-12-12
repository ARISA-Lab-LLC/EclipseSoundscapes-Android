package org.eclipsesoundscapes.ui.about;

import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.ui.custom.CircleTransform;

import butterknife.BindView;
import butterknife.ButterKnife;

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
 * @see OurPartnersActivity
 * @see OurTeamActivity
 *
 */

public class PartnerTeamAdapter extends RecyclerView.Adapter<PartnerTeamAdapter.PartnerTeamViewHolder> {

    private String[] partnersTeams;
    private String[] descriptions;
    private String[] extra; // partner link or team member title
    private TypedArray images;
    private boolean isTeam;

    PartnerTeamAdapter(String[] partnersTeams, String extra[], String[] descriptions,
                       TypedArray images, boolean isTeam) {
        this.partnersTeams = partnersTeams;
        this.extra = extra;
        this.descriptions = descriptions;
        this.images = images;
        this.isTeam = isTeam;
    }

    @NonNull
    @Override
    public PartnerTeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(org.eclipsesoundscapes.R.layout.item_partner, parent, false);
        return new PartnerTeamViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PartnerTeamViewHolder holder, int position) {
        holder.partner_name.setText(partnersTeams[position]);
        holder.extra_detail.setText(extra[position]);
        holder.partner_description.setText(descriptions[position]);

        if (isTeam) {
            final RequestOptions options = new RequestOptions()
                    .centerInside()
                    .transform(new CircleTransform(holder.itemView.getContext()));

            Glide.with(holder.itemView.getContext())
                    .load(images.getDrawable(position))
                    .apply(options)
                    .into(holder.partnerLogo);
        } else {
            holder.partnerLogo.setImageDrawable(images.getDrawable(position));
            Linkify.addLinks(holder.extra_detail, Linkify.ALL);
        }
    }


    @Override
    public int getItemCount() {
        return partnersTeams.length;
    }

    static class PartnerTeamViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.partner_logo) ImageView partnerLogo;
        @BindView(R.id.partner_name) TextView partner_name;
        @BindView(R.id.extra_detail) TextView extra_detail;
        @BindView(R.id.partner_description) TextView partner_description;

        PartnerTeamViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
