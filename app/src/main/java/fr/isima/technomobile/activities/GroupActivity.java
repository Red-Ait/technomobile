package fr.isima.technomobile.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.Button;
import android.widget.TextView;

import fr.isima.technomobile.R;
import fr.isima.technomobile.db.entities.Group;

public class GroupActivity extends AppCompatActivity {

    private static final String TAG = "LOG_INF";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Group groupObjInToClass = getIntent().getExtras().getParcelable("group");

        setContentView(R.layout.group_activity_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(groupObjInToClass.getTitle());
        setSupportActionBar(toolbar);

        TextView textView = (TextView) findViewById(R.id.textview_group_name);
        textView.setText("Groupe : " + groupObjInToClass.getTitle());

        Button btnMembers = (Button) findViewById(R.id.btn_membres);
        btnMembers.setOnClickListener(v -> {
            Intent intent = new Intent(GroupActivity.this, MembersActivity.class).putExtra("group", groupObjInToClass);
            startActivity(intent);

        });

        Button btnDepenses = (Button) findViewById(R.id.btn_depenses);
        btnDepenses.setOnClickListener(v -> {
            Intent intent = new Intent(GroupActivity.this, DepensesActivity.class).putExtra("group", groupObjInToClass);
            startActivity(intent);

        });

    }
}