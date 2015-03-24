package ru.mobigear.mobigearinterview.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by eugene on 3/24/15.
 */
public class ContentSyncService extends Service {
    private static final String TAG = ContentSyncService.class.getSimpleName();
    private static final Object sSyncAdapterLock = new Object();
    private static ContentSyncAdapter syncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        synchronized (sSyncAdapterLock) {
            if (syncAdapter == null) {
                syncAdapter = new ContentSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}

