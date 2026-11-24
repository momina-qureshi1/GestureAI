package com.gestureai.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gestureai.app.R;
import com.gestureai.app.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryAdapter
        extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final ArrayList<DatabaseHelper.HistoryItem> items;

    public HistoryAdapter(
            ArrayList<DatabaseHelper.HistoryItem> items
    ) {
        this.items = items;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull HistoryViewHolder holder,
            int position
    ) {

        DatabaseHelper.HistoryItem item = items.get(position);

        holder.gesture.setText(item.gesture);

        holder.confidence.setText(
                String.format(
                        Locale.getDefault(),
                        "%.2f%% confidence",
                        item.confidence
                )
        );

        SimpleDateFormat format = new SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.getDefault()
        );

        holder.date.setText(
                format.format(new Date(item.timestamp))
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView gesture, confidence, date;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            gesture = itemView.findViewById(R.id.historyGesture);
            confidence = itemView.findViewById(R.id.historyConfidence);
            date = itemView.findViewById(R.id.historyDate);
        }
    }
}