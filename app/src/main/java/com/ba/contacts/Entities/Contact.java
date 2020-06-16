package com.ba.contacts.Entities;

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
    private String primaryPhoneNumber;

    @ColumnInfo(name="secondary_number")
    private String secondaryPhoneNumber;

    @ColumnInfo(name = "email_id")
    private  String emailId;

    @ColumnInfo(name="path")
    private String photoPath;

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public String getPrimaryPhoneNumber() {
        return primaryPhoneNumber;
    }

    public String  getSecondaryPhoneNumber() {
        return secondaryPhoneNumber;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Contact(String firstName, String lastName, String primaryPhoneNumber, String secondaryPhoneNumber, String emailId,String photoPath) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.primaryPhoneNumber = primaryPhoneNumber;
        this.secondaryPhoneNumber = secondaryPhoneNumber;
        this.emailId = emailId;
        this.photoPath=photoPath;
    }

}
