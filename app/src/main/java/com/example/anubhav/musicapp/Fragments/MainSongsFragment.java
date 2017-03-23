package com.example.anubhav.musicapp.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.anubhav.musicapp.Adapters.MainFragmentsAdapter;
import com.example.anubhav.musicapp.R;

/**
 * Created by anubhav on 23/3/17.
 */

public class MainSongsFragment extends android.support.v4.app.Fragment {

    private static MainSongsFragment fragContext = null;
    private ViewPager viewPager;
    private Context activityContext;
    private MainFragmentsAdapter mainFragmentsAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    public static MainSongsFragment getInstance(Bundle bundle){

        if(fragContext==null){
            fragContext = new MainSongsFragment();
        }
        return fragContext;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_main_fragment_songs,null);
        viewPager = (ViewPager) view.findViewById(R.id.viewPagerSongsFragment);
        mainFragmentsAdapter = new MainFragmentsAdapter(getChildFragmentManager(),2,getActivity());
        viewPager.setAdapter(mainFragmentsAdapter);
        return view;
    }
}
