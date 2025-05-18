package com.example.rewear_app1;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "ReWearUserSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.apply(); // Pastikan perubahan disimpan
    }

    public void createLoginSession(int userId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.commit(); // Gunakan commit() bukan apply() untuk memastikan langsung tersimpan
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    // Tambahkan pengecekan validitas session
    public boolean isValidSession() {
        return isLoggedIn() && getUserId() != -1;
    }
}