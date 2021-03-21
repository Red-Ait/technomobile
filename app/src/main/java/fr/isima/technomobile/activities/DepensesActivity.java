package fr.isima.technomobile.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedGroup = getIntent().getExtras().getParcelable("group");

        setContentView(R.layout.activity_depenses);

        Toolbar toolbar = findViewById(R.id.depenses_toolbar);
        toolbar.setTitle("Dépenses : " + selectedGroup.getTitle());
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