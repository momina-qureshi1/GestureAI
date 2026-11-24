package com.gestureai.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "GestureAISession";

    private static final String KEY_LOGIN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );
    }

    public void saveLogin(int userId, String name) {
        preferences.edit()
                .putBoolean(KEY_LOGIN, true)
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_USER_NAME, name)
                .apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_LOGIN, false);
    }

    public int getUserId() {
        return preferences.getInt(KEY_USER_ID, -1);
    }

    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, "User");
    }

    public void logout() {
        preferences.edit().clear().apply();
    }
}