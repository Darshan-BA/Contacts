package com.ba.contacts;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
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
    private int primaryPhoneNumber;

    @ColumnInfo(name="secondary_number")
    private int secondaryPhoneNumber;

    @ColumnInfo(name = "email_id")
    private  String emailId;

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getPrimaryPhoneNumber() {
        return primaryPhoneNumber;
    }

    public int getSecondaryPhoneNumber() {
        return secondaryPhoneNumber;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPrimaryPhoneNumber(int primaryPhoneNumber) {
        this.primaryPhoneNumber = primaryPhoneNumber;
    }

    public void setSecondaryPhoneNumber(int secondaryPhoneNumber) {
        this.secondaryPhoneNumber = secondaryPhoneNumber;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Contact(String firstName, String lastName, int primaryPhoneNumber, int secondaryPhoneNumber, String emailId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.primaryPhoneNumber = primaryPhoneNumber;
        this.secondaryPhoneNumber = secondaryPhoneNumber;
        this.emailId = emailId;
    }

    public Contact(String firstName, int primaryPhoneNumber) {
        this.firstName = firstName;
        this.primaryPhoneNumber = primaryPhoneNumber;
    }
}
