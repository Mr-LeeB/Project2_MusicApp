package hcmute.edu.vn.project2_musicapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MusicApplication extends Service {
    public MusicApplication() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}