package com.gestureai.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gestureai.app.utils.PasswordUtils;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gestureai.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String usersTable =
                "CREATE TABLE users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "email TEXT UNIQUE NOT NULL," +
                        "password_hash TEXT NOT NULL," +
                        "salt TEXT NOT NULL)";

        String historyTable =
                "CREATE TABLE history (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER NOT NULL," +
                        "gesture TEXT NOT NULL," +
                        "confidence REAL NOT NULL," +
                        "timestamp INTEGER NOT NULL," +
                        "FOREIGN KEY(user_id) REFERENCES users(id))";

        db.execSQL(usersTable);
        db.execSQL(historyTable);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {
        db.execSQL("DROP TABLE IF EXISTS history");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    // Register a user. Returns false if the email already exists.
    public boolean registerUser(
            String name,
            String email,
            String password
    ) {

        String salt = PasswordUtils.generateSalt();
        String hash = PasswordUtils.hashPassword(password, salt);

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email.toLowerCase().trim());
        values.put("password_hash", hash);
        values.put("salt", salt);

        SQLiteDatabase db = getWritableDatabase();

        long result = db.insertWithOnConflict(
                "users",
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );

        return result != -1;
    }

    // Verify login details and return the user's database ID.
    public int loginUser(String email, String password) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                "users",
                new String[]{"id", "password_hash", "salt"},
                "email = ?",
                new String[]{email.toLowerCase().trim()},
                null,
                null,
                null
        );

        try {
            if (cursor.moveToFirst()) {

                int id = cursor.getInt(0);
                String savedHash = cursor.getString(1);
                String salt = cursor.getString(2);

                if (PasswordUtils.verifyPassword(
                        password, salt, savedHash)) {
                    return id;
                }
            }

            return -1;

        } finally {
            cursor.close();
        }
    }

    public String getUserName(int userId) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                "users",
                new String[]{"name"},
                "id = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                null
        );

        try {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }

            return "User";

        } finally {
            cursor.close();
        }
    }

    // Save a prediction for the currently logged-in user.
    public boolean addHistory(
            int userId,
            String gesture,
            float confidence
    ) {

        ContentValues values = new ContentValues();

        values.put("user_id", userId);
        values.put("gesture", gesture);
        values.put("confidence", confidence);
        values.put("timestamp", System.currentTimeMillis());

        return getWritableDatabase().insert(
                "history", null, values
        ) != -1;
    }

    // Retrieve only the predictions belonging to one user.
    public ArrayList<HistoryItem> getHistory(int userId) {

        ArrayList<HistoryItem> items = new ArrayList<>();

        Cursor cursor = getReadableDatabase().query(
                "history",
                new String[]{
                        "gesture",
                        "confidence",
                        "timestamp"
                },
                "user_id = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                "timestamp DESC"
        );

        try {
            while (cursor.moveToNext()) {

                items.add(new HistoryItem(
                        cursor.getString(0),
                        cursor.getFloat(1),
                        cursor.getLong(2)
                ));
            }

        } finally {
            cursor.close();
        }

        return items;
    }

    public void clearHistory(int userId) {

        getWritableDatabase().delete(
                "history",
                "user_id = ?",
                new String[]{String.valueOf(userId)}
        );
    }

    // Simple Java object representing one database history row.
    public static class HistoryItem {

        public String gesture;
        public float confidence;
        public long timestamp;

        public HistoryItem(
                String gesture,
                float confidence,
                long timestamp
        ) {
            this.gesture = gesture;
            this.confidence = confidence;
            this.timestamp = timestamp;
        }
    }
}