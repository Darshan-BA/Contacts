package com.ba.contacts.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "friends_list")
public class FriendsList {
    @PrimaryKey
    @ColumnInfo(name = "friends_key")
    private int friend;

    public int getFriend() {
        return friend;
    }

    public FriendsList(int friend) {
        this.friend = friend;
    }
}
