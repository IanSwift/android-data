package io.swift.kata.androiddata.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class NameSyncService extends Service {

    private Object lock = new Object();
    private NameSyncAdapter nameSyncAdapter;

    @Override
    public void onCreate() {
        super.onCreate();

        synchronized (lock) {
            if (nameSyncAdapter == null) {
                nameSyncAdapter = new NameSyncAdapter(getApplicationContext(), true, false);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return nameSyncAdapter.getSyncAdapterBinder();
    }
}
