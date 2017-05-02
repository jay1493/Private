package com.example.anubhav.musicapp.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.anubhav.musicapp.Constants;
import com.example.anubhav.musicapp.Fragments.MainChildAlbumFragment;
import com.example.anubhav.musicapp.Fragments.MainChildSongsFragment;
import com.example.anubhav.musicapp.Model.AlbumModel;
import com.example.anubhav.musicapp.Model.MusicModel;
import com.example.anubhav.musicapp.R;

import java.util.ArrayList;

/**
 * Created by anubhav on 23/3/17.
 */
//Todo: Changed due to arraylist.size crash...WTF !!
public class MainFragmentsAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;
    private int noOfFrags;
    private Context context;
    private MusicModel musicModel;

    public MainFragmentsAdapter(FragmentManager fm, int noOfFrags, Context context, MusicModel model) {
        super(fm);
        this.fragmentManager = fm;
        this.noOfFrags = noOfFrags;
        this.context = context;
        this.musicModel = model;
    }

    @Override
    public Fragment getItem(int i) {
        Bundle bundle = new Bundle();
        Fragment fragment = null;
        switch (i){
            case 0:
                //Songs
                int layout = R.layout.layout_frag_music_songs;
                bundle.putInt(Constants.main_song_fragment_layout,layout);
                bundle.putInt(Constants.main_song_fragment_position,i);
                if(musicModel !=null){
                    bundle.putSerializable(Constants.SEND_MUSIC_AS_EXTRA, musicModel);
                }
                fragment = MainChildSongsFragment.getInstance(bundle);
                break;
            case 1:
                //Albums
                int layoutSong = R.layout.layout_frag_music;
                bundle.putInt(Constants.main_song_fragment_layout,layoutSong);
                bundle.putInt(Constants.main_song_fragment_position,i);
                if(musicModel !=null){
                    bundle.putSerializable(Constants.SEND_MUSIC_AS_EXTRA, musicModel);
                }
                fragment = MainChildAlbumFragment.getInstance(bundle);
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return noOfFrags;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title ="";
        if(position==0){
            title = context.getResources().getString(R.string.Songs);
        }else if(position==1){
            title = context.getResources().getString(R.string.Albums);
        }
        return title;
    }
}
