package fr.isima.technomobile.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.isima.technomobile.db.entities.Depenses;

public class DepensesDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "BD_HELPER";

    public DepensesDBHelper(Context context) {
        super(context, GroupSchema.DB_NAME, null, GroupSchema.DB_VERSION);
    }
    @ Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + GroupSchema.Dépenses.TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT , title TEXT NOT NULL , date DATE NOT NULL )";
        db.execSQL(createTable);
    }
    @ Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS " + GroupSchema.Dépenses.TABLE_NAME; ;
        db.execSQL(dropTable);
        onCreate(db);
    }



    public List<Depenses> getAllDépensess() {
        List<Depenses> dépensess = new ArrayList<>();
        String TASKS_SELECT_QUERY = String.format("SELECT * FROM %s ", GroupSchema.Dépenses.TABLE_NAME);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TASKS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Depenses dépenses = new Depenses();
                    dépenses.setTitle(cursor.getString(cursor.getColumnIndex(GroupSchema.Dépenses.COL_TITLE)));
                   // dépenses.setId(cursor.getInt(cursor.getColumnIndex(GroupSchema.Dépenses.COL_ID)));
                    dépenses.setDate(cursor.getString(cursor.getColumnIndex(GroupSchema.Dépenses.COL_DATE)));
                    dépensess.add(dépenses);
                    Log.d("end","done");
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get dépensess from database");
            return new ArrayList<>();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return dépensess;
    }

    public  void addDépenses(String title,  String date) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put(GroupSchema.Dépenses.COL_TITLE, title);
            values.put(GroupSchema.Dépenses.COL_DATE, date);
            db.insertOrThrow(GroupSchema.Dépenses.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
            Log.d(TAG, "Dépenses Added");
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
