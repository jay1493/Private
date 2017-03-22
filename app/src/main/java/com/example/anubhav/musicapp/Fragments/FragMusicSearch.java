package com.example.anubhav.musicapp.Fragments;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Fade;
import android.transition.Transition;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.anubhav.musicapp.Adapters.LinkClickAdapter;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    private final String SEARCH_URL_1 = Constants.FETCH_RESULTS_VIA_READING_HTML_OF_YOUTUBE_RESULTS_URL_1;
    private final String WEBHILLS_YOUTUBE_API_HIT_SEARCH = Constants.SEARCH_FOR_MUSIC_WEBHILLS_API;
    private final String FETCH_DOWNLOAD_LINKS = Constants.NOT_USED_WEBHILLS_API_DOWNLOAD_LINKS;
    private final String YTGRABBER_FETCH_DOWNLOAD_LINKS = Constants.NOT_USED_YTGRABBER_DOWNLOAD_LINKS;
    private final String KEEPVID_FETCH_DOWNLOAD_LINKS = Constants.KEEPVID_LINK;
    //Todo: change api key...if to use
    private final String SEARCH_URL_2 =Constants.FETCH_RESULTS_VIA_READING_HTML_OF_YOUTUBE_RESULTS_URL_2
            + Constants.Youtube_API_Key;
    private static FragMusicSearch fragmentContext;
    private static String song = null;
    private RecyclerView recyclerView;
    private ImageView progressLoader;
    private GlideDrawableImageViewTarget glideDrawableImageViewTarget;
    private ArrayList<YouTubeSearchedVideos> listOfSearchedYouTubeVids;
    private YoutubeSearchAdapter youTubeSearchAdapter;
    private DownloadVidsModel downloadvidsModel;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notifBuilder;
    private String decodedSearchStr ="";
    private int notifId = 0;
    private ArrayList<VideoLinksModel> videoLinks;
    private ArrayList<AudioLinksModel> audioLinks;
    private View fetchedClickedView;
    private int fetchedClickedPos;
    private PopupWindow popUpWindow;

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
        notifId = 0;
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
                decodedSearchStr = matcher.replaceAll("%20");
//                String searchUrl = SEARCH_URL_1 + decodedSearchStr + SEARCH_URL_2;
                String searchUrl = WEBHILLS_YOUTUBE_API_HIT_SEARCH + "?keyword="+decodedSearchStr+"&api_key="+Constants.WEBHILLS_Youtube_API_KEY;
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
        downloadvidsModel = new DownloadVidsModel();
        videoLinks = new ArrayList<>();
        audioLinks = new ArrayList<>();
        downloadvidsModel.setVideoLinks(videoLinks);
        downloadvidsModel.setAudioLinks(audioLinks);
       youTubeSearchAdapter = new YoutubeSearchAdapter(activityContext, result,downloadvidsModel, new ItemClickListener() {
           @Override
           public void itemClick(View view, int position) {
               fetchedClickedView = view;
               fetchedClickedView.findViewById(R.id.mainGridLayout).setVisibility(View.VISIBLE);
               fetchedClickedPos = position;
              new GenerateDownloadLinks().execute(listOfSearchedYouTubeVids.get(position).getVideoId(),listOfSearchedYouTubeVids.get(position).getToken());
           }
       });
        recyclerView.setAdapter(youTubeSearchAdapter);
    }

    private void commentedCodes(){
        /*    private class GenerateDownloadLinks extends
            AsyncTask<String, Void, DownloadVidsModel> {
        private ProgressDialog progressDialog;

        *//**
         * This async uses API WebHills(YouTube API) to generate Links, but the links are empty when we open them
         * HENCE: Not using this API for now
         *//*

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
            String downloadLinks = FETCH_DOWNLOAD_LINKS + "?"+"video_id="+videoId+"&"+"token="+token+"&"+"api_key="+Constants.WEBHILLS_Youtube_API_KEY;

                try {
                    URL url = new URL(downloadLinks);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setReadTimeout(10000);
                    *//*urlConnection.setRequestProperty("Accept-Encoding", "gzip");
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
                    }*//*

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
            *//** Show links overlay here *//*
            //Todo: Just testing download links
            new DownloadLink().execute(result.getAudioLinks().get(0).getUrl(),result.getAudioLinks().get(0).getExtension());
        }

    }*/


        /**
         * YTGrabber API -- Links also not work for these..
         */
        /*private class GenerateDownloadLinks extends
                AsyncTask<String, Void, DownloadVidsModel> {
            private ProgressDialog progressDialog;

            *//**
             * This async uses API YTGrabber to generate Links,
             * HENCE: Using this API for now
             *//*

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
                String downloadLinks = YTGRABBER_FETCH_DOWNLOAD_LINKS + videoId;

                try {
                    URL url = new URL(downloadLinks);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setRequestProperty("Accept","application/json");
                    urlConnection.setRequestProperty("X-Mashape-Key",Constants.YTGRABBER_API_KEY);

                    *//*urlConnection.setRequestProperty("Accept-Encoding", "gzip");
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
                    }*//*

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
                        List<AudioLinksModel> audioLinks = new ArrayList<>();
                        List<VideoLinksModel> videoLinks = new ArrayList<>();
                        if(jsonObj.has("error") && !jsonObj.isNull("error") && jsonObj.getJSONObject("error")!=null && jsonObj.getJSONObject("error").has("error_message")){
                            String errorMsg = String.valueOf(jsonObj.getJSONObject("error").get("error_message"));
                            errorMsg = errorMsg.substring(0,errorMsg.indexOf("\n"));
                            downloadvidsModel.setErrorMsg(errorMsg);
                        }else {
                            JSONArray linksArray = jsonObj.getJSONArray("link");
                            for (int i = 0; i < linksArray.length(); i++) {
                                JSONObject linkObj = linksArray.getJSONObject(i);
                                if (String.valueOf(linkObj.getJSONObject("type").get("format")).equalsIgnoreCase("mp4") ||
                                        String.valueOf(linkObj.getJSONObject("type").get("format")).equalsIgnoreCase("mp3")) {
                                    //Audio Links
                                    AudioLinksModel audioLinksModel = new AudioLinksModel();
                                    audioLinksModel.setUrl(String.valueOf(linkObj.get("url")));
                                    audioLinksModel.setExtension(String.valueOf(linkObj.getJSONObject("type").get("format")));
                                    audioLinks.add(audioLinksModel);
                                } else {
                                    //Video Links
                                    VideoLinksModel videoLinksModel = new VideoLinksModel();
                                    videoLinksModel.setUrl(String.valueOf(linkObj.get("url")));
                                    videoLinksModel.setExtension(String.valueOf(linkObj.getJSONObject("type").get("format")));
                                    videoLinks.add(videoLinksModel);
                                }
                            }
                            downloadvidsModel.setErrorMsg("");
                            downloadvidsModel.setAudioLinks(audioLinks);
                            downloadvidsModel.setVideoLinks(videoLinks);
                        }
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
                *//** Show links overlay here *//*
                //Todo: Just testing download links
                if(result.getErrorMsg().equalsIgnoreCase("")) {
                    new DownloadLink().execute(result.getAudioLinks().get(0).getUrl(), result.getAudioLinks().get(0).getExtension(),result.getTitle());
                }else{
                    Toast.makeText(activityContext, result.getErrorMsg(), Toast.LENGTH_SHORT).show();
                }
            }

        }*/
    }


    private class GenerateDownloadLinks extends
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
            String downloadLinks = KEEPVID_FETCH_DOWNLOAD_LINKS + videoId;
            Document keepVidHome = null;

            try {
                keepVidHome = Jsoup.connect(downloadLinks).get();
                Element body = keepVidHome.select("body").get(0);
                Elements divLinkBody = body.getElementsByClass("d-info2");
                Elements dlElements = divLinkBody.select("dl");
                for(Element dtElement : dlElements){
                    Elements ddElements = dtElement.select("dd");
                    for(Element ddElement : ddElements){
                        Element link = ddElement.select("a[href]").get(0);
                        Element spanWid = ddElement.select("a[href]").get(0).getElementsByClass("spanWid").get(0);
                        if(spanWid.text().contains("MP3")||spanWid.text().contains("mp3")||spanWid.text().contains("M4A")||spanWid.text().contains("m4a")){
                            //Audio Links
                            AudioLinksModel audioLinksModel = new AudioLinksModel();
                            audioLinksModel.setUrl(link.attr("abs:href"));
                            String quality = spanWid.text().substring(spanWid.text().indexOf("-")+1,spanWid.text().length());
                            String extension = spanWid.text().substring(0,spanWid.text().indexOf("-"));
                            audioLinksModel.setQuality(quality);
                            audioLinksModel.setExtension(extension);
                            audioLinks.add(audioLinksModel);
                        }else{
                            //Video Links
                            VideoLinksModel videoLinksModel = new VideoLinksModel();
                            videoLinksModel.setUrl(link.attr("abs:href"));
                            String quality = spanWid.text().substring(spanWid.text().indexOf("-")+1,spanWid.text().length());
                            String extension = spanWid.text().substring(0,spanWid.text().indexOf("-"));
                            videoLinksModel.setQuality(quality);
                            videoLinksModel.setExtension(extension);
                            videoLinks.add(videoLinksModel);
                        }
                    }
                }
                downloadvidsModel.setAudioLinks(audioLinks);
                downloadvidsModel.setVideoLinks(videoLinks);
            } catch (IOException e) {
                e.printStackTrace();
            }



            return downloadvidsModel;
        }

        @Override
        protected void onPostExecute(DownloadVidsModel result) {
            super.onPostExecute(result);
            // Cancel the Loading Dialog
            initiatePopupWindow(result);
            progressDialog.dismiss();
        }

    }

    private void initiatePopupWindow(DownloadVidsModel result) {
        View view = LayoutInflater.from(activityContext).inflate(R.layout.row_grid_generate_links, null,false);
        popUpWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,true);
        popUpWindow.showAtLocation(fetchedClickedView, Gravity.CENTER_VERTICAL,0,0);
        RecyclerView audioRecycler = (RecyclerView) view.findViewById(R.id.audioLinks);
        RecyclerView videoRecycler = (RecyclerView) view.findViewById(R.id.videoLinks);
        audioRecycler.setLayoutManager(new GridLayoutManager(activityContext,3));
        videoRecycler.setLayoutManager(new GridLayoutManager(activityContext,3));
        LinkClickAdapter audioLinkAdap = new LinkClickAdapter(activityContext, 0, result, new LinkClickAdapter.LinkClickInterface() {
            @Override
            public void onLinkClick(View view, int pos, List<AudioLinksModel> audioLists, List<VideoLinksModel> videoLists) {
                if(audioLists!=null) {
                    popUpWindow.dismiss();
                    new DownloadLink().execute(audioLists.get(pos).getUrl(), audioLists.get(pos).getExtension(), "");
                }
            }
        });
        audioRecycler.setAdapter(audioLinkAdap);
        LinkClickAdapter videoLinkAdap = new LinkClickAdapter(activityContext, 1, result, new LinkClickAdapter.LinkClickInterface() {
            @Override
            public void onLinkClick(View view, int pos, List<AudioLinksModel> audioLists, List<VideoLinksModel> videoLists) {
                if(videoLists!=null){
                    popUpWindow.dismiss();
                    new DownloadLink().execute(videoLists.get(pos).getUrl(), videoLists.get(pos).getExtension(), "");

                }
            }
        });
        videoRecycler.setAdapter(videoLinkAdap);
        setPopupTransitions(popUpWindow);
        view.findViewById(R.id.dismissPopup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpWindow.dismiss();
            }
        });
        popUpWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        popUpWindow.setOutsideTouchable(false);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setPopupTransitions(PopupWindow popUpWindow) {
        if(popUpWindow!=null){
            Transition enterTransition = new Fade(Fade.IN);
            enterTransition.setDuration(5000);
            enterTransition.setInterpolator(new LinearInterpolator());
            popUpWindow.setEnterTransition(enterTransition);
            Transition exitTransition = new Fade(Fade.OUT);
            exitTransition.setDuration(5000);
            exitTransition.setInterpolator(new LinearInterpolator());
            popUpWindow.setExitTransition(exitTransition);
        }
    }


    private class DownloadLink extends
            AsyncTask<String, Integer, String> {
        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Setting up Notification
            notificationManager = (NotificationManager) activityContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notifBuilder = new NotificationCompat.Builder(activityContext);
            notifBuilder.setColor(activityContext.getResources().getColor(R.color.black_slight_alpha));
            notifBuilder.setContentTitle(activityContext.getResources().getString(R.string.app_name));
            notifBuilder.setContentText(activityContext.getResources().getString(R.string.getting_it_downloaded_for_you)+" ("+decodedSearchStr+")");
            notifBuilder.setLights(activityContext.getResources().getColor(R.color.red),1000,5000);
            notifBuilder.setOngoing(true);
            notifBuilder.setSmallIcon(R.drawable.notif_icon);
            progressDialog = new ProgressDialog(activityContext);
            progressDialog.setMessage(activityContext.getResources().getString(R.string.getting_it_downloaded_for_you));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String video = params[0];
            String videoExtension = params[1];
            String videoTitle = params[2];
            notifId++;
            try {
                String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:8.0.1)";
                URL url = new URL(video);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("User-Agent",userAgent);
                urlConnection.setReadTimeout(10000);
                urlConnection.connect();
                int response = urlConnection.getResponseCode();
                if(response == HttpURLConnection.HTTP_OK){
                    int fileLength = urlConnection.getContentLength();
                    InputStream inputStream = urlConnection.getInputStream();
                    File fileDir = new File("/sdcard/MyMusicApp");
                    if(!fileDir.exists()){
                        fileDir.mkdirs();
                    }
                    //Todo: Do something about naming files.. Done!! I Suppose...
                    File songFile = new File(fileDir,decodedSearchStr+"."+videoExtension);
                    if(songFile.exists()){
                        songFile.delete();
                    }
                    songFile.createNewFile();
                    OutputStream outputStream = new FileOutputStream(songFile);
                    byte[] bytes = new byte[4096];
                    int count;
                    long total =0;
                    progressDialog.dismiss();
                    while((count = inputStream.read(bytes))!=-1){
                        if (isCancelled()) {
                            inputStream.close();
                            return null;
                        }
                        total += count;
                        // publishing the progress....
                        if (fileLength > 0) { // only if total length is known
//                            publishProgress((int) (total * 100 / fileLength));
                            notifBuilder.setProgress(99,(int) ((total*100)/fileLength),false);

                        }else{
                            notifBuilder.setProgress(0,0,true);
                        }
                        outputStream.write(bytes, 0, count);
                        notificationManager.notify(notifId,notifBuilder.build());
                    }

                }else{
                    progressDialog.dismiss();
                    Toast.makeText(activityContext, "There seems a problem downloading your resource..Try Again!!", Toast.LENGTH_SHORT).show();
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return "Success";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            notifBuilder.setProgress(0,0,false);
            notifBuilder.setContentText(activityContext.getResources().getString(R.string.download_complete));
            notifBuilder.setOngoing(false);
            notificationManager.notify(notifId,notifBuilder.build());

        }

    }
}
