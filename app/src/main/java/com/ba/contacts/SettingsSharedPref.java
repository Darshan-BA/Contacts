package com.ba.contacts;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.ba.contacts.Fragments.SettingFragment;

//singleton class for getting reference to setting shared preference
public class SettingsSharedPref {

    private static SettingsSharedPref settingsSingleton;
    private SharedPreferences settingsPreferences;

    private SettingsSharedPref(Context applicationContext){
        settingsPreferences=PreferenceManager.getDefaultSharedPreferences(applicationContext);
    }

    public static SettingsSharedPref getInstance(){
        if(settingsSingleton==null){
            throw new IllegalStateException("SettingSharedPref is not initialized,call initalize(applicationContext) static method first");
        }
        return settingsSingleton;
    }
    public static void initSettingsSharedPref(Context applicationContext){
        if(applicationContext==null){
            throw new NullPointerException("Provided application context is null");
        }
        if(settingsSingleton==null){
            synchronized (SettingsSharedPref.class){
                if(settingsSingleton==null){
                    settingsSingleton=new SettingsSharedPref(applicationContext);
                }
            }
        }
    }

    private SharedPreferences getPref(){
        return settingsPreferences;
    }
    public String getTheme(){
        return getPref().getString("theme", "true");
    }

    public String getSort(){
        return getPref().getString("sort","0");
    }

}
