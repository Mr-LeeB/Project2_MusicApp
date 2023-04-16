package hcmute.edu.vn.project2_musicapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {
    private ImageView imgSong, imgPlayOrPause, imgClear, imglistMusic, imgnext, imgprevious;
    private TextView tvTitleSong, tvSingleSong;

    private Song mSong;
    private Boolean isPlaying;

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

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity"));

        imgSong = findViewById(R.id.img_singer);
        imgPlayOrPause = findViewById(R.id.img_play_or_pause);
        imgClear = findViewById(R.id.img_clear);
        imglistMusic = findViewById(R.id.img_listMusic);

        imgnext = findViewById(R.id.img_next);
        imgprevious = findViewById(R.id.img_previous);

        tvTitleSong = findViewById(R.id.txt_nameMusic);
        tvSingleSong = findViewById(R.id.txt_nameSinger);

        Song song = playMusicSelectedFromList();

        if (song != null) {
            System.out.println("Stop service");
            clickStopService();
        }


        imgnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imgprevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        clickStartService(song);
    }

    private Song playMusicSelectedFromList(){
        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            return null;
        return (Song) bundle.get("song_selected_from_list");
    }

    private void clickStartService(Song song){
        if (song == null) {
            song = new Song(1,"Cuối Cùng Thì", "Jack", "https://images.unsplash.com/photo-1575936123452-b67c3203c357?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8aW1hZ2V8ZW58MHx8MHx8&w=1000&q=80", "https://firebasestorage.googleapis.com/v0/b/fa-faw.appspot.com/o/Waiting%20For%20You.mp3?alt=media&token=01d1cd94-0654-4b23-b700-c0b6ad69e107");
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
            }
        });

        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPlayOrPause.setImageResource(R.drawable.ic_play_main);
                sendActionToService(MusicService.ACTION_CLEAR);
            }
        });

        imglistMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityListMusic.class);
                intent.putExtra("nameMusic",mSong.getNameMusic());
                intent.putExtra("nameSinger", mSong.getNameSinger());

                System.out.println(mSong.getNameMusic());
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