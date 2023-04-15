package hcmute.edu.vn.project2_musicapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MusicApplication extends Application {
    public static final String CHANNEL_ID = "channel_service";

    @Override
    public void onCreate() {
        super.onCreate();
        createChannelNotification();
    }

    private void createChannelNotification() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel chanel = new NotificationChannel(CHANNEL_ID,
                    "Channel Service", NotificationManager.IMPORTANCE_DEFAULT);
            chanel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if(manager != null) {
                manager.createNotificationChannel(chanel);
            }
        }
    }
}