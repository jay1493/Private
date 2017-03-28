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

import com.example.anubhav.musicapp.Interfaces.ItemClickListener;
import com.example.anubhav.musicapp.Model.SongsModel;
import com.example.anubhav.musicapp.R;

import java.util.List;

/**
 * Created by anubhav on 29/3/17.
 */

public class SongsListAdapter extends RecyclerView.Adapter<SongsListAdapter.CustomHolder> {
    private Context context;
    private List<SongsModel> songsModel;
    private ItemClickListener itemClickListener;

    public SongsListAdapter(Context context, List<SongsModel> songsModel, ItemClickListener itemClickListener) {
        this.context = context;
        this.songsModel = songsModel;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public SongsListAdapter.CustomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SongsListAdapter.CustomHolder(LayoutInflater.from(context).inflate(R.layout.item_songs,null));

    }

    @Override
    public void onBindViewHolder(SongsListAdapter.CustomHolder holder, int position) {
        Bitmap bitmapFactory = BitmapFactory.decodeFile(songsModel.get(position).getSongAlbumCover());
        if(bitmapFactory!=null && bitmapFactory.getRowBytes()>0) {
            holder.songImage.setImageBitmap(bitmapFactory);
        }else{
            holder.songImage.setImageDrawable(context.getResources().getDrawable(R.drawable.album_placeholder));
        }
        holder.songTitle.setText(songsModel.get(position).getSongTitle());
        holder.songArtistTitle.setText(songsModel.get(position).getSongArtist());

    }

    @Override
    public int getItemCount() {
        if(songsModel !=null && songsModel.size()>0)
            return songsModel.size();
        else
            return 0;
    }



    class CustomHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView songImage;
        private TextView songTitle;
        private TextView songArtistTitle;
        public CustomHolder(View itemView) {
            super(itemView);
            songImage = (ImageView)itemView.findViewById(R.id.songImage_list_of_songs);
            songTitle = (TextView)itemView.findViewById(R.id.songName_list_of_songs);
            songArtistTitle = (TextView)itemView.findViewById(R.id.singerName_list_of_songs);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            itemClickListener.itemClick(view,getAdapterPosition());
        }
    }
}
