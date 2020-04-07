package com.ba.contacts;

import androidx.appcompat.app.AppCompatActivity;
import android.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int PERMISSION_CALL=1;
    Toolbar toolbar;
    FloatingActionButton floatingActionButton;
    ContactViewModel contactViewModel;
    ArrayList<Contact> deleteContactList=new ArrayList<Contact>();
    //private ActionMode actionMode;
    ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //action bar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.primaryTextColor, null));
        setSupportActionBar(toolbar);

        //Floating Button
        floatingActionButton = findViewById(R.id.add_float);

        //RecyclerView Adapter instance
        adapter= new ContactAdapter();

        //RecyclerView
        final RecyclerView recyclerView = findViewById(R.id.recyclerview);
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
            public void onPopUpClick(final Contact contact, View view) {
                //contactViewModel.delete(adapter.getContactAt(position));
                PopupMenu popupMenu=new PopupMenu(MainActivity.this,view);
                popupMenu.getMenuInflater().inflate(R.menu.contact_card_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit:
                                Intent intent = new Intent(MainActivity.this, EditContact.class);
                                intent.putExtra("id", contact.getId());
                                intent.putExtra("first", contact.getFirstName());
                                intent.putExtra("last", contact.getLastName());
                                intent.putExtra("primary", contact.getPrimaryPhoneNumber());
                                intent.putExtra("secondary", contact.getSecondaryPhoneNumber());
                                intent.putExtra("email", contact.getEmailId());
                                startActivity(intent);
                                return true;
                            case R.id.delete:
                                final AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this);
                                alertDialog.setTitle("Delete");
                                alertDialog.setMessage("Are you sure want to delete");
                                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        contactViewModel.delete(contact);
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
                            case R.id.share:
                                //Sharing need to implemented
                            default:
                                return false;
                        }

                    }
                });
                popupMenu.show();
            }

            @Override
            public void onIconClick(int position, View view) {

            }

            @Override
            public void setContextualActionMode() {
                adapter.setSetMultiDelete(true);
                adapter.notifyDataSetChanged();
                toolbar.startActionMode(actionModeCallback);
            }

            @Override
            public void multiSelect(int adapterPosition, boolean check) {
                if(check){
                    deleteContactList.add(adapter.getContactAt(adapterPosition));
                }else {
                    deleteContactList.remove(adapter.getContactAt(adapterPosition));
                }
            }

            @Override
            public void onCardClick(int position) {
               final String[] dialogList;
                Contact cardContact = adapter.getContactAt(position);
                String first=cardContact.getPrimaryPhoneNumber();
                String second=cardContact.getSecondaryPhoneNumber();
                String email=cardContact.getEmailId();
                Log.d("number",first);
                Log.d("number",second);
                Log.d("number",email);
                if(first.isEmpty() && second.isEmpty() && email.isEmpty()){
                    dialogList=new String[0];
                    Toast.makeText(MainActivity.this,"No Phone Numbers to Call",Toast.LENGTH_SHORT).show();
                }else if(second.isEmpty() && email.isEmpty()) {
                        dialogList=new String[1];
                        dialogList[0]=cardContact.getPrimaryPhoneNumber();
                }else if(first.isEmpty() && email.isEmpty()){
                    dialogList=new String[1];
                    dialogList[0]=cardContact.getSecondaryPhoneNumber();
                }else if(first.isEmpty() && second.isEmpty()){
                    dialogList=new String[1];
                    dialogList[0]=cardContact.getEmailId();
                }else if(email.isEmpty()){
                    dialogList=new String[2];
                    dialogList[0]=cardContact.getPrimaryPhoneNumber();
                    dialogList[1]=cardContact.getSecondaryPhoneNumber();
                }else if(second.isEmpty()){
                    dialogList=new String[2];
                    dialogList[0]=cardContact.getPrimaryPhoneNumber();
                    dialogList[1]=cardContact.getEmailId();
                }else if(first.isEmpty()){
                    dialogList=new String[2];
                    dialogList[0]=cardContact.getSecondaryPhoneNumber();
                    dialogList[1]=cardContact.getEmailId();
                }else{
                    dialogList=new String[3];
                    dialogList[0]=cardContact.getPrimaryPhoneNumber();
                    dialogList[1]=cardContact.getSecondaryPhoneNumber();
                    dialogList[2]=cardContact.getEmailId();
                }

                final Intent phoneIntent = new Intent((Intent.ACTION_CALL));
                final Intent emailIntent =new Intent(Intent.ACTION_SENDTO);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setItems(dialogList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String var=dialogList[which];
                        boolean isVarEmail=false;
                        for(int i = 0; i < var.length(); i++){
                            char temp=var.charAt(i);
                            Log.d("ba",String.valueOf(temp));
                            if(temp == '@'){
                                isVarEmail=true;
                                break;
                            }
                        }
                        if(isVarEmail){
                            emailIntent.setData(Uri.parse("mailto:"+dialogList[which]));
                            startActivity(emailIntent);

                        }else {
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                requestCallPermission();
                                //return;
                            }else {
                                phoneIntent.setData(Uri.parse("tel:"+dialogList[which]));
                                startActivity(phoneIntent);
                            }
                        }
                    }
                });
                builder.create();
                builder.show();
            }

            private void requestCallPermission() {
                if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.CALL_PHONE)) {
                    Toast.makeText(MainActivity.this, "Grant Phone Call Permission inorder to Call", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CALL_PHONE},PERMISSION_CALL);
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CALL_PHONE},PERMISSION_CALL);
                }
            }

        });
    }

    public void addContact(View view) {
        Intent intent=new Intent(this,EditContact.class);
        startActivity(intent);
    }


    private ActionMode.Callback actionModeCallback=new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.toolbar_list,menu);
            mode.setTitle("Delete Contact");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.delete_toolbar) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Delete Selected");
                alertDialog.setMessage("Are you sure want to delete selected contacts");
                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        contactViewModel.multipleDelete(deleteContactList.toArray(new Contact[deleteContactList.size()]));
                        deleteContactList.clear();
                        adapter.setSetMultiDelete(false);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Contacts Deleted", Toast.LENGTH_SHORT).show();
                        mode.finish();
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
            adapter.setSetMultiDelete(false);
            adapter.notifyDataSetChanged();
        }
    };

}
