package org.eclipsesoundscapes.adapters;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.eclipsesoundscapes.activity.FutureEclipsesActivity;
import org.eclipsesoundscapes.activity.OurPartnersActivity;
import org.eclipsesoundscapes.activity.OurTeamActivity;
import org.eclipsesoundscapes.activity.SettingsActivity;
import org.eclipsesoundscapes.activity.WalkthroughActivity;
import org.eclipsesoundscapes.fragments.RumbleMapInstructionsFragment;
import org.eclipsesoundscapes.util.Constants;
import org.eclipsesoundscapes.activity.MainActivity;

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
 * @see org.eclipsesoundscapes.fragments.AboutFragment
 *
 */

public class AboutArrayAdapter extends RecyclerView.Adapter<AboutArrayAdapter.CustomViewHolder> {

    private final String[] options;
    private final Integer[] optionImgs;
    public Context mContext;

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(org.eclipsesoundscapes.R.layout.item_list_view, parent, false);

        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {
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
                        mContext.startActivity(new Intent(mContext, OurTeamActivity.class));
                        break;
                    case 2:
                        // Our partners
                        mContext.startActivity(new Intent(mContext, OurPartnersActivity.class));
                        break;
                    case 3:
                        // Future eclipses supported
                        mContext.startActivity(new Intent(mContext, FutureEclipsesActivity.class));
                        break;
                    case 4:
                        // launch walkthrough again as menu mode
                        Intent walkthroughIntent = new Intent(mContext, WalkthroughActivity.class);
                        walkthroughIntent.putExtra("mode", Constants.WALKTHROUGH_MODE_MENU);
                        mContext.startActivity(walkthroughIntent);
                        break;
                    case 5:
                        // settings
                        Intent settingsIntent = new Intent(mContext, SettingsActivity.class);
                        settingsIntent.putExtra("settings", "settings");
                        mContext.startActivity(settingsIntent);
                        break;
                    case 6:
                        // Legal details
                        Intent legalIntent = new Intent(mContext, SettingsActivity.class);
                        legalIntent.putExtra("settings", "legal");
                        mContext.startActivity(legalIntent);
                        break;
                }
            }
        });
    }

    private void showRumbleInstructions(){
        // Create the fragment and show it as a dialog.
        DialogFragment newFragment = new RumbleMapInstructionsFragment();
        newFragment.show(((MainActivity)mContext).getFragmentManager(), "dialog");
    }

    @Override
    public int getItemCount() {
        return options.length;
    }


    static class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView optionText;
        ImageView optionIcon;

        CustomViewHolder(View view) {
            super(view);
            optionText = (TextView) view.findViewById(org.eclipsesoundscapes.R.id.option_text);
            optionIcon = (ImageView) view.findViewById(org.eclipsesoundscapes.R.id.list_avatar);

        }
    }

    public AboutArrayAdapter(Context context, String[] options, Integer[] optionImgs) {
        this.mContext = context;
        this.options = options;
        this.optionImgs = optionImgs;

    }

}
