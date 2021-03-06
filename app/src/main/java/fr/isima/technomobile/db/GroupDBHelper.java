package fr.isima.technomobile.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.isima.technomobile.db.entities.Group;

public class GroupDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "LOG_INF";

    public GroupDBHelper(Context context) {
        super(context, GroupSchema.DB_NAME, null, GroupSchema.DB_VERSION);
    }
    @ Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE group_table (id INTEGER PRIMARY KEY AUTOINCREMENT , title TEXT NOT NULL)";
        String createTable2 = "CREATE TABLE " + GroupSchema.Member.TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT , phone_no TEXT NOT NULL, contact_name TEXT NOT NULL, group_id INTEGER NOT NULL)";
        String createTable3 = "CREATE TABLE " + GroupSchema.Depense.TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT , title TEXT NOT NULL , date DATE NOT NULL , group_id INTEGER NOT NULL)";
        String createTable4 = "CREATE TABLE " + GroupSchema.Emission.TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT , contact_phone TEXT NOT NULL , designation TEXT NOT NULL , value DOUBLE NOT NULL , id_depense INTEGER NOT NULL)";
        String createTable5 = "CREATE TABLE " + GroupSchema.Partition.TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT , contact_phone TEXT NOT NULL , value DOUBLE NOT NULL , id_depense INTEGER NOT NULL)";
        db.execSQL(createTable);
        db.execSQL(createTable2);
        db.execSQL(createTable3);
        db.execSQL(createTable4);
        db.execSQL(createTable5);
    }
    @ Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS group_table";
        db.execSQL(dropTable);
        onCreate(db);
    }

    public List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<>();
        String TASKS_SELECT_QUERY = String.format("SELECT * FROM %s ", GroupSchema.Group.TABLE_NAME);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TASKS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Group group = new Group();
                    group.setTitle(cursor.getString(cursor.getColumnIndex(GroupSchema.Group.COL_TITLE)));
                    group.setId(cursor.getInt(cursor.getColumnIndex(GroupSchema.Group.COL_ID)));
                    groups.add(group);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get groups from database");
            return new ArrayList<>();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return groups;
    }

    public  void addGroup(String title) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(GroupSchema.Group.COL_TITLE, title);
            db.insertOrThrow(GroupSchema.Group.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
            Log.d(TAG, "Group Added");
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

/*    public  void deleteTask(String title) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(TodoEntrySchema.TodoEntry.COL_TODOENTRY, title);

            db.delete(TodoEntrySchema.TodoEntry.TABLE, TodoEntrySchema.TodoEntry.COL_TODOENTRY + "=?", new String[]{title}) ;
            db.setTransactionSuccessful();
        } catch (Exception e) {
//            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    public  void deleteAllTasks() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {


            db.delete(TodoEntrySchema.TodoEntry.TABLE, null, null) ;
            db.setTransactionSuccessful();
        } catch (Exception e) {
//            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    public  void updateTask(String newtitle, String oldtitle) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(TodoEntrySchema.TodoEntry.COL_TODOENTRY, newtitle);

            db.update(TodoEntrySchema.TodoEntry.TABLE, values, "title = ?", new String[]{oldtitle});
            db.setTransactionSuccessful();
        } catch (Exception e) {
//            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }
*/}
