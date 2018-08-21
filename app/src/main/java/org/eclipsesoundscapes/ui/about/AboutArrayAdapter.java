package org.eclipsesoundscapes.ui.about;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.ui.main.MainActivity;
import org.eclipsesoundscapes.ui.rumblemap.RumbleMapInstructionsActivity;
import org.eclipsesoundscapes.ui.walkthrough.WalkthroughActivity;

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
 * Adapter for AboutFragment reyclerview, handles on click listener
 * @see AboutFragment
 *
 */

public class AboutArrayAdapter extends RecyclerView.Adapter<AboutArrayAdapter.CustomViewHolder> {

    private final String[] options;
    private final Integer[] optionImgs;
    private Context mContext;

    AboutArrayAdapter(Context context, String[] options, Integer[] optionImgs) {
        this.mContext = context;
        this.options = options;
        this.optionImgs = optionImgs;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(org.eclipsesoundscapes.R.layout.item_list_view, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, final int position) {
        holder.optionText.setText(options[position]);
        holder.optionIcon.setImageResource(optionImgs[position]);

        // handle on click listeners for both about us and more information list
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (position){
                    case 0:
                        // Rumble map instruction
                        showRumbleInstructions();
                        break;
                    case 1:
                        // Our team
                        launchActivity(new Intent(mContext, OurTeamActivity.class));
                        break;
                    case 2:
                        // Our partners
                        launchActivity(new Intent(mContext, OurPartnersActivity.class));
                        break;
                    case 3:
                        // Future eclipses supported
                        launchActivity(new Intent(mContext, FutureEclipsesActivity.class));
                        break;
                    case 4:
                        // launch walkthrough again as menu mode
                        Intent walkthroughIntent = new Intent(mContext, WalkthroughActivity.class);
                        walkthroughIntent.putExtra(WalkthroughActivity.EXTRA_MODE, WalkthroughActivity.MODE_MENU);
                        launchActivity(walkthroughIntent);
                        break;
                    case 5:
                        // feedback form
                        String url = mContext.getString(R.string.link_feedback_form);
                        Intent feedbackIntent = new Intent(Intent.ACTION_VIEW);
                        feedbackIntent.setData(Uri.parse(url));
                        launchActivity(feedbackIntent);
                        break;
                    case 6:
                        // settings
                        Intent settingsIntent = new Intent(mContext, SettingsActivity.class);
                        settingsIntent.putExtra(SettingsActivity.EXTRA_SETTINGS_MODE, SettingsActivity.MODE_SETTINGS);
                        launchActivity(settingsIntent);
                        break;
                    case 7:
                        // Legal details
                        Intent legalIntent = new Intent(mContext, SettingsActivity.class);
                        legalIntent.putExtra(SettingsActivity.EXTRA_SETTINGS_MODE, SettingsActivity.MODE_LEGAL);
                        launchActivity(legalIntent);
                        break;
                }
            }
        });
    }

    private void showRumbleInstructions(){
        if (mContext instanceof MainActivity) {
            if (!(((MainActivity) mContext).isDestroyed() || ((MainActivity) mContext).isFinishing())) {
                mContext.startActivity(new Intent(mContext, RumbleMapInstructionsActivity.class));
                ((MainActivity) mContext).overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        }
    }

    private void launchActivity(Intent intent){
        if (mContext instanceof MainActivity)
            if (!(((MainActivity) mContext).isDestroyed() || ((MainActivity) mContext).isFinishing())) {
                mContext.startActivity(intent);
                ((MainActivity) mContext).overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
    }

    @Override
    public int getItemCount() {
        return options.length;
    }


    static class CustomViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.option_text) TextView optionText;
        @BindView(R.id.list_avatar) ImageView optionIcon;

        CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
