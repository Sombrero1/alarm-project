package com.example.mediaplayer.activities;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.mediaplayer.adapters.SongAdapter;
import com.example.mediaplayer.adapters.SongAlbumAdapter;
import com.example.mediaplayer.models.Song;
import com.example.mediaplayer.notification.NofiticationCenter;
import com.example.mediaplayer.R;
import java.util.ArrayList;
import java.util.Collections;
public class PlayerFrag extends Fragment {

    public static boolean playin;
    private ArrayList<Song>Asongs;
    private static SeekBar mSeekBar;
    private static PlayerFrag instance;
    int position;
    private static TextView curTime,totTime;
    private TextView songTitle,artistname;
    private static ImageView pause,prev,next;
    private ImageView imageView;
    protected int val;
    protected boolean place;
    private Palette.Swatch DarkVibrantSwatch;
    private Palette.Swatch darkMutedSwatch;
    private Context listener;


    protected NofiticationCenter nofiticationCenter;
    protected LinearLayout linearLayout,linear1;
    private static MediaPlayer mMediaPlayer;

    public int getPosition() {
        return position;
    }


    public void setPosition(int position) {
        this.position = position;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_player, container, false);

        nofiticationCenter = new NofiticationCenter();
        mSeekBar = v.findViewById(R.id.seek);
        songTitle = v.findViewById(R.id.song_name);
        artistname = v.findViewById(R.id.artist_name);
        totTime = v.findViewById(R.id.total_time);
        pause = v.findViewById(R.id.pause);
        linearLayout = v.findViewById(R.id.linear_layout);
        prev = v.findViewById(R.id.previous);
        next = v.findViewById(R.id.next);
        curTime = v.findViewById(R.id.current_time);
        imageView = v.findViewById(R.id.imageplayer);
        linear1 = v.findViewById(R.id.linear1);
        Buttons();

        return v;
    }

    public interface FragmentAListener {
        void onInputASent(int position_, int val_, boolean place_);
    }

    public void startMusic(int position_, int val_, boolean place_){
        position = position_;
        val = val_;
        place = place_;
        if(instance!=null&&place==false){
            songTitle.setText(instance.songTitle.getText());
            artistname.setText(instance.artistname.getText());
            totTime.setText(instance.totTime.getText());
            curTime.setText(instance.curTime.getText());
            imageView.setImageDrawable(instance.imageView.getDrawable());
            initiateSeekBar();
            Asongs=instance.Asongs;
            position=instance.position;
            setBackground();
        }else {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
            }
            instance = this;
            if (val == 1) {
                Asongs = SongAlbumAdapter.albumSong;
            } else if (val == 2) {
                Asongs = SongAdapter.songs;
                Collections.shuffle(Asongs);
            } else {
                Asongs = SongAdapter.songs;
            }
            if (Asongs.get(position).getName() == "shufflee")
                position = (position + 1) % Asongs.size();
            initPlayer(position);
        }

        Buttons();
    }

    public static PlayerFrag getInstance() {
        return instance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
            listener = context;
    }

    public void setData(int position){

        String name= Asongs.get(position).getName();
        String artist= Asongs.get(position).getArtist();
        songTitle.setText(name);
        artistname.setText(artist);
        try {

            Glide
                    .with(listener)
                    .load(ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),Asongs.get(position).getAlbumID()).toString())
                    .thumbnail(0.2f)
                    .centerCrop()
                    .placeholder(R.drawable.track)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            setBackground();
                            return false;
                        }
                    })
                    .into(imageView);
        }
        catch (Exception e){

            Glide.with(this).load(R.drawable.track).into(imageView);
        }


    }

    public static void initiateSeekBar(){
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress*1000);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public  void Buttons(){
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position == 0) {
                    position = Asongs.size() - 1;
                } else {
                    position--;
                }
                initPlayer(position);

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position=(position+1)% Asongs.size();
                initPlayer(position);
            }
        });
    }

    public void initPlayer(final int position) {

        playin=true;
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.reset();
        }
        String name= Asongs.get(position).getName();
        String artist= Asongs.get(position).getArtist();
        setData(position);
        MusicActivity.getInstance().sendOnChannel(name,artist,position);


        mMediaPlayer = MediaPlayer.create(listener, Uri.parse(Asongs.get(position).getPath())); // create and load mediaplayer with song resources
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                String totalTime = createTimeLabel(mMediaPlayer.getDuration());
                totTime.setText(totalTime);
                mSeekBar.setMax(mMediaPlayer.getDuration()/1000 );
                mMediaPlayer.start();
                pause.setBackgroundResource(R.drawable.pause_24dp);

            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                int curSongPoition = position;
                curSongPoition = (curSongPoition + 1) % (Asongs.size());
                initPlayer(curSongPoition);

            }
        });
        initiateSeekBar();


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mMediaPlayer != null) {
                    try {
                        if (mMediaPlayer.isPlaying()) {
                            Message msg = new Message();
                            msg.what = mMediaPlayer.getCurrentPosition();
                            msg.arg1=mMediaPlayer.getDuration();
                            handler.sendMessage(msg);
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int current_position = msg.what;
            mSeekBar.setMax(mMediaPlayer.getDuration()/1000);
            mSeekBar.setProgress(current_position/1000);
            System.out.println(mSeekBar.getProgress());
            String cTime = createTimeLabel(current_position);
            curTime.setText(cTime);
            totTime.setText(createTimeLabel(msg.arg1));
        }
    };
    public void play() {

        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            playin=true;
            mMediaPlayer.start();
            pause.setBackgroundResource(R.drawable.pause_24dp);
            MusicActivity.getInstance().sendOnChannel( Asongs.get(position).getName(), Asongs.get(position).getArtist(),position);
        } else {
            pause();
        }

    }

    public void pause() {
        if (mMediaPlayer.isPlaying()) {
            playin=false;
            mMediaPlayer.pause();
            pause.setBackgroundResource(R.drawable.play_arrow_24dp);
            MusicActivity.getInstance().sendOnChannel( Asongs.get(position).getName(), Asongs.get(position).getArtist(),position);
        }

    }


    public String createTimeLabel(int duration) {
        String timeLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timeLabel += min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;


    }
    public void setBackground(){
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        Palette.from(bitmap).maximumColorCount(32).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                DarkVibrantSwatch = palette.getDarkVibrantSwatch();
                darkMutedSwatch = palette.getDarkMutedSwatch();
                if (DarkVibrantSwatch!= null) {
                    linearLayout.setBackgroundColor( DarkVibrantSwatch.getRgb());
                    linear1.setBackgroundColor( DarkVibrantSwatch.getRgb());

                } else {
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    linear1.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                }
            }
        });
    }

    public static boolean isPlayin() {
        return playin;
    }
}