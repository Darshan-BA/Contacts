package com.ba.contacts;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

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
}
