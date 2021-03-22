package fr.isima.technomobile.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_depense);

        selectedDepense = getIntent().getExtras().getParcelable("depense");

        Toolbar toolbar = findViewById(R.id.detail_depenses_toolbar);
        toolbar.setTitle("Dépense : " + selectedDepense.getTitle());
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
        String shareBody = "Récapitulatif de : " + depense.getTitle() + " - " + depense.getDate() +
                "\nTotal des dépenses : " + formatter.format(depenseSum) + " EUR";
        for(DetailDepense dd : detailDepenses) {
            double part = partitionDBHelper.getPartition(dd.getMember().getNumber(), dd.getDepenseId());
            shareBody += "\n - " + dd.getMember().getName() + " a dépenseé :\n";
            double emitter = 0.0;
            for(Emission e : dd.getEmissions()) {
                shareBody += "\t + " + e.getDesignation() + " : " + formatter.format(e.getValue()) + " EUR\n";
                emitter += e.getValue();
            }
            shareBody += "\t\t\t\t => Emitter : " + formatter.format(emitter) + " EUR. " +
                    "\n\t\t\t\t => Débiter : " + formatter.format((depenseSum * part) / 100) + " EUR. " +
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

}