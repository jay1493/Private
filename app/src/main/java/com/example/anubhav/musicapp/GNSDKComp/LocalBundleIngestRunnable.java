package com.example.anubhav.musicapp.GNSDKComp;

/**
 * Created by anubhav on 21/3/17.
 */

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnLookupLocalStreamIngest;
import com.gracenote.gnsdk.GnLookupLocalStreamIngestStatus;
import com.gracenote.gnsdk.IGnCancellable;
import com.gracenote.gnsdk.IGnLookupLocalStreamIngestEvents;

import java.io.IOException;
import java.io.InputStream;

	 /* Loads a local bundle for MusicID-Stream lookups
        */
     public  class LocalBundleIngestRunnable implements Runnable {
    Context context;

         public  LocalBundleIngestRunnable(Context context) {
        this.context = context;
    }

    public void run() {
        try {

            // our bundle is delivered as a package asset
            // to ingest the bundle access it as a stream and write the bytes to
            // the bundle ingester
            // bundles should not be delivered with the package as this, rather they
            // should be downloaded from your own online service

            InputStream bundleInputStream 	= null;
            int				ingestBufferSize	= 1024;
            byte[] 			ingestBuffer 		= new byte[ingestBufferSize];
            int				bytesRead			= 0;

            GnLookupLocalStreamIngest ingester = new GnLookupLocalStreamIngest(new BundleIngestEvents(context));

            try {

                bundleInputStream = context.getAssets().open("1557.b");

                do {

                    bytesRead = bundleInputStream.read(ingestBuffer, 0, ingestBufferSize);
                    if ( bytesRead == -1 )
                        bytesRead = 0;

                    ingester.write( ingestBuffer, bytesRead );

                } while( bytesRead != 0 );

            } catch (IOException e) {
                e.printStackTrace();
            }

            ingester.flush();

        } catch (GnException e) {
            e.printStackTrace();
        }

    }
}
class BundleIngestEvents implements IGnLookupLocalStreamIngestEvents {

    private Context context;

    public BundleIngestEvents(Context context) {
        this.context = context;
    }

    @Override
    public void statusEvent(GnLookupLocalStreamIngestStatus status, String bundleId, IGnCancellable canceller) {
        Toast.makeText(context, "Loading from Local Bundle..."+status.toString(), Toast.LENGTH_SHORT).show();
    }
}