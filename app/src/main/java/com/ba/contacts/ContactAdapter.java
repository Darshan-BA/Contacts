package com.ba.contacts;

import android.content.ContentProviderClient;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder>{

    private List<Contact> contacts = new ArrayList<>();

    private OnItemClickListner mListener;

    public Contact getContactAt(int position) {
        return contacts.get(position);
    }

    public interface OnItemClickListner {
        void onCardClick(int position);
        void onPopUpClick(Contact contact, View view);
        void onIconClick(int position,View view);
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

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }


    class ContactHolder extends RecyclerView.ViewHolder {
        private TextView first, last, email, primary, secondary;
        private ImageView popUpOption,icon;

        ContactHolder(@NonNull View itemView, final OnItemClickListner listener) {
            super(itemView);
            first = itemView.findViewById(R.id.firstname);
            last = itemView.findViewById(R.id.lastname);
            email = itemView.findViewById(R.id.email);
            primary = itemView.findViewById(R.id.primary);
            secondary = itemView.findViewById(R.id.secondary);
            popUpOption = itemView.findViewById(R.id.popup_option);
            icon =itemView.findViewById(R.id.icon);

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
