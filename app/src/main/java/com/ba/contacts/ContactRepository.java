package com.ba.contacts;

import android.app.Activity;
import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.ba.contacts.DAOs.ContactDao;
import com.ba.contacts.DAOs.FamilyDao;
import com.ba.contacts.DAOs.FriendsDao;
import com.ba.contacts.Entities.Contact;
import com.ba.contacts.Entities.FamilyList;
import com.ba.contacts.Entities.FriendsList;

import java.util.List;

public class ContactRepository {
    private ContactDao contactDao;
    //group
    private FriendsDao friendsDao;
    private FamilyDao familyDao;

    private LiveData<List<Contact>> allContacts;
    private LiveData<List<Contact>> allContactsByLastName;

    private LiveData<List<Contact>> allFamilyContacts;
    private LiveData<List<Contact>> allFriendsContacts;

    private List<Contact> allSimContacts;
    private SimUtil simUtil;

   public ContactRepository(Application application) {
        ContactDatabase database = ContactDatabase.getInstance(application);
        contactDao = database.contactDao();
        friendsDao = database.friendsDao();
        familyDao = database.familyDao();
        allContacts = contactDao.getAllContacts();
        allContactsByLastName = contactDao.getAllContactsByLastName();
        allFamilyContacts=contactDao.getAllFamilyContacts();
        allFriendsContacts=contactDao.getAllFriendsContacts();
    }

    public void insert(Contact contact) {
      new InsertContactAsyncTask(contactDao,familyDao,friendsDao,"").execute(contact);
    }

    public void insetWithGroup(Contact contact,String group){
        new InsertContactAsyncTask(contactDao,familyDao,friendsDao,group).execute(contact);
    }

    public void update(Contact contact) {
        new UpdateContactAsyncTask(contactDao).execute(contact);
    }

    public void delete(Contact contact) {
        new DeleteContactAsyncTask(contactDao,familyDao,friendsDao).execute(contact);
    }

    //multiple delete
    public void multipleDelete(Contact... contacts) {
        new multipleDeleteContactAsyncTask(contactDao).execute(contacts);
    }

    //group
    public void insertFamilies(FamilyList familyList,int which){
        new FamilyListAsyncTask(familyDao,which).execute(familyList);
    }
    public void insertFriends(FriendsList friendsList,int which){
        new FriendsListAsyncTask(friendsDao,which).execute(friendsList);
    }

    public void addContactToGroup(String group, Integer... ids){
        new GroupAsyncTak(familyDao,friendsDao,group).execute(ids);
    }

    public void removeContactFromGroup(String group, Integer... ids){
        new RemoveContactFromGroup(familyDao,friendsDao,group).execute(ids);
    }

    public LiveData<List<Contact>> getAllContacts() {
        return allContacts;
    }

    public LiveData<List<Contact>> getAllContactsByLastName() {
        return allContactsByLastName;
    }

    public LiveData<List<Contact>>getAllFamilyContacts(){
        return allFamilyContacts;
    }
    public LiveData<List<Contact>>getAllFriendsContacts(){
        return allFriendsContacts;
    }

    public List<Contact>getAllSimContacts(Activity activity){
        simUtil=new SimUtil(activity);
        allSimContacts=simUtil.getSimContacts();
        return allSimContacts;
    }

    public int deleteSimContact(Activity activity,Contact contact){
        simUtil=new SimUtil(activity);
        return simUtil.deleteSimContact(contact);
    }

    public int addSimContact(Activity activity,SimContact simContact){
        simUtil=new SimUtil(activity);
        return simUtil.addSimContact(simContact);
    }

    public Contact getDuplicateContact(String first,String last){
       return contactDao.getDuplicateContact(first,last);
    }
    public Contact getContactAt(int id){
       return contactDao.getContactAt(id);
    }

    private static class InsertContactAsyncTask extends AsyncTask<Contact, Void, Void> {
        private ContactDao contactDao;
        private FamilyDao familyDao;
        private FriendsDao friendsDao;
        private String group;

        private InsertContactAsyncTask(ContactDao contactDao,FamilyDao familyDao,FriendsDao friendsDao,String group) {
            this.contactDao = contactDao;
            this.familyDao=familyDao;
            this.friendsDao=friendsDao;
            this.group=group;
        }

        @Override
        protected Void doInBackground(Contact... contacts) {
            int id=(int)contactDao.insert(contacts[0]);
            FamilyList familyList=new FamilyList();
            familyDao.insertFamilies(familyList);
            if(group.equals("Friends")){
                friendsDao.addToFriendsList(id);
            }
            if(group.equals("Family")){
                familyDao.addToFamilyList(id);
            }
            return null;
        }
    }

    private static class UpdateContactAsyncTask extends AsyncTask<Contact, Void, Void> {
        private ContactDao contactDao;

        private UpdateContactAsyncTask(ContactDao contactDao) {
            this.contactDao = contactDao;
        }

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactDao.update(contacts[0]);
            return null;
        }
    }
    private static class RemoveContactFromGroup extends AsyncTask<Integer,Void,Void>{
        private FamilyDao familyDao;
        private FriendsDao friendsDao;
        private String group;

        public RemoveContactFromGroup(FamilyDao familyDao, FriendsDao friendsDao, String group) {
            this.familyDao = familyDao;
            this.friendsDao = friendsDao;
            this.group = group;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            if(group.equals("Friends")){
                friendsDao.removeFriendsContacts(integers[0].intValue());
            }
            if(group.equals("Family")){
                familyDao.removeFamilyContacts(integers[0].intValue());
            }
            return null;
        }
    }

    private static class DeleteContactAsyncTask extends AsyncTask<Contact, Void, Void> {
        private ContactDao contactDao;
        private FamilyDao familyDao;
        private FriendsDao friendsDao;

        private DeleteContactAsyncTask(ContactDao contactDao,FamilyDao familyDao,FriendsDao friendsDao) {
            this.contactDao = contactDao;
            this.familyDao=familyDao;
            this.friendsDao=friendsDao;
        }

        @Override
        protected Void doInBackground(Contact... contacts) {
            int id=contactDao.delete(contacts[0]);
            familyDao.removeFamilyContacts(id);
            friendsDao.removeFriendsContacts(id);
            return null;
        }
    }

    //multiple delete
    private static class multipleDeleteContactAsyncTask extends AsyncTask<Contact, Void, Void> {
        private ContactDao contactDao;

        multipleDeleteContactAsyncTask(ContactDao contactDao) {
            this.contactDao = contactDao;
        }

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactDao.multipleDelete(contacts);
            return null;
        }
    }

    private static class FriendsListAsyncTask extends AsyncTask<FriendsList, Void, Void> {

        FriendsDao friendsDao;
        private int which;

        public FriendsListAsyncTask(FriendsDao friendsDao, int which) {
            this.friendsDao = friendsDao;
            this.which = which;
        }

        @Override
        protected Void doInBackground(FriendsList... friendsLists) {
            if (which == 0){
                    friendsDao.insertFriend(friendsLists[0]);
                }

            if (which == 1)
                friendsDao.insertFriends(friendsLists);
            return null;
        }
    }
    private static class FamilyListAsyncTask extends AsyncTask<FamilyList,Void,Void>{
        FamilyDao familyDao;
        private int which;

        public FamilyListAsyncTask(FamilyDao familyDao, int which) {
            this.familyDao = familyDao;
            this.which = which;
        }

        @Override
        protected Void doInBackground(FamilyList... familyLists) {
            if(which==0)
                familyDao.insertFamily(familyLists[0]);
            if(which==1)
                familyDao.insertFamilies(familyLists);
            return null;
        }
    }
    private static class GroupAsyncTak extends AsyncTask<Integer,Void,Void>{
        private FamilyDao familyDao;
        private FriendsDao friendsDao;
        private String group;

        public GroupAsyncTak(FamilyDao familyDao,FriendsDao friendsDao,String group) {
            this.familyDao = familyDao;
            this.friendsDao=friendsDao;
            this.group=group;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            if(group.equals("Friends")){
                for (int i = 0; i< integers.length; i++){
                    friendsDao.insertFriends(new FriendsList(integers[i]));
                    //friendsDao.addToFriendsList(integers[i]);
                }
            }
            if(group.equals("Family")){
                for (int i = 0; i< integers.length; i++) {
                    familyDao.insertFamilies(new FamilyList(integers[i]));
                    //familyDao.addToFamilyList(integers[i]);
                }
            }
            return null;
        }
    }

}
