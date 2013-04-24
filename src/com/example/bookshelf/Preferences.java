package com.example.bookshelf;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Preferences extends PreferenceActivity {

    static final String PREFS_NAME = "defaults";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPrefs(this);
        getPreferenceManager().setSharedPreferencesName(PREFS_NAME);
        addPreferencesFromResource(R.xml.preferences);
    }

    static SharedPreferences getPrefs(Context context) {
        PreferenceManager.setDefaultValues(context, PREFS_NAME, MODE_PRIVATE,
                R.xml.preferences, false);
        return context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }
}
