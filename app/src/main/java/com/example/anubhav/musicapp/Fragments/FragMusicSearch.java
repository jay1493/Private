package com.example.anubhav.musicapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.anubhav.musicapp.Adapters.YoutubeSearchAdapter;
import com.example.anubhav.musicapp.Constants;
import com.example.anubhav.musicapp.Interfaces.ItemClickListener;
import com.example.anubhav.musicapp.Model.AudioLinksModel;
import com.example.anubhav.musicapp.Model.DownloadVidsModel;
import com.example.anubhav.musicapp.Model.VideoLinksModel;
import com.example.anubhav.musicapp.Model.YouTubeSearchedVideos;
import com.example.anubhav.musicapp.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by anubhav on 22/2/17.
 */

public class FragMusicSearch extends Fragment {
    private Context activityContext;
    private static final String songFetchKey = "SongKey";
    private final String SEARCH_URL_1 = "https://www.googleapis.com/youtube/v3/search?order=viewCount&q=";
    private final String HIT_SEARCH = "https://api.w3hills.com/youtube/search";
    private final String FETCH_DOWNLOAD_LINKS = "https://api.w3hills.com/youtube/get_video_info";
    //Todo: change api key...
    private final String SEARCH_URL_2 = "&type=video&maxResults=20&part=snippet&fields=items(id/videoId,snippet/title,snippet/thumbnails)&key="
            + Constants.Youtube_API_Key;
    private static FragMusicSearch fragmentContext;
    private static String song = null;
    private RecyclerView recyclerView;
    private ImageView progressLoader;
    private GlideDrawableImageViewTarget glideDrawableImageViewTarget;
    private ArrayList<YouTubeSearchedVideos> listOfSearchedYouTubeVids;
    private YoutubeSearchAdapter youTubeSearchAdapter;
    private DownloadVidsModel downloadvidsModel;

    public static FragMusicSearch getInstance(Bundle bundle){
        if(bundle!=null){
            song = bundle.getString(songFetchKey,null);
        }
        if(fragmentContext == null){
            //Will go straight to onCreate..could pass bundle here..
            fragmentContext = new FragMusicSearch();
        }
            return fragmentContext;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        /** if you need to receive bundle from getInstance method here.. then remember to use here..*/
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_frag_search,null);
        recyclerView = (RecyclerView) view.findViewById(R.id.youtubeSearchList);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        progressLoader =(ImageView) view.findViewById(R.id.searchLoader);
        glideDrawableImageViewTarget = new GlideDrawableImageViewTarget(progressLoader);
        if (song != null && !song.equalsIgnoreCase("")) {
            performSearch(song);
        }

        return view;


    }
    public void performSearch(String key){
        if(key!=null && !key.equalsIgnoreCase("")){
            if(glideDrawableImageViewTarget!=null) {
                Glide.with(this).load(R.raw.dashinfinity).into(glideDrawableImageViewTarget);
                progressLoader.setVisibility(View.VISIBLE);

            }
            if(isConnectionPresent()){
                Pattern pattern = Pattern.compile("\\s+");
                Matcher matcher = pattern.matcher(key);
                String decodedSearchStr = matcher.replaceAll("%20");
//                String searchUrl = SEARCH_URL_1 + decodedSearchStr + SEARCH_URL_2;
                String searchUrl = HIT_SEARCH + "?keyword="+decodedSearchStr+"&api_key="+Constants.API_KEY;
                new GetVideoListFromYouTube().execute(searchUrl);
            }else{
                return;
            }
        }
    }

    private boolean isConnectionPresent() {
        if(activityContext!=null){
            ConnectivityManager connectivityManager = (ConnectivityManager) activityContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo!=null && networkInfo.isConnected()){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    private class GetVideoListFromYouTube extends
            AsyncTask<String, Void, ArrayList<YouTubeSearchedVideos>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<YouTubeSearchedVideos> doInBackground(String... params) {
            listOfSearchedYouTubeVids = new ArrayList<>();
            String searchUrl = params[0];

            if (searchUrl != null && searchUrl.length() > 0) {

                try {
                    URL url = new URL(searchUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setReadTimeout(10000);
                    /*urlConnection.setRequestProperty("Accept-Encoding", "gzip");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);

                    int response = urlConnection.getResponseCode();
                    if(response == HttpURLConnection.HTTP_OK){
                        BufferedReader reader = null;
                       if(urlConnection.getContentEncoding().equalsIgnoreCase("gzip")){
                           reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(urlConnection.getInputStream())));
                       }else{
                           reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                       }
                        String line ="";
                        String responseService ="";
                        while((line = reader.readLine())!=null){
                            responseService += line;
                        }
                        JSONObject jsonObj = new JSONObject(responseService);
                        JSONArray itemsArr = jsonObj.getJSONArray("items");
                        for (int i = 0; i < itemsArr.length(); i++) {
                            YouTubeSearchedVideos vItem = new YouTubeSearchedVideos();

                            JSONObject item = itemsArr.getJSONObject(i);

                            JSONObject id = item.getJSONObject("id");
                            vItem.setVideoId(id.getString("videoId"));

                            JSONObject snippet = item.getJSONObject("snippet");
                            vItem.setTitle(snippet.getString("title"));

                            vItem.setIconUrl(snippet.getJSONObject("thumbnails")
                                    .getJSONObject("medium").getString("url"));
                            listOfSearchedYouTubeVids.add(vItem);
                        }
                    }*/

                    int response = urlConnection.getResponseCode();
                    if(response == HttpURLConnection.HTTP_OK){
                        BufferedReader reader = null;
                        reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String line ="";
                        String responseService ="";
                        while((line = reader.readLine())!=null){
                            responseService += line;
                        }
                        JSONObject jsonObj = new JSONObject(responseService);
                        JSONArray itemsArr = jsonObj.getJSONArray("videos");
                        for(int i=0;i<itemsArr.length();i++){
                            JSONObject videoObj = itemsArr.getJSONObject(i);
                            YouTubeSearchedVideos vItem = new YouTubeSearchedVideos();
                            vItem.setVideoId(String.valueOf(videoObj.get("id")));
                            vItem.setToken(String.valueOf(videoObj.get("token")));
                            vItem.setIconUrl(String.valueOf(videoObj.get("thumbnail")));
                            vItem.setTitle(String.valueOf(videoObj.get("title")));
                            listOfSearchedYouTubeVids.add(vItem);

                        }

                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return listOfSearchedYouTubeVids;
        }

        @Override
        protected void onPostExecute(ArrayList<YouTubeSearchedVideos> result) {
            super.onPostExecute(result);
            // Cancel the Loading Dialog
            Glide.with(fragmentContext).onStop();
            progressLoader.setVisibility(View.GONE);
            /** Set Recycler Adapter here */
            setAdapter(result);

        }

    }

    private void setAdapter(ArrayList<YouTubeSearchedVideos> result) {
       youTubeSearchAdapter = new YoutubeSearchAdapter(activityContext, result, new ItemClickListener() {
           @Override
           public void itemClick(View view, int position) {
              new DownloadTask().execute(listOfSearchedYouTubeVids.get(position).getVideoId(),listOfSearchedYouTubeVids.get(position).getToken());
           }
       });
        recyclerView.setAdapter(youTubeSearchAdapter);
    }
    private class DownloadTask extends
            AsyncTask<String, Void, DownloadVidsModel> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activityContext);
            progressDialog.setMessage(activityContext.getResources().getString(R.string.fetching_download_links));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected DownloadVidsModel doInBackground(String... params) {
            String videoId = params[0];
            String token = params[1];
            String downloadLinks = FETCH_DOWNLOAD_LINKS + "?"+"video_id="+videoId+"&"+"token="+token+"&"+"api_key="+Constants.API_KEY;

                try {
                    URL url = new URL(downloadLinks);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setReadTimeout(10000);
                    /*urlConnection.setRequestProperty("Accept-Encoding", "gzip");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);

                    int response = urlConnection.getResponseCode();
                    if(response == HttpURLConnection.HTTP_OK){
                        BufferedReader reader = null;
                       if(urlConnection.getContentEncoding().equalsIgnoreCase("gzip")){
                           reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(urlConnection.getInputStream())));
                       }else{
                           reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                       }
                        String line ="";
                        String responseService ="";
                        while((line = reader.readLine())!=null){
                            responseService += line;
                        }
                        JSONObject jsonObj = new JSONObject(responseService);
                        JSONArray itemsArr = jsonObj.getJSONArray("items");
                        for (int i = 0; i < itemsArr.length(); i++) {
                            YouTubeSearchedVideos vItem = new YouTubeSearchedVideos();

                            JSONObject item = itemsArr.getJSONObject(i);

                            JSONObject id = item.getJSONObject("id");
                            vItem.setVideoId(id.getString("videoId"));

                            JSONObject snippet = item.getJSONObject("snippet");
                            vItem.setTitle(snippet.getString("title"));

                            vItem.setIconUrl(snippet.getJSONObject("thumbnails")
                                    .getJSONObject("medium").getString("url"));
                            listOfSearchedYouTubeVids.add(vItem);
                        }
                    }*/

                    int response = urlConnection.getResponseCode();
                    if(response == HttpURLConnection.HTTP_OK){
                        BufferedReader reader = null;
                        reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String line ="";
                        String responseService ="";
                        while((line = reader.readLine())!=null){
                            responseService += line;
                        }
                        JSONObject jsonObj = new JSONObject(responseService);
                        downloadvidsModel = new DownloadVidsModel();
                        downloadvidsModel.setIconUrl(String.valueOf(jsonObj.get("thumbnail")));
                        downloadvidsModel.setTitle(String.valueOf(jsonObj.get("title")));
                        JSONArray audioLinksArray = jsonObj.getJSONObject("links").getJSONArray("audios");
                        JSONArray videoLinksArray = jsonObj.getJSONObject("links").getJSONArray("videos");
                        List<AudioLinksModel> audioLinks = new ArrayList<>();
                        List<VideoLinksModel> videoLinks = new ArrayList<>();
                        Gson gson = new Gson();
                        for(int i=0;i<audioLinksArray.length();i++){
                            JSONObject audioLinkObj = audioLinksArray.getJSONObject(i);
                            AudioLinksModel audioObj = gson.fromJson(audioLinkObj.toString(),AudioLinksModel.class);
                            audioLinks.add(audioObj);
                        }
                        for(int i=0;i<videoLinksArray.length();i++){
                            JSONObject videoLinkObj = videoLinksArray.getJSONObject(i);
                            VideoLinksModel videoObj = gson.fromJson(videoLinkObj.toString(),VideoLinksModel.class);
                            videoLinks.add(videoObj);
                        }
                        downloadvidsModel.setAudioLinks(audioLinks);
                        downloadvidsModel.setVideoLinks(videoLinks);
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            return downloadvidsModel;
        }

        @Override
        protected void onPostExecute(DownloadVidsModel result) {
            super.onPostExecute(result);
            // Cancel the Loading Dialog
            progressDialog.dismiss();
            /** Show links overlay here */

        }

    }
}
