package com.ba.contacts.Adapters;


import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ba.contacts.Entities.Contact;
import com.ba.contacts.R;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> implements Filterable {

    private List<Contact> contacts = new ArrayList<>();
    private List<Contact> simContacts=new ArrayList<>();
    private List<Contact> duplicateContacts;

    private List<Contact> multiSelectedContacts=new ArrayList<>();

    private boolean setMultiDelete=false;
    private OnItemClickListner mListener;

    public void setSetMultiDelete(boolean setMultiDelete) {
        this.setMultiDelete = setMultiDelete;
    }

    public Contact getContactAt(int position) {
        return contacts.get(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String queryChar=constraint.toString();
                if(queryChar.isEmpty()){
                    contacts=duplicateContacts;
                }else{
                    List<Contact>filterContacts=new ArrayList<>();
                    for(Contact row:duplicateContacts){
                        if(row.getFirstName().toLowerCase().contains(queryChar.toLowerCase()) || row.getLastName().toLowerCase().contains(queryChar.toLowerCase()) || row.getPrimaryPhoneNumber().contains(
                                queryChar) || row.getSecondaryPhoneNumber().contains(queryChar)){
                            filterContacts.add(row);
                        }
                    }
                    contacts=filterContacts;
                }
                FilterResults filterResults=new FilterResults();
                filterResults.values=contacts;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                contacts=(ArrayList<Contact>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnItemClickListner {
        void onCardClick(int position);
        void onPopUpClick(Contact contact, View view);
        void onIconClick(int position,View view);
        void setContextualActionMode();
        void multiSelect(int adapterPosition,boolean check);
    }

    public void setOnItemClickListener(OnItemClickListner listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_cardview, parent, false);
        return new ContactHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, final int position) {
        final Contact currentContact = contacts.get(position);
        holder.first.setText(currentContact.getFirstName());
        holder.last.setText(currentContact.getLastName());
        holder.email.setText(currentContact.getEmailId());
        holder.primary.setText(currentContact.getPrimaryPhoneNumber());
        holder.secondary.setText(currentContact.getSecondaryPhoneNumber());
        if(currentContact.getPhotoPath()!=null) {
            //Glide.with(mainActivity).load(new File(currentContact.getPhotoPath())).into(holder.icon);
            if (currentContact.getPhotoPath().equals("")) {
                holder.icon.setImageResource(R.drawable.photo_icon);
            }else {
                holder.icon.setImageBitmap(BitmapFactory.decodeFile(currentContact.getPhotoPath()));
            }
        }
        if(!setMultiDelete){
            holder.checkBox.setVisibility(View.GONE);
            holder.popUpOption.setVisibility(View.VISIBLE);
        }else {
            holder.popUpOption.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(false);
        }
    }


    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
        duplicateContacts=contacts;
        notifyDataSetChanged();
    }
    public void setSimContacts(List<Contact> contacts){
        this.simContacts=contacts;
        notifyDataSetChanged();
    }

    public List<Contact> getMultiSelectedContacts(){
        return multiSelectedContacts;
    }

    class ContactHolder extends RecyclerView.ViewHolder {
        private TextView first, last, email, primary, secondary;
        private ImageView popUpOption,icon;
        private CheckBox checkBox;

        ContactHolder(@NonNull View itemView, final OnItemClickListner listener) {
            super(itemView);
            first = itemView.findViewById(R.id.firstname);
            last = itemView.findViewById(R.id.lastname);
            email = itemView.findViewById(R.id.email);
            primary = itemView.findViewById(R.id.primary);
            secondary = itemView.findViewById(R.id.secondary);
            popUpOption = itemView.findViewById(R.id.popup_option);
            icon =itemView.findViewById(R.id.icon);
            checkBox =itemView.findViewById(R.id.recyclerview_checkbox);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.setContextualActionMode();
                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onCardClick(position);
                        }
                    }
                }
            });
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkBox.isChecked()){
                        listener.multiSelect(getAdapterPosition(), true);
                        Log.d("check", "Check box checked");
                    }else {
                        listener.multiSelect(getAdapterPosition(), false);
                        Log.d("check", "Check box un checked");
                    }
                }
            });
            popUpOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION){
                            listener.onPopUpClick(contacts.get(position),v);
                        }
                    }
                }
            });

            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(listener!=null && position!=RecyclerView.NO_POSITION){
                        listener.onIconClick(position,v);
                    }
                }
            });

            }
    }
}
