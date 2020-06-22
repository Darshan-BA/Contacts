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
    private boolean restartNeed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SettingsSharedPref.getInstance().getTheme().equals("0"))
            setTheme(R.style.darkTheme);
        else
            setTheme(R.style.lightTheme);
        setContentView(R.layout.activity_settings);
        settingPreference = PreferenceManager.getDefaultSharedPreferences(this);
        settingPreferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("theme")) {
                    //TaskStackBuilder.create().addNextIntent(new Intent(,MainActivity.class)).startActivities();
                    restart();
                }
                if (key.equals("sort"))
                    restartNeed = true;
            }
        };
        getSupportFragmentManager().beginTransaction().replace(R.id.settings_container, new SettingFragment()).commit();
        toolbar = findViewById(R.id.toolbar_settings_activity);
        toolbar.setTitle("Settings");
        toolbar.setNavigationIcon(R.drawable.icon_baseline_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        settingPreference.registerOnSharedPreferenceChangeListener(settingPreferenceListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        settingPreference.unregisterOnSharedPreferenceChangeListener(settingPreferenceListener);
    }

    public void restart() {
        Intent settingIntent = new Intent(this, Settings.class);
        settingIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        TaskStackBuilder.create(this).addNextIntent(mainIntent).addNextIntent(settingIntent).startActivities();
    }

    @Override
    public void onBackPressed() {
        if (restartNeed) {
            setResult(5);
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}