package com.ba.contacts.Activities;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ba.contacts.Adapters.ContactAdapter;
import com.ba.contacts.Adapters.GroupAdapter;
import com.ba.contacts.Adapters.GroupAdapterTouchListner;
import com.ba.contacts.Entities.Contact;
import com.ba.contacts.R;
import com.ba.contacts.SettingsSharedPref;
import com.ba.contacts.ViewModels.ContactViewModel;

import java.util.List;
import java.util.Objects;

public class ContactSelectActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private GroupAdapter groupAdapter;
    private ContactViewModel contactViewModel;
    private String groupName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // theme
        if (SettingsSharedPref.getInstance().getTheme().equals("1"))
            setTheme(R.style.lightTheme);
        else
            setTheme(R.style.darkTheme);
        setContentView(R.layout.activity_contact_select);
        // getting intent data
        groupName = getIntent().getStringExtra("group_name");

        //toolbar
        toolbar=findViewById(R.id.toolbar_contact_activity);
        toolbar.setTitle("Choose Contact");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close);

        // group adapter
        groupAdapter=new GroupAdapter();
        final RecyclerView recyclerView =findViewById(R.id.recyclerview_contactselect);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(groupAdapter);
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        contactViewModel.getAllContacts().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                groupAdapter.setContacts(contacts);
            }
        });

        // recycler view touch listener
        recyclerView.addOnItemTouchListener(new GroupAdapterTouchListner(this, recyclerView, new GroupAdapterTouchListner.ClickListner() {
            @Override
            public void onClick(View view, int position) {
                Contact cardContact = groupAdapter.getContactAt(position);
                Integer id = cardContact.getId();
                contactViewModel.addContactToGroup(groupName,id);
                finish();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }//end of on create
}//end of activity
