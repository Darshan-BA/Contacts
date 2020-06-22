package com.ba.contacts.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
//import androidx.preference.Preference;
//import androidx.preference.PreferenceManager;

import com.ba.contacts.Entities.Contact;
import com.ba.contacts.Adapters.ContactAdapter;
import com.ba.contacts.SettingsSharedPref;
import com.ba.contacts.ViewModels.ContactViewModel;
import com.ba.contacts.Fragments.GroupFragment;
import com.ba.contacts.Fragments.MainFragment;
import com.ba.contacts.Fragments.SimListFragment;
import com.ba.contacts.R;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    private static final int PERMISSION_WRITE_CONTACTS = 8;
    private static final int PERMISSION_READ_CONTACTS = 9;
    private static final int PERMISSION_READ_WRITE_CONTACTS = 10;
    private static final int SettingsResultCode=11;

    private int fragIndex = 0;
    private ContactViewModel contactViewModel;
    private ArrayList<Contact> deleteContactList = new ArrayList<>();
    public ContactAdapter adapter;
    private List<Contact> exportContacts;
    private SearchView searchView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsSharedPref.initSettingsSharedPref(getApplicationContext());
        if (SettingsSharedPref.getInstance().getTheme().equals("1"))
            setTheme(R.style.lightTheme);
        else
            setTheme(R.style.darkTheme);
        setContentView(R.layout.activity_main);
        //drawerLayout and navigationView
        drawerLayout = findViewById(R.id.drawerayout);
        navigationView = findViewById(R.id.navigation_view);
        //ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openNavigation, R.string.closeNavigation);
        //drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.contact_menu_item:
                    getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new MainFragment()).commit();

                    break;
                case R.id.sim_menu_item:
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        permissionForSimContactsList();
                    } else {
                        gc(3);
                    }
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
                /*case R.id.sort_menu_item:
                    sc();
                    break;*/
                case R.id.family_menu_item:
                    gc(0);
                    break;

                case R.id.friends_menu_item:
                    gc(1);
                    break;
                case R.id.settings:
                    Intent intent = new Intent(this, Settings.class);
                    startActivityForResult(intent,SettingsResultCode);
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        if (savedInstanceState == null) {
            if (fragment == null) {
                gc(2);
            }
        }

        //RecyclerView Adapter instance
        adapter = new ContactAdapter();

        //SharedPrefrence for Contacts Sort
        SharedPreferences sortPrep = getSharedPreferences("SORT", MODE_PRIVATE);
        int sortId = sortPrep.getInt("name", 0);

       /* SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String name = sharedPreferences.getString("sort", "");
        Log.d("preference",name);
        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d("preference","Preference value was updated to:" + sharedPreferences.getString(key,""));
            }
        });*/


        //ViewModel instance
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        if (SettingsSharedPref.getInstance().getSort().equals("1")) {
            contactViewModel.getAllContactsByLastName().observe(this, new Observer<List<Contact>>() {
                @Override
                public void onChanged(List<Contact> contacts) {
                    //adapter.setContacts(contacts);
                    exportContacts = contacts;
                }
            });
        }
        if (SettingsSharedPref.getInstance().getSort().equals("1")) {
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
    public void permissionForSimContactsList() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Need Permission");
            builder.setMessage("To show SIM contacts allow read contacts permission");
            builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, 10);
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
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, 10);
        }
    }

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
        if (permission.equals(Manifest.permission.WRITE_CONTACTS)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_CONTACTS)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Permission");
                builder.setMessage("To Export contacts to SIM allow to write contacts ");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSION_WRITE_CONTACTS);
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
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSION_WRITE_CONTACTS);
            }
        }
        if (permission.equals(Manifest.permission.READ_CONTACTS)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Permission");
                builder.setMessage("To Import contacts from SIM allow read contacts permission");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_READ_CONTACTS);
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
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_READ_CONTACTS);
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
        if (requestCode == PERMISSION_WRITE_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Write Contact Permission Granted", Toast.LENGTH_SHORT).show();
                new ExportToSimAsyncTask().execute();
            }
        }
        if (requestCode == PERMISSION_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Read Contact Permission Granted", Toast.LENGTH_SHORT).show();
                new ImportFromSimAsyncTask().execute();
            }
        }
        if (requestCode == PERMISSION_READ_WRITE_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Read/Write Contact Permission Granted", Toast.LENGTH_SHORT).show();
                fragment = new SimListFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, fragment).commit();
            }
        }
    }//end of Permission Result

    // Activity Results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_JSON_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null)
                new ImportDeviceContactsAsyncTask(adapter, 0).execute(data.getData());
            else
                Toast.makeText(MainActivity.this, "Import Failed", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == CREATE_JSON_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null)
                new ExportDeviceContactsAsyncTask(adapter, 0).execute(data.getData());
            else
                Toast.makeText(MainActivity.this, "Export Failed", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == PICK_VCF_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null)
                new ImportDeviceContactsAsyncTask(adapter, 1).execute(data.getData());
            else
                Toast.makeText(MainActivity.this, "Import Failed", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == CREATE_VCF_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null)
                new ExportDeviceContactsAsyncTask(adapter, 1).execute(data.getData());
            else
                Toast.makeText(MainActivity.this, "Export Failed", Toast.LENGTH_SHORT).show();
        }

        if(requestCode==SettingsResultCode){
            Log.d("restart","request code="+5);
            recreate();
        }
    }//end of Activity Results

    // Import Contacts
    void ic() {
        final String[] dialogList = new String[]{"Import .json", "Import .vcf", "Import from SIM"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setItems(dialogList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        startActivityForResult(intent, PICK_JSON_FILE);
                        break;
                    }
                    case 1: {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        startActivityForResult(intent, PICK_VCF_FILE);
                        break;
                    }
                    case 2: {
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                            permissionNotGrandted(Manifest.permission.READ_CONTACTS);
                        } else {
                            new ImportFromSimAsyncTask().execute();
                        }
                    }
                }
            }
        });
        builder.create();
        builder.show();

    }//end of Import Contacts

    // Export Contacts
    void ec() {
        final String[] dialogList = new String[]{"Export as .json", "Export as .vcf", "Export to SIM"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setItems(dialogList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("text/json");
                        intent.putExtra(Intent.EXTRA_TITLE, "contacts.json");
                        startActivityForResult(intent, CREATE_JSON_FILE);
                        break;
                    }
                    case 1: {
                        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("text/json");
                        intent.putExtra(Intent.EXTRA_TITLE, "contacts.vcf");
                        startActivityForResult(intent, CREATE_VCF_FILE);
                        break;
                    }
                    case 2: {
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                            permissionNotGrandted(Manifest.permission.WRITE_CONTACTS);
                        } else {
                            new ExportToSimAsyncTask().execute();
                        }
                        break;
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
    void gc(int which) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new GroupFragment();
        Bundle bundle = new Bundle();
        if (which == 0) {
            fragmentTransaction.replace(R.id.framelayout, fragment);
            bundle.putString("group_name", "Family");
            fragment.setArguments(bundle);
            fragmentTransaction.commit();
        }
        if (which == 1) {
            fragmentTransaction.replace(R.id.framelayout, fragment);
            bundle.putString("group_name", "Friends");
            fragment.setArguments(bundle);
            fragmentTransaction.commit();
        }
        if (which == 2) {
            fragment = new MainFragment();
            fragmentTransaction.replace(R.id.framelayout, fragment).commit();
            navigationView.setCheckedItem(R.id.contact_menu_item);
        }
        if (which == 3) {
            fragment = new SimListFragment();
            fragmentTransaction.replace(R.id.framelayout, fragment);
            fragmentTransaction.commit();
        }
    }

    // Option Menu Create
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_toolbar).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("searchview", "search view on close listner");
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
        switch (fragIndex) {
            case 2:
                menu.findItem(R.id.search_toolbar).setVisible(false);

        }
        return super.onPrepareOptionsMenu(menu);
    }


    // Action CallBack for Multiple Delete
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_actionmodecallback, menu);
            mode.setTitle("Delete Contact");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.multiple_delete) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Delete Selected");
                alertDialog.setMessage("Are you sure want to delete selected contacts");
                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        contactViewModel.multipleDelete(deleteContactList.toArray(new Contact[deleteContactList.size()]));
                        deleteContactList.clear();
                        //adapter.setMultiDelete = false;
                        adapter.setSetMultiDelete(false);
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
            //adapter.setMultiDelete = false;
            adapter.setSetMultiDelete(false);
            adapter.notifyDataSetChanged();
        }
    };//end of Action CallBack for Multiple Delete


    //on Back Button Pressed
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (!(fragment instanceof MainFragment)) {
            gc(2);
        } else {
            super.onBackPressed();
        }
    }

    private class ImportFromSimAsyncTask extends AsyncTask<Void, Void, String> {
        Uri simUri = Uri.parse("content://icc/adn");
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(simUri, null, null, null, "ASC");

        @Override
        protected String doInBackground(Void... voids) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String number = cursor.getString(cursor.getColumnIndex("number"));
                Log.d("simImport", "name=" + name);
                Log.d("simImport", "number=" + number);
                Contact contact = new Contact(name, "", number, "", "", "");
                contactViewModel.insert(contact);
            }
            return null;
        }
    }

    private class ExportToSimAsyncTask extends AsyncTask<Void, Void, String> {
        Uri simUri = Uri.parse("content://icc/adn");
        ContentValues contentValues = new ContentValues();
        ContentResolver contentResolver = getContentResolver();

        @Override
        protected String doInBackground(Void... voids) {
            for (Contact contact : exportContacts) {
                String name = contact.getFirstName() + " " + contact.getLastName();
                if (!contact.getPrimaryPhoneNumber().equals("")) {
                    String phonenumber = contact.getPrimaryPhoneNumber();
                    contentValues.put("tag", name);
                    contentValues.put("number", phonenumber);
                    Uri insteredPrimary = contentResolver.insert(simUri, contentValues);
                    if (insteredPrimary != null)
                        //Toast.makeText(MainActivity.this,name+" exported to sim",Toast.LENGTH_SHORT).show();
                        Log.d("sim", String.valueOf(insteredPrimary));
                }
                if (!contact.getSecondaryPhoneNumber().equals("")) {
                    String phonenumber = contact.getSecondaryPhoneNumber();
                    contentValues.put("name", name);
                    contentValues.put("number", phonenumber);
                    Uri insteredSecondary = contentResolver.insert(simUri, contentValues);
                    if (insteredSecondary != null)
                        Log.d("sim", String.valueOf(insteredSecondary));
                }

            }
            return "Export Finished";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }

    private class ImportDeviceContactsAsyncTask extends AsyncTask<Uri, Void, String> {
        private ContactAdapter adapter;
        private int which;

        public ImportDeviceContactsAsyncTask(ContactAdapter adapter, int which) {
            this.adapter = adapter;
            this.which = which;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Import started in Background... ", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Uri... uris) {
            Uri path = uris[0];
            try (InputStream importStream = getContentResolver().openInputStream(path)) {
                switch (which) {
                    case 0: {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(importStream));
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
                        return "Contacts Imported";
                    }
                    case 1: {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(importStream));
                        String line = bufferedReader.readLine();
                        boolean hasNext = false;
                        if (line.equals("BEGIN:VCARD"))
                            hasNext = true;
                        else
                            return "Select vcf format";
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
                        break;
                    }
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return "Contacts Imported";
        }
    }

    private class ExportDeviceContactsAsyncTask extends AsyncTask<Uri, Void, String> {

        private ContactAdapter adapter;
        private int which;

        public ExportDeviceContactsAsyncTask(ContactAdapter adapter, int which) {
            this.adapter = adapter;
            this.which = which;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Exported started in background", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            NotificationCompat.Builder exportFinishedBuilder=new NotificationCompat.Builder(MainActivity.this)
                    .setSmallIcon(R.mipmap.contact_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.contact_icon))
                    .setContentTitle("Export")
                    .setContentText("Export Contacts Finished Successfully")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                 final String CHANNEL_ID = "Export";
                NotificationChannel channel=new NotificationChannel(CHANNEL_ID,"Export",NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
                exportFinishedBuilder.setChannelId(CHANNEL_ID);
            }
            notificationManager.notify(0,exportFinishedBuilder.build());
        }

        @Override
        protected String doInBackground(Uri... uris) {
            Uri path = uris[0];
            try (OutputStream exportStream = getContentResolver().openOutputStream(path)) {
                if (which == 0) {
                    JSONObject jsonObject = new JSONObject();
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < adapter.getItemCount(); i++) {
                        Contact contact = adapter.getContactAt(i);
                        JSONObject tempObject = new JSONObject();
                        tempObject.put("first", contact.getFirstName());
                        tempObject.put("last", contact.getLastName());
                        tempObject.put("primary", contact.getPrimaryPhoneNumber());
                        tempObject.put("secondary", contact.getSecondaryPhoneNumber());
                        tempObject.put("email", contact.getEmailId());
                        jsonArray.put(tempObject);
                        jsonObject.put("Contacts", jsonArray);
                        exportStream.write(jsonObject.toString().getBytes());
                    }
                }
                if (which == 1) {
                    byte[] begin = "BEGIN:VCARD\n".getBytes();
                    byte[] version = "VERSION:2.1\n".getBytes();
                    byte[] end = "END:VCARD\n".getBytes();
                    for (int i = 0; i < adapter.getItemCount(); i++) {
                        Contact contact = adapter.getContactAt(i);
                        exportStream.write(begin);
                        exportStream.write(version);
                        byte[] n = ("N:" + contact.getLastName() + ";" + contact.getFirstName() + ";;;\n").getBytes();
                        exportStream.write(n);
                        byte[] fn = ("FN:" + contact.getFirstName() + " " + contact.getLastName() + "\n").getBytes();
                        exportStream.write(fn);
                        byte[] tel1 = ("TEL;CELL;PREF:" + contact.getPrimaryPhoneNumber() + "\n").getBytes();
                        exportStream.write(tel1);
                        byte[] tel2 = ("TEL;CELL:" + contact.getSecondaryPhoneNumber() + "\n").getBytes();
                        exportStream.write(tel2);
                        byte[] email = ("EMAIL;HOME:" + contact.getEmailId() + "\n").getBytes();
                        exportStream.write(email);
                        exportStream.write(end);
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return "Contacts Exported to: " + path;
        }
    }

} //end of main activity


