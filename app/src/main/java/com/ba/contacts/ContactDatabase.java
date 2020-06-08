package com.ba.contacts;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.ba.contacts.DAOs.FamilyDao;
import com.ba.contacts.DAOs.FriendsDao;
import com.ba.contacts.Entities.FamilyList;
import com.ba.contacts.Entities.FriendsList;

@Database(entities = {Contact.class, FriendsList.class, FamilyList.class},version = 6)
public abstract class ContactDatabase extends RoomDatabase {

    private static ContactDatabase instance;

    public abstract ContactDao contactDao();
    //group
    public abstract FriendsDao friendsDao();
    public abstract FamilyDao familyDao();

    static synchronized ContactDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),ContactDatabase.class,"contact_database")
                    .fallbackToDestructiveMigration().addCallback(roomCallback).build();
        }
        return instance;
    }
    private  static RoomDatabase.Callback roomCallback=new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(instance).execute();

        }
    };
    private static class PopulateDBAsyncTask extends AsyncTask<Void,Void,Void>{
        private ContactDao contactDao;
        private PopulateDBAsyncTask(ContactDatabase db){
            contactDao=db.contactDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            contactDao.insert(new Contact("Ambulance","","100","","",""));
            contactDao.insert(new Contact("Police","","108","","",""));
            return null;
        }
    }
}
