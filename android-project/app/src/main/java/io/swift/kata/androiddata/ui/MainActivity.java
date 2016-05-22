package io.swift.kata.androiddata.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import io.swift.kata.androiddata.R;
import io.swift.kata.androiddata.contentprovider.ContentProvider;
import io.swift.kata.androiddata.contentprovider.DatabaseHelper;
import io.swift.kata.androiddata.model.Name;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String accountType = "io.swift.kata.androiddata.account";
    public static final String accountName = "myAccount";

    private final List<Name> names = new ArrayList<>();
    private NameRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getLoaderManager().initLoader(0, null, this);


        final Account account = new Account(accountName, accountType);
        AccountManager accountManager = (AccountManager) this.getSystemService(ACCOUNT_SERVICE);
        accountManager.addAccountExplicitly(account, null, null);

        ContentObserver contentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);

                ContentResolver.requestSync(account, ContentProvider.AUTHORITY, Bundle.EMPTY);
            }
        };

        final ContentResolver contentResolver = getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://"+ ContentProvider.AUTHORITY+"/"+DatabaseHelper.TABLE), true, contentObserver);
        ContentResolver.setSyncAutomatically(account, ContentProvider.AUTHORITY, true);
        ContentResolver.addPeriodicSync(account, ContentProvider.AUTHORITY, new Bundle(), 60L);

        findViewById(R.id.insertButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputText = (EditText) findViewById(R.id.nameEditText);
                if (!inputText.getText().toString().isEmpty()) {
                    ContentValues values = new ContentValues();
                    values.put("name", inputText.getText().toString());
                    values.put("status", DatabaseHelper.PENDING_INSERTION);
                    contentResolver.insert(Uri.parse("content://"+ ContentProvider.AUTHORITY+"/"+DatabaseHelper.TABLE), values);
                }
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.nameRecyclerView);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layout);
        adapter = new NameRecyclerViewAdapter(names, this, account);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(this, Uri.parse("content://"+ ContentProvider.AUTHORITY+"/"+DatabaseHelper.TABLE), null, null, null, null);
        cursorLoader.forceLoad();
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        names.clear();
        while (data.moveToNext()) {
            Name name = new Name();
            name.setName(data.getString(data.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
            name.setId(data.getInt(data.getColumnIndex(DatabaseHelper.COLUMN_ID)));
            name.setStatus(data.getInt(data.getColumnIndex(DatabaseHelper.COLUMN_STATUS)));
            names.add(name);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
