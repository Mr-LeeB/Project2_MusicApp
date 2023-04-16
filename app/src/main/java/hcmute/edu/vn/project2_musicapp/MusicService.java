package hcmute.edu.vn.project2_musicapp;

import static hcmute.edu.vn.project2_musicapp.MusicApplication.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;

public class MusicService extends Service {

    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_RESUME = 2;
    public static final int ACTION_CLEAR = 3;
    public static final int ACTION_START = 4;
    private MediaPlayer mediaPlayer;
    private boolean isplaying;
    private Song mSong;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Le Quang Bao", "MyService onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            Song song = (Song) bundle.get("object_song");

            if(song != null){
                mSong = song;
                startMusic(song);
                sendNotification(song);
            }
        }

        int actionMusic = intent.getIntExtra("action_music_service",0);
        handleActionMusic(actionMusic);

        return START_NOT_STICKY;
    }

    private void startMusic(Song song) {
        if(mediaPlayer == null) {
            try{
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(song.getResouce());
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                mediaPlayer.prepare();

            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        isplaying = true;
        sendActionToActivity(ACTION_START);
    }

    private void handleActionMusic(int action){
        switch (action){
            case ACTION_PAUSE:
                pauseMusic();
                break;
            case ACTION_RESUME:
                resumeMusic();
                break;
            case ACTION_CLEAR:
                stopSelf();
                sendActionToActivity(ACTION_CLEAR);
                break;
        }
    }

    private void pauseMusic(){
        if(mediaPlayer != null && isplaying){
            mediaPlayer.pause();
            isplaying = false;
            sendNotification(mSong);
            sendActionToActivity(ACTION_PAUSE);
        }
    }

    private void resumeMusic(){
        if(mediaPlayer != null && !isplaying){
            mediaPlayer.start();
            isplaying = true;
            sendNotification(mSong);
            sendActionToActivity(ACTION_RESUME);
        }
    }
    private void sendNotification(Song song) {
        Intent intent = new Intent(this, MusicService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_music);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_custom_notification);
        remoteViews.setTextViewText(R.id.txt_nameMusic, song.getNameMusic());
        remoteViews.setTextViewText(R.id.txt_nameSinger, song.getNameSinger());
        remoteViews.setImageViewBitmap(R.id.img_song, bitmap);

        remoteViews.setImageViewResource(R.id.img_play_or_pause, R.drawable.ic_pause);

        if(isplaying) {
            remoteViews.setOnClickPendingIntent(R.id.img_play_or_pause, getPendingIntent(this, ACTION_PAUSE));
            remoteViews.setImageViewResource(R.id.img_play_or_pause, R.drawable.ic_pause);
        } else {
            remoteViews.setOnClickPendingIntent(R.id.img_play_or_pause, getPendingIntent(this, ACTION_RESUME));
            remoteViews.setImageViewResource(R.id.img_play_or_pause, R.drawable.icon_play);
        }

        remoteViews.setOnClickPendingIntent(R.id.img_clear, getPendingIntent(this, ACTION_CLEAR));

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .setSound(null)
                .build();

        startForeground(1, notification);
    }

    private PendingIntent getPendingIntent(Context context, int action){
        Intent intent = new Intent(this, MyReceiver.class);
        intent.putExtra("action_music", action);
        return PendingIntent.getBroadcast(context.getApplicationContext(),action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Le Quang Bao", "MyService onDestroy");
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void sendActionToActivity(int action){
        Intent intent = new Intent("send_data_to_activity");
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song", mSong);
        bundle.putBoolean("status_player", isplaying);
        bundle.putInt("action_music", action);

        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }
}