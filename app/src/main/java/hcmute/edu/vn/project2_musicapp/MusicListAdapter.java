package hcmute.edu.vn.project2_musicapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicViewHolder> {

    private final List<Song> mListSongs;
    private Context context;
    public MusicListAdapter(Context context, List<Song> mListSongs) {
        this.context = context;
        this.mListSongs = mListSongs;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music,parent,false);

        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        Song song = mListSongs.get(position);
        if(song == null)
            return;
        holder.numMusic.setText(String.valueOf(song.getId()));
        holder.nameMusicInList.setText(song.getNameMusic());
        holder.nameSingerInList.setText(song.getNameSinger());

        holder.itemSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickToSendData(song);
            }
        });
    }
    private void onClickToSendData(Song song) {
        Intent intent = new Intent(context, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("song_selected_from_list", song);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
    @Override
    public int getItemCount() {
        if(mListSongs!=null)
            return mListSongs.size();
        return 0;
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder{

        private TextView numMusic;
        private TextView nameMusicInList;
        private TextView nameSingerInList;
        private LinearLayout itemSong;


        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            numMusic = itemView.findViewById(R.id.txt_numMusic);
            itemSong = itemView.findViewById(R.id.item_song);
            nameMusicInList = itemView.findViewById(R.id.txt_nameMusicInList);
            nameSingerInList = itemView.findViewById(R.id.txt_nameSingerInList);
        }
    }
}
