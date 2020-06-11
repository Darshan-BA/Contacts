package com.ba.contacts.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ba.contacts.Contact;
import com.ba.contacts.ContactAdapter;
import com.ba.contacts.ContactViewModel;
import com.ba.contacts.EditContact;
import com.ba.contacts.MainActivity;
import com.ba.contacts.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.content.ContextCompat.getSystemService;

public class MainFragment extends Fragment {
    ContactViewModel contactViewModel;
    ContactAdapter adapter;
    FloatingActionButton floatingActionButton;
    ArrayList<Contact> deleteContactList = new ArrayList<>();
    private String phoneNumberHolder;
    private SearchView searchView;

   // private Toolbar toolbar;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("frag", "onAttach main_frag");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("frag", "onCreate main_frag");
        Log.d("fragment","No of back stacks main: "+ getParentFragmentManager().getBackStackEntryCount());
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_toolbar).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setMinimumWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.setIconified(true);
                View view =getActivity().getCurrentFocus();
                //InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                //inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
                return false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("frag", "onCreateView main_frag");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        //setHasOptionsMenu(true);
        //toolbar
        //Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        Toolbar toolbar=((MainActivity)getActivity()).toolbar;
        toolbar.setTitle(R.string.app_name);
        /*toolbar=view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        ((AppCompatActivity) getActivity()).getSupportActionBar();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        SharedPreferences sortPrep = getActivity().getSharedPreferences("SORT", MODE_PRIVATE);
        int sortId = sortPrep.getInt("name", 0);
        //adapter = new ContactAdapter();
        adapter=((MainActivity)getActivity()).adapter;
        floatingActionButton = view.findViewById(R.id.add_float);
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        if (sortId == 1) {
            contactViewModel.getAllContactsByLastName().observe(getActivity(), new Observer<List<Contact>>() {
                @Override
                public void onChanged(List<Contact> contacts) {
                    adapter.setContacts(contacts);
                }
            });
        }
        if (sortId == 0) {
            contactViewModel.getAllContacts().observe(getActivity(), new Observer<List<Contact>>() {
                @Override
                public void onChanged(List<Contact> contacts) {
                    adapter.setContacts(contacts);
                }
            });
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditContact.class);
                startActivity(intent);
            }
        });
        // OnClick listner for deleting and editing
        adapter.setOnItemClickListener(new ContactAdapter.OnItemClickListner() {
            @Override
            public void onPopUpClick(final Contact contact, View view) {
                //contactViewModel.delete(adapter.getContactAt(position));
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.contact_card_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                Intent intent = new Intent(getActivity(), EditContact.class);
                                intent.putExtra("id", contact.getId());
                                intent.putExtra("first", contact.getFirstName());
                                intent.putExtra("last", contact.getLastName());
                                intent.putExtra("primary", contact.getPrimaryPhoneNumber());
                                intent.putExtra("secondary", contact.getSecondaryPhoneNumber());
                                intent.putExtra("email", contact.getEmailId());
                                intent.putExtra("photoPath", contact.getPhotoPath());
                                startActivity(intent);
                                return true;
                            case R.id.delete:
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                                alertDialog.setTitle("Delete");
                                alertDialog.setMessage("Are you sure want to delete");
                                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        contactViewModel.delete(contact);
                                    }
                                });
                                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alertDialog.create();
                                alertDialog.show();
                                return true;
                            case R.id.share:
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, "Name: " + contact.getFirstName() + "\b" + contact.getLastName() + "\n" + "Primary Number: "
                                        + contact.getPrimaryPhoneNumber() + "\n" + "Secondary Number: " + contact.getSecondaryPhoneNumber() + "\n" + "EmailId :" + contact.getEmailId());
                                shareIntent.setType("text/plain");
                                startActivity(Intent.createChooser(shareIntent, "Sharing Contact"));
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }

            @Override
            public void onIconClick(int position, View view) {
                //need to implement adding profile photo
            }

            @Override
            public void setContextualActionMode() {
                adapter.setSetMultiDelete(true);
                adapter.notifyDataSetChanged();
                toolbar.startActionMode(actionModeCallback);
            }

            @Override
            public void multiSelect(int adapterPosition, boolean check) {
                if (check) {
                    deleteContactList.add(adapter.getContactAt(adapterPosition));
                } else {
                    deleteContactList.remove(adapter.getContactAt(adapterPosition));
                }
            }

            @Override
            public void onCardClick(int position) {
                final String[] dialogList;
                Contact cardContact = adapter.getContactAt(position);
                String first = cardContact.getPrimaryPhoneNumber();
                String second = cardContact.getSecondaryPhoneNumber();
                String email = cardContact.getEmailId();
                Log.d("number", first);
                Log.d("number", second);
                Log.d("number", email);
                if (first.isEmpty() && second.isEmpty() && email.isEmpty()) {
                    dialogList = new String[0];
                    Toast.makeText(getContext(), "No Phone Numbers to Call", Toast.LENGTH_SHORT).show();
                } else if (second.isEmpty() && email.isEmpty()) {
                    dialogList = new String[1];
                    dialogList[0] = cardContact.getPrimaryPhoneNumber();
                } else if (first.isEmpty() && email.isEmpty()) {
                    dialogList = new String[1];
                    dialogList[0] = cardContact.getSecondaryPhoneNumber();
                } else if (first.isEmpty() && second.isEmpty()) {
                    dialogList = new String[1];
                    dialogList[0] = cardContact.getEmailId();
                } else if (email.isEmpty()) {
                    dialogList = new String[2];
                    dialogList[0] = cardContact.getPrimaryPhoneNumber();
                    dialogList[1] = cardContact.getSecondaryPhoneNumber();
                } else if (second.isEmpty()) {
                    dialogList = new String[2];
                    dialogList[0] = cardContact.getPrimaryPhoneNumber();
                    dialogList[1] = cardContact.getEmailId();
                } else if (first.isEmpty()) {
                    dialogList = new String[2];
                    dialogList[0] = cardContact.getSecondaryPhoneNumber();
                    dialogList[1] = cardContact.getEmailId();
                } else {
                    dialogList = new String[3];
                    dialogList[0] = cardContact.getPrimaryPhoneNumber();
                    dialogList[1] = cardContact.getSecondaryPhoneNumber();
                    dialogList[2] = cardContact.getEmailId();
                }

                final Intent phoneIntent = new Intent((Intent.ACTION_CALL));
                final Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setItems(dialogList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String var = dialogList[which];
                        boolean isVarEmail = false;
                        for (int i = 0; i < var.length(); i++) {
                            char temp = var.charAt(i);
                            Log.d("ba", String.valueOf(temp));
                            if (temp == '@') {
                                isVarEmail = true;
                                break;
                            }
                        }
                        if (isVarEmail) {
                            emailIntent.setData(Uri.parse("mailto:" + dialogList[which]));
                            startActivity(emailIntent);

                        } else {
                            phoneNumberHolder = dialogList[which];
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ((MainActivity) getActivity()).permissionNotGrandted(Manifest.permission.CALL_PHONE);
                            } else {
                                phoneIntent.setData(Uri.parse("tel:" + dialogList[which]));
                                startActivity(phoneIntent);
                            }
                        }
                    }
                });
                builder.create();
                builder.show();
            }

        });
        return view;
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.toolbar_list, menu);
            mode.setTitle("Delete Contact");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.delete_toolbar) {
                final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getContext());
                alertDialog.setTitle("Delete Selected");
                alertDialog.setMessage("Are you sure want to delete selected contacts");
                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        contactViewModel.multipleDelete(deleteContactList.toArray(new Contact[deleteContactList.size()]));
                        deleteContactList.clear();
                        adapter.setSetMultiDelete(false);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Contacts Deleted", Toast.LENGTH_SHORT).show();
                        mode.finish();
                        //finish();
                        //Intent intent = new Intent(getActivity(), MainActivity.class);
                        //startActivity(intent);
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.create();
                alertDialog.show();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.setSetMultiDelete(false);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("frag", "onActivityCreated main_frag");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("frag", "onStart main_frag");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("frag", "onResume main_frag");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("frag", "onPause main_frag");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("frag", "onStop main_frag");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("frag", "onDestroyView main_frag");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("frag", "onDestroy main_frag");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("frag", "onDetach main_frag");
    }
}
