package fr.isima.technomobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import fr.isima.technomobile.R;
import fr.isima.technomobile.db.entities.DetailDepense;
import fr.isima.technomobile.db.entities.Emission;

public class EmissionListAdapter extends ArrayAdapter<Emission> {

    public EmissionListAdapter(Context context, ArrayList<Emission> emissions) {
        super(context, 0, emissions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Emission emission = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.emission_entry, parent, false);
        }
        NumberFormat formatter = new DecimalFormat("#0.00");

        TextView des = convertView.findViewById(R.id.emission_designation);
        des.setText(emission.getDesignation());
        TextView val = convertView.findViewById(R.id.emission_value);
        val.setText(formatter.format(emission.getValue()));

        return convertView;
    }
}
