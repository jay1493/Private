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
import com.example.anubhav.musicapp.Model.AlbumModel;
import com.example.anubhav.musicapp.R;

import java.util.List;

/**
 * Created by anubhav on 1/3/17.
 */

public class AlbumsListAdapter extends RecyclerView.Adapter<AlbumsListAdapter.CustomHolder> {
    private Context context;
    private List<AlbumModel> albumModel;
    private ItemClickListener itemClickListener;

    public AlbumsListAdapter(Context context, List<AlbumModel> albumModel, ItemClickListener itemClickListener) {
        this.context = context;
        this.albumModel = albumModel;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public CustomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomHolder(LayoutInflater.from(context).inflate(R.layout.row_grid_dashboard,null));

    }

    @Override
    public void onBindViewHolder(CustomHolder holder, int position) {
        Bitmap bitmapFactory = BitmapFactory.decodeFile(albumModel.get(position).getAlbumCover());
        if(bitmapFactory!=null && bitmapFactory.getRowBytes()>0) {
            holder.albumImage.setImageBitmap(bitmapFactory);
        }else{
            holder.albumImage.setImageDrawable(context.getResources().getDrawable(R.drawable.album_placeholder));
        }
        holder.albumTitle.setText(albumModel.get(position).getAlbumTitle());
        holder.artistTitle.setText(albumModel.get(position).getArtistTitle());

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
