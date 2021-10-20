package com.example.mediaplayer.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mediaplayer.R;
import com.example.mediaplayer.activities.MusicActivity;
import com.example.mediaplayer.activities.PlayerFrag;
import com.example.mediaplayer.adapters.SongAdapter;


import interfaces.OnClickListen;

import static com.example.mediaplayer.adapters.SongAdapter.songs;

public class SongsFragment extends Fragment implements OnClickListen, PlayerFrag.FragmentAListener {
   private View v;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mmanager;
    private static SongAdapter songAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.songs_fragment,container,false);
        recyclerView = v.findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);
        mmanager=new LinearLayoutManager(getContext());
        songAdapter = new SongAdapter(MusicActivity.getInstance(), songs,this);
        recyclerView.setLayoutManager(mmanager);
        recyclerView.setAdapter(songAdapter);
        return v;
    }


    @Override
    public void onClick(int position) {
//        Fragment f = getFragmentManager().findFragmentById(R.id.container_a);
//
//        Intent intent = new Intent(MainActivity.getInstance(), PlayerActivity.class).putExtra("index", position).putExtra("val", 0).putExtra("from",true);
//        startActivity(intent);
//
        MusicActivity.play_mm(position,0,true);
    }
    public static void search(String text){
        songAdapter.getFilter().filter(text);
    }

    @Override
    public void onInputASent(int position_, int val_, boolean place_) {

    }
}
