package org.eclipsesoundscapes.fragments;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.eclipsesoundscapes.activity.MainActivity;

/**
 * Dialog to handle location permission and display enabled/disabled states accordingly
 */

public class PermissionDialogFragment  extends DialogFragment {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 47;
    Context mContext;
    SharedPreferences preference;
    private TextView permissionText;
    private TextView permissionHeader;

    public static PermissionDialogFragment newInstance() {
        return new PermissionDialogFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(org.eclipsesoundscapes.R.layout.dialog_location_permission, container, false);


        ImageButton exit = (ImageButton) v.findViewById(org.eclipsesoundscapes.R.id.close_button);
        permissionText = (TextView) v.findViewById(org.eclipsesoundscapes.R.id.details) ;
        permissionHeader = (TextView) v.findViewById(org.eclipsesoundscapes.R.id.header);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        Button permissionButton = (Button) v.findViewById(org.eclipsesoundscapes.R.id.permission_button);
        permissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLocationPermission();
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        preference = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        permissionHeader.announceForAccessibility(permissionHeader.getText().toString());
        permissionText.announceForAccessibility(permissionText.getText().toString());
    }

    private void requestLocationPermission() {

        final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, MainActivity.LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        ActivityCompat.requestPermissions(getActivity(), permissions, MainActivity.LOCATION_PERMISSION_REQUEST_CODE);

        /*
        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.permission_rationale_location)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, null)
                .show();
                */

    }


    public void permissionEnabled(){
        Button permissionButton = (Button) getDialog().findViewById(org.eclipsesoundscapes.R.id.permission_button);
        permissionButton.setBackground(ResourcesCompat.getDrawable(getResources(), org.eclipsesoundscapes.R.drawable.button_round_enabled, null));
        permissionButton.setCompoundDrawablesWithIntrinsicBounds(org.eclipsesoundscapes.R.drawable.ic_check_white, 0, 0, 0);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        int rc = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if (rc != PackageManager.PERMISSION_GRANTED) {
            SharedPreferences.Editor editor = preference.edit();
            editor.putBoolean("settings_location", false);
            editor.apply();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        int rc = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if (rc == PackageManager.PERMISSION_GRANTED) {

            SharedPreferences.Editor editor = preference.edit();
            editor.putBoolean("settings_location", true);
            editor.commit();

            Button permissionButton = (Button) getDialog().findViewById(org.eclipsesoundscapes.R.id.permission_button);
            permissionButton.setBackground(ResourcesCompat.getDrawable(getResources(), org.eclipsesoundscapes.R.drawable.button_round_enabled, null));
            permissionButton.setCompoundDrawablesWithIntrinsicBounds( org.eclipsesoundscapes.R.drawable.ic_check_white, 0, 0, 0);
        }
    }
}
