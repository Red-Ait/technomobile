package fr.isima.technomobile.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import fr.isima.technomobile.R;
import fr.isima.technomobile.adapters.DetailDepenseListAdapter;
import fr.isima.technomobile.adapters.MembersListAdapter;
import fr.isima.technomobile.db.MemberDBHelper;
import fr.isima.technomobile.db.entities.Contact;
import fr.isima.technomobile.db.entities.Depenses;
import fr.isima.technomobile.db.entities.DetailDepense;
import fr.isima.technomobile.db.entities.Emission;

public class DetailDepenseActivity extends AppCompatActivity {

    private Depenses selectedDepense;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_depense);

        selectedDepense = getIntent().getExtras().getParcelable("depense");

        TextView title = (TextView) findViewById(R.id.depense_name);
        title.setText(selectedDepense.getTitle());

        TextView date = (TextView) findViewById(R.id.depense_date);
        date.setText(selectedDepense.getDate());
        updateDetailDepenseList();
    }

    public void updateDetailDepenseList() {
        MemberDBHelper memberDBHelper = new MemberDBHelper(this);
        List<Contact> contacts = memberDBHelper.getAllGroupMember(selectedDepense.getGroupId());

        ListView listView = (ListView) findViewById(R.id.detail_depense_list);

        ArrayList<DetailDepense> array = new ArrayList<>();
        for(Contact c : contacts) {
            array.add(new DetailDepense(c));
        }

        DetailDepenseListAdapter adapter = new DetailDepenseListAdapter(this, array);
        listView.setAdapter(adapter);
    }
}