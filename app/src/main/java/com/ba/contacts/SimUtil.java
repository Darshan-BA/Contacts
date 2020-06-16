package com.ba.contacts;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;

import com.ba.contacts.Entities.Contact;

import java.util.ArrayList;
import java.util.List;

public class SimUtil {
    private Activity activity;
    private ContentResolver contentResolver;
    Uri simUri=Uri.parse("content://icc/adn");

    public SimUtil(Activity activity) {
        this.activity = activity;
        this.contentResolver=activity.getContentResolver();
    }

    public List<Contact>getSimContacts(){
        List<Contact>simContactsList=new ArrayList<>();
        Cursor cursor=contentResolver.query(simUri,null,null,null, Contacts.PeopleColumns.NAME);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String number = cursor.getString(cursor.getColumnIndex("number"));
            Contact contact=new Contact(name,"",number,"","","");
            simContactsList.add(contact);
        }
        return simContactsList;
    }
    public int deleteSimContact(Contact contact){
        String where="tag='"+contact.getFirstName()+"' AND number='"+contact.getPrimaryPhoneNumber()+"'";
        return contentResolver.delete(simUri,where,null);
    }
    public int addSimContact(SimContact simContact){
        ContentValues contentValues=new ContentValues();
        String name=simContact.getName();
        String phonenumber=simContact.getNumber();
        contentValues.put("tag",name);
        contentValues.put("number",phonenumber);
        Uri addStatus=contentResolver.insert(simUri,contentValues);
        if(addStatus!=null)
            return 1;
        return 0;
    }
}
