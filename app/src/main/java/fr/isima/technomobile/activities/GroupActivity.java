package fr.isima.technomobile.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fr.isima.technomobile.R;
import fr.isima.technomobile.db.entities.Group;

public class GroupActivity extends AppCompatActivity {

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
    }
}