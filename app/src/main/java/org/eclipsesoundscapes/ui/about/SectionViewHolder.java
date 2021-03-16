package org.eclipsesoundscapes.ui.about;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.eclipsesoundscapes.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SectionViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.title) TextView title;

    SectionViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(final String title) {
        this.title.setText(title);
    }
}
