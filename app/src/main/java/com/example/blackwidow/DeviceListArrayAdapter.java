package com.example.blackwidow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Ryan on 10/9/2017.
 */

public class DeviceListArrayAdapter extends ArrayAdapter<Device> {
    private final Context _context;
    private final Device[] _objects;

    public DeviceListArrayAdapter (Context context, Device[] objects) {
        super(context, -1, objects);
        _context = context;
        _objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get an inflator so that we can inflate our custom row
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Inflate our custom row into a view that we can use
        View rowView = inflater.inflate(R.layout.custom_device_list_item, parent, false);
        // Get the views in this row that will be set based on the current object
        TextView txtHostName = (TextView) rowView.findViewById(R.id.txtDeviceListItemHostName);
        TextView txtIpAddress = (TextView) rowView.findViewById(R.id.txtDeviceListItemIpAddress);
        // Set the view based on the current object in the list
        txtHostName.setText(_objects[position].getHostName());
        txtIpAddress.setText(_objects[position].getIpAddress());
        return rowView;
    }
}
