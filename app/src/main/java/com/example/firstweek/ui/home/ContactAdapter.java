package com.example.firstweek.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.firstweek.R;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {
    public ContactAdapter(Context context, List<Contact> contacts){ super(context,0,contacts);}

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Contact contact=getItem(position);
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.contact_item,parent,false);
            TextView textViewName=convertView.findViewById(R.id.textViewName);
            TextView textViewPhone=convertView.findViewById(R.id.textViewPhone);

            textViewName.setText(contact.getName());
            textViewPhone.setText(contact.getPhone());

        }
        return convertView;
    }
}
