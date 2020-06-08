package com.ba.contacts.Adapters;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ba.contacts.Contact;
import com.ba.contacts.ContactAdapter;
import com.ba.contacts.Entities.FamilyList;
import com.ba.contacts.R;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupHolder> {

    private boolean setMultiselect=false;
    List<Integer> ids=new ArrayList<>();

    private List<Contact> contacts = new ArrayList<>();
    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_cardview, parent, false);
        return new GroupAdapter.GroupHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupHolder holder, int position) {
        final Contact currentContact = contacts.get(position);
        holder.first.setText(currentContact.getFirstName());
        holder.last.setText(currentContact.getLastName());
        if(currentContact.getPhotoPath()!=null) {
            //Glide.with(mainActivity).load(new File(currentContact.getPhotoPath())).into(holder.icon);
            if (currentContact.getPhotoPath().equals("")) {
                holder.profilePic.setImageResource(R.drawable.photo_icon);
            }else {
                holder.profilePic.setImageBitmap(BitmapFactory.decodeFile(currentContact.getPhotoPath()));
            }
        }
        if(setMultiselect){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(false);
        }
        else {
            holder.checkBox.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    public Contact getContactAt(int position) {
        return contacts.get(position);
    }

    public void setSetMultiselect(boolean setMultiselect) {
        this.setMultiselect = setMultiselect;
    }
    public List<Integer> getIdList(){
        return ids;
    }

    class GroupHolder extends RecyclerView.ViewHolder{

        private TextView first,last;
        private ImageView profilePic;
        private MaterialCheckBox checkBox;

        public GroupHolder(@NonNull View itemView) {
            super(itemView);
            first=itemView.findViewById(R.id.group_firstname);
            last=itemView.findViewById(R.id.group_lastname);
            checkBox=itemView.findViewById(R.id.group_checkbox);
            profilePic=itemView.findViewById(R.id.group_icon);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    Contact cardContact = getContactAt(position);
                    int id=cardContact.getId();
                    if (checkBox.isChecked()) {
                        ids.add(id);
                    } else {
                        ids.remove(id);
                    }
                }
            });
        }
    }
}
