package com.ba.contacts.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ba.contacts.Adapters.GroupAdapter;
import com.ba.contacts.Adapters.GroupAdapterTouchListner;
import com.ba.contacts.Entities.Contact;
import com.ba.contacts.Adapters.ContactAdapter;
import com.ba.contacts.ViewModels.ContactViewModel;
import com.ba.contacts.Entities.FamilyList;
import com.ba.contacts.Entities.FriendsList;
import com.ba.contacts.Activities.MainActivity;
import com.ba.contacts.R;

import java.util.List;

public class ContactListShowFragment extends Fragment {

    private ContactViewModel contactViewModel;
    private ContactAdapter adapter;
    private GroupAdapter groupAdapter;
    private String groupName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list_show, container, false);

        groupName = getArguments().getString("group_name","");
        adapter = new ContactAdapter();
        groupAdapter = new GroupAdapter();
        final RecyclerView recyclerView = view.findViewById(R.id.contactList_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(groupAdapter);
        contactViewModel = new ViewModelProvider(getActivity()).get(ContactViewModel.class);
        contactViewModel.getAllContacts().observe(getActivity(), new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                groupAdapter.setContacts(contacts);
            }
        });
        recyclerView.addOnItemTouchListener(new GroupAdapterTouchListner(getContext(), recyclerView, new GroupAdapterTouchListner.ClickListner() {
            @Override
            public void onClick(View view, int position) {
                Contact cardContact = groupAdapter.getContactAt(position);
                Integer id = cardContact.getId();
                contactViewModel.addContactToGroup(groupName,id);
                getParentFragmentManager().popBackStack();
            }

            @Override
            public void onLongClick(View view, int position) {
                //groupAdapter.setSetMultiselect(true);
                //groupAdapter.notifyDataSetChanged();
                //toolbar.startActionMode(callback);
            }
        }));
        return view;
    }

    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contactlist_toolbar_menu, menu);
            mode.setTitle(groupName);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if(item.getItemId()==R.id.save_toolbar){
                final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getContext());
                alertDialog.setTitle("Add");
                alertDialog.setMessage("Are you sure want to add selected "+groupName);
                alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<Integer> ids=groupAdapter.getIdList();
                        for(int i:ids){
                            if(groupName.equals("Families")) {
                                contactViewModel.insertFriend(new FriendsList(i), 1);
                            }
                            if(groupName.equals("Friends")){
                                contactViewModel.insertFamily(new FamilyList(i), 1);
                            }
                        }
                        getFragmentManager().popBackStack();
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.create();
                alertDialog.show();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("fragmentContact", "onDestroyView group_frag");
        getFragmentManager().popBackStack();
    }
}
