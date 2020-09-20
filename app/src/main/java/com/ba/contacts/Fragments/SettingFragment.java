package com.ba.contacts.Fragments;

import android.content.Context;
import android.os.Bundle;
import com.ba.contacts.R;
import androidx.preference.*;

public class SettingFragment extends PreferenceFragmentCompat {
    public static String themeKey="theme";
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);
        // theme preference
        ListPreference themePreference=new ListPreference(context);
        themePreference.setKey(themeKey);
        CharSequence[] themeEntries=new CharSequence[]{"Light Mode","Dark Mode"};
        CharSequence[] themeEntryValues=new CharSequence[]{"0","1"};
        themePreference.setEntries(themeEntries);
        themePreference.setEntryValues(themeEntryValues);
        themePreference.setTitle("Choose Theme");
        themePreference.setDefaultValue("0");
        themePreference.setPersistent(true);
        themePreference.setIcon(R.drawable.icon_theme_toggle);
        themePreference.setPositiveButtonText("Apply");
        themePreference.setNegativeButtonText("Cancel");
        screen.addPreference(themePreference);

        //sort preference
        ListPreference sortPreference =new ListPreference(context);
        sortPreference.setKey("sort");
        CharSequence[] entries=new CharSequence[]{"First Name","Last Name"};
        CharSequence[] entryValues=new CharSequence[]{"0","1"};
        sortPreference.setEntries(entries);
        sortPreference.setEntryValues(entryValues);
        sortPreference.setTitle("Sort by");
        sortPreference.setSummary("Sort summary");
        sortPreference.setDefaultValue("0");
        sortPreference.setPersistent(true);
        sortPreference.setIcon(R.drawable.icon_sort);
        screen.addPreference(sortPreference);
        setPreferenceScreen(screen);
    }

}
