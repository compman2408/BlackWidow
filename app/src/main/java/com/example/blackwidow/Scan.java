package com.example.blackwidow;

import java.util.ArrayList;

/**
 * Contributed to by Alex
 * Data type to store the information about a specific nmap scan that was saved, including a time stamp, a name to save it as in the SQLite Database and an ArrayList of all the devices returned by the scan
 */

public class Scan {

        private String _id;
        private String _name;
        private String _timeStamp;
        private ArrayList<Device> _deviceList;

        public Scan(String id, String name, String timeStamp, ArrayList<Device> deviceList) {
            _id = id;
            _name = name;
            _timeStamp = timeStamp;
            _deviceList = deviceList;

        }

        // Getters
        public String getId() {
            return _id;
        }

        public String getName() { return _name; }

        public String getTimeStamp() { return _timeStamp; }

        public ArrayList<Device> getDevices() { return _deviceList; }

        // Setters
        public void setId(String id) { _id = id; }

        public void setName(String name) { _name = name; }

        public void setTimeStamp(String timeStamp) { _timeStamp = timeStamp; }

        public void setDeviceList(ArrayList<Device> deviceList) { _deviceList = deviceList; }
}
