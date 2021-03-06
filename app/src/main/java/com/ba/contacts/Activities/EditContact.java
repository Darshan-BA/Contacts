package com.ba.contacts.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.ba.contacts.ContactRepository;
import com.ba.contacts.Entities.Contact;
import com.ba.contacts.SettingsSharedPref;
import com.ba.contacts.ViewModels.ContactViewModel;
import com.ba.contacts.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class EditContact extends AppCompatActivity {
    private Contact contact;
    private ContactViewModel contactViewModel;
    private Toolbar toolbar;
    private TextInputLayout firstNameLay, lastNameLay, emailAddressLay, primaryPhoneNumberLay, secondaryPhoneNumberLay, spinnerLay;
    private TextInputEditText firstName, lastName, emailAddress, primaryPhoneNumber, secondaryPhoneNumber;
    private ImageView photo;
    private Bitmap photoBitmap;
    private AutoCompleteTextView groupAutoCompleteTextView, saveOptionAutoCompleteText;
    private int saveUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //theme
        if (SettingsSharedPref.getInstance().getTheme().equals("0"))
            setTheme(R.style.lightTheme);
        else
            setTheme(R.style.darkTheme);
        setContentView(R.layout.activity_edit_contact);

        //View widgets initialization
        firstNameLay = findViewById(R.id.firstname_edit_layout);
        lastNameLay = findViewById(R.id.lastname_edit_layout);
        emailAddressLay = findViewById(R.id.email_edit_layout);
        primaryPhoneNumberLay = findViewById(R.id.primary_edit_layout);
        secondaryPhoneNumberLay = findViewById(R.id.secondary_edit_layout);
        spinnerLay = findViewById(R.id.spinner_edit_layout);
        firstName = findViewById(R.id.firstname_edit);
        lastName = findViewById(R.id.lastname_edit);
        emailAddress = findViewById(R.id.email_edit);
        primaryPhoneNumber = findViewById(R.id.primary_edit);
        secondaryPhoneNumber = findViewById(R.id.secondary_edit);
        photo = findViewById(R.id.circlr_image);
        photo.setImageResource(R.drawable.add_photo);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(photoIntent, 1);
            }
        });

        //autocomplete text view for group
        String[] groupNames = {"Family", "Friends"};
        ArrayAdapter<String> groupDropDownArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_item, groupNames);
        groupAutoCompleteTextView = findViewById(R.id.dropdown_edit);
        groupAutoCompleteTextView.setAdapter(groupDropDownArrayAdapter);
        groupAutoCompleteTextView.setInputType(InputType.TYPE_NULL);


        //Toolbar initialization
        toolbar = findViewById(R.id.toolbar_edit_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_baseline_arrow);


        //fetched Intent extras
        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            toolbar.setTitle("Edit Contact");
            firstName.setText(intent.getStringExtra("first"));
            lastName.setText(intent.getStringExtra("last"));
            primaryPhoneNumber.setText(intent.getStringExtra("primary"));
            secondaryPhoneNumber.setText(intent.getStringExtra("secondary"));
            emailAddress.setText(intent.getStringExtra("email"));
            if (Objects.equals(intent.getStringExtra("photoPath"), "")) {
                photo.setImageResource(R.drawable.add_photo);
            } else {
                photo.setImageBitmap(BitmapFactory.decodeFile(intent.getStringExtra("photoPath")));
            }
            spinnerLay.setVisibility(View.GONE);
            saveUpdate = 1;

        } else {
            toolbar.setTitle("Add Contact");
            saveUpdate = 0;
        }
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

    }//end of on create

    //onActivity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            new PhotoLoaderAsyncTask(this.getContentResolver()).execute(uri);
        }
    }//end of on activity result

    // option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editcontact_toolbar_ment, menu);
        return true;
    }//end of option menu creation

    // option menu item selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.save_editcontact_toolbar):
                if (saveUpdate == 0)
                    saveDialog();
                if (saveUpdate == 1)
                    updateDialog();
                return true;
            case (android.R.id.home):
                onBackPressed();
                return true;
        }
        return false;
    }//end of option menu item selected


    // Async task for adding and updating contacts with photo
    private class PhotoSaveAsyncTask extends AsyncTask<String, Void, String> {
        int which;
        int id;

        PhotoSaveAsyncTask(int which, int id) {
            this.which = which;
            this.id = id;
        }

        PhotoSaveAsyncTask(int which) {
            this.which = which;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(EditContact.this, s, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyy-HHmmss");
            String dateTime = simpleDateFormat.format(new Date());
            String first = strings[0];
            String last = strings[1];
            String primary = strings[2];
            String secondary = strings[3];
            String email = strings[4];
            String group = strings[5];
            String photoPath = "";
            File file = new File(getApplicationContext().getFilesDir(), "Photos");
            Log.d("edit", "filepath1=" + file.getAbsolutePath());
            File photoFile = new File(file, "Photo-" + dateTime + ".png");
            if (!file.exists()) {
                file.mkdir();
                Log.d("edit", "filepath2=" + file.getAbsolutePath());
            }
            if (photoBitmap != null) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(photoFile)) {
                    photoBitmap.compress(Bitmap.CompressFormat.PNG, 50, fileOutputStream);
                    photoPath = photoFile.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d("photoPath", "photoPath:" + photoPath);
            if (which == 0) {
                contact = new Contact(first, last, primary, secondary, email, photoPath);
                contactViewModel.insetWithGroup(contact, group);
                return "Contact Saved";
            }
            if (which == 1) {
                contact = new Contact(first, last, primary, secondary, email, photoPath);
                contact.setId(id);
                contactViewModel.update(contact);
                return "Contact Updated";
            }
            return "Contact not Saved";
        }
    }//end of async task for adding and updating contacts with photo


    // Async task for loading photo to image view
    private class PhotoLoaderAsyncTask extends AsyncTask<Uri, Void, Bitmap> {

        ContentResolver contentResolver;

        PhotoLoaderAsyncTask(ContentResolver contentResolver) {
            this.contentResolver = contentResolver;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            photo.setImageBitmap(bitmap);
            photoBitmap = bitmap;
        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        protected Bitmap doInBackground(Uri... uris) {
            Bitmap bitmap = null;
            try {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uris[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }//end of async task for loading photo to image view

    //Dialog when clicked on cancel button
    public void cancelDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Cancel");
        builder.setMessage("Do you want to Cancel?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }//end of cancel dialog


    //Dialog when clicled on save
    public void saveDialog() {
        String first = firstName.getText().toString().trim();
        String second = lastName.getText().toString().trim();
        String primary = primaryPhoneNumber.getText().toString().trim();
        String secondary = secondaryPhoneNumber.getText().toString().trim();
        String email = emailAddress.getText().toString().trim();
        String selectedGroup = groupAutoCompleteTextView.getText().toString().trim();
        if (first.isEmpty() && !second.isEmpty()) {
            firstNameLay.setError("Is Empty");
        } else if (first.isEmpty() && second.isEmpty() && primary.isEmpty()
                && secondary.isEmpty() && email.isEmpty()) {

            Toast.makeText(EditContact.this, "All Fields Are Empty", Toast.LENGTH_SHORT).show();
        } else {
            if (first.isEmpty() && !primary.isEmpty())
                first = primary;
            if (first.isEmpty() && !secondary.isEmpty() && primary.isEmpty())
                first = secondary;
            String finalFirst = first;
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    ContactRepository contactRepository = new ContactRepository(getApplication());
                    Contact duplicateContact = contactRepository.getDuplicateContact(finalFirst, second);
                    if (duplicateContact != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditContact.this);
                                builder.setCancelable(false);
                                builder.setTitle("Duplicate");
                                builder.setMessage("Contact already exits, Are You want merge?");
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(EditContact.this, "Yes clicked", Toast.LENGTH_SHORT).show();
                                        new PhotoSaveAsyncTask(1, duplicateContact.getId()).execute(finalFirst, second, primary, secondary, email, "");
                                        finish();
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(EditContact.this, "No clicked", Toast.LENGTH_SHORT).show();
                                        new PhotoSaveAsyncTask(0).execute(finalFirst, second, primary, secondary, email, selectedGroup);
                                        finish();
                                    }
                                });
                                builder.create();
                                builder.show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditContact.this);
                                builder.setCancelable(false);
                                builder.setTitle("Save");
                                builder.setMessage("Press Confirm to Save");
                                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new PhotoSaveAsyncTask(0).execute(finalFirst, second, primary, secondary, email, selectedGroup);
                                        finish();
                                    }
                                });
                                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                            }
                        });
                    }
                }
            }.start();
        }
    }//end of save dialog

    //update button dialog
    public void updateDialog() {
        String first = firstName.getText().toString().trim();
        String second = lastName.getText().toString().trim();
        String primary = primaryPhoneNumber.getText().toString().trim();
        String secondary = secondaryPhoneNumber.getText().toString().trim();
        String email = emailAddress.getText().toString().trim();
        int id = getIntent().getIntExtra("id", -1);
        if (first.isEmpty() && !second.isEmpty())
            if (id == -1) {
                Toast.makeText(EditContact.this, "Contact can not be update", Toast.LENGTH_SHORT).show();
            }
        if (first.isEmpty() && second.isEmpty() && primary.isEmpty()
                && secondary.isEmpty() && email.isEmpty()) {
            Toast.makeText(EditContact.this, "All Fields Are Empty", Toast.LENGTH_SHORT).show();
        } else {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    ContactRepository contactRepository = new ContactRepository(getApplication());
                    Contact duplicateContact = contactRepository.getDuplicateContact(first, second);
                    Contact originalContact=contactRepository.getContactAt(id);
                    if (duplicateContact.getId() == id) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditContact.this);
                                builder.setCancelable(false);
                                builder.setTitle("Update");
                                builder.setMessage("Press Confirm to Update");
                                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new PhotoSaveAsyncTask(1, id).execute(first, second, primary, secondary, email, "");
                                        finish();
                                    }
                                });
                                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditContact.this);
                                builder.setCancelable(false);
                                builder.setTitle("Duplicate");
                                builder.setMessage("Contact already exits, Are You want merge?");
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new PhotoSaveAsyncTask(1, duplicateContact.getId()).execute(first, second, primary, secondary, email, "");
                                        contactRepository.delete(originalContact);
                                        finish();
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new PhotoSaveAsyncTask(1, id).execute(first, second, primary, secondary, email, "");
                                        finish();
                                    }
                                });
                                builder.create();
                                builder.show();
                            }
                        });
                    }
                }
            }.start();
        }//end of update dialog
    }

    // Back button press
    @Override
    public void onBackPressed() {
        cancelDialog(null);
    }//end of back button press

}//end of activity

