package fr.isima.technomobile.activities;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import fr.isima.technomobile.R;
import fr.isima.technomobile.adapters.GroupListAdapter;
import fr.isima.technomobile.db.GroupDBHelper;
import fr.isima.technomobile.db.entities.Group;

public class MainActivity extends AppCompatActivity {

    public static WeakReference<MainActivity> weakActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.mes_groupes);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddGroupForm();
            }
        });
        updateGroupList();
        weakActivity = new WeakReference<>(MainActivity.this);
    }

    public static MainActivity getInstanceActivity() {
        return weakActivity.get();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void updateGroupList() {
        GroupDBHelper groupDBHelper = new GroupDBHelper(this);

        ListView listView = (ListView) findViewById(R.id.group_list);
        ArrayList<Group> array = new ArrayList<>();
        array.addAll(groupDBHelper.getAllGroups());
        GroupListAdapter adapter = new GroupListAdapter(this, array);
        listView.setAdapter(adapter);
    }
    public void showAddGroupForm() {
        final EditText editText = new EditText( this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Group")
                .setMessage("Group Name")
                .setView(editText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener () {
                    @ Override
                    public void onClick ( DialogInterface dialog , int which ) {
                        String groupName = String.valueOf(editText.getText());
                        GroupDBHelper groupDBHelper = new GroupDBHelper(MainActivity.this.getApplicationContext());
                        groupDBHelper.addGroup(groupName);
                        updateGroupList();
                    }
                })
                .setNegativeButton ("Cancel", null )
                .create();
        dialog.show() ;
    }
}