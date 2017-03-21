package com.example.anubhav.musicapp.GNSDKComp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anubhav.musicapp.DashboardActivity;
import com.example.anubhav.musicapp.R;
import com.gracenote.gnsdk.GnAlbum;
import com.gracenote.gnsdk.GnAlbumIterator;
import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnImageSize;
import com.gracenote.gnsdk.GnResponseAlbums;
import com.gracenote.gnsdk.GnUser;

import java.util.ArrayList;

/**
 * Created by anubhav on 21/3/17.
 */

public class UpdateResultsRunnable implements Runnable {

    private final ArrayList<mOnClickListener> metadataRow_OnClickListeners;
    GnResponseAlbums albumsResult;
    private LinearLayout parentLayout;
    private Context context;
    private GnUser user;

    public UpdateResultsRunnable(GnResponseAlbums albumsResult, LinearLayout parent, Context context,GnUser gnUser) {
        this.albumsResult = albumsResult;
        this.parentLayout = parent;
        this.context = context;
        this.user = gnUser;
        metadataRow_OnClickListeners = new ArrayList<mOnClickListener>();
    }

    @Override
    public void run() {
        try {
            if (albumsResult.resultCount() == 0) {

                Toast.makeText(context, "Sorry!! No Match Found!", Toast.LENGTH_SHORT).show();

            } else {

                GnAlbumIterator iter = albumsResult.albums().getIterator();
                parentLayout.removeAllViews();
                while (iter.hasNext()) {
                    updateMetaDataFields(iter.next(), true, false);

                }
                trackChanges(albumsResult,context,user);

            }
        } catch (GnException e) {
            e.printStackTrace();
            return;
        }

    }
    private void updateMetaDataFields(final GnAlbum album, boolean displayNoCoverArtAvailable, boolean fromTxtOrLyricSearch) throws GnException {

        // Load metadata layout from resource .xml
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View metadataView = inflater.inflate(R.layout.file_meta_data, null);

        parentLayout.addView(metadataView);

        final ImageView coverArtImage = (ImageView) metadataView.findViewById(R.id.coverArtImage);

        TextView albumText = (TextView) metadataView.findViewById( R.id.albumName );
        TextView trackText = (TextView) metadataView.findViewById( R.id.trackTitle );
        TextView artistText = (TextView) metadataView.findViewById( R.id.artistName );

        // enable pressing row to get track listing
        metadataView.setClickable(true);
        mOnClickListener onClickListener = new mOnClickListener(album, coverArtImage,context,parentLayout,user);
        if(metadataRow_OnClickListeners.add(onClickListener)){
            metadataView.setOnClickListener(onClickListener);
        }

        if (album == null) {

            coverArtImage.setVisibility(View.GONE);
            albumText.setVisibility(View.GONE);
            trackText.setVisibility(View.GONE);
            // Use the artist text field to display the error message
            //artistText.setText("Music Not Identified");
        } else {

            // populate the display tow with metadata and cover art

            albumText.setText( album.title().display() );
            String artist = album.trackMatched().artist().name().display();

            //use album artist if track artist not available
            if(artist.isEmpty()){
                artist = album.artist().name().display();
            }
            artistText.setText( artist );

            if ( album.trackMatched() != null ) {
                trackText.setText( album.trackMatched().title().display() );
            } else {
                trackText.setText("");
            }

            // limit the number of images added to display so we don't run out of memory,
            // a real app would page the results
            //Todo: Will think about later(For: Below Commented Code)....
         /*   if ( parentLayout.getChildCount() <= metadataMaxNumImages ){
                String coverArtUrl = album.coverArt().asset(GnImageSize.kImageSizeSmall).url();
                loadAndDisplayCoverArt( coverArtUrl, coverArtImage );
            } else {
                coverArtImage.setVisibility(View.GONE);
            }*/

        }
    }

    private synchronized void trackChanges(GnResponseAlbums albums,Context context,GnUser user) {
        Thread thread = new Thread (new InsertChangesRunnable(albums,context,user));
        thread.start();

    }
}
class mOnClickListener implements View.OnClickListener{

    DetailView detailView;

    mOnClickListener(GnAlbum album, ImageView imageView,Context context,LinearLayout parent,GnUser user){
        detailView = new DetailView(album, context,parent,user);

    }

    @Override
    public void onClick (View v){
        detailView.show(v);
    }
}