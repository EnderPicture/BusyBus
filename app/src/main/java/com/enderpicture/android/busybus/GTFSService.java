package com.enderpicture.android.busybus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GTFSService extends Service {
    public GTFSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
