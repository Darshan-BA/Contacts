package com.ba.contacts;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class EditContact extends AppCompatActivity {
    Contact contact;
    ContactViewModel contactViewModel;
    Toolbar toolbar;
    EditText firstName,lastName,emailAddress,primaryPhoneNumber,secondaryPhoneNumber;
    MaterialButton cancelButton,saveButton,editButton;
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
            saveButton.setVisibility(View.GONE);
            editButton.getVisibility();
            editButton.setVisibility(View.VISIBLE);
        }else {
            actionBar.setTitle("Add Contact");
        }


        contactViewModel=new ViewModelProvider(this).get(ContactViewModel.class);
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
                    contact=new Contact(first,second,primary,secondary,email);
                    contactViewModel.insert(contact);
                    Toast.makeText(EditContact.this,"Contact Saved",Toast.LENGTH_SHORT).show();
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
                    contact=new Contact(first,second,primary,secondary,email);
                    contact.setId(id);
                    contactViewModel.update(contact);
                    Toast.makeText(EditContact.this,"Contact Updated",Toast.LENGTH_SHORT).show();
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

