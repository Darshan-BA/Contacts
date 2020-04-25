package com.ba.contacts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import android.widget.Adapter;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_CALL=1;
    private static final int PERMISSION_EXTERNAL_WRITE=2;
    private static final int PERMISSION_EXTERNAL_READ=3;
    private static final int CREATE_JSON_FILE=4;
    private static final int PICK_JSON_FILE=5;

    Toolbar toolbar;
    FloatingActionButton floatingActionButton;
    ContactViewModel contactViewModel;
    ArrayList<Contact> deleteContactList= new ArrayList<>();
    ContactAdapter adapter;
    List<Contact> exportContacts;

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
                adapter.setContacts(contacts);
                exportContacts=contacts;
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
                //need to implement adding profile photo
            }

            @Override
            public boolean setContextualActionMode() {
                //adapter.setSetMultiDelete(true);
                //adapter.notifyDataSetChanged();
                toolbar.startActionMode(actionModeCallback);
                return true;
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
        
    }//end of onCreate()


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.import_menu_item) {
            Log.d("BA", "On Import Clicked");
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Toast.makeText(MainActivity.this, "Need READ permission to Import Contacts ", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_EXTERNAL_READ);
                }else {
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_EXTERNAL_WRITE);
                }
            }else {
                Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent,PICK_JSON_FILE);
            }
        }

        if (item.getItemId() == R.id.export_menu_item) {
            Log.d("BA", "On Export Clicked");
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(MainActivity.this, "Need WRITE permission to Export Contacts", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_EXTERNAL_WRITE);
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_EXTERNAL_WRITE);
                }
            }else{
                Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_TITLE,"contacts.json");
                startActivityForResult(intent,CREATE_JSON_FILE);
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_EXTERNAL_READ){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this, "Read External Storage Granted", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent,PICK_JSON_FILE);
            }
            else{
                Toast.makeText(MainActivity.this, "Grant Read Permission To Export Contacts", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == PERMISSION_EXTERNAL_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Write External Storage Granted", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_TITLE,"contacts.json");
                startActivityForResult(intent,CREATE_JSON_FILE);
            } else {
                Toast.makeText(MainActivity.this, "Grant Write Permission To Export Contacts", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_JSON_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri=null;
            if(data!=null){
                uri=data.getData();
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                new ImportAsyncTask().execute(split[1]);
            }else {
                Toast.makeText(MainActivity.this,"Choose Appropriate File",Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode==CREATE_JSON_FILE && resultCode==Activity.RESULT_OK){
            Uri uri=null;
        if(data!=null){
            uri=data.getData();
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            new ExportAsyncTask(adapter).execute(split[1]);
        }else{
            Toast.makeText(MainActivity.this,"Export Failed",Toast.LENGTH_SHORT).show();
        }
        }
    }

    // Start of ImportAsyncTask
    private class ImportAsyncTask extends AsyncTask<String,Void,String>{

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String path = strings[0];
            Log.d("import", path);
            File file = new File(Environment.getExternalStorageDirectory(), path);
            String filePath = fileExtenstion(path);
            Log.d("filepath", filePath);
            if (!filePath.equals(".json")) {
                return "Select JSON format file";
            } else {
                try (FileReader fileReader = new FileReader(file)) {
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        stringBuilder.append(line).append("\n");
                        line = bufferedReader.readLine();
                    }
                    bufferedReader.close();
                    String jsonString = stringBuilder.toString();
                    Log.d("object", jsonString);
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray jsonArray = jsonObject.getJSONArray("Contacts");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject tempObject = jsonArray.getJSONObject(i);
                        String first = tempObject.getString("first");
                        String last = tempObject.getString("last");
                        String primary = tempObject.getString("primary");
                        String secondary = tempObject.getString("secondary");
                        String email = tempObject.getString("email");
                        Contact contact = new Contact(first, last, primary, secondary, email);
                        contactViewModel.insert(contact);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return "Contacts Imported";
            }
        }

        private String fileExtenstion(String path) {
            if(path.lastIndexOf(".")!=-1 && path.lastIndexOf(".")!=0){
                return path.substring(path.lastIndexOf("."));
            }
            else
                return "";
        }
    }

    // start of ExportAsyncTask
    private class ExportAsyncTask extends AsyncTask<String,Void,String>{
        private ContactAdapter adapter;

        private ExportAsyncTask(ContactAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Exported started in background",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String  path=strings[0];
            JSONObject jsonObject=new JSONObject();
            JSONArray jsonArray=new JSONArray();
            for (int i=0;i<adapter.getItemCount();i++){
                Contact contact=adapter.getContactAt(i);
                JSONObject tempObject=new JSONObject();
                try {
                    tempObject.put("first",contact.getFirstName());
                    tempObject.put("last",contact.getLastName());
                    tempObject.put("primary",contact.getPrimaryPhoneNumber());
                    tempObject.put("secondary",contact.getSecondaryPhoneNumber());
                    tempObject.put("email",contact.getEmailId());
                    jsonArray.put(tempObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    jsonObject.put("Contacts",jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                File dir=new File(Environment.getExternalStorageDirectory(),path);
                dir.mkdirs();
                try (FileOutputStream fileOutputStream=new FileOutputStream(dir)){
                    fileOutputStream.write(jsonObject.toString().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "Contacts Exported to: "+path;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
        }
    }//end of exportAsyncTask

} //end of main activity


