package hcmute.edu.vn.project2_musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActivityListMusic extends AppCompatActivity {

    private ImageView imgBack;
    private TextView musicName, singerName;
    private RecyclerView recyclerView;
    private MusicListAdapter musicListAdapter;
    private List<Song> mListSong;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_music);

        setTitle("List Music");

        imgBack = findViewById(R.id.img_back);

        setInfoIsPlaying();

        setListSongFromFirebase();

        getListSongFromRealTimeDatabase();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void setInfoIsPlaying(){
        musicName = findViewById(R.id.txt_nameMusic);
        singerName = findViewById(R.id.txt_nameSinger);

        Intent intent = getIntent();
        musicName.setText(intent.getStringExtra("nameMusic"));
        singerName.setText(intent.getStringExtra("nameSinger"));
    }

    private void setListSongFromFirebase(){
        recyclerView = findViewById(R.id.list_item_music);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        mListSong = new ArrayList<>();
        musicListAdapter = new MusicListAdapter(this, mListSong);

        recyclerView.setAdapter(musicListAdapter);
    }

    private void getListSongFromRealTimeDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("List-Songs");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (mListSong != null)
                    mListSong.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Song song = dataSnapshot.getValue(Song.class);
                    mListSong.add(song);
                    System.out.println("here is song: " + song);
                }

                musicListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ActivityListMusic.this, "Get List Song Faild", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
