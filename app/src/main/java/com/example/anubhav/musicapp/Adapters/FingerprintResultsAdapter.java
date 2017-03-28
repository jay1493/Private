package com.example.anubhav.musicapp.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.anubhav.musicapp.Model.AudioFingerPrintingResultModel;
import com.example.anubhav.musicapp.R;

/**
 * Created by anubhav on 28/3/17.
 */

public class FingerprintResultsAdapter extends PagerAdapter implements View.OnClickListener {
    private Context context;
    private AudioFingerPrintingResultModel audioFingerPrintingResultModel;
    private FingerprintResultsClickListener fingerprintResultsClickListener;

    public FingerprintResultsAdapter(Context context, AudioFingerPrintingResultModel audioFingerPrintingResultModel, FingerprintResultsClickListener fingerprintResultsClickListener) {
        this.context = context;
        this.audioFingerPrintingResultModel = audioFingerPrintingResultModel;
        this.fingerprintResultsClickListener = fingerprintResultsClickListener;
    }

    @Override
    public int getCount() {
        if(audioFingerPrintingResultModel!=null && audioFingerPrintingResultModel.getMusicList()!=null) {
            return audioFingerPrintingResultModel.getMusicList().size();
        }else{
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {

        return view==o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.fingerprint_results_flipper_item,null);
        TextView songTitle = (TextView) view.findViewById(R.id.textFlipperItem);
        if(audioFingerPrintingResultModel!=null && audioFingerPrintingResultModel.getMusicList()!=null) {
            songTitle.setText(audioFingerPrintingResultModel.getMusicList().get(position).getSongTitle() + "\n\n" +
                    audioFingerPrintingResultModel.getMusicList().get(position).getArtistModel().get(position).getArtistName());
        }else{
            songTitle.setText("No Records Found" + "\n\n" +
                    "Unknown");
        }
        view.setOnClickListener(this);
        container.addView(view);
        return view;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return audioFingerPrintingResultModel.getMusicList().get(position).getSongTitle();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        container.removeView((LinearLayout)object);
    }

    @Override
    public void onClick(View v) {
       fingerprintResultsClickListener.onPagerClick(v,getItemPosition(v));
    }
    public interface FingerprintResultsClickListener{
        void onPagerClick(View view,int pos);
    }

}
