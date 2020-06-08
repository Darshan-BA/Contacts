package com.ba.contacts;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
//import com.ba.contacts.DAOs.*;

@Dao
public interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Contact contact);

    @Update
    void update(Contact contact);

    @Delete
    int delete(Contact contact);

    @Delete
    void multipleDelete(Contact... contacts);

    @Query("SELECT * FROM contacts ORDER BY first_name COLLATE NOCASE ASC,last_name COLLATE NOCASE ASC")
    LiveData<List<Contact>>getAllContacts();

    @Query("SELECT * FROM contacts ORDER BY last_name COLLATE NOCASE ASC,first_name COLLATE NOCASE ASC")
    LiveData<List<Contact>>getAllContactsByLastName();
    //IfNULL(last_name),

    @Query("SELECT * FROM  contacts LEFT JOIN family_list ON contacts.id=family_list.family_key WHERE id=family_key")
    LiveData<List<Contact>>getAllFamilyContacts();


    @Query("SELECT * FROM  contacts LEFT JOIN friends_list ON contacts.id=friends_list.friends_key WHERE id=friends_key")
    LiveData<List<Contact>>getAllFriendsContacts();

}
