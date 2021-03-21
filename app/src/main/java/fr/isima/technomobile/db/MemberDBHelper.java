package fr.isima.technomobile.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.isima.technomobile.db.entities.Contact;
import fr.isima.technomobile.db.entities.Group;

public class MemberDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "BD_HELPER";
    private Context context;

    public MemberDBHelper(Context context) {
        super(context, GroupSchema.DB_NAME, null, GroupSchema.DB_VERSION);
        this.context = context;
    }

    @ Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + GroupSchema.Member.TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT , phone_no TEXT NOT NULL, contact_name TEXT NOT NULL, group_id INTEGER NOT NULL)";
        db.execSQL(createTable);
    }

    @ Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS " + GroupSchema.Member.TABLE_NAME;
        db.execSQL(dropTable);
        onCreate(db);
    }

    public Contact getMemberByPhoneNo(String phoneNo) {
        Contact contact = null;

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNo));

        List<Contact> contacts = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(uri,
                new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID},
                null,
                null,
                null);

        String contactid26 = null;
        if(cursor!=null) {
            while(cursor.moveToNext()){
                contact = new Contact();
                contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME)));
                contact.setNumber(phoneNo);
                contactid26 = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
            if(contactid26 == null) {
                contact.setName(phoneNo);
                contact.setNumber(phoneNo);
            }
        }
        return contact;

    }
    public List<Contact> getAllGroupMember(int groupId) {
        List<Contact> contacts = new ArrayList<>();
        String TASKS_SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = %s",
                GroupSchema.Member.TABLE_NAME, GroupSchema.Member.COL_GROUP_ID, groupId);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TASKS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Contact contact = new Contact();
                    contact.setNumber(cursor.getString(cursor.getColumnIndex(GroupSchema.Member.COL_PHONE_NO)));
                    contact.setName(cursor.getString(cursor.getColumnIndex(GroupSchema.Member.COL_CONTACT_NAME)));
                    contacts.add(contact);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get members from database");
            return new ArrayList<>();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return contacts;
    }

    public  void addContactToGroupGroup(Contact contact, int groupId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(GroupSchema.Member.COL_PHONE_NO, contact.getNumber());
            values.put(GroupSchema.Member.COL_CONTACT_NAME, contact.getName());
            values.put(GroupSchema.Member.COL_GROUP_ID, groupId);
            db.insertOrThrow(GroupSchema.Member.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
            Log.d(TAG, "Member Added : " + contact.getName() + " => " + groupId);
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    public void deleteContactFromGroup(Contact contact, int group_id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(GroupSchema.Member.COL_GROUP_ID, group_id);
            values.put(GroupSchema.Member.COL_PHONE_NO, contact.getNumber());

            db.delete(GroupSchema.Member.TABLE_NAME,
                    GroupSchema.Member.COL_GROUP_ID + "=? and " + GroupSchema.Member.COL_PHONE_NO + " =?",
                    new String[]{String.valueOf(group_id), contact.getNumber()}) ;
            db.setTransactionSuccessful();
            Log.d(TAG, "delete");
            Log.d(TAG, "*********************************");
        } catch (Exception e) {
            Log.d(TAG, "*********************************");
            Log.d(TAG, "Error while trying to delete");
            Log.d(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

}
