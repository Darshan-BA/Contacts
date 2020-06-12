package com.ba.contacts.Fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ba.contacts.Contact;
import com.ba.contacts.ContactAdapter;
import com.ba.contacts.ContactViewModel;
import com.ba.contacts.R;


import java.util.List;

public class SimListFragment extends Fragment {
    private ContactViewModel simContactViewModel;
    private ContactAdapter simContactAdapter;
    private Uri simUri=Uri.parse("content://icc/adn");
    private ContentResolver contentResolver;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_sim,container,false);
        contentResolver=getActivity().getContentResolver();
        RecyclerView recyclerView=view.findViewById(R.id.sim_recyclerview);
        simContactAdapter=new ContactAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(simContactAdapter);
        simContactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        simContactAdapter.setContacts(simContactViewModel.getAllSimContacts(getActivity()));
        simContactAdapter.setOnItemClickListener(new ContactAdapter.OnItemClickListner() {
            @Override
            public void onCardClick(int position) {

            }

            @Override
            public void onPopUpClick(Contact contact, View view) {
                PopupMenu popupMenu=new PopupMenu(getContext(),view);
                popupMenu.getMenuInflater().inflate(R.menu.sim_card_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.sim_delete_menu_item:{
                                int deleteStatus=simContactViewModel.deleteSimContact(getActivity(),contact);
                                if(deleteStatus==0){
                                    Toast.makeText(getContext(),"Couldn't deleted",Toast.LENGTH_SHORT).show();
                                }else {
                                    simContactAdapter.notifyDataSetChanged();
                                    Toast.makeText(getContext(),"Contact in SIM deleted",Toast.LENGTH_SHORT).show();
                                }
                                return true;
                            }
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
