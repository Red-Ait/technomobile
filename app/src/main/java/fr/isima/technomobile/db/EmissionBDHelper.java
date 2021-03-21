package fr.isima.technomobile.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.isima.technomobile.db.entities.Contact;
import fr.isima.technomobile.db.entities.Depenses;
import fr.isima.technomobile.db.entities.Emission;

public class EmissionBDHelper extends SQLiteOpenHelper {

    private static final String TAG = "LOG_INF";
    private Context context;
    public EmissionBDHelper(Context context) {
        super(context, GroupSchema.DB_NAME, null, GroupSchema.DB_VERSION);
        this.context = context;
    }
    @ Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + GroupSchema.Emission.TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT , contact_phone TEXT NOT NULL , designation TEXT NOT NULL , value DOUBLE NOT NULL , id_depense INTEGER NOT NULL)";
        db.execSQL(createTable);
    }
    @ Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS " + GroupSchema.Emission.TABLE_NAME; ;
        db.execSQL(dropTable);
        onCreate(db);
    }

    public  void addEmissionToDepense(Emission emission, int depenseId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            Log.d(TAG, "==> " + emission.getMember().getNumber());
            values.put(GroupSchema.Emission.COL_CONTACT_PHONE, emission.getMember().getNumber());
            values.put(GroupSchema.Emission.COL_DEPENSE_ID, depenseId);
            values.put(GroupSchema.Emission.COL_VALUE, emission.getValue());
            values.put(GroupSchema.Emission.COL_DESIGNATION, emission.getDesignation());
            db.insertOrThrow(GroupSchema.Emission.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
            Log.d(TAG, "Mission Added");

            // TODO call add dep details
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database" + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public List<Emission> getAllEmission(int depenseId, String phoneNo) {
        MemberDBHelper memberDBHelper = new MemberDBHelper(context);
        List<Emission> emissions = new ArrayList<>();
        String TASKS_SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = %s AND %s = \'%s\'",
                GroupSchema.Emission.TABLE_NAME, GroupSchema.Emission.COL_DEPENSE_ID, depenseId, GroupSchema.Emission.COL_CONTACT_PHONE, phoneNo);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TASKS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Emission emission = new Emission();
                    emission.setMember(memberDBHelper.getMemberByPhoneNo(phoneNo));
                    emission.setValue(Double.parseDouble(cursor.getString(cursor.getColumnIndex(GroupSchema.Emission.COL_VALUE))));
                    emission.setDesignation(cursor.getString(cursor.getColumnIndex(GroupSchema.Emission.COL_DESIGNATION)));
                    emissions.add(emission);
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
        return emissions;
    }


}
