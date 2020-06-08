package com.ba.contacts.DAOs;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ba.contacts.Entities.FamilyList;
import com.ba.contacts.Entities.FriendsList;

@Dao
public interface FriendsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFriend(FriendsList friendsList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFriends(FriendsList... friendsList);

    @Query("DELETE FROM friends_list WHERE friends_key= :ids")
    void removeFamilyContacts(int... ids);

    @OnConflictStrategy
    @Query("INSERT INTO friends_list(friends_key) VALUES(:ids)  ")
    void  addToFriendsList(int... ids);

    @OnConflictStrategy
    @Query("INSERT INTO friends_list(friends_key) VALUES(:ids)")
    void  addToFriendsList(Integer[] ids);
}
