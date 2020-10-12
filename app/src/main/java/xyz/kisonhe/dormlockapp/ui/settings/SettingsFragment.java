package xyz.kisonhe.dormlockapp.ui.settings;


import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import xyz.kisonhe.dormlockapp.ChangePasswordActivity;
import xyz.kisonhe.dormlockapp.MainActivity;
import xyz.kisonhe.dormlockapp.R;




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

    }
}
