package com.example.mediaplayer.adapters;

import android.content.ContentUris;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.mediaplayer.R;
import com.example.mediaplayer.models.Song;
import com.example.mediaplayer.activities.MusicActivity;


import interfaces.OnClickListen;

public class AlbumAdapter  extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> implements Filterable {
    private OnClickListen alclicklisten;
//for search
    //ArrayList<ArrayList<Song>>filtered=new ArrayList<>();

    private  LayoutInflater inflater;

    public AlbumAdapter(OnClickListen alclicklisten) {
        this.alclicklisten = alclicklisten;
        inflater=(LayoutInflater) MusicActivity.getInstance().getSystemService(MusicActivity.getInstance().LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_album_layout,viewGroup,false);
        return new ViewHolder(view,alclicklisten);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Song song= MusicActivity.al.get(i).get(0);
        viewHolder.album.setText(song.getAlbum());
        viewHolder.artist.setText(song.getArtist());
        Glide
                .with(MusicActivity.getInstance())
                .load(ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),song.getAlbumID()).toString())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.xd)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                )
                .thumbnail(0.1f)
                .transition(new DrawableTransitionOptions()
                        .crossFade()
                )
                .into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return MusicActivity.al.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
       private TextView album,artist;
      private   ImageView imageView;
       private OnClickListen onClickListen;
        public ViewHolder(@NonNull View itemView,OnClickListen onClickListen) {
            super(itemView);
            album=itemView.findViewById(R.id.album_name);
            artist=itemView.findViewById(R.id.album_artist);
            imageView=itemView.findViewById(R.id.album_image);
            this.onClickListen=onClickListen;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListen.onClick(getAdapterPosition());
        }
    }
   /* private Filter  filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint == null || constraint.length() == 0) {
                filtered = MainActivity.al;
            } else {
                String filterpattern = constraint.toString().toLowerCase().trim();
                for (int i=0;i<filtered.size(); i++) {
                    if ((Song)filtered[i][0].getName().toLowerCase().startsWith(filterpattern) || oneSong.getArtist().toLowerCase().startsWith(filterpattern)) {
                        filteredList.add(oneSong);
                    }
                }

                return null;
            }

            @Override
            protected void publishResults (CharSequence constraint, FilterResults results){

            }
        }
    }*/


}
