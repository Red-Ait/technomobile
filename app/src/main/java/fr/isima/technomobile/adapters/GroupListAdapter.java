package fr.isima.technomobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import fr.isima.technomobile.R;
import fr.isima.technomobile.activities.GroupActivity;
import fr.isima.technomobile.activities.MainActivity;
import fr.isima.technomobile.db.entities.Group;

public class GroupListAdapter extends ArrayAdapter<Group> {

    private static final String TAG = "LOG_INF";

    public GroupListAdapter(Context context, ArrayList<Group> groups) {
        super(context, 0, groups);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Group group = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_entry, parent, false);
        }
        TextView tvTask = (TextView) convertView.findViewById(R.id.itemTitle);
        tvTask.setText(group.getTitle());

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.getInstanceActivity(), GroupActivity.class).putExtra("group", group);
            MainActivity.getInstanceActivity().startActivity(intent);
        });

        return convertView;
    }
}
