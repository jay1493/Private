package com.example.anubhav.musicapp.Adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.anubhav.musicapp.Interfaces.ItemClickListener;
import com.example.anubhav.musicapp.Model.AudioLinksModel;
import com.example.anubhav.musicapp.Model.DownloadVidsModel;
import com.example.anubhav.musicapp.Model.VideoLinksModel;
import com.example.anubhav.musicapp.Model.YouTubeSearchedVideos;
import com.example.anubhav.musicapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anubhav on 19/3/17.
 */

public class YoutubeSearchAdapter  extends RecyclerView.Adapter<YoutubeSearchAdapter.CustomHolder> {

    private void commentedCode(){
        /*
             private Context context;
    private List<YouTubeSearchedVideos> albumModel;
    private ItemClickListener itemClickListener;
    private DownloadVidsModel downloadVidsModel;
    public static ArrayList<clickedState> states;
    private LinkClickAdapter audioAdapter;
    private LinkClickAdapter videoAdapter;

    private enum clickedState{
        DEFAULT_LAYOUT,
        CLICKED_LAYOUT ;
    }
    private int layoutType;

    public void setLayout(clickedState type,int pos){
        states.set(pos,type);
    }

    @Override
    public int getItemViewType(int position) {

        if(states.get(position) == clickedState.CLICKED_LAYOUT){
            return 1;
        }
        return 0;
    }

    public YoutubeSearchAdapter(Context context, List<YouTubeSearchedVideos> albumModel,DownloadVidsModel model, ItemClickListener itemClickListener) {
        this.context = context;
        this.albumModel = albumModel;
        this.downloadVidsModel = model;
        this.itemClickListener = itemClickListener;
        states = new ArrayList<>();
        //By Default the Default_Layout
        for(int i=0;i<albumModel.size();i++){
            states.add(i,clickedState.DEFAULT_LAYOUT);
        }
    }

    @Override
    public YoutubeSearchAdapter.CustomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType){
            case 0:
                view = LayoutInflater.from(context).inflate(R.layout.row_grid_dashboard,null);
                break;
            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.row_grid_generate_links,null);
                break;
        }
        return new YoutubeSearchAdapter.CustomHolder(view,viewType);

    }

    @Override
    public void onBindViewHolder(YoutubeSearchAdapter.CustomHolder holder, int position) {
        if(holder.getItemViewType() == 1) {
            //Clicked Holder
            audioAdapter = new LinkClickAdapter(context, 0, downloadVidsModel, new LinkClickAdapter.LinkClickInterface() {
                @Override
                public void onLinkClick(View view, int pos, List<AudioLinksModel> audioLists, List<VideoLinksModel> videoLists) {

                }
            });
            holder.clickedViewAudioLinks.setAdapter(audioAdapter);
            videoAdapter = new LinkClickAdapter(context, 1, downloadVidsModel, new LinkClickAdapter.LinkClickInterface() {
                @Override
                public void onLinkClick(View view, int pos, List<AudioLinksModel> audioLists, List<VideoLinksModel> videoLists) {

                }
            });
            holder.clickedViewVideoLinks.setAdapter(videoAdapter);

        }else{
            Glide.with(context).load(albumModel.get(position).getIconUrl()).into(holder.albumImage);
            holder.albumTitle.setText(albumModel.get(position).getTitle());
        }

    }

    @Override
    public int getItemCount() {
        if(albumModel!=null && albumModel.size()>0)
            return albumModel.size();
        else
            return 0;
    }



    class CustomHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerView clickedViewVideoLinks;
        private ImageView albumImage;
        private TextView albumTitle;
        private TextView artistTitle;
        private RecyclerView clickedViewAudioLinks;
        public CustomHolder(View itemView,int viewType) {
            super(itemView);
            if(itemView != null){
                switch (viewType){
                    case 0:
                        albumImage = (ImageView)itemView.findViewById(R.id.albumImage);
                        albumTitle = (TextView)itemView.findViewById(R.id.albumName);
                        artistTitle = (TextView)itemView.findViewById(R.id.songName);
                        itemView.setOnClickListener(this);
                        break;
                    case 1:
                        clickedViewAudioLinks = (RecyclerView)itemView.findViewById(R.id.audioLinks);
                        clickedViewAudioLinks.setLayoutManager(new GridLayoutManager(context,4));
                        clickedViewVideoLinks = (RecyclerView)itemView.findViewById(R.id.videoLinks);
                        clickedViewVideoLinks.setLayoutManager(new GridLayoutManager(context,4));
                        break;
                }
            }



        }

        @Override
        public void onClick(View view) {
            itemClickListener.itemClick(view,getAdapterPosition());
        }
    }
         */
    }


    private Context context;
    private List<YouTubeSearchedVideos> albumModel;
    private ItemClickListener itemClickListener;
    private DownloadVidsModel downloadVidsModel;


    public YoutubeSearchAdapter(Context context, List<YouTubeSearchedVideos> albumModel,DownloadVidsModel model, ItemClickListener itemClickListener) {
        this.context = context;
        this.albumModel = albumModel;
        this.downloadVidsModel = model;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public YoutubeSearchAdapter.CustomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_grid_dashboard,null);
        return new YoutubeSearchAdapter.CustomHolder(view,viewType);

    }

    @Override
    public void onBindViewHolder(YoutubeSearchAdapter.CustomHolder holder, int position) {
            Glide.with(context).load(albumModel.get(position).getIconUrl()).into(holder.albumImage);
            holder.albumTitle.setText(albumModel.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        if(albumModel!=null && albumModel.size()>0)
            return albumModel.size();
        else
            return 0;
    }



    class CustomHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private ImageView albumImage;
        private TextView albumTitle;
        private TextView artistTitle;
        private LinearLayout mainGrid;
//        private GridView gridView;
        public CustomHolder(View itemView,int viewType) {
            super(itemView);
            albumImage = (ImageView)itemView.findViewById(R.id.albumImage);
            albumTitle = (TextView)itemView.findViewById(R.id.albumName);
            artistTitle = (TextView)itemView.findViewById(R.id.songName);
            mainGrid = (LinearLayout)itemView.findViewById(R.id.mainGridLayout);
//            gridView = (GridView)itemView.findViewById(R.id.gridLinks);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.itemClick(view,getAdapterPosition());
        }
    }
}

