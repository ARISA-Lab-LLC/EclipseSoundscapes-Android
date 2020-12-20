package org.eclipsesoundscapes.ui.features;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.ui.main.MainActivity;
import org.eclipsesoundscapes.ui.rumblemap.RumbleMapInteractionActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class EclipseFragment extends Fragment {

    private int currentImg = 1; // current eclipse img displaying
    private SparseIntArray images;

    @BindView(R.id.contact_point_img) ImageView imageView;
    @OnClick(R.id.contact_point_img) void launchRumbleMap(){
        if (getActivity() == null) return;

        Intent rumbleIntent = new Intent(getActivity(), RumbleMapInteractionActivity.class);
        rumbleIntent.putExtra(RumbleMapInteractionActivity.EXTRA_IMG, currentImg);
        getActivity().startActivity(rumbleIntent);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_rumble_map, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        images = new SparseIntArray();

        if (getActivity() != null && getActivity() instanceof MainActivity){
            if (!((MainActivity) getActivity()).isAfterFirstContact()){
                images.put(1, R.drawable.eclipse_bailys_beads);
                images.put(2, R.drawable.bailys_beads_close_up);
                images.put(3, R.drawable.eclipse_corona);
                images.put(4, R.drawable.eclipse_diamond_ring);
                images.put(5, R.drawable.helmet_streamers);
                images.put(6, R.drawable.helmet_streamer_closeup);
                images.put(7, R.drawable.eclipse_prominence);
                images.put(8, R.drawable.prominence_closeup);
                return;
            }

            images.put(1, R.drawable.eclipse_first_contact);
            images.put(2, R.drawable.eclipse_bailys_beads);
            images.put(3, R.drawable.bailys_beads_close_up);
            images.put(4, R.drawable.eclipse_corona);
            images.put(5, R.drawable.eclipse_diamond_ring);
            images.put(6, R.drawable.helmet_streamers);
            images.put(7, R.drawable.helmet_streamer_closeup);
            images.put(8, R.drawable.eclipse_prominence);
            images.put(9, R.drawable.prominence_closeup);

            if (((MainActivity) getActivity()).isAfterTotality())
                images.put(10, R.drawable.eclipse_totality);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            int currentPoint = ((MainActivity) getActivity()).getCurrentView();
            updateView(currentPoint);
        }
    }

    /**
     * Update current image view
     * @param contactPoint Corresponds to a particular eclipse image
     */
    public void updateView(int contactPoint){
        imageView.setImageResource(images.get(contactPoint));
        currentImg = images.get(contactPoint);
    }
}
