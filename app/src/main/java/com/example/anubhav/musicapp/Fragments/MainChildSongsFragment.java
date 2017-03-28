package com.example.anubhav.musicapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.anubhav.musicapp.Adapters.SongsListAdapter;
import com.example.anubhav.musicapp.Constants;
import com.example.anubhav.musicapp.Interfaces.ItemClickListener;
import com.example.anubhav.musicapp.Model.AlbumModel;
import com.example.anubhav.musicapp.Model.MusicModel;
import com.example.anubhav.musicapp.R;

import java.util.ArrayList;

/**
 * Created by anubhav on 23/3/17.
 */

public class MainChildSongsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private Context activityContext;
    private static MainChildSongsFragment fragContext = null;
    private static int position;
    private static int layout;
    private SongsListAdapter songsListAdapter;
    private static MusicModel musicModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    public static MainChildSongsFragment getInstance(Bundle bundle){
        if(bundle!=null){
            position = bundle.getInt(Constants.main_song_fragment_position);
            layout = bundle.getInt(Constants.main_song_fragment_layout);
            if(bundle.getSerializable(Constants.SEND_MUSIC_AS_EXTRA)!=null){
                musicModel = (MusicModel) bundle.getSerializable(Constants.SEND_MUSIC_AS_EXTRA);
            }else{
                musicModel = null;
            }
        }
        fragContext = new MainChildSongsFragment();
        return fragContext;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layout,null);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView_musicLibrarySongs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        if(musicModel !=null) {
            songsListAdapter = new SongsListAdapter(getActivity(), musicModel.getAllSongs(), new ItemClickListener() {
                @Override
                public void itemClick(View view, int position) {

                }
            });
            recyclerView.setAdapter(songsListAdapter);
        }
        return view;
    }
}
