package com.ba.contacts;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName ="contacts")
public class Contact {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;

    @ColumnInfo(name = "primary_number")
    private String primaryPhoneNumber;

    @ColumnInfo(name="secondary_number")
    private String secondaryPhoneNumber;

    @ColumnInfo(name = "email_id")
    private  String emailId;

    public int getId() {
        return id;
    }

    String getFirstName() {
        return firstName;
    }

    String getLastName() {
        return lastName;
    }

    String getPrimaryPhoneNumber() {
        return primaryPhoneNumber;
    }

    String  getSecondaryPhoneNumber() {
        return secondaryPhoneNumber;
    }

    String getEmailId() {
        return emailId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Contact(String firstName, String lastName, String primaryPhoneNumber, String secondaryPhoneNumber, String emailId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.primaryPhoneNumber = primaryPhoneNumber;
        this.secondaryPhoneNumber = secondaryPhoneNumber;
        this.emailId = emailId;
    }

}
