package fr.isima.technomobile.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.ajts.androidmads.library.SQLiteToExcel;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import fr.isima.technomobile.R;
import fr.isima.technomobile.adapters.DetailDepenseListAdapter;
import fr.isima.technomobile.adapters.MembersListAdapter;
import fr.isima.technomobile.db.EmissionBDHelper;
import fr.isima.technomobile.db.MemberDBHelper;
import fr.isima.technomobile.db.entities.Contact;
import fr.isima.technomobile.db.entities.Depenses;
import fr.isima.technomobile.db.entities.DetailDepense;
import fr.isima.technomobile.db.entities.Emission;

public class DetailDepenseActivity extends AppCompatActivity {
    double depenseSum = 0;
    public static WeakReference<DetailDepenseActivity> weakActivity;
    private static final String TAG = "LOG_INF";
    private Depenses selectedDepense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_depense);
        selectedDepense = getIntent().getExtras().getParcelable("depense");

        Toolbar toolbar = findViewById(R.id.detail_depenses_toolbar);
        toolbar.setTitle("Dépense : " + selectedDepense.getTitle());
        setSupportActionBar(toolbar);


      /*  TextView title = (TextView) findViewById(R.id.depense_name);
        title.setText(selectedDepense.getTitle());
*/
        TextView date = (TextView) findViewById(R.id.depense_date);
        date.setText(selectedDepense.getDate());
        updateDetailDepenseList();
        weakActivity = new WeakReference<>(DetailDepenseActivity.this);
    }
    public static DetailDepenseActivity getInstanceActivity() {
        return weakActivity.get();
    }

    public void updateSumValue(double d) {
        depenseSum += d;
        NumberFormat formatter = new DecimalFormat("#0.00");
        TextView depSum = findViewById(R.id.depense_sum);
        depSum.setText(formatter.format(depenseSum) + " EUR");

    }
    public void updateDetailDepenseList() {
        MemberDBHelper memberDBHelper = new MemberDBHelper(this);
        List<Contact> contacts = memberDBHelper.getAllGroupMember(selectedDepense.getGroupId());

        ListView listView = (ListView) findViewById(R.id.detail_depense_list);

        ArrayList<DetailDepense> array = new ArrayList<>();
        depenseSum = 0.0;
        for(Contact c : contacts) {
            array.add(new DetailDepense(c, selectedDepense.getId()));
            EmissionBDHelper emissionBDHelper = new EmissionBDHelper(this);
            List<Emission> emissions = emissionBDHelper.getAllEmission(selectedDepense.getId(), c.getNumber());
            for(Emission e : emissions) {
                depenseSum += e.getValue();
            }
        }
        NumberFormat formatter = new DecimalFormat("#0.00");
        TextView depSum = findViewById(R.id.depense_sum);
        depSum.setText(formatter.format(depenseSum) + " EUR");

        DetailDepenseListAdapter adapter = new DetailDepenseListAdapter(this, array);
        listView.setAdapter(adapter);

        Log.i(TAG, "Dep det Act updt");
    }
    public void showDeleteEmissionConfirmation(Emission emission) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Voulez-vous supprimer " + emission.getDesignation() + " de dépense :" + selectedDepense.getTitle() + " ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener () {
                    @ Override
                    public void onClick ( DialogInterface dialog , int which ) {
                        EmissionBDHelper emissionBDHelper = new EmissionBDHelper(DetailDepenseActivity.this);
                        emissionBDHelper.deleteEmission(emission);
                        updateSumValue(emission.getValue() * -1);
                        updateDetailDepenseList();
                    }
                })
                .setNegativeButton ("Non", null )
                .create();
        dialog.show() ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_emissions, menu);
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.export_emission:
                // Export SQLite DB as EXCEL FILE
                SQLiteToExcel sqliteToExcel = new SQLiteToExcel(getApplicationContext(), "fr.isima.technomobile.db");
                Log.d(TAG, "export on");
                sqliteToExcel.exportSingleTable("emission_table", "emission.xls", new SQLiteToExcel.ExportListener() {
                    @Override
                    public void onStart() {
                        Log.d(TAG, "staaaaaaaaaaaaart");

                    }

                    @Override
                    public void onCompleted(String filePath) {
                        Log.d(TAG, "done");

                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(TAG, "error");
                        e.getMessage();
                    }

                });
                return true;

            /*case R.id.import_db:

                Log.d(TAG, "import");
                Intent intent2 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent2.addCategory(Intent.CATEGORY_OPENABLE);
                intent2.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                intent2.setType("application/json");
                intent2.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.getStorageDirectory().getPath() + "primary/depenses.xls");
                intent2.putExtra(Intent.EXTRA_TITLE,"depenses-" + Instant.now() + ".json");

                Log.d(TAG, "intent");
                // Is used to import data from excel without dropping table
                // ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(), DBHelper.DB_NAME);

                // if you want to add column in excel and import into DB, you must drop the table
                ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(), "fr.isima.technomobile.db", false);
                // Import EXCEL FILE to SQLite
                excelToSQLite.importFromFile(Environment.getStorageDirectory().getPath() + "primary/depenses.xls", new ExcelToSQLite.ImportListener() {
                    @Override
                    public void onStart() {
                        Log.d(TAG, "start");
                    }

                    @Override
                    public void onCompleted(String dbName) {
                        Log.d(TAG, "done");

                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(TAG, "error");
                        e.getMessage();

                    }
                });
                return  true;*/

            default:
                return super.onOptionsItemSelected(item);

        }

    }

}