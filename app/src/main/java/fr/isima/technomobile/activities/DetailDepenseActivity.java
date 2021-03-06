package fr.isima.technomobile.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ajts.androidmads.library.ExcelToSQLite;
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
import fr.isima.technomobile.db.GroupSchema;
import fr.isima.technomobile.db.MemberDBHelper;
import fr.isima.technomobile.db.PartitionDBHelper;
import fr.isima.technomobile.db.entities.Contact;
import fr.isima.technomobile.db.entities.Depenses;
import fr.isima.technomobile.db.entities.DetailDepense;
import fr.isima.technomobile.db.entities.Emission;

public class DetailDepenseActivity extends AppCompatActivity {
    double depenseSum = 0;
    public static WeakReference<DetailDepenseActivity> weakActivity;
    private static final String TAG = "LOG_INF";
    private Depenses selectedDepense;
    ArrayList<DetailDepense> detailDepenses = new ArrayList<>();
    private static final int FILE_SELECT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_depense);

        selectedDepense = getIntent().getExtras().getParcelable("depense");

        Toolbar toolbar = findViewById(R.id.detail_depenses_toolbar);
        toolbar.setTitle("D??pense : " + selectedDepense.getTitle());
        setSupportActionBar(toolbar);

        TextView title = (TextView) findViewById(R.id.depense_name);
        title.setText(selectedDepense.getTitle());

        TextView date = (TextView) findViewById(R.id.depense_date);
        date.setText(selectedDepense.getDate());
        updateDetailDepenseList();
        weakActivity = new WeakReference<>(DetailDepenseActivity.this);
        Button recap = findViewById(R.id.send_recap);
        recap.setOnClickListener(v -> {
            sendRecap(selectedDepense);
        });
    }
    public static DetailDepenseActivity getInstanceActivity() {
        return weakActivity.get();
    }
    public void sendRecap(Depenses depense) {

        NumberFormat formatter = new DecimalFormat("#0.00");
        PartitionDBHelper partitionDBHelper = new PartitionDBHelper(this);

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        String shareBody = "R??capitulatif de : " + depense.getTitle() + " - " + depense.getDate() +
                "\nTotal des d??penses : " + formatter.format(depenseSum) + " EUR";
        for(DetailDepense dd : detailDepenses) {
            double part = partitionDBHelper.getPartition(dd.getMember().getNumber(), dd.getDepenseId());
            shareBody += "\n - " + dd.getMember().getName() + " a d??pense?? :\n";
            double emitter = 0.0;
            for(Emission e : dd.getEmissions()) {
                shareBody += "\t + " + e.getDesignation() + " : " + formatter.format(e.getValue()) + " EUR\n";
                emitter += e.getValue();
            }
            shareBody += "\t\t\t\t => Emitter : " + formatter.format(emitter) + " EUR. " +
                    "\n\t\t\t\t => D??biter : " + formatter.format((depenseSum * part) / 100) + " EUR. " +
                    "\n\t\t\t\t => Balance : " + formatter.format(emitter - (depenseSum * part) / 100);
        }
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(intent, ""));
    }

    public void updateSumValue(double d) {
        depenseSum += d;
        NumberFormat formatter = new DecimalFormat("#0.00");
        TextView depSum = findViewById(R.id.depense_sum);
        depSum.setText(formatter.format(depenseSum) + " EUR");

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
                            Toast.makeText(getApplicationContext(),"Le fichier a ??t?? import??",Toast.LENGTH_SHORT).show();


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

    public void updateDetailDepenseList() {
        MemberDBHelper memberDBHelper = new MemberDBHelper(this);
        List<Contact> contacts = memberDBHelper.getAllGroupMember(selectedDepense.getGroupId());

        ListView listView = (ListView) findViewById(R.id.detail_depense_list);

        detailDepenses = new ArrayList<>();
        depenseSum = 0.0;
        for(Contact c : contacts) {
            //
            EmissionBDHelper emissionBDHelper = new EmissionBDHelper(this);
            List<Emission> emissions = emissionBDHelper.getAllEmission(selectedDepense.getId(), c.getNumber());
            for(Emission e : emissions) {
                depenseSum += e.getValue();
            }
            DetailDepense detailDepense = new DetailDepense(c, selectedDepense.getId());
            detailDepense.setEmissions(emissions);
            detailDepenses.add(detailDepense);
        }
        NumberFormat formatter = new DecimalFormat("#0.00");
        TextView depSum = findViewById(R.id.depense_sum);
        depSum.setText(formatter.format(depenseSum) + " EUR");

        DetailDepenseListAdapter adapter = new DetailDepenseListAdapter(this, detailDepenses, depenseSum);
        listView.setAdapter(adapter);

        Log.i(TAG, "Dep det Act updt");
    }
    public void showDeleteEmissionConfirmation(Emission emission) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Voulez-vous supprimer " + emission.getDesignation() + " de d??pense :" + selectedDepense.getTitle() + " ?")
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

                    }

                    @Override
                    public void onCompleted(String filePath) {
                        Toast.makeText(getApplicationContext(),"Le fichier est bien sauvegard?? ?? l'endroit " + filePath +"",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getApplicationContext(),"Erreur , veuillez r??essayez ",Toast.LENGTH_SHORT).show();

                    }

                });
                return true;

            case R.id.import_emission:

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


            default:
                return super.onOptionsItemSelected(item);

        }

    }

}