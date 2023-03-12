package com.jubayedtry.bongoapptry;

import static com.jubayedtry.bongoapptry.MasterJubaYoutube.BUFFERING;
import static com.jubayedtry.bongoapptry.MasterJubaYoutube.CUED;
import static com.jubayedtry.bongoapptry.MasterJubaYoutube.ENDED;
import static com.jubayedtry.bongoapptry.MasterJubaYoutube.PAUSED;
import static com.jubayedtry.bongoapptry.MasterJubaYoutube.PLAYING;
import static com.jubayedtry.bongoapptry.MasterJubaYoutube.UNSTARTED;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

        /*
    >>Source Code provided by
    >>Jubayer Hossain [https://www.facebook.com/JubayerHossainbd]
    >>Please don't forget to put a review on my site [www.jubayedtry.com]
    >>Share my course with your friends on Facebook
    >>Your positive words help me doing even better
     */

    MasterJubaYoutube webYouTube;
    LinearLayout layoutContainer;
    LottieAnimationView myLottie;
    TextView durationTextView;
    SeekBar seekBar;
    ScrollView mainScroll;
    RelativeLayout _rootLay, layPlayerRoot, seek_lay;
    InterstitialAd mInterstitialAd;
    YouTubePlayerView youTubeView;
    YouTubePlayer myYoutubePlayer;
    MyPlaybackEventListener myPlaybackEventListener  = new MyPlaybackEventListener();
    LinearLayout layPlayer;
    ImageView imngClosePlayer, imgPlayPause, imgPrevious, imgNext, mainCover, repeat_one;

    public static ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    HashMap<String, String> hashMap;
    int PLAYING_NOW = 0;
    boolean playingVideo = false;
    public static int SCREEN_WIDTH = 0;
    boolean YOUTUBE_PLAYER = true;
    public static boolean REPEAT_ONE = false;
    String CURRENT_VIDEO_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Home.CategoryClicked = -10;

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


       //tvDate = findViewById(R.id.tvDate);
        layoutContainer = findViewById(R.id.layoutContainer);
        _rootLay = findViewById(R.id._rootLay);
        layPlayer = findViewById(R.id.layPlayer);
        imngClosePlayer = findViewById(R.id.imngClosePlayer);
        imgPlayPause = findViewById(R.id.imgPlayPause);
        imgNext = findViewById(R.id.imgNext);
        imgPrevious = findViewById(R.id.imgPrevious);
        mainCover = findViewById(R.id.mainCover);
        layPlayerRoot = findViewById(R.id.layPlayerRoot);
        myLottie = findViewById(R.id.myLottie);
        mainScroll = findViewById(R.id.mainScroll);
        repeat_one = findViewById(R.id.repeat_one);
        seek_lay = findViewById(R.id.seek_lay);
        seekBar = findViewById(R.id.seekBar);
        durationTextView = findViewById(R.id.durationTextView);


        //init multiple players with this method
        initMultiPlayers();

        //Calling addVideos method by which we will make a video list
        makeListView();


        //Adding Cover Dynamically ----------------------------------------------
        Random r = new Random();
        int low = 0;
        int high = arrayList.size()-1;
        int randomNum = r.nextInt(high-low) + low;

        HashMap<String, String> hashMap = arrayList.get(randomNum);
        String vdo_id = hashMap.get("vdo_id");

        // Youtube thumnail link is like
        //https://i.ytimg.com/vi/<VIDEO ID>/0.jpg
        String thumb_link = "https://i.ytimg.com/vi/"+vdo_id+"/0.jpg";
        Picasso.get().
                load(""+thumb_link)
                .placeholder(R.mipmap.ic_launcher)
                .into(mainCover);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~




        imngClosePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePlayer();
                playingVideo = false;

            }
        });

        imgPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v!=null && v.getTag()!=null){
                    String tag = v.getTag().toString();
                    if (tag.contains("PLAYING")){
                        if (myYoutubePlayer!=null) myYoutubePlayer.pause();
                        else if (webYouTube!=null) webYouTube.pause();
                        else Toast.makeText(MainActivity.this, "Please wait...", Toast.LENGTH_LONG).show();

                    }else if (tag.contains("PAUSED")){
                        if (myYoutubePlayer!=null) myYoutubePlayer.play();
                        else if (webYouTube!=null) webYouTube.play();
                        else Toast.makeText(MainActivity.this, "Please wait...", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextVideo();
            }
        });

        imgPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousVideo();
            }
        });



        repeat_one.setBackgroundColor(Color.TRANSPARENT);
        REPEAT_ONE = false;
        repeat_one.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (!REPEAT_ONE){
                    REPEAT_ONE = true;
                    //repeat_one.setBackgroundColor(Color.parseColor("#44ffffff"));
                    repeat_one.setColorFilter( Color.parseColor("#ffcdd2"));
                    Toast.makeText(MainActivity.this, "Done!\nRepeat one enabled", Toast.LENGTH_SHORT).show();
                }else{
                    REPEAT_ONE = false;
                    //repeat_one.setBackgroundColor(Color.TRANSPARENT);
                    repeat_one.setColorFilter( Color.parseColor("#ffffff"));
                }

            }
        });




        myLottie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });





    } // End of onCreate Bundle





    private void makeListView(){



        ExpandableHeightGridView mainGrid = findViewById(R.id.mainGrid);
        mainGrid.setExpanded(true);
        //------------
        MyAdapter myAdapter = new MyAdapter();
        mainGrid.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myLottie.setVisibility(View.GONE);
                mainScroll.setVisibility(View.VISIBLE);
            }
        }, 5000);

    }

    ///==============================================




    //==============================================
    private void showInterstitial() {
        // Show the ad if it's ready.
        if (mInterstitialAd != null ) {
            mInterstitialAd.show(this);
        }
    }
    //============================================




    ///==============================================
    ///==============================================

    private void initMultiPlayers(){

        //Adding Master Juba YouTube Player
        webYouTube = new MasterJubaYoutube(MainActivity.this);
        layPlayerRoot.addView(webYouTube);
        webYouTube.setVisibility(View.GONE);
        webYouTube.initialize();
        setupMasterJubaYoutube();

        //Getting screen height and width
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREEN_WIDTH = size.x;

        // Adding Official youtube library
        youTubeView = new YouTubePlayerView(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 50);
        layoutParams.width = SCREEN_WIDTH;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        youTubeView.setLayoutParams(layoutParams);
        layPlayerRoot.addView(youTubeView);

        //finally init the yt official player
        youTubeView.initialize("ABCD", MainActivity.this);


        // Master Juba Player listener handler
        setMasterJubaPlayerListener();







    } //end of initMultiplayer ------------------------------------>

    ///==============================================
    ///==============================================






    private int currentSeconds = 0, videoDuration = 1;
    private float videoBuffered = 0;
    private boolean isUserChangingTouch = false;
    private Handler secondsHandler = new Handler();


    private void setMasterJubaPlayerListener(){
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++
        webYouTube.setOnPlaybackStateChange(new Runnable() {
            @Override
            public synchronized void run() {
                switch (webYouTube.getPlaybackState()) {

                    case BUFFERING:
                        if (!YOUTUBE_PLAYER && imgPlayPause != null ){
                            imgPlayPause.setImageResource(R.drawable.buffer);
                            imgPlayPause.setTag("BUFFERING");
                        }
                        break;

                    case CUED:
                        //Toast.makeText(getApplicationContext(), "Cued : "+Service_Overlay.playerView.getPlaybackState(), Toast.LENGTH_LONG).show();
                        break;

                    case ENDED:
                        // Toast.makeText(getApplicationContext(), "Ended : "+Service_Overlay.playerView.getPlaybackState(), Toast.LENGTH_LONG).show();
                        if (REPEAT_ONE) webYouTube.play();
                        else playNextVideo();


                        break;

                    case PAUSED:
                        // Toast.makeText(getApplicationContext(), "Paused : "+Service_Overlay.playerView.getPlaybackState(), Toast.LENGTH_LONG).show();
                        if (!YOUTUBE_PLAYER && imgPlayPause != null ){
                            imgPlayPause.setImageResource(R.drawable.icon_play);
                            imgPlayPause.setTag("PAUSED");
                        }

                        break;


                    case PLAYING:
                        if (!YOUTUBE_PLAYER && imgPlayPause != null ){
                            imgPlayPause.setImageResource(R.drawable.icon_pause);
                            imgPlayPause.setTag("PLAYING");
                            if (seek_lay!=null) seek_lay.setVisibility(View.VISIBLE);
                        }
                        break;

                    case UNSTARTED:
                        //Toast.makeText(getApplicationContext(), "Unstarted : "+Service_Overlay.playerView.getPlaybackState(), Toast.LENGTH_LONG).show();
                        break;
                }

            }
        });
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && videoDuration != 0) {
                    currentSeconds = (int) ((progress / 100f) * videoDuration);
                    if (isUserChangingTouch) {
                        webYouTube.seekTo(currentSeconds, false);
                    } else {
                        webYouTube.seekTo(currentSeconds, true);

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserChangingTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserChangingTouch = false;
                currentSeconds = (int) ((seekBar.getProgress() / 100f) * videoDuration);
                webYouTube.seekTo(currentSeconds, true);
                webYouTube.play();
            }
        });


        webYouTube.setCurrentTimeListener(new MasterJubaYoutube.NumberReceivedListener() {
            @Override
            public void onReceive(float n) {
                currentSeconds = (int) n;
            }
        }); //TODO: Put this in player view itself
        webYouTube.setDurationListener(new MasterJubaYoutube.NumberReceivedListener() {
            @Override
            public void onReceive(float n) {
                videoDuration = (int) n;
            }
        }); //TODO: Put this in player view itself
        webYouTube.setVideoLoadedFractionListener(new MasterJubaYoutube.NumberReceivedListener() {
            @Override
            public void onReceive(float n) {
                videoBuffered = n;
            }
        }); //TODO: Put this in player view itself


        //Update seekbar every second with handler // -- juba@
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (seekBar!=null){
                    if (seekBar.getVisibility()==View.VISIBLE){

                        if (!isUserChangingTouch) {
                            //=======================================================
                            updateSeekBarUI();
                        }
                        //=======================================================

                    }
                }
                secondsHandler.postDelayed(this, 1000);
            }
        };
        secondsHandler.postDelayed(r, 200);





    }

    ///==============================================
    ///========================================================================================


    private void updateSeekBarUI() {
        //Log.d("Service", "Updating SeekUI, " + videoDuration);
        if (isUserChangingTouch || videoDuration == 0) {
            seekBar.setProgress(0);
            seekBar.setSecondaryProgress(0);
            currentSeconds = 0;
            updateTextUI();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            seekBar.setProgress(100 * currentSeconds / videoDuration, true);
        } else {
            seekBar.setProgress(100 * currentSeconds / videoDuration);
        }
        seekBar.setSecondaryProgress((int) (100 * videoBuffered));
        updateTextUI();
    }

    private void updateTextUI() {
        String text = currentSeconds / 60 + ":" + currentSeconds % 60;
        if (durationTextView!=null) durationTextView.setText(text);

    }


    /**
     * Sets up the player view
     */
    public void setupMasterJubaYoutube() {
        webYouTube.setOnPlayerReadyRunnable(new Runnable() {
            @Override
            public void run() {
                webYouTube.pause();
                webYouTube.showOverlay();
            }
        });

    }


    //=================================================================






    ///==============================================
    ///==============================================


    private class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyAdapter(){
            this.inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.video_item, parent, false);

            TextView tvTitle = convertView.findViewById(R.id.tvTitle);
            TextView tvDescription = convertView.findViewById(R.id.tvDescription);
            ImageView imgThumb = convertView.findViewById(R.id.imgThumb);
            RelativeLayout layItem = convertView.findViewById(R.id.layItem);

            Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_grid);
            animation.setStartOffset(position*200);
            convertView.startAnimation(animation);

            HashMap<String, String> hashMap = arrayList.get(position);
            String vdo_id = hashMap.get("vdo_id");
            String vdo_title = hashMap.get("vdo_title");
            String vdo_desciption = hashMap.get("vdo_desciption");

            tvTitle.setText(vdo_title);
            tvDescription.setText(vdo_desciption);

            // Youtube thumnail link is like
            //https://i.ytimg.com/vi/<VIDEO ID>/0.jpg
            String thumb_link = "https://i.ytimg.com/vi/"+vdo_id+"/0.jpg";
            Picasso.get().
                    load(""+thumb_link)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(imgThumb);

            layItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Play Video
                    PLAYING_NOW = position;
                    playVideo(vdo_id);
                    showInterstitial();

                }
            });




            return convertView;
        }
    }



    //================================================
    private void playVideo(String video_id){
        seek_lay.setVisibility(View.GONE);
        CURRENT_VIDEO_ID = video_id;

        try{
            YOUTUBE_PLAYER = true;
            layPlayer.setVisibility(View.VISIBLE);
            layPlayer.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
            myYoutubePlayer.loadVideo(video_id);
            myYoutubePlayer.play();
            playingVideo = true;
            youTubeView.bringToFront();

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 50);
            layoutParams.width = SCREEN_WIDTH;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            youTubeView.setLayoutParams(layoutParams);
            exitMasterJuba();

        } catch(Exception e){
            //Toast.makeText(MainActivity.this, "Please wait..", Toast.LENGTH_LONG).show();
            playWithMasterJubaPlayer(video_id);
        }



    }



    //===================================================================================

    private void playWithMasterJubaPlayer(String video_id){


        YOUTUBE_PLAYER = false;
        //=====================================================================
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(150, 50);
        int width = youTubeView.getMeasuredWidth();
        int height = youTubeView.getMeasuredHeight();
        layoutParams1.width = SCREEN_WIDTH;
        layoutParams1.height = (int) (SCREEN_WIDTH * 9/16);
        webYouTube.setLayoutParams(layoutParams1);
        //webYouTube.setVisibility(View.GONE);

        layPlayer.setVisibility(View.VISIBLE);
        layPlayer.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
        webYouTube.setVisibility(View.VISIBLE);
        try{
            webYouTube.loadVideoById(video_id, 0, "default");
            webYouTube.play();
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "Please try again!", Toast.LENGTH_LONG).show();
        }
        playingVideo = true;
        webYouTube.bringToFront();
        exitYtPlayer();


    }



    //===================================================================================
    //===================================================================================
    //===================================================================================
    //===================================================================================
    //================================================

    private void closePlayer(){
        if(myYoutubePlayer!=null && myYoutubePlayer.isPlaying()) myYoutubePlayer.pause();
        if(webYouTube!=null) webYouTube.pause();
        layPlayer.setVisibility(View.GONE);
        layPlayer.clearAnimation();
    }



    //===================================================================================
    //================================================
    public  void exitYtPlayer(){
        final RelativeLayout.LayoutParams layoutParams22 = (RelativeLayout.LayoutParams) youTubeView.getLayoutParams();
        layoutParams22.width =0;
        layoutParams22.height =0;
        youTubeView.setLayoutParams(layoutParams22);
    }

    //===================================================================================
    //================================================
    public  void exitMasterJuba(){
        final RelativeLayout.LayoutParams layoutParams22 = (RelativeLayout.LayoutParams) webYouTube.getLayoutParams();
        layoutParams22.width =0;
        layoutParams22.height =0;
        webYouTube.setLayoutParams(layoutParams22);
    }

    //==============================================
    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {
        public void onBuffering(boolean arg0) {
            //updateLog("onBuffering(): " + String.valueOf(arg0));
            if (imgPlayPause != null ){
                imgPlayPause.setImageResource(R.drawable.buffer);
                imgPlayPause.setTag("BUFFERING");
            }
        }

        public void onPaused() {
            //updateLog("onPaused()");

            if (imgPlayPause != null ){
                imgPlayPause.setImageResource(R.drawable.icon_play);
                imgPlayPause.setTag("PAUSED");
            }

            //Toast.makeText(getApplicationContext(), "Paused", Toast.LENGTH_SHORT).show();
        }

        public void onPlaying() {
            //updateLog("onPlaying()");
            // Toast.makeText(getApplicationContext(), "Paying", Toast.LENGTH_SHORT).show();

            if (imgPlayPause != null ){
                imgPlayPause.setImageResource(R.drawable.icon_pause);
                imgPlayPause.setTag("PLAYING");
            }

        }

        public void onSeekTo(int arg0) {
            //updateLog("onSeekTo(): " + String.valueOf(arg0));
        }

        public void onStopped() {

            if (imgPlayPause != null ){
                imgPlayPause.setImageResource(R.drawable.icon_play);
            }

            if (myYoutubePlayer!=null && myYoutubePlayer.getCurrentTimeMillis()>2000 && (myYoutubePlayer.getDurationMillis()- myYoutubePlayer.getCurrentTimeMillis() <=2000) ){
                if (REPEAT_ONE) myYoutubePlayer.play();
                else playNextVideo();
            }


            //Toast.makeText(getApplicationContext(), "Stopped at:"+myYoutubePlayer.getCurrentTimeMillis() +"\nDuration:"+myYoutubePlayer.getDurationMillis(), Toast.LENGTH_LONG).show();

        }

    }

    //*******************************************************************



    //=================================================================




    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        public void onAdStarted() {
            //updateLog("onAdStarted()");
        }

        public void onError(
                YouTubePlayer.ErrorReason arg0) {
            //updateLog("onError(): " + arg0.toString());
            Toast.makeText(getApplicationContext(), ""+arg0.toString(), Toast.LENGTH_SHORT).show();

            if (arg0.toString().contains("NOT_PLAYABLE") && YOUTUBE_PLAYER ){
                playWithMasterJubaPlayer(CURRENT_VIDEO_ID);
            }
            else if (arg0.toString().contains("INTERNAL_ERROR") && YOUTUBE_PLAYER ){
                playWithMasterJubaPlayer(CURRENT_VIDEO_ID);
            }


        }

        public void onLoaded(String arg0) {
            //updateLog("onLoaded(): " + arg0);
        }

        public void onLoading() {
            //updateLog("onLoading()");
        }

        public void onVideoEnded() {
            //Toast.makeText(getApplicationContext(), "ended", Toast.LENGTH_LONG).show();
            //playNextVideo();


        }


        public void onVideoStarted() {
            //updateLog("onVideoStarted()");
        }

    }
    //==============================================================



//=================================================
    private void playNextVideo(){
        if(PLAYING_NOW >= (arrayList.size()-1))
            PLAYING_NOW = 0;
        else PLAYING_NOW++;

        HashMap<String, String> hashMap = arrayList.get(PLAYING_NOW);
        String vdo_id = hashMap.get("vdo_id");
        playVideo(vdo_id);
    }


    private void playPreviousVideo(){
        if(PLAYING_NOW > 0){
            PLAYING_NOW--;
            HashMap<String, String> hashMap = arrayList.get(PLAYING_NOW);
            String vdo_id = hashMap.get("vdo_id");
            playVideo(vdo_id);
        }else{
            Toast.makeText(MainActivity.this, "Playing the first video", Toast.LENGTH_LONG).show();
        }

    }






    ///==============================================
    ///==============================================
    //===================================================

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    //==========================================================================
//==========================================================================





    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        myYoutubePlayer = youTubePlayer;
        myYoutubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        myYoutubePlayer.setPlaybackEventListener(myPlaybackEventListener);

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        myYoutubePlayer = null;
    }






    protected boolean isAppInstalled(String packageName) {
        Intent mIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (mIntent != null) {
            return true;
        }
        else {
            return false;
        }
    }

    //=======================================================
    //method to show a dialog in android
    private void showYoutubeInsallDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Install Youtube App");
        alertDialog.setMessage(getString(R.string.app_name)+" will not work if you don't have youtube official app installed on your device");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Install NOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.dismiss();
                        openStoreIntent("com.google.android.youtube");
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Exit App",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Exit App
                        if(Build.VERSION.SDK_INT>=16 && Build.VERSION.SDK_INT<21){
                            finishAffinity();
                        } else if(Build.VERSION.SDK_INT>=21){
                            finishAndRemoveTask();
                        }

                    }
                });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    //===============================================================



    //=================================================
    ///==============================================
    ///==============================================
    ///==============================================
    //try to download youtube app from app stores
    private void openStoreIntent(String app_package){
        String url="";
        Intent storeintent=null;
        try {
            url = "market://details?id="+app_package;
            storeintent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            storeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(storeintent);
        } catch ( final Exception e ) {
            url = "https://play.google.com/store/apps/details?id="+app_package;
            storeintent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            storeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(storeintent);
        }

    }
    ///==============================================
    ///==============================================
    ///==============================================
    ///==============================================




    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (secondsHandler != null) secondsHandler.removeCallbacksAndMessages(null);

        super.onDestroy();
    }




}