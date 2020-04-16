package com.ba.contacts;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder>{

    private List<Contact> contacts = new ArrayList<>();
    //private MainActivity mainActivity=new MainActivity();
    private boolean setMultiDelete=false;
    private OnItemClickListner mListener;

    void setSetMultiDelete(boolean setMultiDelete) {
        this.setMultiDelete = setMultiDelete;
    }

    Contact getContactAt(int position) {
        return contacts.get(position);
    }

    public interface OnItemClickListner {
        void onCardClick(int position);
        void onPopUpClick(Contact contact, View view);
        void onIconClick(int position,View view);
        void setContextualActionMode();
        void multiSelect(int adapterPosition,boolean check);
    }

    void setOnItemClickListener(OnItemClickListner listener) {
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
        if(!setMultiDelete){
            Log.d("BA", "if setMultiDelete="+String.valueOf(setMultiDelete));
            holder.checkBox.setVisibility(View.GONE);

        }else {
            Log.d("BA","else setMultiDelete="+String.valueOf(setMultiDelete));
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.popUpOption.setVisibility(View.INVISIBLE);
            holder.checkBox.setChecked(false);
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
            checkBox=itemView.findViewById(R.id.recyclerview_checkbox);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        listener.multiSelect(getAdapterPosition(), true);
                        Log.d("check", "Check box checked");
                    } else {
                        listener.multiSelect(getAdapterPosition(), false);
                        Log.d("check", "Check box un checked");
                    }
                }
            });
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
