package fr.isima.technomobile.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.MemoryHandler;

import fr.isima.technomobile.R;
import fr.isima.technomobile.adapters.GroupListAdapter;
import fr.isima.technomobile.adapters.MembersListAdapter;
import fr.isima.technomobile.db.GroupDBHelper;
import fr.isima.technomobile.db.MemberDBHelper;
import fr.isima.technomobile.db.entities.Contact;
import fr.isima.technomobile.db.entities.Group;

public class MembersActivity extends AppCompatActivity {

    public static WeakReference<MembersActivity> weakActivity;
    private static final String TAG = "LOG_INF";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 400;
    private List<Contact> selectedContact = new ArrayList<>();
    Group selectedGroup = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedGroup = getIntent().getExtras().getParcelable("group");

        setContentView(R.layout.activity_members);

        Toolbar toolbar = findViewById(R.id.members_toolbar);
        toolbar.setTitle("Members : " + selectedGroup.getTitle());
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.members_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddContactsForm(askForContacts());
            }
        });

        updateMembersList();
        weakActivity = new WeakReference<>(MembersActivity.this);
    }
    public static MembersActivity getInstanceActivity() {
        return weakActivity.get();
    }

    public void updateMembersList() {
        MemberDBHelper memberDBHelper = new MemberDBHelper(this);

        ListView listView = (ListView) findViewById(R.id.members_list);
        ArrayList<Contact> array = new ArrayList<>();
        array.addAll(memberDBHelper.getAllGroupMember(selectedGroup.getId()));
        MembersListAdapter adapter = new MembersListAdapter(this, array);
        listView.setAdapter(adapter);
    }
    public void showDeleteContactConfirmation(Contact contact) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Voulez-vous supprimer " + contact.getName() + " de groupe :" + selectedGroup.getTitle() + " ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener () {
                    @ Override
                    public void onClick ( DialogInterface dialog , int which ) {
                        MemberDBHelper memberDBHelper = new MemberDBHelper(MembersActivity.this.getApplicationContext());
                        memberDBHelper.deleteContactFromGroup(contact, selectedGroup.getId());
                        updateMembersList();
                    }
                })
                .setNegativeButton ("Non", null )
                .create();
        dialog.show() ;
    }
    public void showAddContactsForm(List<Contact> contacts) {

        MemberDBHelper memberDBHelper = new MemberDBHelper(this);
        List<Contact> attachedContacts = memberDBHelper.getAllGroupMember(selectedGroup.getId());

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        scrollView.addView(linearLayout);

        for(Contact c : contacts) {
            boolean attached = false;
            for(Contact ac : attachedContacts) {
                if(ac.getNumber().equals(c.getNumber())) {
                    attached = true;
                }
            }
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(c.getName());
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            checkBox.setClickable(!attached);
            checkBox.setChecked(attached);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        selectedContact.add(c);
                    } else {
                        selectedContact.remove(c);
                    }
                }
            });
            linearLayout.addView(checkBox);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Ajouter les contacts")
                .setView(scrollView)
                .setPositiveButton("Ajouter", new DialogInterface.OnClickListener () {
                    @ Override
                    public void onClick ( DialogInterface dialog , int which ) {
                        MemberDBHelper memberDBHelper = new MemberDBHelper(MembersActivity.this.getApplicationContext());
                        for(Contact c : selectedContact) {
                            memberDBHelper.addContactToGroupGroup(c, selectedGroup.getId());
                        }
                        updateMembersList();
                    }
                })
                .setNegativeButton ("Annuler", null )
                .create();
        dialog.show() ;
    }
    private List<Contact> askForContacts() {
        List<Contact> contacts = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
             contacts = getContactNames();
        }
        return contacts;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                askForContacts();
            } else {
                Toast.makeText(this, "Nous avons besoin de votre permission pour afficher les contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private List<Contact> getContactNames() {
        List<Contact> contacts = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC"
        );

        if (cursor.moveToFirst()) {
            do {

                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null
                    );

                    while (pCur.moveToNext()) {

                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contacts.add(new Contact(phoneNo, name));
                    }

                    pCur.close();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contacts;
    }
}
