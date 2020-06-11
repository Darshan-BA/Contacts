package com.ba.contacts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.DocumentsContract;

import android.view.ActionMode;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import android.widget.Toast;
import com.ba.contacts.Fragments.GroupFragment;
import com.ba.contacts.Fragments.MainFragment;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_CALL = 1;
    private static final int PERMISSION_EXTERNAL_WRITE = 2;
    private static final int PERMISSION_EXTERNAL_READ = 3;
    private static final int CREATE_JSON_FILE = 4;
    private static final int PICK_JSON_FILE = 5;
    private static final int CREATE_VCF_FILE = 6;
    private static final int PICK_VCF_FILE = 7;

    private int fragIndex=0;

    public Toolbar toolbar;
    ContactViewModel contactViewModel;
    ArrayList<Contact> deleteContactList = new ArrayList<>();
    public ContactAdapter adapter;
    List<Contact> exportContacts;
    //private SearchView searchView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private Fragment fragment=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //action bar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.primaryTextColor, null));
        //toolbar.setLogo(R.mipmap.contact_icon);
        toolbar.setTitle("Contacts");
        setSupportActionBar(toolbar);

        //drawerLayout and navigationView
        drawerLayout = findViewById(R.id.drawerayout);
        navigationView = findViewById(R.id.navigation_view);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openNavigation, R.string.closeNavigation);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.contact_menu_item:
                    getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,new MainFragment()).commit();
                    toolbar.setTitle("Contacts");
                    break;

                case R.id.import_menu_item:
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        permissionNotGrandted(Manifest.permission.READ_EXTERNAL_STORAGE);
                    } else {
                        ic();
                    }
                    break;

                case R.id.export_menu_item:
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        permissionNotGrandted(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    } else {
                        ec();
                    }
                    break;
                case R.id.sort_menu_item:
                    sc();
                    break;
                case R.id.family_menu_item:
                    gc(0);
                    break;

                case R.id.friends_menu_item:
                    gc(1);
                    break;

            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        if(savedInstanceState==null){
            if(fragment==null){
                gc(2);
            }
        }

        //RecyclerView Adapter instance
        adapter = new ContactAdapter();

        //SharedPrefrence for Contacts Sort
        SharedPreferences sortPrep = getSharedPreferences("SORT", MODE_PRIVATE);
        int sortId = sortPrep.getInt("name", 0);

        //ViewModel instance
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        if (sortId == 1) {
            contactViewModel.getAllContactsByLastName().observe(this, new Observer<List<Contact>>() {
                @Override
                public void onChanged(List<Contact> contacts) {
                    //adapter.setContacts(contacts);
                    exportContacts = contacts;
                }
            });
        }
        if (sortId == 0) {
            contactViewModel.getAllContacts().observe(this, new Observer<List<Contact>>() {
                @Override
                public void onChanged(List<Contact> contacts) {
                    //adapter.setContacts(contacts);
                    exportContacts = contacts;
                }
            });
        }

    }//end of onCreate()


    //permission Request
    public void permissionNotGrandted(String permission) {

        if (permission.equals(Manifest.permission.CALL_PHONE)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CALL_PHONE)) {
                Toast.makeText(MainActivity.this, "Need Telephone permission to start call", Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Permission");
                builder.setMessage("To import contacts allow to read external storage ");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL);
                    }
                });
                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create();
                builder.show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL);
            }
        }

        if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Permission");
                builder.setMessage("To import contacts allow to read external storage ");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_READ);
                    }
                });
                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create();
                builder.show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_READ);
            }
        }

        if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Permission");
                builder.setMessage("To import contacts allow to read external storage ");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_WRITE);
                    }
                });
                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create();
                builder.show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_WRITE);
            }
        }

    }//end of permission Request

    // Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Call Permission Granted", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(Intent.ACTION_CALL);
                //intent.setData(Uri.parse("tel:" + phoneNumberHolder));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    //startActivity(intent);
                }
            }
        }

        if (requestCode == PERMISSION_EXTERNAL_READ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Read External Storage Granted", Toast.LENGTH_SHORT).show();
                ic();
            }
        }

        if (requestCode == PERMISSION_EXTERNAL_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Write External Storage Granted", Toast.LENGTH_SHORT).show();
                ec();
            }
        }
    }//end of Permission Result

    // Activity Results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_JSON_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                final String docId = DocumentsContract.getDocumentId(uri);
                Log.d("split", "doc=" + docId);
                final String[] split = docId.split(":");
                Log.d("split", "split 0=" + split[0]);
                Log.d("split", "split 1=" + split[1]);
                new ImportAsyncTask(0).execute(split[1]);
            }
        }

        if (requestCode == CREATE_JSON_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                new ExportAsyncTask(adapter, 0).execute(split[1]);
            }
        }

        if (requestCode == PICK_VCF_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                new ImportAsyncTask(1).execute(split[1]);
            }
        }

        if (requestCode == CREATE_VCF_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                new ExportAsyncTask(adapter, 1).execute(split[1]);
            } else {
                Toast.makeText(MainActivity.this, "Export Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }//end of Activity Results

    // Import Contacts
    void ic() {
        final String[] dialogList = new String[]{"Import .json", "Import .vcf"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setItems(dialogList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    startActivityForResult(intent, PICK_JSON_FILE);
                }
                if (which == 1) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    startActivityForResult(intent, PICK_VCF_FILE);
                }
            }
        });
        builder.create();
        builder.show();

    }//end of Import Contacts

    // Export Contacts
    void ec() {
        final String[] dialogList = new String[]{"Export to .json", "Export to .vcf"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setItems(dialogList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/json");
                    intent.putExtra(Intent.EXTRA_TITLE, "contacts.json");
                    startActivityForResult(intent, CREATE_JSON_FILE);
                }
                if (which == 1) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Toast.makeText(MainActivity.this, "Need WRITE permission to Export Contacts", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_WRITE);
                        } else {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_WRITE);
                        }
                    } else {
                        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("text/json");
                        intent.putExtra(Intent.EXTRA_TITLE, "contacts.vcf");
                        startActivityForResult(intent, CREATE_VCF_FILE);
                    }
                }
            }
        });
        builder.create();
        builder.show();
    }//end of Export Contacts

    // Sort Contacts
    private void sc() {
        final String[] dialogList = new String[]{"First Name", "Last Name"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sort Contacts By");
        final SharedPreferences sortPrep = getSharedPreferences("SORT", MODE_PRIVATE);
        int selectedItem = sortPrep.getInt("name", 0);
        builder.setSingleChoiceItems(dialogList, selectedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor sortPrepEditor = sortPrep.edit();
                switch (which) {
                    case 0:
                        sortPrepEditor.putInt("name", 0);
                        sortPrepEditor.apply();
                        dialog.dismiss();
                        contactViewModel.getAllContacts().observe(MainActivity.this, new Observer<List<Contact>>() {
                            @Override
                            public void onChanged(List<Contact> contacts) {
                                adapter.setContacts(contacts);
                                exportContacts = contacts;
                            }
                        });
                        break;
                    case 1:
                        sortPrepEditor.putInt("name", 1);
                        sortPrepEditor.apply();
                        dialog.dismiss();
                        contactViewModel.getAllContactsByLastName().observe(MainActivity.this, new Observer<List<Contact>>() {
                            @Override
                            public void onChanged(List<Contact> contacts) {
                                adapter.setContacts(contacts);
                                exportContacts = contacts;
                            }
                        });
                        break;
                }
            }
        });
        builder.create();
        builder.show();
    }//end of Sort Contacts*/

    // group Contacts
    void gc(int which){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragment=new GroupFragment();
        Bundle bundle=new Bundle();
        if(which==0) {
            fragmentTransaction.replace(R.id.framelayout, fragment);
            bundle.putString("group_name","Family");
            fragment.setArguments(bundle);
            fragmentTransaction.commit();
        }
        if(which==1) {
            fragmentTransaction.replace(R.id.framelayout, fragment);
            bundle.putString("group_name","Friends");
            fragment.setArguments(bundle);
            fragmentTransaction.commit();
        } if(which==2){
            fragment=new MainFragment();
            fragmentTransaction.replace(R.id.framelayout,fragment).commit();
            navigationView.setCheckedItem(R.id.contact_menu_item);
        }

    }

    // Option Menu Create
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_toolbar).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnCloseListener(new  SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("searchview","search view on close listner");
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }//end of Option Menu Create

    public void setFragIndex(int fragIndex) {
        this.fragIndex = fragIndex;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (fragIndex){
            case 2:
                menu.findItem(R.id.search_toolbar).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }



    // Action CallBack for Multiple Delete
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.toolbar_list, menu);
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
                        adapter.setMultiDelete = false;
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Contacts Deleted", Toast.LENGTH_SHORT).show();
                        mode.finish();
                        finish();
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
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
            adapter.setMultiDelete = false;
            adapter.notifyDataSetChanged();
        }
    };//end of Action CallBack for Multiple Delete


    //on Back Button Pressed
    @Override
    public void onBackPressed() {
        /*if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        } else*/ if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }else if(!(fragment instanceof MainFragment)){
            gc(2);
        }
        else {
            super.onBackPressed();
        }
    }


    // Start of ImportAsyncTask
    private class ImportAsyncTask extends AsyncTask<String, Void, String> {
        int which;

        ImportAsyncTask(int which) {
            this.which = which;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String path = strings[0];
            Log.d("import", path);
            File file = new File(Environment.getExternalStorageDirectory(), path);
            String filePath = fileExtenstion(path, which);
            Log.d("filepath", filePath);
            if (which == 0) {
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
                            Contact contact = new Contact(first, last, primary, secondary, email, "");
                            contactViewModel.insert(contact);
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    return "Contacts Imported";
                }
            }
            if (which == 1) {
                if (!filePath.equals(".vcf")) {
                    return "Select vcf format";
                } else {
                    try (FileInputStream fileInputStream = new FileInputStream(file)) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                        String line = bufferedReader.readLine();
                        boolean hasNext = false;
                        if (line.equals("BEGIN:VCARD"))
                            hasNext = true;
                        String first = "", last = "", primary = "", secondary = "", email = "";
                        List<String> phonenumbers = new ArrayList<>();
                        while (hasNext) {
                            String s = bufferedReader.readLine();
                            if (s.lastIndexOf("N:", 2) == 0) {
                                String[] temp = s.split(";", 3);
                                last = temp[0].substring(temp[0].lastIndexOf(":") + 1);
                                first = temp[1];
                                Log.d("con", "last:" + temp[0]);
                                Log.d("con", "first:" + temp[1]);
                            }
                            if (s.contains("TEL;") && s.lastIndexOf(":") != 0) {
                                phonenumbers.add(s.substring(s.lastIndexOf(":") + 1));
                                //primary = s.substring(s.lastIndexOf(":") + 1);
                                //Log.d("con", "primary" + primary);
                                Log.d("con", "primary:" + s.substring(s.lastIndexOf(":") + 1));
                                Log.d("con", "Now phonenumber size is:" + String.valueOf(phonenumbers.size()));
                            }
                            /*if (s.contains("TEL;") && s.lastIndexOf(":") != 0 ) {
                                secondary = s.substring(s.lastIndexOf(":") + 1);
                                Log.d("con", "secondary:" + secondary);
                            }*/
                            if (s.contains("EMAIL") && s.lastIndexOf(":") != 0) {
                                email = s.substring(s.lastIndexOf(":") + 1);
                                Log.d("con", "email:" + email);
                            }
                            if (s.equals("END:VCARD")) {
                                if (phonenumbers.size() == 1) {
                                    Contact importContact = new Contact(first, last, phonenumbers.get(0), "", email, "");
                                    contactViewModel.insert(importContact);
                                } else if (phonenumbers.size() >= 2) {
                                    Contact importContact = new Contact(first, last, phonenumbers.get(0), phonenumbers.get(1), email, "");
                                    contactViewModel.insert(importContact);
                                }
                                first = "";
                                last = "";
                                primary = "";
                                secondary = "";
                                email = "";
                                phonenumbers.clear();
                                String next = bufferedReader.readLine();
                                if (next == null)
                                    hasNext = false;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return "Contacts Imported";
                }
            }
            return "Import failed";
        }

        private String fileExtenstion(String path, int which) {
            if (path.lastIndexOf(".") != -1 && path.lastIndexOf(".") != 0) {
                if (which == 0) {
                    int index = path.lastIndexOf(".");
                    String p = path.substring(index);
                    if (p.length() <= 4) {
                        Log.d("file", p);
                        return p;
                    } else {
                        return path.substring(index, index + 5);
                    }
                    //Log.d("file", path.substring(index, index + 5));
                }
                if (which == 1) {
                    int index = path.lastIndexOf(".");
                    String p = path.substring(index);
                    if (p.length() <= 4) {
                        Log.d("file", p);
                        return p;
                    } else {
                        Log.d("file", path.substring(index, index + 4));
                        return path.substring(index, index + 4);
                    }
                }
            }
            return "";
        }
    }

    // start of ExportAsyncTask
    private class ExportAsyncTask extends AsyncTask<String, Void, String> {
        private ContactAdapter adapter;
        int which;

        private ExportAsyncTask(ContactAdapter adapter, int which) {
            this.adapter = adapter;
            this.which = which;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Exported started in background", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String path = strings[0];
            File dir = new File(Environment.getExternalStorageDirectory(), path);
            dir.mkdirs();
            if (which == 0) {
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    Contact contact = adapter.getContactAt(i);
                    JSONObject tempObject = new JSONObject();
                    try {
                        tempObject.put("first", contact.getFirstName());
                        tempObject.put("last", contact.getLastName());
                        tempObject.put("primary", contact.getPrimaryPhoneNumber());
                        tempObject.put("secondary", contact.getSecondaryPhoneNumber());
                        tempObject.put("email", contact.getEmailId());
                        jsonArray.put(tempObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonObject.put("Contacts", jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try (FileOutputStream fileOutputStream = new FileOutputStream(dir)) {
                        fileOutputStream.write(jsonObject.toString().getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return "Contacts Exported to: " + path;
            }
            if (which == 1) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(dir)) {
                    byte[] begin = "BEGIN:VCARD\n".getBytes();
                    byte[] version = "VERSION:2.1\n".getBytes();
                    byte[] end = "END:VCARD\n".getBytes();
                    for (int i = 0; i < adapter.getItemCount(); i++) {
                        Contact contact = adapter.getContactAt(i);
                        fileOutputStream.write(begin);
                        fileOutputStream.write(version);
                        byte[] n = ("N:" + contact.getLastName() + ";" + contact.getFirstName() + ";;;\n").getBytes();
                        fileOutputStream.write(n);
                        byte[] fn = ("FN:" + contact.getFirstName() + " " + contact.getLastName() + "\n").getBytes();
                        fileOutputStream.write(fn);
                        byte[] tel1 = ("TEL;CELL;PREF:" + contact.getPrimaryPhoneNumber() + "\n").getBytes();
                        fileOutputStream.write(tel1);
                        byte[] tel2 = ("TEL;CELL:" + contact.getSecondaryPhoneNumber() + "\n").getBytes();
                        fileOutputStream.write(tel2);
                        byte[] email = ("EMAIL;HOME:" + contact.getEmailId() + "\n").getBytes();
                        fileOutputStream.write(email);
                        fileOutputStream.write(end);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "Contacts Exported to: " + path;
            }

            return "Export Failed";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }//end of exportAsyncTask

} //end of main activity


