package com.ba.contacts.Fragments;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NavigationRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ba.contacts.Activities.AddSimContact;
import com.ba.contacts.Entities.Contact;
import com.ba.contacts.Adapters.ContactAdapter;
import com.ba.contacts.ViewModels.ContactViewModel;
import com.ba.contacts.Activities.MainActivity;
import com.ba.contacts.R;


public class SimListFragment extends Fragment {
    private ContactViewModel simContactViewModel;
    private ContactAdapter simContactAdapter;
    private Uri simUri=Uri.parse("content://icc/adn");
    private Toolbar toolbar;
    private TextView emptyText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_sim,container,false);
        emptyText=view.findViewById(R.id.sim_empty_text);

        //toolbar
        toolbar=view.findViewById(R.id.toolbar_sim_fragment);
        toolbar.setTitle("SIM Contacts");
        toolbar.setNavigationIcon(R.drawable.icon_hamburger);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = ((MainActivity)getActivity()).findViewById(R.id.drawerayout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        toolbar.inflateMenu(R.menu.addsimcontact_toolbar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.addNew){
                    Intent intent=new Intent(getActivity(), AddSimContact.class);
                    startActivityForResult(intent,1);
                }
                return true;
            }
        });

        ContentResolver contentResolver = getActivity().getContentResolver();
        RecyclerView recyclerView=view.findViewById(R.id.sim_recyclerview);
        simContactAdapter=new ContactAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(simContactAdapter);
        simContactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        simContactAdapter.setContacts(simContactViewModel.getAllSimContacts(getActivity()));
        simContactAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(simContactAdapter.getItemCount()==0){
                    emptyText.setVisibility(View.VISIBLE);
                }else
                    emptyText.setVisibility(View.INVISIBLE);
            }
        });

        // adapter on onclick listener
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
                                deleteDialog(contact);
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

    //dialog confirmation for deleting contact in sim card
    private void deleteDialog(Contact contact) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Delete");
        alertDialog.setMessage("Are you sure want to delete");
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int id=contact.getId();
                int deleteStatus=simContactViewModel.deleteSimContact(getActivity(),contact);
                if(deleteStatus==0){
                    Toast.makeText(getContext(),"Couldn't deleted",Toast.LENGTH_SHORT).show();
                }if(deleteStatus>0) {
                    simContactAdapter.notifyDataSetChanged();
                    simContactAdapter.setContacts(simContactViewModel.getAllSimContacts(getActivity()));
                    Toast.makeText(getContext(),"Contact in SIM deleted",Toast.LENGTH_SHORT).show();
                }
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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.addsimcontact_toolbar_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.addNew){
            Intent intent=new Intent(getActivity(), AddSimContact.class);
            startActivityForResult(intent,1);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getIntExtra("addStatus",0)==1){
            simContactAdapter.notifyDataSetChanged();
            simContactAdapter.setContacts(simContactViewModel.getAllSimContacts(getActivity()));
            Toast.makeText(getContext(),"Contact added to SIM",Toast.LENGTH_SHORT).show();
        }
    }
}
