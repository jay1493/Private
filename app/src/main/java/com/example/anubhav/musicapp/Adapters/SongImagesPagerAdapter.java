package com.example.anubhav.musicapp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
    private ArrayList<SongsModel> prevPlaylistList;
    private ArrayList<SongsModel> nextPlaylistList;
    private SongsModel currentPlayingSong;
    private ViewPager viewPager;
    private ArrayList<SongsModel> musicList;
    private ArrayList<SongsModel> copyMusicList;
    private int currentSongPosInPrevList = -1;

    public SongImagesPagerAdapter(Context context, ArrayList<SongsModel> copyPlaylistList, SongsModel currentPlayingSong,ViewPager currentPager) {
        this.context = context;
        this.copyPlaylistList = copyPlaylistList;
        this.currentPlayingSong = currentPlayingSong;
        this.viewPager = currentPager;
//        getViewPagerContentPosition();
        compileList(null);
        copyMusicList = new ArrayList<>();
        for(SongsModel song: musicList){
            copyMusicList.add(song);
        }
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
        View view = LayoutInflater.from(context).inflate(R.layout.view_pager_image_layout,null);
        ImageView songImage = (ImageView) view.findViewById(R.id.songAlbumImage_musicLayout);

      /*
       //Todo OLD CODE
        Log.e("", "instantiateItem: "+position );
        Log.e("", "Count: "+getCount());
        if(copyPlaylistList.size()>0){
            if(copyPlaylistList.size()==1){
                if(position<=prevPlaylistList.size()-1){
                    Bitmap bitmapFactory = BitmapFactory.decodeFile(prevPlaylistList.get(position).getSongAlbumCover());
                    if(bitmapFactory!=null && bitmapFactory.getRowBytes()>0) {
                        songImage.setImageBitmap(bitmapFactory);
                    }else{
                        songImage.setImageDrawable(context.getResources().getDrawable(R.drawable.index));
                    }
                }else{
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
                    viewPager.setCurrentItem(position,true);
                }
            }else{
                if(position == copyPlaylistList.size()/2){
                    //place current playing song here
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
                    viewPager.setCurrentItem(position,true);
                }else{
                    if(position<=prevPlaylistList.size()-1){
                        Bitmap bitmapFactory = BitmapFactory.decodeFile(prevPlaylistList.get(position).getSongAlbumCover());
                        if(bitmapFactory!=null && bitmapFactory.getRowBytes()>0) {
                            songImage.setImageBitmap(bitmapFactory);
                        }else{
                            songImage.setImageDrawable(context.getResources().getDrawable(R.drawable.index));
                        }
                    }else{
                        if(nextPlaylistList.size()-(position-1)>=0) {
                            Bitmap bitmapFactory = BitmapFactory.decodeFile(nextPlaylistList.get(nextPlaylistList.size()-(position-1)).getSongAlbumCover());
                            if (bitmapFactory != null && bitmapFactory.getRowBytes() > 0) {
                                songImage.setImageBitmap(bitmapFactory);
                            } else {
                                songImage.setImageDrawable(context.getResources().getDrawable(R.drawable.index));
                            }
                        }else{
                            songImage.setImageDrawable(context.getResources().getDrawable(R.drawable.index));
                        }
                    }
                }
            }
        }else {
            if (currentPlayingSong != null && currentPlayingSong.getSongAlbumCover() != null) {
                Bitmap bitmapFactory = BitmapFactory.decodeFile(currentPlayingSong.getSongAlbumCover());
                if (bitmapFactory != null && bitmapFactory.getRowBytes() > 0) {
                    songImage.setImageBitmap(bitmapFactory);
                } else {
                    songImage.setImageDrawable(context.getResources().getDrawable(R.drawable.index));
                }
            }else{
                songImage.setImageDrawable(context.getResources().getDrawable(R.drawable.index));
            }
            viewPager.setCurrentItem(position,true);
        }*/
      if(musicList!=null && musicList.size()>0 && musicList.get(position)!=null){
          if(musicList.get(position).getSongAlbumCover()!=null){
              Bitmap bitmapFactory = BitmapFactory.decodeFile(musicList.get(position).getSongAlbumCover());
              if(bitmapFactory!=null && bitmapFactory.getRowBytes()>0) {
                  songImage.setImageBitmap(bitmapFactory);
              }else{
                  songImage.setImageDrawable(context.getResources().getDrawable(R.drawable.index));
              }
          }else{
              songImage.setImageDrawable(context.getResources().getDrawable(R.drawable.index));
          }
      }


     /*   if(position == 0){
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
        }*/
//        if(currentSongPosInPrevList!=-1){
//            viewPager.setCurrentItem(currentSongPosInPrevList,true);
//        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((LinearLayout) object);
    }
    public void getViewPagerContentPosition(){
     /*   //Todo: Replace the list..i.e; prev with next list
        prevPlaylistList = new ArrayList<>();
        nextPlaylistList = new ArrayList<>();
        if(copyPlaylistList!=null && copyPlaylistList.size()>0){
            if(copyPlaylistList.size()==1){
                prevPlaylistList.add(copyPlaylistList.get(0));
                return;
            }else{
                for(int i=0;i<copyPlaylistList.size()/2;i++){
                    prevPlaylistList.add(copyPlaylistList.get(i));
                }
                for(int j=copyPlaylistList.size()-1;j>=copyPlaylistList.size()/2;j--){
                    nextPlaylistList.add(copyPlaylistList.get(j));
                }
                return;
            }
        }
        return;*/
    }
    public void compileList(Integer pos){
        musicList = new ArrayList<>();
        if(currentPlayingSong!=null){
            if(pos!=null){
                for(SongsModel songsModel: copyPlaylistList){
                    musicList.add(songsModel);
                }
                if(musicList.get(pos)!=null){
                        Integer copyPos = new Integer(pos);
                        int noOfModelsToAdd = musicList.size()-pos;
                        for(int i=0;i<noOfModelsToAdd;i++){
                            copyPos +=1;
                            musicList.add(copyPos,musicList.get(copyPos-1));
                        }
                        musicList.add(pos,currentPlayingSong);
                }
            }else {
                if(currentSongPosInPrevList == -1) {
                    musicList.add(currentPlayingSong);
                    for (SongsModel songsModel : copyPlaylistList) {
                        musicList.add(songsModel);
                    }
                }else{
                    for(SongsModel songsModel: copyPlaylistList){
                        musicList.add(songsModel);
                    }
                    if(musicList.get(currentSongPosInPrevList)!=null){
                        Integer copyPos = new Integer(currentSongPosInPrevList);
                        int noOfModelsToAdd = musicList.size()-currentSongPosInPrevList;
                        for(int i=0;i<noOfModelsToAdd;i++){
                            copyPos +=1;
                            musicList.add(copyPos,musicList.get(copyPos-1));
                        }
                        musicList.add(currentSongPosInPrevList,currentPlayingSong);
                    }
                }
                currentSongPosInPrevList = musicList.indexOf(currentPlayingSong);
            }
        }
    }

    @Override
    public void notifyDataSetChanged() {
        compileList(null);
        super.notifyDataSetChanged();

    }

    public void setPreviousSongPosition(int i) {
         currentSongPosInPrevList  = i;
        compileList(new Integer(i));
    }
}
