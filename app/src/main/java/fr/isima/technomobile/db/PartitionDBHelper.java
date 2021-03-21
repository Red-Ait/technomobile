package fr.isima.technomobile.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.isima.technomobile.db.entities.Depenses;

public class PartitionDBHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String TAG = "LOG_INF";

    public PartitionDBHelper(Context context) {
        super(context, GroupSchema.DB_NAME, null, GroupSchema.DB_VERSION);
        this.context = context;
    }
    @ Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + GroupSchema.Partition.TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT , contact_phone TEXT NOT NULL , value DOUBLE NOT NULL , id_depense INTEGER NOT NULL)";
        db.execSQL(createTable);
    }
    @ Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS " + GroupSchema.Depense.TABLE_NAME; ;
        db.execSQL(dropTable);
        onCreate(db);
    }



    public Double getPartition(String phone, int depenseId) {
        double re = 0.0;
        String TASKS_SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = %s AND %s = \'%s\'",
                GroupSchema.Partition.TABLE_NAME,
                GroupSchema.Partition.COL_DEPENSE_ID,
                depenseId,
                GroupSchema.Partition.COL_CONTACT_PHONE,
                phone);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TASKS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    re = cursor.getDouble(cursor.getColumnIndex(GroupSchema.Partition.COL_VALUE));
                    Log.i(TAG, "get part => " + re + " dep id => " + depenseId + " phone => " + phone);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get dépensess from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return re;
        }
    }

    public  void updatePartition(String phone, int depenseId, double value) {
        boolean exist = false;
        String TASKS_SELECT_QUERY = String.format("DELETE FROM %s WHERE %s = %s AND %s = \'%s\'",
                GroupSchema.Partition.TABLE_NAME,
                GroupSchema.Partition.COL_DEPENSE_ID,
                depenseId,
                GroupSchema.Partition.COL_CONTACT_PHONE,
                phone);
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(TASKS_SELECT_QUERY);


        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(GroupSchema.Partition.COL_CONTACT_PHONE, phone);
            values.put(GroupSchema.Partition.COL_DEPENSE_ID, depenseId);
            values.put(GroupSchema.Partition.COL_VALUE, value);
               db.insertOrThrow(GroupSchema.Partition.TABLE_NAME, null, values);
                db.setTransactionSuccessful();
                Log.i(TAG, "add part => " + value + " dep id => " + depenseId + " phone => " + phone);

            // TODO call add dep details

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            Log.d(TAG, "Error while trying to add part to database");
        } finally {
            db.endTransaction();
        }
    }

}
