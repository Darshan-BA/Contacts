package com.ba.contacts;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDao {

    @Insert
    void insert(Contact contact);

    @Update
    void update(Contact contact);

    @Delete
    void delete(Contact contact);

    @Delete
    void multipleDelete(Contact... contacts);

    @Query("SELECT * FROM contacts ORDER BY first_name COLLATE NOCASE ASC")
    LiveData<List<Contact>>getAllContacts();

    @Query("SELECT * FROM contacts ORDER BY last_name COLLATE NOCASE ASC ")
    LiveData<List<Contact>>getAllContactsByLastName();
    //IfNULL(last_name),
}
