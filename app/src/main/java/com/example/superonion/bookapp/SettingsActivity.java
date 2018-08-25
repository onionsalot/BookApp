package com.example.superonion.bookapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import static android.content.ContentValues.TAG;


public class SettingsActivity extends PreferenceActivity{
    public static final int RESULT_CODE_THEME_UPDATED = 1;
    int resultCode = 0;
    String themeListText;
    String resultListText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        themeChooser(pref.getString("theme","1"));
        resultListText= pref.getString("resultsReturned","1");
        super.onCreate(savedInstanceState);
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null)
        {
            // Restore value of members from saved state
            resultCode = savedInstanceState.getInt("result");
            setResult(resultCode);
            Log.d(TAG, "onCreate: Instance has been recreated with resultCode = " + resultCode);
        }
        else
        {
            // Probably initialize members with default values for a new instance
        }

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addPreferencesFromResource(R.xml.app_preferences);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String theme = SP.getString("theme", "1");



        ListPreference themeListener = (ListPreference) findPreference("theme");
        themeListener.setTitle(themeListText);
        themeListener.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                resultCode = RESULT_CODE_THEME_UPDATED;
                recreate();
                return true;
            }
        });



        final CheckBoxPreference bookListener = (CheckBoxPreference) findPreference("showBooks");
        bookListener.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean mags = SP.getBoolean("showMags", true);
                Log.d(TAG, "onPreferenceClick: " +mags);
                if (!mags) {
                    Log.d(TAG, "Books clicked. Seems mags is unchecked. One of the two MUST be checked.");
                    bookListener.setChecked(true);
                }
                return false;
            }
        });
        final CheckBoxPreference magListener = (CheckBoxPreference) findPreference("showMags");
        magListener.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean books = SP.getBoolean("showBooks", true);
                Log.d(TAG, "onPreferenceClick: " +books);
                if (!books) {
                    Log.d(TAG, "Mags clicked. Seems books is unchecked. One of the two MUST be checked.");
                    magListener.setChecked(true);
                }
                return false;
            }
        });

        final ListPreference resultsListener= (ListPreference) findPreference("resultsReturned");
        resultsListener.setTitle("Results per search (" +resultListText+"0)");
        resultsListener.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            SharedPreferences.Editor editor;
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                 Log.d(TAG, "onPreferenceChange: resultsReturned " + newValue);
                switch (newValue.toString()) {
                    case "1":
                        resultsListener.setTitle("Results per search (10)");
                        break;
                    case "2":
                        resultsListener.setTitle("Results per search (20)");
                        break;
                    case "3":
                        resultsListener.setTitle("Results per search (30)");
                        break;
                    case "4":
                        resultsListener.setTitle("Results per search (40)");
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onSaveInstanceState: called with resultCode = " +resultCode);
        // Save the user's current game state
        savedInstanceState.putInt("result", resultCode);
        setResult(resultCode);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void themeChooser(String theme) {
        switch (theme) {
            case "1":
                setTheme(R.style.AppTheme);
                themeListText = "Eggplant (default)";
                break;
            case "2":
                setTheme(R.style.AppTheme_Earth);
                themeListText = "Earthy";
                break;
            case "3":
                setTheme(R.style.AppTheme_3);
                themeListText = "CottonCandy";
                break;

        }
    }
}
