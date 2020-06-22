package com.ba.contacts.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ba.contacts.SettingsSharedPref;
import com.ba.contacts.ViewModels.ContactViewModel;
import com.ba.contacts.R;
import com.ba.contacts.SimContact;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class AddSimContact extends AppCompatActivity {
    MaterialButton save, cancel;
    TextInputLayout phone, name;
    TextInputEditText phone_edit, name_edit;
    private ContactViewModel contactViewModel;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SettingsSharedPref.getInstance().getTheme().equals("0"))
            setTheme(R.style.darkTheme);
        else
            setTheme(R.style.lightTheme);
        setContentView(R.layout.activity_add_sim_contact);
        toolbar=findViewById(R.id.toolbar_add_sim_activity);
        toolbar.setTitle("Add SIM Contact");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_baseline_arrow);
        save = findViewById(R.id.save_addSimContact);
        cancel = findViewById(R.id.cancel_addSimContact);
        phone = findViewById(R.id.primary_addSimContact_txtlay);
        name = findViewById(R.id.name_addSimContact_txtlay);
        phone_edit = findViewById(R.id.primary_addSimContact);
        name_edit = findViewById(R.id.name_addSimContact);
        contactViewModel=new ContactViewModel(getApplication());
    }

    @Override
    public void onBackPressed() {
        cancelDialog(null);
    }
    public void cancelDialog(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddSimContact.this);
        builder.setCancelable(false);
        builder.setTitle("Cancel");
        builder.setMessage("Do you want to Cancel?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent();
                intent.putExtra("addStatus",0);
                setResult(1,intent);
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }
    public void saveDialog(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddSimContact.this);
        builder.setCancelable(false);
        builder.setTitle("Save");
        builder.setMessage("Press Confirm to Save");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String phoneNumber=phone_edit.getText().toString().trim();
                String fullName=name_edit.getText().toString().trim();
                if(fullName.equals("")){
                   name.setError("Can't be empty");
                }
                else if(phoneNumber.equals("")){
                    phone.setError("Can't be empty");
                }else {
                    SimContact simContact=new SimContact(fullName,phoneNumber);
                    int i=contactViewModel.addSimContact(AddSimContact.this,simContact);
                    Log.d("addStatus","i="+i);
                    if(i>0){
                        Intent intent=new Intent();
                        intent.putExtra("addStatus",i);
                        setResult(1,intent);
                        finish();
                    }
                }
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }
}