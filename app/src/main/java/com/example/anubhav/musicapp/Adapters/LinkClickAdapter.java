package com.example.anubhav.musicapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.anubhav.musicapp.Model.AudioLinksModel;
import com.example.anubhav.musicapp.Model.DownloadVidsModel;
import com.example.anubhav.musicapp.Model.VideoLinksModel;
import com.example.anubhav.musicapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anubhav on 22/3/17.
 */

public class LinkClickAdapter extends RecyclerView.Adapter<LinkClickAdapter.LinkHolder> {

    private void commentedCode(){
        /*

         */
    }

    private Context context;
    private int whichModelToChoose;
    private DownloadVidsModel downloadVidsModel;
    private LinkClickInterface linkClickInterface;
    private List<AudioLinksModel> audioLists = null;
    private List<VideoLinksModel> videoLists = null;


    public LinkClickAdapter(Context context, int whichModelToChoose, DownloadVidsModel downloadVidsModel, LinkClickInterface linkClickInterface) {
        this.context = context;
        this.whichModelToChoose = whichModelToChoose;
        this.downloadVidsModel = downloadVidsModel;
        this.linkClickInterface = linkClickInterface;
        if(whichModelToChoose == 0){
            audioLists = downloadVidsModel.getAudioLinks();
        }else if(whichModelToChoose == 1){
            videoLists = downloadVidsModel.getVideoLinks();
        }

    }

    @Override
    public LinkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LinkHolder(LayoutInflater.from(context).inflate(R.layout.item_generate_link,parent,false));
    }

    @Override
    public void onBindViewHolder(LinkHolder holder, int position) {
        if(audioLists!=null){
            holder.link.setText(audioLists.get(position).getExtension()+" "+audioLists.get(position).getQuality());
        }else if(videoLists!=null){
            holder.link.setText(videoLists.get(position).getExtension()+" "+videoLists.get(position).getQuality());
        }

    }

    @Override
    public int getItemCount() {
        if(audioLists != null)
        return audioLists.size();
        else if(videoLists != null)
            return videoLists.size();
        else
            return 0;
    }

    class LinkHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       private TextView link;
        public LinkHolder(View itemView) {
            super(itemView);
            link = (TextView) itemView.findViewById(R.id.link);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(audioLists!=null) {
                linkClickInterface.onLinkClick(view, getAdapterPosition(), audioLists, null);
            }else if(videoLists !=null){
                linkClickInterface.onLinkClick(view, getAdapterPosition(), null, videoLists);
            }
        }
    }
    public interface LinkClickInterface{
         void onLinkClick(View view,int pos,List<AudioLinksModel> audioLists,List<VideoLinksModel> videoLists);
    }
}
