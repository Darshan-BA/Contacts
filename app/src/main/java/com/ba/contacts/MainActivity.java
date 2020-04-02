package com.ba.contacts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity  {
    Toolbar toolbar;
    FloatingActionButton floatingActionButton;
    ContactViewModel contactViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //action bar
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.primaryTextColor,null));
        setSupportActionBar(toolbar);

        //Floating Button
        floatingActionButton=findViewById(R.id.add_float);

        //RecyclerView Adapter instance
        final ContactAdapter adapter=new ContactAdapter();

        //RecyclerView
        final RecyclerView recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        //ViewModel instance
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        contactViewModel.getAllContacts().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                //Toast.makeText(MainActivity.this,"onChanged",Toast.LENGTH_LONG).show();
                adapter.setContacts(contacts);
            }
        });

        // OnClick listner for deleting and editing
        adapter.setOnItemClickListener(new ContactAdapter.OnItemClickListner() {
            @Override
            public void onDeleteClick(int position) {
                contactViewModel.delete(adapter.getContactAt(position));
            }

            @Override
            public void onEditClick(Contact contact) {
                Intent intent=new Intent(MainActivity.this,EditContact.class);
                intent.putExtra("id",contact.getId());
                intent.putExtra("first",contact.getFirstName());
                intent.putExtra("last",contact.getLastName());
                intent.putExtra("primary",contact.getPrimaryPhoneNumber());
                intent.putExtra("secondary",contact.getSecondaryPhoneNumber());
                intent.putExtra("email",contact.getEmailId());
                startActivity(intent);
            }

            @Override
            public void onCardClick(int position) {

            }

        });

    }

    public void addContact(View view) {
        Intent intent=new Intent(this,EditContact.class);
        startActivity(intent);
    }

}
