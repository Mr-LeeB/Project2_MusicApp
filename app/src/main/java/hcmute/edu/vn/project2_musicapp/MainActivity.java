package hcmute.edu.vn.project2_musicapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageView imgSong, imgPlayOrPause, imgClear, imglistMusic, imgnext, imgprevious;
    private TextView tvTitleSong, tvSingleSong;

    private Song mSong;
    private Boolean isPlaying;

    private List<Song> mListSong;
    private int action = -1;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle == null){
                return;
            }
            mSong = (Song) bundle.get("object_song");
            isPlaying = bundle.getBoolean("status_player");
            int actionMusic = bundle.getInt("action_music");
            handleLayoutMusic(actionMusic);
        }
    };

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("ĐAng được chạy ở đây");

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity"));

        imgSong = findViewById(R.id.img_singer);
        setImgSinger("https://firebasestorage.googleapis.com/v0/b/fa-faw.appspot.com/o/music.jpg?alt=media&token=5a238469-7272-4dbf-bb22-9269ad62862e");

        imgPlayOrPause = findViewById(R.id.img_play_or_pause);
        imgClear = findViewById(R.id.img_clear);
        imglistMusic = findViewById(R.id.img_listMusic);

        mListSong = new ArrayList<>();

        imgnext = findViewById(R.id.img_next);
        imgprevious = findViewById(R.id.img_previous);

        tvTitleSong = findViewById(R.id.txt_nameMusic);
        tvSingleSong = findViewById(R.id.txt_nameSinger);

        getListSongFromRealTimeDatabase();

        Song song = playMusicSelectedFromList();

        if (song != null) {
            System.out.println("Stop service");
            clickStopService();
            clickStartService(song);
        }
        openListMusic();
        imgPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickStartService(song);
            }
        });
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Get List Song Faild", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Song playMusicSelectedFromList(){
        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            return null;
        return (Song) bundle.get("song_selected_from_list");
    }

    private void clickStartService(Song song){
        if (song == null) {
            Song selectSong = mListSong.get(0);
            song = new Song(selectSong.getId(),
                    selectSong.getNameMusic(),
                    selectSong.getNameSinger(),
                    selectSong.getImage(),
                    selectSong.getResouce());
        }
        Intent intent = new Intent(this, MusicService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song", song);
        intent.putExtras(bundle);

        mSong = song;

        startService(intent);

    }

    private void clickStopService(){
        Intent intent = new Intent(this, MusicService.class);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void handleLayoutMusic(int action){
        switch (action){
            case MusicService.ACTION_START:
                showInfoSong();
                setStatusButtonPlayOrPause();
                break;
            case MusicService.ACTION_PAUSE:
                setStatusButtonPlayOrPause();
                break;
            case MusicService.ACTION_RESUME:
                setStatusButtonPlayOrPause();
                break;
            case MusicService.ACTION_CLEAR:
                this.action = action;
                setStatusButtonPlayOrPause();
                break;
        }
    }

    private void setImgSinger(String imageResource){
        Glide.with(this)
                .load(imageResource)
                .circleCrop()
                .placeholder(R.drawable.img_music)
                .error(R.mipmap.ic_launcher)
                .into(imgSong);
    }
    private void showInfoSong(){
        if(mSong == null)
            return;
        setImgSinger(mSong.getImage());
        tvTitleSong.setText(mSong.getNameMusic());
        tvSingleSong.setText(mSong.getNameSinger());

        imgPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying){
                    sendActionToService(MusicService.ACTION_PAUSE);
                } else{
                    sendActionToService(MusicService.ACTION_RESUME);
                }
                if (action == MusicService.ACTION_CLEAR) {
                    clickStartService(mSong);
                    action = -1;
                }
            }
        });

        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPlayOrPause.setImageResource(R.drawable.ic_play_main);
//                mSong = null;
//                isPlaying = false;
                sendActionToService(MusicService.ACTION_CLEAR);
                clickStopService();
            }
        });

        openListMusic();
        imgnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSong != null) {
                    int id = mSong.getId();
                    if (id == mListSong.size())
                        id = 0;
                    mSong = mListSong.get(id);
                    clickStopService();
                    clickStartService(mSong);
                }
            }
        });

        imgprevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSong != null) {
                    int id = mSong.getId() - 2;
                    if (id < 0)
                        id = mListSong.size() - 1;
                    mSong = mListSong.get(id);
                    clickStopService();
                    clickStartService(mSong);
                }
            }
        });
    }

    private void openListMusic() {
        imglistMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityListMusic.class);
                Bundle bundle = new Bundle();

                if (mSong != null) {
                    intent.putExtra("nameMusic", mSong.getNameMusic());
                    intent.putExtra("nameSinger", mSong.getNameSinger());
                }
                launcher.launch(intent);
            }
        });
    }

    private void setStatusButtonPlayOrPause(){
        if(isPlaying) {
            imgPlayOrPause.setImageResource(R.drawable.ic_pause_main);
        } else {
            imgPlayOrPause.setImageResource(R.drawable.ic_play_main);
        }
    }

    private void sendActionToService(int action){
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("action_music_service", action);

        startService(intent);
    }
}