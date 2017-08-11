package org.eclipsesoundscapes.eclipsesoundscapes.adapters;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipsesoundscapes.eclipsesoundscapes.R;
import org.eclipsesoundscapes.eclipsesoundscapes.activity.FutureEclipsesActivity;
import org.eclipsesoundscapes.eclipsesoundscapes.activity.MainActivity;
import org.eclipsesoundscapes.eclipsesoundscapes.activity.OurPartnersActivity;
import org.eclipsesoundscapes.eclipsesoundscapes.activity.OurTeamActivity;
import org.eclipsesoundscapes.eclipsesoundscapes.activity.SettingsActivity;
import org.eclipsesoundscapes.eclipsesoundscapes.activity.WalkthroughActivity;
import org.eclipsesoundscapes.eclipsesoundscapes.fragments.RumbleMapInstructionsFragment;
import org.eclipsesoundscapes.eclipsesoundscapes.util.Constants;

/**
 * Created by horus on 7/19/17.
 */

public class AboutArrayAdapter extends RecyclerView.Adapter<AboutArrayAdapter.CustomViewHolder> {

    private final String[] options;
    private final Integer[] optionImgs;
    public Context mContext;

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_view, parent, false);

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
                        showRumbleInstructions();
                        break;
                    case 1:
                        mContext.startActivity(new Intent(mContext, OurTeamActivity.class));
                        break;
                    case 2:
                        mContext.startActivity(new Intent(mContext, OurPartnersActivity.class));
                        break;
                    case 3:
                        mContext.startActivity(new Intent(mContext, FutureEclipsesActivity.class));
                        break;
                    case 4:
                        Intent walkthroughIntent = new Intent(mContext, WalkthroughActivity.class);
                        walkthroughIntent.putExtra("mode", Constants.WALKTHROUGH_MODE_MENU);
                        mContext.startActivity(walkthroughIntent);
                        break;
                    case 5:
                        Intent settingsIntent = new Intent(mContext, SettingsActivity.class);
                        settingsIntent.putExtra("settings", "settings");
                        mContext.startActivity(settingsIntent);
                        break;
                    case 6:
                        Intent legalIntent = new Intent(mContext, SettingsActivity.class);
                        legalIntent.putExtra("settings", "legal");
                        mContext.startActivity(legalIntent);
                        break;
                }
            }
        });
    }

    public void showRumbleInstructions(){
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
            optionText = (TextView) view.findViewById(R.id.option_text);
            optionIcon = (ImageView) view.findViewById(R.id.list_avatar);

        }
    }


    public AboutArrayAdapter(Context context, String[] options, Integer[] optionImgs) {
        this.mContext = context;
        this.options = options;
        this.optionImgs = optionImgs;

    }

}
