package com.ba.contacts.DAOs;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ba.contacts.Entities.FamilyList;

@Dao
public interface FamilyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFamily(FamilyList familyList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFamilies(FamilyList... familyLists);

    @Query("DELETE FROM family_list WHERE family_key= :ids")
    void removeFamilyContacts(int... ids);

    @OnConflictStrategy
    @Query("INSERT INTO family_list(family_key) VALUES(:ids) ")
    void addToFamilyList(int... ids);

    @Query("INSERT INTO family_list(family_key) VALUES(:ids) ")
    void addToFamilyList(Integer[] ids);

}
