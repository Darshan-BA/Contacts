package com.ba.contacts.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.preference.*;

public class SettingFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);

        ListPreference sortPreference =new ListPreference(context);
        sortPreference.setKey("sort");
        CharSequence[] entries=new CharSequence[]{"First Name","Last Name"};
        CharSequence[] entryValues=new CharSequence[]{"1","2"};
        sortPreference.setEntries(entries);
        sortPreference.setEntryValues(entryValues);
        sortPreference.setTitle("Sort by");
        sortPreference.setSummary("Sort summary");
        sortPreference.setDefaultValue("1");
        sortPreference.setPersistent(true);
        screen.addPreference(sortPreference);
        setPreferenceScreen(screen);
    }

}
