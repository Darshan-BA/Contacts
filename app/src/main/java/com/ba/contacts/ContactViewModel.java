package com.ba.contacts;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ba.contacts.Entities.FamilyList;
import com.ba.contacts.Entities.FriendsList;

import java.util.List;

public class ContactViewModel extends AndroidViewModel {
    private ContactRepository repository;
    private LiveData<List<Contact>>allContacts;
    private LiveData<List<Contact>>allContactsByLastName;

    private LiveData<List<Contact>> allFamilyContacts;
    private LiveData<List<Contact>> allFriendsContacts;

    private LiveData<List<Contact>>allSimContacts;

    public ContactViewModel(@NonNull Application application) {
        super(application);
        repository=new ContactRepository(application);
        allContacts=repository.getAllContacts();
        allContactsByLastName=repository.getAllContactsByLastName();
        allFamilyContacts=repository.getAllFamilyContacts();
        allFriendsContacts=repository.getAllFriendsContacts();
    }
    public void insert(Contact contact){
        repository.insert(contact);
    }
    public void insetWithGroup(Contact contact,String group){
        repository.insetWithGroup(contact,group);
    }
    public void update(Contact contact){
        repository.update(contact);
    }
    public void delete(Contact contact){
        repository.delete(contact);
    }

    //multiple delete
    public void multipleDelete(Contact... contacts){
        repository.multipleDelete(contacts);
    }

    //group
    public void insertFriend(FriendsList friendsList,int which){
        repository.insertFriends(friendsList,which);
    }
    public void insertFamily(FamilyList familyList,int which){
        repository.insertFamilies(familyList,which);
    }

    public void addContactToGroup(String group,Integer... ids){
        repository.addContactToGroup(group,ids);
    }

    public void removeContactFromGroup(String group,Integer... ids){
        repository.removeContactFromGroup(group,ids);
    }

    public List<Contact>getAllSimContacts(Activity activity){
        return repository.getAllSimContacts(activity);
    }
    public int deleteSimContact(Activity activity,Contact contact){
        return repository.deleteSimContact(activity,contact);
    }

    public LiveData<List<Contact>>getAllContacts(){
        return allContacts;
    }
    public LiveData<List<Contact>>getAllContactsByLastName(){
        return allContactsByLastName;
    }
    public LiveData<List<Contact>>getAllFamilyContacts(){
        return allFamilyContacts;
    }
    public LiveData<List<Contact>>getAllFriendsContacts(){
        return allFriendsContacts;
    }
}
