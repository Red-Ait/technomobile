package fr.isima.technomobile.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.isima.technomobile.db.entities.Depenses;

public class DepensesDBHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String TAG = "LOG_INF";

    public DepensesDBHelper(Context context) {
        super(context, GroupSchema.DB_NAME, null, GroupSchema.DB_VERSION);
        this.context = context;
    }
    @ Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + GroupSchema.Depense.TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT , title TEXT NOT NULL , date DATE NOT NULL , group_id INTEGER NOT NULL)";
        db.execSQL(createTable);
    }
    @ Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS " + GroupSchema.Depense.TABLE_NAME; ;
        db.execSQL(dropTable);
        onCreate(db);
    }



    public List<Depenses> getAllDépensess(int groupId) {
        List<Depenses> dépensess = new ArrayList<>();
        String TASKS_SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = %s", GroupSchema.Depense.TABLE_NAME, GroupSchema.Depense.COL_GROUP_ID, groupId);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TASKS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Depenses dépenses = new Depenses();
                    dépenses.setTitle(cursor.getString(cursor.getColumnIndex(GroupSchema.Depense.COL_TITLE)));
                    dépenses.setId(cursor.getInt(cursor.getColumnIndex(GroupSchema.Depense.COL_ID)));
                    dépenses.setDate(cursor.getString(cursor.getColumnIndex(GroupSchema.Depense.COL_DATE)));
                    dépenses.setGroupId(groupId);
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

    public  void addDépenses(String title,  String date, int groupId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(GroupSchema.Depense.COL_TITLE, title);
            values.put(GroupSchema.Depense.COL_DATE, date);
            values.put(GroupSchema.Depense.COL_GROUP_ID, groupId);
            db.insertOrThrow(GroupSchema.Depense.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
            Log.d(TAG, "Dépenses Added");

            // TODO call add dep details

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

}
