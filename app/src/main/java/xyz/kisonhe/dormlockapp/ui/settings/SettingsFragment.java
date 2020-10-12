package xyz.kisonhe.dormlockapp.ui.settings;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import xyz.kisonhe.dormlockapp.ChangePasswordActivity;
import xyz.kisonhe.dormlockapp.R;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;


public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.main_preferences, rootKey);


        Preference ChangePasswordPreference = findPreference("ChangePasswordPreference");
        ChangePasswordPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(requireActivity(), ChangePasswordActivity.class);
                startActivity(intent);

                return false;
            }
        });

        Preference DarkModePreference = findPreference("DarkModeConfig");
        DarkModePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.equals("MODE_NIGHT_FOLLOW_SYSTEM")) {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM);
                    return true;
                }
                if (newValue.equals("MODE_NIGHT_NO")) {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
                    return true;
                }
                if (newValue.equals("MODE_NIGHT_YES")) {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
                    return true;
                }
                return false;
            }
        });

    }
}
