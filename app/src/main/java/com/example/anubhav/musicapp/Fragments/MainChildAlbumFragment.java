package com.example.anubhav.musicapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.anubhav.musicapp.Adapters.AlbumsListAdapter;
import com.example.anubhav.musicapp.Adapters.SongsListAdapter;
import com.example.anubhav.musicapp.Constants;
import com.example.anubhav.musicapp.Interfaces.ItemClickListener;
import com.example.anubhav.musicapp.Model.AlbumModel;
import com.example.anubhav.musicapp.Model.MusicModel;
import com.example.anubhav.musicapp.Model.SongsModel;
import com.example.anubhav.musicapp.R;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.blurry.Blurry;

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
    private LinearLayout albumSongsLayout;
    private ImageView backtoAlbums;
    private TextView albumName;
    private RecyclerView albumSongsRecycler;
    private SongsListAdapter songsListAdapter;
    private View albumSongsView;
    private MainChildSongsFragment.SongClickListener songClickListener;
    private MainChildSongsFragment.SongAddToPlaylistListener songAddToPlaylistListener;
    private TextView albumArtistName;
    private ImageView overlayImage;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
        songClickListener = (MainChildSongsFragment.SongClickListener)context;
        songAddToPlaylistListener = (MainChildSongsFragment.SongAddToPlaylistListener)context;
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
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layout,null);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView_musicLibrary);
        albumSongsLayout = (LinearLayout) view.findViewById(R.id.albumSongsLayout);
        albumSongsLayout.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        if(musicModel !=null) {
            albumsListAdapter = new AlbumsListAdapter(getActivity(), musicModel.getAllAlbums(), new ItemClickListener() {
                @Override
                public void itemClick(View view, final int position) {
                    //Add a fragment on the layout, and make it visible
                    albumSongsView = inflater.inflate(R.layout.layout_album_songs,null);
                    backtoAlbums = (ImageView) albumSongsView.findViewById(R.id.backToAllAlbums);
                    backtoAlbums.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(albumSongsView!=null){
                                albumSongsLayout.removeAllViews();
                                recyclerView.setVisibility(View.VISIBLE);
                                albumSongsLayout.setVisibility(View.GONE);
                            }
                        }
                    });
                    albumName =(TextView) albumSongsView.findViewById(R.id.albumName);
                    albumArtistName =(TextView) albumSongsView.findViewById(R.id.albumArtistName);
                    albumName.setText(musicModel.getAllAlbums().get(position).getAlbumTitle());
                    albumArtistName.setText(musicModel.getAllAlbums().get(position).getArtistTitle());
                    albumSongsRecycler = (RecyclerView)albumSongsView.findViewById(R.id.albumSongsRecycler);
                    overlayImage = (ImageView)albumSongsView.findViewById(R.id.overlayImage);
                    String albumImage = musicModel.getAllAlbums().get(position).getAlbumCover();
                    Bitmap indexBitmap  = BitmapFactory.decodeResource(activityContext.getResources(),R.drawable.main3);
                    if(!TextUtils.isEmpty(albumImage)){
                        Bitmap bitmap = BitmapFactory.decodeFile(albumImage);
                        if(bitmap!=null && bitmap.getRowBytes() > 0){
                            Blurry.with(activityContext).radius(5).sampling(2).animate(500).from(bitmap).into(overlayImage);
                        }else{
                            Blurry.with(activityContext).radius(10).sampling(8).animate(500).from(indexBitmap).into(overlayImage);
                        }
                    }else{
                        Blurry.with(activityContext).radius(10).sampling(8).animate(500).from(indexBitmap).into(overlayImage);
                    }
                    albumSongsRecycler.setLayoutManager(new LinearLayoutManager(getParentFragment().getActivity(),LinearLayoutManager.VERTICAL,false));
                        songsListAdapter = new SongsListAdapter(getParentFragment().getActivity(), musicModel.getAllAlbums().get(position).getSongs(), new ItemClickListener() {
                            @Override
                            public void itemClick(View view, int pos) {
                    songClickListener.onSongClick(musicModel.getAllAlbums().get(position).getSongs().get(pos), true);
                            }
                        }, false, new SongsListAdapter.SongOptionsToAddInPlaylistListener() {
                            @Override
                            public void addInPlaylist(SongsModel songsModel, int pos) {
                    songAddToPlaylistListener.addInPlaylist(songsModel,pos);
                            }
                        },null,true);
                    albumSongsRecycler.setAdapter(songsListAdapter);
                    albumSongsLayout.addView(albumSongsView);
                    albumSongsLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);


                }
            });
            recyclerView.setAdapter(albumsListAdapter);
        }
        return view;
    }

}
