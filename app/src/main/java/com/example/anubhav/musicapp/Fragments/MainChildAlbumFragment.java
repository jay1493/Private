package com.example.anubhav.musicapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.anubhav.musicapp.Adapters.AlbumsListAdapter;
import com.example.anubhav.musicapp.Constants;
import com.example.anubhav.musicapp.Interfaces.ItemClickListener;
import com.example.anubhav.musicapp.Model.AlbumModel;
import com.example.anubhav.musicapp.Model.MusicModel;
import com.example.anubhav.musicapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anubhav on 23/3/17.
 */

public class MainChildAlbumFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private List<AlbumModel> albumModelList;
    private AlbumsListAdapter albumsListAdapter;
    private Context activityContext;
    private static MainChildAlbumFragment fragContext = null;
    private static int position;
    private static int layout;
    private static MusicModel musicModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    public static MainChildAlbumFragment getInstance(Bundle bundle){
        if(bundle!=null){
            position = bundle.getInt(Constants.main_song_fragment_position);
            layout = bundle.getInt(Constants.main_song_fragment_layout);
            if(bundle.getSerializable(Constants.SEND_MUSIC_AS_EXTRA)!=null){
                musicModel = (MusicModel) bundle.getSerializable(Constants.SEND_MUSIC_AS_EXTRA);
            }else {
                musicModel = null;
            }
        }
        fragContext = new MainChildAlbumFragment();
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
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView_musicLibrary);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        if(musicModel !=null) {
            albumsListAdapter = new AlbumsListAdapter(getActivity(), musicModel.getAllAlbums(), new ItemClickListener() {
                @Override
                public void itemClick(View view, int position) {

                }
            });
            recyclerView.setAdapter(albumsListAdapter);
        }
        return view;
    }

}
