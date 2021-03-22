package fr.isima.technomobile.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import fr.isima.technomobile.R;
import fr.isima.technomobile.activities.DepensesActivity;
import fr.isima.technomobile.activities.DetailDepenseActivity;
import fr.isima.technomobile.db.DepensesDBHelper;
import fr.isima.technomobile.db.EmissionBDHelper;
import fr.isima.technomobile.db.PartitionDBHelper;
import fr.isima.technomobile.db.entities.Contact;
import fr.isima.technomobile.db.entities.Depenses;
import fr.isima.technomobile.db.entities.DetailDepense;
import fr.isima.technomobile.db.entities.Emission;

public class DetailDepenseListAdapter extends ArrayAdapter<DetailDepense> {

    private static final String TAG = "LOG_INF";

    private Context context;
    private double sumDepense;
    public DetailDepenseListAdapter(Context context, ArrayList<DetailDepense> detailDepenses, double s) {
        super(context, 0, detailDepenses);
        this.context = context;
        this.sumDepense = s;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DetailDepense detailDepense = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.detail_depense_entry, parent, false);
        }
        TextView title = convertView.findViewById(R.id.detail_depense_membre_name);
        title.setText(detailDepense.getMember().getName());



        Button addEmission = convertView.findViewById(R.id.add_emission);
        addEmission.setOnClickListener(v -> {
            showAddDepensesForm(context, detailDepense.getMember(), detailDepense.getDepenseId());
        });

        initListEmission(convertView, detailDepense.getMember(), detailDepense.getDepenseId());

        updatePart(convertView, detailDepense);

        LinearLayout editPart = convertView.findViewById(R.id.edit_part);
        View finalConvertView = convertView;
        editPart.setOnClickListener(v -> {
            showEditPartitionForm(context, detailDepense.getMember().getNumber(),
                    new PartitionDBHelper(context).getPartition(detailDepense.getMember().getNumber(),
                            detailDepense.getDepenseId()), finalConvertView, detailDepense);
        });

        return convertView;
    }
    void updatePart(View convertView, DetailDepense detailDepense) {

        PartitionDBHelper partitionDBHelper = new PartitionDBHelper(context);
        double val = partitionDBHelper.getPartition(detailDepense.getMember().getNumber(), detailDepense.getDepenseId());
        double creditVal = (sumDepense * val)/100;
        double emitterVal = getEmitterVal(detailDepense.getMember(), detailDepense.getDepenseId());
        double balanceVal = emitterVal - creditVal;

        NumberFormat formatter = new DecimalFormat("#0.00");

        TextView partition = convertView.findViewById(R.id.detail_depense_membre_part);
        partition.setText(formatter.format(val) + " %");

        TextView credit = convertView.findViewById(R.id.credit_value);
        credit.setText(formatter.format(creditVal) + " EUR");

        TextView balance = convertView.findViewById(R.id.balance_value);
        balance.setText(formatter.format(balanceVal) + " EUR");

        if(balanceVal < 0) {
            balance.setTextColor(Color.parseColor("#FF0000"));
        } else {
            balance.setTextColor(Color.parseColor("#00FF00"));
        }


    }
    private double getEmitterVal( Contact contact, int depenseId) {
        EmissionBDHelper emissionBDHelper = new EmissionBDHelper(context);
        List<Emission> emissions = emissionBDHelper.getAllEmission(depenseId, contact.getNumber());

        double emitterSum = 0.0;
        for(Emission e : emissions) {
            emitterSum += e.getValue();
        }
        return emitterSum;
    }

    private void initListEmission(View view, Contact contact, int depenseId) {

        EmissionBDHelper emissionBDHelper = new EmissionBDHelper(context);
        List<Emission> emissions = emissionBDHelper.getAllEmission(depenseId, contact.getNumber());

        NumberFormat formatter = new DecimalFormat("#0.00");
        TextView emitterTxt = view.findViewById(R.id.emitter_value);
        emitterTxt.setText(formatter.format(getEmitterVal(contact, depenseId)) + " EUR");

        ListView listView = (ListView) view.findViewById(R.id.emission_list);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) listView.getLayoutParams();
        params.height = 155 * emissions.size() + 50;
        listView.setLayoutParams(params);
        listView.requestLayout();

        EmissionListAdapter adapter = new EmissionListAdapter(getContext(), new ArrayList<>(emissions));
        listView.setAdapter(adapter);

    }



    public void showAddDepensesForm(Context context, Contact contact, int depenseId) {

        ScrollView scrollView = new ScrollView(context);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        scrollView.addView(linearLayout);
        EditText des = new EditText(context);
        EditText val = new EditText(context);
        val.setInputType(InputType.TYPE_CLASS_NUMBER);

        TextView text = new TextView(context);
        text.setText("Désignation");
        linearLayout.addView(text);
        linearLayout.addView(des);
        TextView text2 = new TextView(context);
        text2.setText("Valeur d\'émission");
        linearLayout.addView(text2);
        linearLayout.addView(val);


        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Ajouter émission")
                .setView(scrollView)
                .setPositiveButton("Ajouter", new DialogInterface.OnClickListener () {
                    @ Override
                    public void onClick ( DialogInterface dialog , int which ) {
                        Emission emission = new Emission();
                        emission.setDesignation(String.valueOf(des.getText()));
                        emission.setValue(Integer.parseInt(String.valueOf(val.getText())));
                        emission.setMember(contact);
                        EmissionBDHelper emissionBDHelper = new EmissionBDHelper(context);
                        emissionBDHelper.addEmissionToDepense(emission, depenseId);
                        Log.i(TAG, "Det DET  Adapt add dep");

                    }
                })
                .setNegativeButton ("Annuler", null )
                .create();
        dialog.show() ;
    }


    public void showEditPartitionForm(Context context, String phone, double part, View convertView, DetailDepense detailDepense) {

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        EditText val = new EditText(context);
        val.setInputType(InputType.TYPE_CLASS_NUMBER);
        val.setText(String.valueOf(part));

        TextView text = new TextView(context);
        text.setText("Pourcentage");
        linearLayout.addView(text);
        linearLayout.addView(val);


        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Modifier le pourcentage")
                .setView(linearLayout)
                .setPositiveButton("Modifier", new DialogInterface.OnClickListener () {
                    @ Override
                    public void onClick ( DialogInterface dialog , int which ) {
                        PartitionDBHelper partitionDBHelper = new PartitionDBHelper(context);
                        partitionDBHelper.updatePartition(phone, detailDepense.getDepenseId(), Double.valueOf(String.valueOf(val.getText())));
                        updatePart(convertView, detailDepense);
                    }
                })
                .setNegativeButton ("Annuler", null )
                .create();
        dialog.show() ;
    }
}
