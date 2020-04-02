package com.ba.contacts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> {
    public static final String TAG = "BA";

    private List<Contact> contacts = new ArrayList<>();


    private OnItemClickListner mListener;

    public Contact getContactAt(int position) {
        return contacts.get(position);
    }


    public interface OnItemClickListner {
        void onCardClick(int position);
        void onDeleteClick(int position);
        void onEditClick(Contact contact);
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
        private ImageView icon;
        private ImageView deleteIcon,editIcon;

        ContactHolder(@NonNull View itemView, final OnItemClickListner listener) {
            super(itemView);
            first = itemView.findViewById(R.id.firstname);
            last = itemView.findViewById(R.id.lastname);
            email = itemView.findViewById(R.id.email);
            primary = itemView.findViewById(R.id.primary);
            secondary = itemView.findViewById(R.id.secondary);
            icon = itemView.findViewById(R.id.icon);
            deleteIcon = itemView.findViewById(R.id.delete_contact);
            editIcon=itemView.findViewById(R.id.edit_contact_icon);

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
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
            editIcon.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onEditClick(contacts.get(position));
                        }
                    }
                }
            });

        }
    }
}
