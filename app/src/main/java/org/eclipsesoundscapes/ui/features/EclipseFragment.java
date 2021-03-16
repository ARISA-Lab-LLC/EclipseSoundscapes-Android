package org.eclipsesoundscapes.ui.features;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.ui.main.MainActivity;
import org.eclipsesoundscapes.ui.rumblemap.RumbleMapInteractionActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class EclipseFragment extends Fragment {

    private int currentImg;
    private TypedArray images;

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

        final boolean showAllFeatures = getResources().getBoolean(R.bool.show_all_content);
        if (showAllFeatures) {
            images = getResources().obtainTypedArray(R.array.totality_features_image);
            return;
        }

        images = getResources().obtainTypedArray(R.array.default_features_image);

        if (getActivity() != null && getActivity() instanceof MainActivity) {
            if (((MainActivity) getActivity()).isAfterTotality()) {
                images = getResources().obtainTypedArray(R.array.totality_features_image);
            } else {
                images = getResources().obtainTypedArray(R.array.first_contact_features_image);
            }
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
        imageView.setImageDrawable(images.getDrawable(contactPoint));
        currentImg = images.getResourceId(contactPoint, R.drawable.eclipse_first_contact);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (images != null) {
            images.recycle();
        }
    }
}
