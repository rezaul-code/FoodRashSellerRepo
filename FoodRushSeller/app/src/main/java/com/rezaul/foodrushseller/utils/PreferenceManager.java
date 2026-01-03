package com.rezaul.foodrushseller.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private SharedPreferences preferences;

    public PreferenceManager(Context context) {
        preferences = context.getSharedPreferences(
                Constants.PREF_NAME,
                Context.MODE_PRIVATE
        );
    }

    // ================= AUTH =================

    public void saveLogin(String token, String role) {
        preferences.edit()
                .putString(Constants.KEY_TOKEN, token)
                .putString(Constants.KEY_ROLE, role)
                .putBoolean(Constants.KEY_LOGGED_IN, true)
                .apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(Constants.KEY_LOGGED_IN, false);
    }

    public String getToken() {
        return preferences.getString(Constants.KEY_TOKEN, null);
    }

    public String getRole() {
        return preferences.getString(Constants.KEY_ROLE, null);
    }

    public void logout() {
        preferences.edit().clear().apply();
    }
}
