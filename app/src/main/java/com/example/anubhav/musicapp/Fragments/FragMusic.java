package com.example.anubhav.musicapp.Fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.anubhav.musicapp.Adapters.MusicAdapter;
import com.example.anubhav.musicapp.Interfaces.ItemClickListener;
import com.example.anubhav.musicapp.Model.AlbumModel;
import com.example.anubhav.musicapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anubhav on 22/2/17.
 */

public class FragMusic extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private List<AlbumModel> albumModelList;
    private MusicAdapter musicAdapter;
    private final int Manifest_permission_READ_EXTERNAL_STORAGE = 1991;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        permissions(context);
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void permissions(Context context) {
        if(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},Manifest_permission_READ_EXTERNAL_STORAGE);
        }else{
           initializeLoader();
        }
        return;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case Manifest_permission_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    initializeLoader();
                } else {
                    // User refused to grant permission.
                    permissions(getActivity());
                }
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albumModelList = new ArrayList<>();
        setRetainInstance(true);

    }

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_frag_music,null);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView_musicLibrary);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        musicAdapter = new MusicAdapter(getActivity(), albumModelList, new ItemClickListener() {
            @Override
            public void itemClick(View view, int position) {

            }
        });
        recyclerView.setAdapter(musicAdapter);


       /* if(savedInstanceState!=null){
            if(savedInstanceState.get("ETValue")!=null){
                ((EditText)view.findViewById(R.id.save_instance)).setText(savedInstanceState.getString("ETValue"));
            }
        }*/
        return view;

    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
 /*       Bundle bundle = new Bundle();
        bundle.putString("ETValue",text);
        outState.putAll(bundle);*/
    }

    public void initializeLoader(){
        getLoaderManager().initLoader(0,null,this);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id){
            case 0:
                /**
                 * We will be running a query for albums, and when user clicks an album, we will run another query
                 * for Audio.Media(Songs Query) WHERE ALBUM_KEY = _ID of clicked album.
                 */
                return new CursorLoader(getActivity(),MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,null,null,null,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
         if(progressDialog!=null){
             progressDialog.dismiss();
         }
        if(data!=null && data.moveToFirst()){
              int albumId = data.getColumnIndex(MediaStore.Audio.Albums._ID);
              int artistTitle = data.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
              int albumTitle = data.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
              int albumCover = data.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            do{
                AlbumModel albumModel = new AlbumModel();

                albumModel.setAlbumId(String.valueOf(data.getLong(albumId)));
                albumModel.setAlbumTitle(data.getString(albumTitle));
                albumModel.setArtistTitle(data.getString(artistTitle));
                albumModel.setAlbumCover(data.getString(albumCover));

                albumModelList.add(albumModel);
            }while(data.moveToNext());
         musicAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        albumModelList = null;
        musicAdapter.notifyDataSetChanged();
    }
}