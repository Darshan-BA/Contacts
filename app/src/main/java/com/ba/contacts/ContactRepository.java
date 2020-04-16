package com.ba.contacts;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;

import java.util.List;

class ContactRepository {
    private ContactDao contactDao;
    private LiveData<List<Contact>> allContacts;
    //private List<Contact> exportList;

    ContactRepository(Application application) {
        ContactDatabase database = ContactDatabase.getInstance(application);
        contactDao = database.contactDao();
        allContacts = contactDao.getAllContacts();
        //exportList=contactDao.getExportContactsList();
    }

    void insert(Contact contact) {
        new InsertContactAsyncTask(contactDao).execute(contact);
    }

    void update(Contact contact) {
        new UpdateContactAsyncTask(contactDao).execute(contact);
    }

    void delete(Contact contact) {
        new DeleteContactAsyncTask(contactDao).execute(contact);
    }

    //multiple delete
    void multipleDelete(Contact... contacts) {
        new multipleDeleteContactAsyncTask(contactDao).execute(contacts);
    }

    LiveData<List<Contact>> getAllContacts() {
        return allContacts;
    }

    /*export
    List<Contact>getExportList(){
        return contactDao.getExportContactsList();
    }*/


    private static class InsertContactAsyncTask extends AsyncTask<Contact, Void, Void> {
        private ContactDao contactDao;

        private InsertContactAsyncTask(ContactDao contactDao) {
            this.contactDao = contactDao;
        }

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactDao.insert(contacts[0]);
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

    private static class DeleteContactAsyncTask extends AsyncTask<Contact, Void, Void> {
        private ContactDao contactDao;

        private DeleteContactAsyncTask(ContactDao contactDao) {
            this.contactDao = contactDao;
        }

        @Override
        protected Void doInBackground(Contact... contacts) {
            contactDao.delete(contacts[0]);
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

}
