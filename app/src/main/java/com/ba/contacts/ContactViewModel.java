package com.ba.contacts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ContactViewModel extends AndroidViewModel {
    private ContactRepository repository;
    private LiveData<List<Contact>>allContacts;

    public ContactViewModel(@NonNull Application application) {
        super(application);
        repository=new ContactRepository(application);
        allContacts=repository.getAllContacts();
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
    LiveData<List<Contact>>getAllContacts(){
        return allContacts;
    }

}
