package com.ba.contacts.Fragments;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ba.contacts.Entities.Contact;
import com.ba.contacts.Adapters.ContactAdapter;
import com.ba.contacts.SettingsSharedPref;
import com.ba.contacts.ViewModels.ContactViewModel;
import com.ba.contacts.Activities.EditContact;
import com.ba.contacts.Activities.MainActivity;
import com.ba.contacts.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {
    private ContactViewModel contactViewModel;
    private ContactAdapter adapter;
    private FloatingActionButton floatingActionButton;
    private ArrayList<Contact> deleteContactList = new ArrayList<>();
    private SearchView searchView;
    private Toolbar toolbar;

    private BottomSheetBehavior bottomSheetBehavior;
    private TextView primaryPhoneNumber,secondaryPhoneNumber,emailAddress;
    private ImageView photoView;
    private Toolbar toolbarCollapsingLayout;

    private Contact contact;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("frag", "onAttach main_frag");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("frag", "onCreate main_frag");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("frag", "onCreateView main_frag");
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        View bottomsheet = view.findViewById(R.id.bootomsheet);
        primaryPhoneNumber=view.findViewById(R.id.primary_phone_number);
        secondaryPhoneNumber=view.findViewById(R.id.secondary_phone_number);
        emailAddress=view.findViewById(R.id.email_address);
        photoView=view.findViewById(R.id.expandedImage);

        toolbarCollapsingLayout=view.findViewById(R.id.toolbar_collapsing_layout);
        toolbarCollapsingLayout.inflateMenu(R.menu.contact_card_menu);
        toolbarCollapsingLayout.setOnMenuItemClickListener(toolbarCollapsingLayoutMenuClickListener);
        //toolbar
        toolbar=view.findViewById(R.id.toolbar_main_fragment);
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setNavigationIcon(R.drawable.icon_hamburger);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("toolbar","toolbar clicked");
                DrawerLayout drawerLayout = ((MainActivity)getActivity()).findViewById(R.id.drawerayout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        //toolbar menu item click listener
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.search_toolbar){
                    MenuItem menuItem=item;
                    SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
                    searchView = (SearchView) menuItem.getActionView();
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
                            return false;
                        }
                    });
                }
                return false;
            }
        });
        //getting contacts from adapter in main activity
        adapter=((MainActivity)getActivity()).adapter;
        floatingActionButton = view.findViewById(R.id.add_float);
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        //contact view model
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        if (SettingsSharedPref.getInstance().getSort().equals("1")) {
            contactViewModel.getAllContactsByLastName().observe(getActivity(), new Observer<List<Contact>>() {
                @Override
                public void onChanged(List<Contact> contacts) {
                    adapter.setContacts(contacts);
                }
            });
        }
        if (SettingsSharedPref.getInstance().getSort().equals("0")) {
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

        // OnClick listener for deleting and editing
        adapter.setOnItemClickListener(new ContactAdapter.OnItemClickListner() {
            @Override
            public void onPopUpClick(final Contact contact, View view) {
                /*PopupMenu popupMenu = new PopupMenu(getContext(), view);
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
                popupMenu.show();*/
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
                toolbar.hideOverflowMenu();
                floatingActionButton.hide();
            }

            @Override
            public void multiSelect(int adapterPosition, boolean check) {
               if(check) {
                    deleteContactList.add(adapter.getContactAt(adapterPosition));
                }else {
                    deleteContactList.remove(adapter.getContactAt(adapterPosition));
                }
            }

            @Override
            public void onCardClick(int position) {
                bottomSheetBehavior=BottomSheetBehavior.from(bottomsheet);
                bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);
                CollapsingToolbarLayout collapsingToolbarLayout=view.findViewById(R.id.collapsing_layout);
                Contact cardContact = adapter.getContactAt(position);
                contact=adapter.getContactAt(position);
                String first = cardContact.getPrimaryPhoneNumber();
                String second = cardContact.getSecondaryPhoneNumber();
                String email = cardContact.getEmailId();
                final Intent phoneIntent = new Intent((Intent.ACTION_CALL));
                final Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                collapsingToolbarLayout.setTitle(cardContact.getFirstName()+" "+cardContact.getLastName());
                collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.lightColorOnPrimary));
                collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.lightColorOnPrimary));
                primaryPhoneNumber.setText(first);
                secondaryPhoneNumber.setText(second);
                emailAddress.setText(email);
                photoView.setImageBitmap(BitmapFactory.decodeFile(cardContact.getPhotoPath()));
                primaryPhoneNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ((MainActivity) getActivity()).permissionNotGrandted(Manifest.permission.CALL_PHONE);
                        }else {
                            phoneIntent.setData(Uri.parse("tel:" + first));
                            startActivity(phoneIntent);
                        }
                    }
                });
                secondaryPhoneNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ((MainActivity) getActivity()).permissionNotGrandted(Manifest.permission.CALL_PHONE);
                        }else {
                            phoneIntent.setData(Uri.parse("tel:" + second));
                            startActivity(phoneIntent);
                        }
                    }
                });
                emailAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        emailIntent.setData(Uri.parse("mailto:" + email));
                        startActivity(emailIntent);
                    }
                });
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

        });
        return view;
    }

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback=new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            switch (newState) {
                case BottomSheetBehavior.STATE_HIDDEN:
                    Log.d("bottomsheet","hidden");
                    break;
                case BottomSheetBehavior.STATE_EXPANDED: {
                    Log.d("bottomsheet","expanded");
                }
                break;
                case BottomSheetBehavior.STATE_COLLAPSED: {
                    Log.d("bottomsheet","collapsed");
                }
                break;
                case BottomSheetBehavior.STATE_DRAGGING:
                    Log.d("bottomsheet","draging");
                    break;
                case BottomSheetBehavior.STATE_SETTLING:
                    Log.d("bottomsheet","settling");
                    break;
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }

    };

    private Toolbar.OnMenuItemClickListener toolbarCollapsingLayoutMenuClickListener=new Toolbar.OnMenuItemClickListener() {
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
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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
    };


    //contextual action mode
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_actionmodecallback, menu);
            mode.setTitle("Delete Contact");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.multiple_delete) {
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
                        floatingActionButton.show();
                        mode.finish();
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
            floatingActionButton.show();
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
