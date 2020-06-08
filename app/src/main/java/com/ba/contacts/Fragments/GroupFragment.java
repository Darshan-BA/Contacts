package com.ba.contacts.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ba.contacts.Contact;
import com.ba.contacts.ContactAdapter;
import com.ba.contacts.ContactViewModel;
import com.ba.contacts.MainActivity;
import com.ba.contacts.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class GroupFragment extends Fragment {

    ContactViewModel contactViewModel1;
    ContactAdapter groupContactAdapter;
    private FloatingActionButton floatingActionButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_group,container,false);
        Log.d("frag","onCreateView created ");
        String groupName=getArguments().getString("group_name");
        Toolbar toolbar=((MainActivity)getActivity()).toolbar;
        //adapter=((MainActivity)getActivity()).adapter;
        groupContactAdapter=new ContactAdapter();
        toolbar.setTitle(groupName);
        final RecyclerView recyclerView = view.findViewById(R.id.group_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(groupContactAdapter);
        contactViewModel1 = new ViewModelProvider(this).get(ContactViewModel.class);
        if(groupName.equals("Friends")){
            contactViewModel1.getAllFriendsContacts().observe(getActivity(), new Observer<List<Contact>>() {
                @Override
                public void onChanged(List<Contact> contacts) {
                    groupContactAdapter.setContacts(contacts);
                }
            });
        }
        if(groupName.equals("Family")){
            contactViewModel1.getAllFamilyContacts().observe(getActivity(), new Observer<List<Contact>>() {
                @Override
                public void onChanged(List<Contact> contacts) {
                    groupContactAdapter.setContacts(contacts);
                }
            });
        }

        floatingActionButton=view.findViewById(R.id.group_add_float);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm=getFragmentManager();
                FragmentTransaction transaction=fm.beginTransaction();
                ContactListShowFramgment contactListShowFramgment=new ContactListShowFramgment();
                Bundle bundle=new Bundle();
                bundle.putString("group_name",groupName);
                contactListShowFramgment.setArguments(bundle);
                transaction.replace(R.id.framelayout,contactListShowFramgment);
                transaction.addToBackStack(null);
                transaction.commit();
                Log.d("gr","group fab clicked");
            }
        });
        groupContactAdapter.setOnItemClickListener(new ContactAdapter.OnItemClickListner() {
            @Override
            public void onCardClick(int position) {

            }

            @Override
            public void onPopUpClick(Contact contact, View view) {

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
        Log.d("frag", "onActivityCreated main_frag");
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
