package org.eclipsesoundscapes.eclipsesoundscapes.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eclipsesoundscapes.eclipsesoundscapes.R;
import org.eclipsesoundscapes.eclipsesoundscapes.adapters.MoreArrayAdapter;


public class MoreFragment extends Fragment {

    private Context mContext;
    private RecyclerView listViewAbout;
    private RecyclerView listViewMore;
    private String[] aboutOptions;
    private String[] moreOptions;
    private Integer[] aboutImgs = {R.drawable.ic_team, R.drawable.ic_partners};
    private Integer[] moreImgs = {R.drawable.ic_instructions,
                                    R.drawable.ic_settings, R.drawable.ic_legal};

    public MoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // about us list and info list
        aboutOptions = getResources().getStringArray(R.array.aboutOptions);
        moreOptions = getResources().getStringArray(R.array.moreOptions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_more, container, false);

        listViewAbout = (RecyclerView) root.findViewById(R.id.about_list_view);
        listViewMore = (RecyclerView) root.findViewById(R.id.more_list_view);

        listViewAbout.setLayoutManager(new LinearLayoutManager(mContext));
        listViewMore.setLayoutManager(new LinearLayoutManager(mContext));

        // adapters
        MoreArrayAdapter aboutAdapter = new MoreArrayAdapter(mContext, aboutOptions, aboutImgs );
        listViewAbout.setAdapter(aboutAdapter);

        MoreArrayAdapter moreAdapter = new MoreArrayAdapter(mContext, moreOptions, moreImgs);
        listViewMore.setAdapter(moreAdapter);

        return root;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
