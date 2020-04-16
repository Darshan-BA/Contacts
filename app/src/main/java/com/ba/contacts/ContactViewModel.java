package com.ba.contacts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ContactViewModel extends AndroidViewModel {
    private ContactRepository repository;
    private LiveData<List<Contact>>allContacts;
    //private List<Contact>allExportContacts;

    public ContactViewModel(@NonNull Application application) {
        super(application);
        repository=new ContactRepository(application);
        allContacts=repository.getAllContacts();
        //allExportContacts=repository.getExportList();
    }
    void insert(Contact contact){
        repository.insert(contact);
    }
    void update(Contact contact){
        repository.update(contact);
    }
    void delete(Contact contact){
        repository.delete(contact);
    }

    //multiple delete
    void multipleDelete(Contact... contacts){
        repository.multipleDelete(contacts);
    }
    LiveData<List<Contact>>getAllContacts(){
        return allContacts;
    }

    /*List<Contact>getAllExportContacts(){
        return allExportContacts;
    }*/

}
