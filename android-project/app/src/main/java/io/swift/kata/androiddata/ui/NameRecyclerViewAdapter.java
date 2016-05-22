package io.swift.kata.androiddata.ui;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import io.swift.kata.androiddata.R;
import io.swift.kata.androiddata.contentprovider.ContentProvider;
import io.swift.kata.androiddata.contentprovider.DatabaseHelper;
import io.swift.kata.androiddata.model.Name;

public class NameRecyclerViewAdapter extends RecyclerView.Adapter<NameRecyclerViewAdapter.NameRecyclerViewHolder> {
    private List<Name> names;
    private Context context;
    private Account account;

    public NameRecyclerViewAdapter(List<Name> names, Context context, Account account) {
        this.names = names;
        this.context = context;
        this.account = account;
    }

    @Override
    public NameRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.name_view, parent, false);
        return new NameRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NameRecyclerViewHolder holder, final int position) {
        final Name name = names.get(position);

        holder.nameTextView.setText(name.getName());

        if (name.getStatus() == DatabaseHelper.NO_PENDING_ACTION) {
            holder.pendingUpdateView.setVisibility(View.INVISIBLE);
        } else {
            holder.pendingUpdateView.setVisibility(View.VISIBLE);
        }

        final ContentResolver contentResolver = context.getContentResolver();

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("status", DatabaseHelper.PENDING_DELETION);
                contentResolver.update(Uri.parse("content://"+ ContentProvider.AUTHORITY+"/"+DatabaseHelper.TABLE), values, "_ID=?", new String[]{String.valueOf(name.getId())});
                contentResolver.requestSync(account, ContentProvider.AUTHORITY, Bundle.EMPTY);
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class NameRecyclerViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout itemView;
        private final View deleteButton;
        private final TextView nameTextView;
        private final View pendingUpdateView;

        public NameRecyclerViewHolder(View itemView) {
            super(itemView);
            this.itemView = (LinearLayout) itemView;
            deleteButton = itemView.findViewById(R.id.delete_button);
            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            pendingUpdateView = itemView.findViewById(R.id.pending_update_view);
        }
    }
}