package fr.isima.technomobile.activities;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ajts.androidmads.library.ExcelToSQLite;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ajts.androidmads.library.SQLiteToExcel;

import java.io.File;
import java.lang.ref.WeakReference;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;

import fr.isima.technomobile.R;
import fr.isima.technomobile.adapters.DepensesListAdapter;
import fr.isima.technomobile.db.DepensesDBHelper;
import fr.isima.technomobile.db.GroupSchema;
import fr.isima.technomobile.db.entities.Depenses;
import fr.isima.technomobile.db.entities.Group;

public class DepensesActivity extends AppCompatActivity {

    public static WeakReference<DepensesActivity> weakActivity;
    private static final String TAG = "LOG_INF";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 400;
    Group selectedGroup = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedGroup = getIntent().getExtras().getParcelable("group");

        setContentView(R.layout.activity_depenses);

        Toolbar toolbar = findViewById(R.id.depenses_toolbar);
        toolbar.setTitle("Groupe : " + selectedGroup.getTitle());
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.depenses_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDepensesForm();
            }
        });


        updateDépensesList();
        weakActivity = new WeakReference<DepensesActivity>(DepensesActivity.this);

    }

    public static DepensesActivity getInstanceActivity() {
        return weakActivity.get();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_depenses, menu);
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
            case R.id.export:

                // Export SQLite DB as EXCEL FILE
                SQLiteToExcel sqliteToExcel = new SQLiteToExcel(getApplicationContext(), "fr.isima.technomobile.db");
                Log.d(TAG, "export on");
                sqliteToExcel.exportSingleTable("depenses_table", "depenses.xls", new SQLiteToExcel.ExportListener() {
                    @Override
                    public void onStart() {

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

            case R.id.import_db:

                Log.d(TAG, "import");
         /*       Intent intent2 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent2.addCategory(Intent.CATEGORY_OPENABLE);
                intent2.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                intent2.setType("application/json");
                intent2.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.getStorageDirectory().getPath() + "primary/depenses.xls");
                intent2.putExtra(Intent.EXTRA_TITLE,"depenses-" + Instant.now() + ".json");

                Log.d(TAG, "intent");*/
                // Is used to import data from excel without dropping table
                // ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(), DBHelper.DB_NAME);

                // if you want to add column in excel and import into DB, you must drop the table
                ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(), "fr.isima.technomobile.db", false);
                // Import EXCEL FILE to SQLite
                excelToSQLite.importFromFile( "/storage/self/primary/depenses.xls", new ExcelToSQLite.ImportListener() {
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
                return  true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    public void updateDépensesList() {

        DepensesDBHelper dépensesDBHelper = new DepensesDBHelper(this);

        ListView listView = (ListView) findViewById(R.id.depenses_list);
        ArrayList<Depenses> array = new ArrayList<>();
        array.addAll(dépensesDBHelper.getAllDépensess(selectedGroup.getId()));
        DepensesListAdapter adapter = new DepensesListAdapter(this, array);
        listView.setAdapter(adapter);
        Log.i(TAG, "Dep Act updt");
    }

    public void showAddDepensesForm() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        scrollView.addView(linearLayout);
        EditText editText = new EditText(this);
        DatePicker datePicker = new DatePicker( this);
        TextView text = new TextView(this);
        text.setText("Nom de dépense");
        linearLayout.addView(text);
        linearLayout.addView(editText);
        TextView text2 = new TextView(this);
        text2.setText("Date");
        linearLayout.addView(text2);
        linearLayout.addView(datePicker);


        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Ajouter dépense")
                .setView(scrollView)
                .setPositiveButton("Ajouter", new DialogInterface.OnClickListener () {
                    @ Override
                    public void onClick ( DialogInterface dialog , int which ) {
                       // Log.d("staart","ok");
                        String title = String.valueOf(editText.getText());
                        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
                        String date = dateformat.format(new Date( (datePicker.getYear())-1900 , datePicker.getMonth(),datePicker.getDayOfMonth()));
                        Log.d("date", date);
                        DepensesDBHelper dépensesDBHelper = new DepensesDBHelper(DepensesActivity.this.getApplicationContext());
                        dépensesDBHelper.addDépenses(title,date, selectedGroup.getId());
                        updateDépensesList();
                       // Log.d("end","done");

                    }
                })
                .setNegativeButton ("Annuler", null )
                .create();
        dialog.show() ;
    }
}