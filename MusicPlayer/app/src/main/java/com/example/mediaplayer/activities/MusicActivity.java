package com.example.mediaplayer.activities;
import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;
import android.support.v7.widget.SearchView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.support.design.widget.TabLayout;

import com.example.mediaplayer.fragments.AlbumsFragment;
import com.example.mediaplayer.models.DataReading;
import com.example.mediaplayer.notification.NotiService;
import com.example.mediaplayer.R;
import com.example.mediaplayer.models.Song;
import com.example.mediaplayer.adapters.SongAdapter;
import com.example.mediaplayer.fragments.SongsFragment;
import com.example.mediaplayer.adapters.ViewPagerAdapter;

import static com.example.mediaplayer.notification.NofiticationCenter.channel_1_ID;
import static com.example.mediaplayer.adapters.SongAdapter.songs;

public class MusicActivity extends AppCompatActivity implements PlayerFrag.FragmentAListener{
    private int Storage_Permission_code=1;
    private static final String TAG = "MainActivity";
     DataReading dataReading;
    protected static MusicActivity instance;
   private byte arts[];

     private HashMap<String, List<Song> >albums=new HashMap<>();
     public static  ArrayList<ArrayList<Song>>al=new ArrayList<>();


    private static PlayerFrag playerFrag;
    private NotificationManagerCompat notificationManager;
     MediaMetadataRetriever metadataRetriever;
    private MediaSessionCompat mediaSession;

     TabLayout tableLayout;
     ViewPager viewPager;
     protected ViewPagerAdapter viewPagerAdapter;
    public static Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        tableLayout=findViewById(R.id.table_Layout);
        viewPager=findViewById(R.id.view_Pager);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());

        View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        viewPagerAdapter.addFragment(new SongsFragment(),"Songs");
        viewPagerAdapter.addFragment(new AlbumsFragment(),"Albums");

        viewPager.setAdapter(viewPagerAdapter);
        tableLayout.setupWithViewPager(viewPager);
        notificationManager = NotificationManagerCompat.from(this);

        mediaSession = new MediaSessionCompat(this, "tag");
        instance=this;

        if(ContextCompat.checkSelfPermission(MusicActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
            requestStoragePermission();
        }else {
         start();

        }

        playerFrag = new PlayerFrag();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_a, playerFrag)
                .commit();

    }
    public static MusicActivity getInstance() {
        return instance;
    }

    private void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this).setTitle("Permission Needed").setMessage("Need to read songs from your storage").setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(MusicActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},Storage_Permission_code);

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                }
            }).create().show();

        }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},Storage_Permission_code);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==Storage_Permission_code){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
            start();
            }else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();

            }
        }
    }
    private void start(){
        dataReading=new DataReading(this);
        songs=new ArrayList<>();
        songs.add(new Song());
        ArrayList<Song> songs = dataReading.getAllAudioFromDevice();
        Collections.sort(songs);
        SongAdapter.songs=songs;
        albums=dataReading.getAlbums();
        shift();


    }
    public void shift(){
        Iterator it = albums.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            al.add((ArrayList<Song>) pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);

        MenuItem searchItem=menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               SongsFragment.search(newText);
                return false;
            }
        });
        return true;
    }

    public void sendOnChannel(String name,String artist,int position) {

        Intent activityIntent = new Intent(this, MusicActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        int plaorpa;
        if(PlayerActivity.playin){
            plaorpa=R.drawable.pause_24dp;
        }else{
            plaorpa=R.drawable.play_arrow_24dp;
        }
        metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(songs.get(position).getPath());
        arts= metadataRetriever.getEmbeddedPicture();
        Bitmap artwork;
        try {
            artwork=BitmapFactory.decodeByteArray(arts,0,arts.length);
        }catch (Exception e){
            artwork = BitmapFactory.decodeResource(getResources(), R.drawable.track_2);
        }
         notification = new NotificationCompat.Builder(this, channel_1_ID)
                .setSmallIcon(R.drawable.music_note_24dp)
                .setContentTitle(name)
                .setContentText("Song")
                .setLargeIcon(artwork)
                .addAction(R.drawable.previous_24dp, "Previous", playbackAction(3))
                .addAction(plaorpa, "Pause", playbackAction(1))
                .addAction(R.drawable.next_24dp, "Next", playbackAction(2))
                .setContentIntent(contentIntent)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSession.getSessionToken()))
                .setSubText(artist)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();


        notificationManager.notify(1, notification);
    }


    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, NotiService.class);
        switch (actionNumber) {
            case 1:
                // Pause
                playbackAction.setAction("com.mypackage.ACTION_PAUSE_MUSIC");
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction("com.mypackage.ACTION_NEXT_MUSIC");
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                // Previous track
                playbackAction.setAction("com.mypackage.ACTION_PREV_MUSIC");
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }


    @Override
    public void onInputASent(int position_, int val_, boolean place_) {
        playerFrag.startMusic(position_,val_,place_);
    }

    public static void play_mm(int position_, int val_, boolean place_){
        playerFrag.startMusic(position_,val_,place_);
    }
}
