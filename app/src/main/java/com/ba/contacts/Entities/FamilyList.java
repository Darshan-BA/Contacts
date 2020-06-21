package com.ba.contacts.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;

@Entity(tableName = "family_list")
public class FamilyList {
    @PrimaryKey
    @ColumnInfo(name = "family_key")
    private int family;

    public int getFamily() {
        return family;
    }

    public FamilyList(int family) {
        this.family = family;
    }

    public void setFamily(int family) {
        this.family = family;
    }

    public FamilyList() {
    }
}
