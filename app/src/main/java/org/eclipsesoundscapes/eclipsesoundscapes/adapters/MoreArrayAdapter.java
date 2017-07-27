package org.eclipsesoundscapes.eclipsesoundscapes.adapters;

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
import org.eclipsesoundscapes.eclipsesoundscapes.activity.OurPartnersActivity;
import org.eclipsesoundscapes.eclipsesoundscapes.activity.OurTeamActivity;
import org.eclipsesoundscapes.eclipsesoundscapes.activity.SettingsActivity;

/**
 * Created by horus on 7/19/17.
 */

public class MoreArrayAdapter extends RecyclerView.Adapter<MoreArrayAdapter.CustomViewHolder> {

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
                switch (options[position]){
                    case "Our team":
                        mContext.startActivity(new Intent(mContext, OurTeamActivity.class));
                        break;
                    case "Our partners":
                        mContext.startActivity(new Intent(mContext, OurPartnersActivity.class));
                        break;
                    case "How to use this app":
                        Toast.makeText(mContext, "Instructions", Toast.LENGTH_SHORT).show();
                        break;
                    case "Settings":
                        Intent settingsIntent = new Intent(mContext, SettingsActivity.class);
                        settingsIntent.putExtra("settings", "settings");
                        mContext.startActivity(settingsIntent);
                        break;
                    case "Legal":
                        Intent legalIntent = new Intent(mContext, SettingsActivity.class);
                        legalIntent.putExtra("settings", "legal");
                        mContext.startActivity(legalIntent);
                        break;
                }
            }
        });
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


    public MoreArrayAdapter(Context context, String[] options, Integer[] optionImgs) {
        this.mContext = context;
        this.options = options;
        this.optionImgs = optionImgs;

    }

}
