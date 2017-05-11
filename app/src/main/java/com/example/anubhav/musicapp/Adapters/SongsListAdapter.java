package com.example.anubhav.musicapp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.anubhav.musicapp.CircleTransformer;
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
    private boolean showPlaylistLayout;
    private SongOptionsToAddInPlaylistListener songOptionsToAddInPlaylistListener;
    private SongOptionsDeleteFromPlaylistListener songOptionsDeleteFromPlaylistListener;
    private boolean fromAlbumLayout;

    public SongsListAdapter(Context context, List<SongsModel> songsModel, ItemClickListener itemClickListener,
                            boolean showPlaylistLayout, SongOptionsToAddInPlaylistListener songOptionsToAddInPlaylistListener,
                            SongOptionsDeleteFromPlaylistListener songOptionsDeleteFromPlaylistListener, boolean fromAlbumClickedLayout) {

        this.context = context;
        this.songsModel = songsModel;
        this.itemClickListener = itemClickListener;
        this.showPlaylistLayout = showPlaylistLayout;
        this.songOptionsToAddInPlaylistListener = songOptionsToAddInPlaylistListener;
        this.songOptionsDeleteFromPlaylistListener = songOptionsDeleteFromPlaylistListener;
        this.fromAlbumLayout = fromAlbumClickedLayout;
    }

    @Override
    public SongsListAdapter.CustomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SongsListAdapter.CustomHolder(LayoutInflater.from(context).inflate(R.layout.item_songs,null));

    }

    @Override
    public void onBindViewHolder(SongsListAdapter.CustomHolder holder, final int position) {
        if(fromAlbumLayout){
            holder.parentLayout.setBackgroundColor(context.getResources().getColor(R.color.background_drawable_3));
            holder.parentLayout.setAlpha(0.7f);
            holder.songTitle.setSingleLine(true);
            holder.songTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            holder.songTitle.setMarqueeRepeatLimit(-1);
            holder.songTitle.setSelected(true);
            holder.songTitle.setTypeface(holder.songTitle.getTypeface(), Typeface.BOLD);
            holder.songArtistTitle.setTypeface(holder.songArtistTitle.getTypeface(), Typeface.BOLD_ITALIC);
        }
        Bitmap bitmapFactory = BitmapFactory.decodeFile(songsModel.get(position).getSongAlbumCover());
        if(bitmapFactory!=null && bitmapFactory.getRowBytes()>0) {
            holder.songImage.setImageBitmap(getCircleBitmap(bitmapFactory));
        }else{
            holder.songImage.setImageDrawable(context.getResources().getDrawable(R.drawable.album_placeholder));
        }
        holder.songTitle.setText(songsModel.get(position).getSongTitle());
        holder.songArtistTitle.setText(songsModel.get(position).getSongArtist());
        if(showPlaylistLayout){
            //noinspection RestrictedApi
            if(holder.songOptions.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.library_add).getConstantState()){
                holder.songOptions.setImageDrawable(context.getResources().getDrawable(R.drawable.arrange_songs_in_playlist));
            }
            holder.songOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(songOptionsDeleteFromPlaylistListener!=null) {
                        songOptionsDeleteFromPlaylistListener.deleteFromPlaylist(songsModel.get(position), position);
                    }
                }
            });
        }else{
            holder.songOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(songOptionsToAddInPlaylistListener!=null) {
                        songOptionsToAddInPlaylistListener.addInPlaylist(songsModel.get(position), position);
                    }
                }
            });
        }

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
        private ImageView songOptions;
        private TextView songTitle;
        private TextView songArtistTitle;
        private RelativeLayout parentLayout;
        public CustomHolder(View itemView) {
            super(itemView);
            parentLayout = (RelativeLayout)itemView.findViewById(R.id.mainLayoutItemSongs);
            songImage = (ImageView)itemView.findViewById(R.id.songImage_list_of_songs);
            songOptions = (ImageView)itemView.findViewById(R.id.songOptions);
            songTitle = (TextView)itemView.findViewById(R.id.songName_list_of_songs);
            songArtistTitle = (TextView)itemView.findViewById(R.id.singerName_list_of_songs);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            itemClickListener.itemClick(view,getAdapterPosition());
        }
    }
    public interface SongOptionsToAddInPlaylistListener{
        void addInPlaylist(SongsModel songsModel,int pos);
    }
    public interface SongOptionsDeleteFromPlaylistListener{
        void deleteFromPlaylist(SongsModel songsModel,int pos);
    }
    private Bitmap getCircleBitmap(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        int x = (bitmap.getWidth() - size) / 2;
        int y = (bitmap.getHeight() - size) / 2;
        //Create Squared Bitmap
        Bitmap squared = Bitmap.createBitmap(bitmap, x, y, size, size);


        final Bitmap output = Bitmap.createBitmap(size,
               size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        //overlay this canvas with the image, which we made in squared shape.
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;
        //Now draw circle from canvas(which would lie inside rectangular area
        canvas.drawCircle(r, r, r, paint);
        return output;
    }
}
