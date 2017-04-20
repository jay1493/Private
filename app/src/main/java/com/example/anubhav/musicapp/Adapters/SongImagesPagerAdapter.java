package com.example.anubhav.musicapp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.anubhav.musicapp.Model.SongsModel;
import com.example.anubhav.musicapp.R;

import java.util.ArrayList;

/**
 * Created by anubhav on 18/4/17.
 */

public class SongImagesPagerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<SongsModel> copyPlaylistList;
    private SongsModel currentPlayingSong;

    public SongImagesPagerAdapter(Context context, ArrayList<SongsModel> copyPlaylistList, SongsModel currentPlayingSong) {
        this.context = context;
        this.copyPlaylistList = copyPlaylistList;
        this.currentPlayingSong = currentPlayingSong;
    }

    @Override
    public int getCount() {
        if(copyPlaylistList!=null && copyPlaylistList.size()>0) {
            return copyPlaylistList.size()+1;
        }
        else
            return 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.e("", "instantiateItem: "+position );
        Log.e("", "Count: "+getCount());
        View view = LayoutInflater.from(context).inflate(R.layout.view_pager_image_layout,null);
        ImageView songImage = (ImageView) view.findViewById(R.id.songAlbumImage_musicLayout);
        if(position == 0){
            if(currentPlayingSong!=null && currentPlayingSong.getSongAlbumCover()!=null){
                Bitmap bitmapFactory = BitmapFactory.decodeFile(currentPlayingSong.getSongAlbumCover());
                if(bitmapFactory!=null && bitmapFactory.getRowBytes()>0) {
                    songImage.setImageBitmap(bitmapFactory);
                }else{
                    songImage.setImageDrawable(context.getResources().getDrawable(R.drawable.index));
                }
            }else{
                songImage.setImageDrawable(context.getResources().getDrawable(R.drawable.index));
            }
        }else{
            if(copyPlaylistList!=null && position!=0 && copyPlaylistList.get(position-1).getSongAlbumCover()!=null){
                Bitmap bitmapFactory = BitmapFactory.decodeFile(copyPlaylistList.get(position-1).getSongAlbumCover());
                if(bitmapFactory!=null && bitmapFactory.getRowBytes()>0) {
                    songImage.setImageBitmap(bitmapFactory);
                }else{
                    songImage.setImageDrawable(context.getResources().getDrawable(R.drawable.index));
                }
            }else{
                songImage.setImageDrawable(context.getResources().getDrawable(R.drawable.index));
            }
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        container.removeView((LinearLayout) object);
    }
}
