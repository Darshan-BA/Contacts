package com.ba.contacts;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
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
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class EditContact extends AppCompatActivity {
    Contact contact;
    ContactViewModel contactViewModel;
    Toolbar toolbar;
    EditText firstName,lastName,emailAddress,primaryPhoneNumber,secondaryPhoneNumber;
    MaterialButton cancelButton,saveButton,editButton;
    ImageView photo;
    private Uri pUri;
    private String photoPath;
    private Bitmap photoBitmap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode == RESULT_OK && data != null){
            Uri uri=data.getData();
            new PhotoLoaderAsyncTask(this.getContentResolver()).execute(uri);
            Log.d("edit",String.valueOf(uri));
            /*try {
                assert uri != null;
                Bitmap bitmap= ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.getContentResolver(),uri));
                File file=new File(getApplicationContext().getFilesDir(),"Photos");
                Log.d("edit","filepath1="+file.getAbsolutePath());
                File photoFile=new File(file,"Photo-"+dateTime+".png");
                if(!file.exists()){
                    file.mkdir();
                    Log.d("edit","filepath2="+file.getAbsolutePath());
                }
                try(FileOutputStream fileOutputStream=new FileOutputStream(photoFile)){
                    bitmap.compress(Bitmap.CompressFormat.PNG,50,fileOutputStream);
                    String pf=photoFile.getAbsolutePath();
                    Log.d("edit","pf="+photoFile.getAbsolutePath());
                }
                FileInputStream fileInputStream=new FileInputStream(photoFile);
                Bitmap bitmap1= BitmapFactory.decodeStream(fileInputStream);
                photo.setImageBitmap(bitmap1);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        //View widgets initialization
        firstName=findViewById(R.id.firstname_edit);
        lastName=findViewById(R.id.lastname_edit);
        emailAddress=findViewById(R.id.email_edit);
        primaryPhoneNumber=findViewById(R.id.primary_edit);
        secondaryPhoneNumber=findViewById(R.id.secondary_edit);
        cancelButton=findViewById(R.id.cancel_button);
        saveButton=findViewById(R.id.save_button);
        editButton=findViewById(R.id.update_button);
        photo=findViewById(R.id.circlr_image);
        photo.setImageResource(R.drawable.add_photo);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(photoIntent,1);
            }
        });

        //Toolbar initialization
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.primaryTextColor,null));
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();

        //fetched Intent extras
        Intent intent=getIntent();
        if(intent.hasExtra("id")) {
            actionBar.setTitle("Edit Contact");
            firstName.setText(intent.getStringExtra("first"));
            lastName.setText(intent.getStringExtra("last"));
            primaryPhoneNumber.setText(intent.getStringExtra("primary"));
            secondaryPhoneNumber.setText(intent.getStringExtra("secondary"));
            emailAddress.setText(intent.getStringExtra("email"));
            //Uri pathuri=Uri.parse(intent.getStringExtra("photoPath"));
            //new PhotoLoaderAsyncTask(this.getContentResolver()).execute(pathuri);
            if(Objects.equals(intent.getStringExtra("photoPath"), "")){
                photo.setImageResource(R.drawable.add_photo);
            }else{
                photo.setImageBitmap(BitmapFactory.decodeFile(intent.getStringExtra("photoPath")));
            }
            saveButton.setVisibility(View.GONE);
            editButton.getVisibility();
            editButton.setVisibility(View.VISIBLE);
        }else {
            actionBar.setTitle("Add Contact");
        }
        contactViewModel=new ViewModelProvider(this).get(ContactViewModel.class);

    }
    private class PhotoSaveAsyncTask extends AsyncTask<String,Void,String>{
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
            Toast.makeText(EditContact.this,s,Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("ddMMyy-HHmmss");
            String dateTime=simpleDateFormat.format(new Date());
            String first=strings[0];
            String last=strings[1];
            String primary=strings[2];
            String secondary=strings[3];
            String email=strings[4];
            String photoPath="";
            File file=new File(getApplicationContext().getFilesDir(),"Photos");
            Log.d("edit","filepath1="+file.getAbsolutePath());
            File photoFile=new File(file,"Photo-"+dateTime+".png");
            if(!file.exists()){
                file.mkdir();
                Log.d("edit","filepath2="+file.getAbsolutePath());
            }
            if(photoBitmap!=null) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(photoFile)) {
                    photoBitmap.compress(Bitmap.CompressFormat.PNG, 50, fileOutputStream);
                    photoPath=photoFile.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d("photoPath","photoPath:"+photoPath);
            if(which==0){
                contact=new Contact(first,last,primary,secondary,email,photoPath);
                contactViewModel.insert(contact);
                return "Contact Saved";
            }
            if(which==1){
                contact=new Contact(first,last,primary,secondary,email,photoPath);
                contact.setId(id);
                contactViewModel.update(contact);
                return "Contact Updated";
            }
            return "Contact not Saved";
        }
    }
    private class PhotoLoaderAsyncTask extends AsyncTask<Uri,Void,Bitmap> {

        ContentResolver contentResolver;

        PhotoLoaderAsyncTask(ContentResolver contentResolver) {
            this.contentResolver = contentResolver;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            photo.setImageBitmap(bitmap);
            photoBitmap=bitmap;
        }

        @Override
        protected Bitmap doInBackground(Uri... uris) {
            Bitmap bitmap= null;
            try {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver,uris[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }
    //Dialog when clicked on cancel button
    public void cancelDialog(View view) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
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
    }

    //save button dialog
    public void saveDialog(View view) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Save");
        builder.setMessage("Press Confirm to Save");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String first=firstName.getText().toString().trim();
                String second=lastName.getText().toString().trim();
                String primary=primaryPhoneNumber.getText().toString().trim();
                String secondary=secondaryPhoneNumber.getText().toString().trim();
                String email=emailAddress.getText().toString().trim();
                if(first.isEmpty() && second.isEmpty() && primary.isEmpty()
                        && secondary.isEmpty() && email.isEmpty()){
                    Toast.makeText(EditContact.this,"All Fields Are Empty",Toast.LENGTH_SHORT).show();
                }else{
                    new PhotoSaveAsyncTask(0).execute(first,second,primary,secondary,email);
                    //contact=new Contact(first,second,primary,secondary,email);
                    //contactViewModel.insert(contact);
                    //Toast.makeText(EditContact.this,"Contact Saved",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        builder.setNegativeButton("NO",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //update button dialog
    public void updateDialog(View view) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Update");
        builder.setMessage("Press Confirm to Update");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String first=firstName.getText().toString().trim();
                String second=lastName.getText().toString().trim();
                String primary=primaryPhoneNumber.getText().toString().trim();
                String secondary=secondaryPhoneNumber.getText().toString().trim();
                String email=emailAddress.getText().toString().trim();
                int id=getIntent().getIntExtra("id",-1);
                if(id == -1){
                    Toast.makeText(EditContact.this,"Contact can not be update",Toast.LENGTH_SHORT).show();
                }
                if(first.isEmpty() && second.isEmpty() && primary.isEmpty()
                        && secondary.isEmpty() && email.isEmpty()){
                    Toast.makeText(EditContact.this,"All Fields Are Empty",Toast.LENGTH_SHORT).show();
                }else{
                    //contact=new Contact(first,second,primary,secondary,email,"");
                    //contact.setId(id);
                    //contactViewModel.update(contact);
                    new PhotoSaveAsyncTask(1,id).execute(first,second,primary,secondary,email);
                    finish();
                }


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

        @Override
    public void onBackPressed() {
        cancelDialog(null);
    }
}

