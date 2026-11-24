package com.gestureai.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gestureai.app.adapters.HistoryAdapter;
import com.gestureai.app.database.DatabaseHelper;
import com.gestureai.app.utils.SessionManager;

public class HistoryActivity extends AppCompatActivity {

    private DatabaseHelper database;
    private SessionManager session;

    private RecyclerView recyclerView;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        database = new DatabaseHelper(this);
        session = new SessionManager(this);

        recyclerView = findViewById(R.id.historyRecycler);
        emptyText = findViewById(R.id.emptyText);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        Button clearButton = findViewById(R.id.clearButton);

        clearButton.setOnClickListener(v -> {

            database.clearHistory(session.getUserId());

            loadHistory();

            Toast.makeText(
                    this,
                    "History cleared",
                    Toast.LENGTH_SHORT
            ).show();
        });

        loadHistory();
    }

    private void loadHistory() {

        java.util.ArrayList<DatabaseHelper.HistoryItem> items =
                database.getHistory(session.getUserId());

        recyclerView.setAdapter(new HistoryAdapter(items));

        emptyText.setVisibility(
                items.isEmpty() ? TextView.VISIBLE : TextView.GONE
        );

        recyclerView.setVisibility(
                items.isEmpty() ? RecyclerView.GONE : RecyclerView.VISIBLE
        );
    }
}