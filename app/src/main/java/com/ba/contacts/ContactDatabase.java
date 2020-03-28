package com.ba.contacts;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Contact.class},version = 1)
public abstract class ContactDatabase extends RoomDatabase {

    private static ContactDatabase contactDB;

    public abstract ContactDao contactDao();

    public static synchronized ContactDatabase getContactDB(Context context){
        if(contactDB==null){
            contactDB= Room.databaseBuilder(context.getApplicationContext(),ContactDatabase.class,"contact_database")
                    .fallbackToDestructiveMigration().build();
        }
        return contactDB;
    }
    private  static RoomDatabase.Callback roomCallback=new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(contactDB).execute();

        }
    };
    private static class PopulateDBAsyncTask extends AsyncTask<Void,Void,Void>{
        private ContactDao contactDao;
        private PopulateDBAsyncTask(ContactDatabase db){
            contactDao=db.contactDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            contactDao.insert(new Contact("Ambulance",108));
            contactDao.insert(new Contact("Police",100));
            return null;
        }
    }
}
