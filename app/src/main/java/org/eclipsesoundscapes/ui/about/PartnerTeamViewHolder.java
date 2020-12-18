package org.eclipsesoundscapes.ui.about;

import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.model.Partner;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PartnerTeamViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.partner_logo) ImageView partnerLogo;

    @BindView(R.id.partner_name) TextView partner_name;

    @BindView(R.id.extra_detail) TextView extra_detail;

    @BindView(R.id.partner_description) TextView partner_description;

    PartnerTeamViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(final Partner partner) {
        partner_name.setText(partner.getTitle());
        extra_detail.setText(partner.getLink());
        partner_description.setText(partner.getDescription());
        partnerLogo.setImageDrawable(partner.getLogo());
        Linkify.addLinks(extra_detail, Linkify.ALL);
    }
}