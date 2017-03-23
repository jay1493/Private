package com.example.anubhav.musicapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.anubhav.musicapp.Constants;
import com.example.anubhav.musicapp.Interfaces.ItemClickListener;
import com.example.anubhav.musicapp.Model.AlbumModel;
import com.example.anubhav.musicapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anubhav on 23/3/17.
 */

public class MainChildSongsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private Context activityContext;
    private static MainChildSongsFragment fragContext = null;
    private static int position;
    private static int layout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    public static MainChildSongsFragment getInstance(Bundle bundle){
        if(bundle!=null){
            position = bundle.getInt(Constants.main_song_fragment_position);
            layout = bundle.getInt(Constants.main_song_fragment_layout);
        }
        fragContext = new MainChildSongsFragment();
        return fragContext;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        albumModelList = new ArrayList<>();
        initializeLoader();
        setRetainInstance(true);

    }
    public void initializeLoader(){
        /**
         * NOTE: Firstly save values obtained from cursor in SharedPrefs (List of models could be stored here)
         * and IF AND ONLY IF the a new song is added..only then hit cursor loader, as it is taking very much
         * time to load...Everytime...
         */
       /* getLoaderManager().initLoader(0,null,this);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layout,null);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        /**
         * We will be running a query for albums, and when user clicks an album, we will run another query
         * for Audio.Media(Songs Query) WHERE ALBUM_KEY = _ID of clicked album.
         */
//        return new CursorLoader(getActivity(), MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,null,null,null,null);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
        if(loader!=null) {

        }else{
            return;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        albumModelList = null;
//        musicAdapter.notifyDataSetChanged();

    }
}
