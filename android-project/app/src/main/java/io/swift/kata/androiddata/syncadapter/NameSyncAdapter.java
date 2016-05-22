package io.swift.kata.androiddata.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

import io.swift.kata.androiddata.contentprovider.ContentProvider;
import io.swift.kata.androiddata.contentprovider.DatabaseHelper;
import io.swift.kata.androiddata.model.Name;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class NameSyncAdapter extends AbstractThreadedSyncAdapter {
    private NameService nameService;

    public NameSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://10.0.2.2:8080")
                .build();
        nameService = restAdapter.create(NameService.class);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        try {
            Cursor cursor = provider.query(Uri.parse("content://"+ ContentProvider.AUTHORITY+"/"+DatabaseHelper.TABLE), null, null, null, null);
            int statusIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS);
            int nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
            int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);

            List<Name> pendingDeletion = new ArrayList<>();
            List<Name> pendingInsertion = new ArrayList<>();
            while (cursor.moveToNext()) {
                int status = cursor.getInt(statusIndex);
                if (!(status == DatabaseHelper.NO_PENDING_ACTION)) {
                    Name name = new Name();
                    name.setName(cursor.getString(nameIndex));
                    name.setId(cursor.getInt(idIndex));
                    if (status == DatabaseHelper.PENDING_DELETION) {
                        pendingDeletion.add(name);
                    } else {
                        pendingInsertion.add(name);
                    }
                }
            }

            for (Name name : pendingDeletion) {
                try {
                    nameService.deleteName(name.getId());
                    provider.delete(Uri.parse("content://"+ ContentProvider.AUTHORITY+"/"+DatabaseHelper.TABLE), DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(name.getId())});
                } catch (RetrofitError e) {
                }
            }

            for (Name name : pendingInsertion) {
                try {
                    nameService.postName(name);
                    ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.COLUMN_STATUS, DatabaseHelper.NO_PENDING_ACTION);
                    provider.update(Uri.parse("content://"+ ContentProvider.AUTHORITY+"/"+DatabaseHelper.TABLE), values, DatabaseHelper.COLUMN_ID+"=?", new String[]{String.valueOf(name.getId())});
                } catch (RetrofitError e) {
                }
            }

        } catch (RemoteException e) {
        }
    }
}
