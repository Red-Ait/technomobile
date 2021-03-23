package fr.isima.technomobile.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ajts.androidmads.library.ExcelToSQLite;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ajts.androidmads.library.SQLiteToExcel;

import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import fr.isima.technomobile.R;
import fr.isima.technomobile.adapters.DepensesListAdapter;
import fr.isima.technomobile.db.DepensesDBHelper;
import fr.isima.technomobile.db.entities.Depenses;
import fr.isima.technomobile.db.entities.Group;

public class DepensesActivity extends AppCompatActivity {

    public static WeakReference<DepensesActivity> weakActivity;
    private static final String TAG = "LOG_INF";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 400;
    Group selectedGroup = null;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int FILE_SELECT_CODE = 0;


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

        if (!checkPermission()) {
            requestPermission(); // Code for permission
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(DepensesActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(DepensesActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(DepensesActivity.this, "Write External Storage permission allows us to do export data. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(DepensesActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
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
                sqliteToExcel.exportSingleTable("depenses_table", "depenses.xls", new SQLiteToExcel.ExportListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onCompleted(String filePath) {
                        Log.d(TAG, "done");
                       Toast.makeText(getApplicationContext(),"Le fichier est bien sauvegardé à l'endroit " + filePath +"",Toast.LENGTH_SHORT).show();
                   /*    Intent intent2 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent2.addCategory(Intent.CATEGORY_OPENABLE);
                        intent2.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                        intent2.putExtra(DocumentsContract.EXTRA_INITIAL_URI, filePath);
                        startActivity(intent2);*/



                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getApplicationContext(),"Erreur , veuillez réessayez ",Toast.LENGTH_SHORT).show();

                    }

                });
                return true;

            case R.id.import_db:

                Log.d(TAG, "import");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(
                            Intent.createChooser(intent, "Select a File to Upload"),
                            FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(this, "Please install a File Manager.",
                            Toast.LENGTH_SHORT).show();
                }

                // Is used to import data from excel without dropping table
                // ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(), DBHelper.DB_NAME);

                // if you want to add column in excel and import into DB, you must drop the table
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path = null;
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    Log.d(TAG, "File Uri: " +  uri);
                    // Get the path

                    path = uri.getPath();
                    Log.d(TAG, "File Path: " + path);
                    final String[] split = path.split(":");//split the path.
                    Log.d(TAG, "split: " + split);
                    String filePath = split[1];//assign it to a string(your choice).
                    Log.d(TAG, "filePath: " + filePath);
                    Log.d(TAG, "path: " + Environment.getExternalStorageDirectory().getPath()+"/"+filePath);


                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                    ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(), "fr.isima.technomobile.db", false);
                    // Import EXCEL FILE to SQLite
                    excelToSQLite.importFromFile(Environment.getExternalStorageDirectory().getPath()+"/"+ filePath, new ExcelToSQLite.ImportListener() {
                        @Override
                        public void onStart() {
                            Log.d(TAG, "start");
                        }

                        @Override
                        public void onCompleted(String dbName) {
                            Log.d(TAG, "done");
                            Toast.makeText(getApplicationContext(),"Le fichier a été importé",Toast.LENGTH_SHORT).show();


                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d(TAG, "error");
                            e.getMessage();

                        }
                    });

                }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

                        String title = String.valueOf(editText.getText());
                        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
                        String date = dateformat.format(new Date( (datePicker.getYear())-1900 , datePicker.getMonth(),datePicker.getDayOfMonth()));
                        DepensesDBHelper dépensesDBHelper = new DepensesDBHelper(DepensesActivity.this.getApplicationContext());
                        dépensesDBHelper.addDépenses(title,date, selectedGroup.getId());
                        updateDépensesList();

                    }
                })
                .setNegativeButton ("Annuler", null )
                .create();
        dialog.show() ;
    }
}