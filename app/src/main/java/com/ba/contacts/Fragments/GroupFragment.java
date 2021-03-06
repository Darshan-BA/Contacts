package com.ba.contacts.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ba.contacts.Activities.ContactSelectActivity;
import com.ba.contacts.Entities.Contact;
import com.ba.contacts.Adapters.ContactAdapter;
import com.ba.contacts.ViewModels.ContactViewModel;
import com.ba.contacts.Activities.MainActivity;
import com.ba.contacts.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class GroupFragment extends Fragment {

    private ContactViewModel contactViewModel1;
    private ContactAdapter groupContactAdapter;
    private TextView textView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_group,container,false);
        Log.d("frag","onCreateView created ");
        String groupName=getArguments().getString("group_name");
        textView=view.findViewById(R.id.empty_text);
        //toolbar
        Toolbar toolbar=view.findViewById(R.id.toolbar_group_fragment);
        toolbar.setNavigationIcon(R.drawable.icon_hamburger);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = ((MainActivity)getActivity()).findViewById(R.id.drawerayout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //adapter
        groupContactAdapter=new ContactAdapter();
        toolbar.setTitle(groupName);
        final RecyclerView recyclerView = view.findViewById(R.id.group_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(groupContactAdapter);

        //contact view model
        contactViewModel1 = new ViewModelProvider(this).get(ContactViewModel.class);
        if(groupName.equals("Friends")){
            contactViewModel1.getAllFriendsContacts().observe(getActivity(), new Observer<List<Contact>>() {
                @Override
                public void onChanged(List<Contact> contacts) {
                    groupContactAdapter.setContacts(contacts);
                    if(contacts.isEmpty()){
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("Empty List, click button below to add friends to list.");
                    }else {
                        textView.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
        if(groupName.equals("Family")){
            contactViewModel1.getAllFamilyContacts().observe(getActivity(), new Observer<List<Contact>>() {
                @Override
                public void onChanged(List<Contact> contacts) {
                    groupContactAdapter.setContacts(contacts);
                    if(contacts.isEmpty()){
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("Empty List, click button below to add contacts to family.");
                    }else {
                        textView.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }

        //floating button
        FloatingActionButton floatingActionButton = view.findViewById(R.id.group_add_float);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(getActivity(),ContactSelectActivity.class);
               intent.putExtra("group_name",groupName);
               startActivity(intent);
        }
        });

        //onclick listener for group adapter
        groupContactAdapter.setOnItemClickListener(new ContactAdapter.OnItemClickListner() {
            @Override
            public void onCardClick(int position) {

            }

            @Override
            public void onPopUpClick(Contact contact, View view) {
                PopupMenu popupMenu=new PopupMenu(getContext(),view);
                popupMenu.getMenuInflater().inflate(R.menu.group_card_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.remove:
                                        int id=contact.getId();
                                        contactViewModel1.removeContactFromGroup(groupName,id);
                                        return true;
                                    case R.id.share:
                                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Name: " + contact.getFirstName() + "\b" + contact.getLastName() + "\n" + "Primary Number: "
                                                + contact.getPrimaryPhoneNumber() + "\n" + "Secondary Number: " + contact.getSecondaryPhoneNumber() + "\n" + "EmailId :" + contact.getEmailId());
                                        shareIntent.setType("text/plain");
                                        startActivity(Intent.createChooser(shareIntent, "Sharing Contact"));
                                        return true;
                                }
                                return false;
                    }
                });
                popupMenu.show();
            }

            @Override
            public void onIconClick(int position, View view) {

            }

            @Override
            public void setContextualActionMode() {

            }

            @Override
            public void multiSelect(int adapterPosition, boolean check) {

            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("frag", "onActivityCreated group_frag");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("frag", "onStart group_frag");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("frag", "onResume group_frag");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("frag", "onPause group_frag");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("frag", "onStop group_frag");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("frag", "onDestroyView group_frag");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("frag", "onDestroy group_frag");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("frag", "onDetach group_frag");
    }

}
