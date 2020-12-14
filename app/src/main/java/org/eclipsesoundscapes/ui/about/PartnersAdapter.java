package org.eclipsesoundscapes.ui.about;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.model.Partner;
import org.eclipsesoundscapes.model.Section;

import java.util.ArrayList;

/**
 * @author Joel Goncalves
 *
 * Adapter that populates a list of current and past partners
 *
 */
public class PartnersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int ITEM_VIEW_TYPE_SECTION = 0;

    private final static int ITEM_VIEW_TYPE_ITEM = 1;

    private ArrayList<Object> data = new ArrayList<>();

    PartnersAdapter(final Context context,
                    @NonNull final ArrayList<Partner> currentPartners,
                    @NonNull final ArrayList<Partner> previousPartners) {
        this.data.add(new Section(context.getString(R.string.current_partners)));
        this.data.addAll(currentPartners);

        this.data.add(new Section(context.getString(R.string.past_partners)));
        this.data.addAll(previousPartners);
    }

    @Override
    public int getItemViewType(int position) {
        final Object item = data.get(position);

        if (item instanceof Section) {
            return ITEM_VIEW_TYPE_SECTION;
        } else if (item instanceof Partner) {
            return ITEM_VIEW_TYPE_ITEM;
        }

        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_SECTION) {
            final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_section, viewGroup, false);
            return new SectionViewHolder(itemView);
        }

        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_partner, viewGroup, false);
        return new PartnerTeamViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final Object item = data.get(i);

        if (viewHolder instanceof SectionViewHolder && item instanceof Section) {
            ((SectionViewHolder) viewHolder).bind(((Section) item).getTitle());
        } else if (viewHolder instanceof PartnerTeamViewHolder && item instanceof Partner) {
            ((PartnerTeamViewHolder) viewHolder).bind((Partner) item);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
