package fr.isima.technomobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import fr.isima.technomobile.R;
import fr.isima.technomobile.activities.MembersActivity;
import fr.isima.technomobile.db.entities.Contact;

public class MembersListAdapter extends ArrayAdapter<Contact> {

    private static final String TAG = "LOG_INF";

    public MembersListAdapter(Context context, ArrayList<Contact> groups) {
        super(context, 0, groups);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Contact contact = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.member_entry, parent, false);
        }
        TextView tvTask = (TextView) convertView.findViewById(R.id.member_name);
        tvTask.setText(contact.getName());

        convertView.setOnLongClickListener(v -> {
            MembersActivity.getInstanceActivity().showDeleteContactConfirmation(contact);
            return true;
        });

        return convertView;
    }
}
