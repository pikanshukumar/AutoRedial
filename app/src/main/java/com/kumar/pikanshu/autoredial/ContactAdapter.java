package com.kumar.pikanshu.autoredial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by pika on 24/10/16.
 */

public class ContactAdapter extends ArrayAdapter<Contact> {

    public ContactAdapter(Context context, ArrayList<Contact> contacts){
        super(context,0,contacts);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // check if current view is being reused, otherwise inflate the view

        View listItemView = convertView;

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.contact_list_item,parent,false);
        }

        final Contact currentContact = getItem(position);

        TextView nameTextView = (TextView) listItemView.findViewById(R.id.name);
        nameTextView.setText(currentContact.getName());

        TextView phoneNumberTextView = (TextView) listItemView.findViewById(R.id.phoneNumber);
        phoneNumberTextView.setText(currentContact.getPhoneNumber());

        final CheckBox checkBox = (CheckBox) listItemView.findViewById(R.id.checkBox);

        checkBox.setChecked(currentContact.isSelected());

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentContact.setSelected(checkBox.isChecked());
            }
        });


        return listItemView;
    }
}
