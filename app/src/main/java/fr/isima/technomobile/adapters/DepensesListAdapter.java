package fr.isima.technomobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import fr.isima.technomobile.R;
import fr.isima.technomobile.activities.DépensesActivity;
import fr.isima.technomobile.activities.MainActivity;
import fr.isima.technomobile.db.entities.Depenses;

public class DepensesListAdapter extends ArrayAdapter<Depenses> {

    private static final String TAG = "ADAPTER";

    public DepensesListAdapter(Context context, ArrayList<Depenses> dépenses) {
        super(context, 0, dépenses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Depenses dépense = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.depense_entry, parent, false);
        }
        TextView tvTask = (TextView) convertView.findViewById(R.id.itemTitle);
        tvTask.setText(dépense.getTitle());

        TextView tvTask2 = (TextView) convertView.findViewById(R.id.date);
        tvTask2.setText(dépense.getDate());


        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.getInstanceActivity(), DépensesActivity.class);
            MainActivity.getInstanceActivity().startActivity(intent);
        });

        return convertView;
    }
}