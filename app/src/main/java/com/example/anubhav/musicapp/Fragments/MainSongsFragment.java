package com.example.anubhav.musicapp.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.anubhav.musicapp.Adapters.MainFragmentsAdapter;
import com.example.anubhav.musicapp.Constants;
import com.example.anubhav.musicapp.Model.AlbumModel;
import com.example.anubhav.musicapp.Model.MusicModel;
import com.example.anubhav.musicapp.R;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by anubhav on 23/3/17.
 */

public class MainSongsFragment extends android.support.v4.app.Fragment {

    private static MainSongsFragment fragContext = null;
    private static ViewPager viewPager;
    private static Context activityContext;
    private static MainFragmentsAdapter mainFragmentsAdapter;
    private static MusicModel musicModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    public static MainSongsFragment getInstance(Bundle bundle){
        if(bundle!=null && bundle.getSerializable(Constants.SEND_MUSIC_AS_EXTRA)!=null){
            musicModel = (MusicModel) bundle.getSerializable(Constants.SEND_MUSIC_AS_EXTRA);
        }else{
            musicModel = null;
        }
        /** Due to the below code, whenever we try to recreate our MainSongsFragment, we are not getting the
         * fragment or the fragment was not visible, due to the fact that due to below code, we are getting
         * the old context of this fragment and that we are trying to replace/add in fragmentManager in
         * DashboardActivity.
         *
         * /* if(fragContext==null){
            fragContext = new MainSongsFragment();
         *   }
         *  /
         */

        return new MainSongsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*SharedPreferences modelPrefs= activityContext.getSharedPreferences(Constants.SHARED_PREFS_NAME,Context.MODE_PRIVATE);
        if(modelPrefs.getString(Constants.SHARED_PREFS_SAVED_MODEL,null)!=null){
            try {
                musicModel = new Gson().fromJson(modelPrefs.getString(Constants.SHARED_PREFS_SAVED_MODEL, null), MusicModel.class);
            }catch (Exception e){
                e.printStackTrace();
            }
        }*/
//        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_main_fragment_songs,null);
        viewPager = (ViewPager) view.findViewById(R.id.viewPagerSongsFragment);
        PagerTabStrip mPagerTabStrip = (PagerTabStrip) view.findViewById(R.id.pagerTabSongsFragment);
        mPagerTabStrip.setTabIndicatorColor(activityContext.getResources().getColor(R.color.off_white));
        for (int i = 0; i < mPagerTabStrip.getChildCount(); ++i) {
            View nextChild = mPagerTabStrip.getChildAt(i);
            if (nextChild instanceof TextView) {
                TextView textViewToConvert = (TextView) nextChild;
                textViewToConvert.setTextColor(activityContext.getResources().getColor(R.color.off_white));
            }
        }
        mainFragmentsAdapter = new MainFragmentsAdapter(getChildFragmentManager(),2,getActivity(),musicModel);
        viewPager.setAdapter(mainFragmentsAdapter);
        return view;
    }
    public static void setCurrentViewPagerItem(String item,int scrollPosition){
        if(item.equalsIgnoreCase(activityContext.getResources().getString(R.string.Songs))){
            if(viewPager!=null){
                viewPager.setCurrentItem(0,true);
                MainChildSongsFragment.scrollRecyclerView(scrollPosition);
            }
        }else if(item.equalsIgnoreCase(activityContext.getResources().getString(R.string.Albums))){
            if(viewPager!=null){
                viewPager.setCurrentItem(1,true);
            }
        }
    }
    public static void notifyAdapterFromActivity(final MusicModel musicModelFromActivity){
        if(mainFragmentsAdapter!=null) {
            Handler handler = new Handler(activityContext.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    musicModel = musicModelFromActivity;
                    mainFragmentsAdapter.notifyDataSetChanged();
                }
            });

        }else{
            return;
        }
    }
}
