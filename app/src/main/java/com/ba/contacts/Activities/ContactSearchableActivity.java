package com.ba.contacts.Activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ba.contacts.Adapters.ContactAdapter;
import com.ba.contacts.Entities.Contact;
import com.ba.contacts.R;
import com.ba.contacts.ViewModels.ContactViewModel;

import java.util.List;

class ContactSearchableActivity extends AppCompatActivity {
    private ContactViewModel searchableContactViewModel;
    private ContactAdapter searchableAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_searchable);

        searchableAdapter=new ContactAdapter();
        searchableContactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        searchableContactViewModel.getAllContacts().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                searchableAdapter.setContacts(contacts);
            }
        });

        searchContact();
    }//end of OnCreate()

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        searchContact();
    }

    private void searchContact() {
        Intent intent=getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query=intent.getStringExtra(SearchManager.QUERY);
            searchableAdapter.getFilter().filter(query);
        }
    }
}//end of Activity
