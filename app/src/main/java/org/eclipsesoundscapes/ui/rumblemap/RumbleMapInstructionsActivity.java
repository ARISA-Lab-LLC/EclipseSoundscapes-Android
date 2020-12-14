package org.eclipsesoundscapes.ui.rumblemap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import org.eclipsesoundscapes.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RumbleMapInstructionsActivity extends AppCompatActivity {

    @BindView(R.id.bottom_view_title) TextView title;
    @BindView(R.id.bottom_view_more) TextView instructions;
    @BindView(R.id.exit_button) ImageButton closeButton;

    @OnClick(R.id.exit_button) void onExit(){
        onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rumble_map_instructions);
        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }
}
