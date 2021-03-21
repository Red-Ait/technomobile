package fr.isima.technomobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import fr.isima.technomobile.R;
import fr.isima.technomobile.db.entities.Contact;
import fr.isima.technomobile.db.entities.Depenses;
import fr.isima.technomobile.db.entities.DetailDepense;
import fr.isima.technomobile.db.entities.Emission;

public class DetailDepenseListAdapter extends ArrayAdapter<DetailDepense> {

    public DetailDepenseListAdapter(Context context, ArrayList<DetailDepense> detailDepenses) {
        super(context, 0, detailDepenses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DetailDepense detailDepense = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.detail_depense_entry, parent, false);
        }
        TextView title = convertView.findViewById(R.id.detail_depense_membre_name);
        title.setText(detailDepense.getMember().getName());

        NumberFormat formatter = new DecimalFormat("#0.00");

        double emitter = 0.0;
        for(Emission e : detailDepense.getEmissions()) {
            emitter += e.getValue();
        }
        TextView emitterTxt = convertView.findViewById(R.id.emitter_value);
        emitterTxt.setText(formatter.format(emitter) + " EUR");


        initListEmission(convertView,detailDepense.getEmissions());

        return convertView;
    }

    private void initListEmission(View view, List<Emission> emissions) {

        ListView listView = (ListView) view.findViewById(R.id.emission_list);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) listView.getLayoutParams();
        params.height = 155 * emissions.size();
        listView.setLayoutParams(params);
        listView.requestLayout();

        EmissionListAdapter adapter = new EmissionListAdapter(getContext(), new ArrayList<>(emissions));
        listView.setAdapter(adapter);

    }
}
