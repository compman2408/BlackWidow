package com.example.blackwidow;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.example.blackwidow.PhoneDB.*;

public class SavedScans extends Activity {

    private ArrayList<Scan> scans;
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<ListItem>> listHash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_scans);

        scans = GetScansFromDB(this);
        listView = (ExpandableListView) findViewById(R.id.lvScans);
        prepareListData(scans);
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listHash);
    }

    private void prepareListData(ArrayList<Scan> scans) {
        listDataHeader = new ArrayList<String>();
        listHash = new HashMap<String, List<ListItem>>();

        Iterator<Scan> scanIterator = scans.iterator();
        while (scanIterator.hasNext()) {


            List<ListItem> hostsInScan = new ArrayList<ListItem>();
            Scan scan = scanIterator.next();
            listDataHeader.add(scan.getName());
            ArrayList<Device> devices = scan.getDevices();
            Iterator<Device> deviceIterator = devices.iterator();

            while (deviceIterator.hasNext()) {

                StringBuilder deviceInfo = new StringBuilder();
                Device device = deviceIterator.next();

                deviceInfo.append(device.getId() + " ");
                deviceInfo.append("Host Name: " + device.getHostName() + "\n");
                deviceInfo.append("Ip: " + device.getIpAddress() + "\n");
                deviceInfo.append("Open Ports: " + device.getOpenPorts() + "\n");
                deviceInfo.append("Os: " + device.getOperatingSystem() + "\n");
                deviceInfo.append("Exploits:\n");

                ArrayList<Exploit> exploits = device.getExploits();
                Iterator<Exploit> exploitIterator = exploits.iterator();

                while (exploitIterator.hasNext()) {
                    Exploit exploit = exploitIterator.next();
                    deviceInfo.append("\tName: " + exploit.getName());
                    deviceInfo.append("\tDescription: " + exploit.getDescription());
                }

                ListItem item = new ListItem(deviceInfo.toString(),false);
                hostsInScan.add(item);

            }
            listHash.put(scan.getName(), hostsInScan);
        }
    }

}
