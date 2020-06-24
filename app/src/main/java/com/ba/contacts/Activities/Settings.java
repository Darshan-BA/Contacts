package com.ba.contacts.Activities;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.ba.contacts.Fragments.SettingFragment;
import com.ba.contacts.R;
import com.ba.contacts.SettingsSharedPref;

public class Settings extends AppCompatActivity {
    private Toolbar toolbar;
    private SharedPreferences settingPreference;
    private SharedPreferences.OnSharedPreferenceChangeListener settingPreferenceListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //theme
        if (SettingsSharedPref.getInstance().getTheme().equals("0"))
            setTheme(R.style.darkTheme);
        else
            setTheme(R.style.lightTheme);
        setContentView(R.layout.activity_settings);

        // shared preference listener to detect changes
        settingPreference = PreferenceManager.getDefaultSharedPreferences(this);
        settingPreferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("theme")) {
                    restart();
                }

            }
        };

        //calling setting preference listener
        getSupportFragmentManager().beginTransaction().replace(R.id.settings_container, new SettingFragment()).commit();

        //toolbar
        toolbar = findViewById(R.id.toolbar_settings_activity);
        toolbar.setTitle("Settings");
        toolbar.setNavigationIcon(R.drawable.icon_baseline_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }//end of onCreate

    @Override
    protected void onResume() {
        super.onResume();
        //registering for sharedPreference for settings changes
        settingPreference.registerOnSharedPreferenceChangeListener(settingPreferenceListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregistering for sharedPreference for settings changes
        settingPreference.unregisterOnSharedPreferenceChangeListener(settingPreferenceListener);
    }

    // recreating activities when there is change in theme preference
    public void restart() {
        Intent settingIntent = new Intent(this, Settings.class);
        settingIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        TaskStackBuilder.create(this).addNextIntent(mainIntent).addNextIntent(settingIntent).startActivities();
    }

    //on back press
    @Override
    public void onBackPressed() {

            super.onBackPressed();

    }//end of back press

}//end of activity