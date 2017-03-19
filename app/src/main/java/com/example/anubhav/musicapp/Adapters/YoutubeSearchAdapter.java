package com.example.anubhav.musicapp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.anubhav.musicapp.Interfaces.ItemClickListener;
import com.example.anubhav.musicapp.Model.AlbumModel;
import com.example.anubhav.musicapp.Model.YouTubeSearchedVideos;
import com.example.anubhav.musicapp.R;

import java.util.List;

/**
 * Created by anubhav on 19/3/17.
 */

public class YoutubeSearchAdapter  extends RecyclerView.Adapter<YoutubeSearchAdapter.CustomHolder> {
    private Context context;
    private List<YouTubeSearchedVideos> albumModel;
    private ItemClickListener itemClickListener;

    public YoutubeSearchAdapter(Context context, List<YouTubeSearchedVideos> albumModel, ItemClickListener itemClickListener) {
        this.context = context;
        this.albumModel = albumModel;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public YoutubeSearchAdapter.CustomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new YoutubeSearchAdapter.CustomHolder(LayoutInflater.from(context).inflate(R.layout.row_grid_dashboard,null));

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
        public CustomHolder(View itemView) {
            super(itemView);
            albumImage = (ImageView)itemView.findViewById(R.id.albumImage);
            albumTitle = (TextView)itemView.findViewById(R.id.albumName);
            artistTitle = (TextView)itemView.findViewById(R.id.songName);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            itemClickListener.itemClick(view,getAdapterPosition());
        }
    }
}

