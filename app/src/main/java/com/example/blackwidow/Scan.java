package com.example.blackwidow;

/**
 * Created by Alex on 11/27/2017.
 */

public class Scan {

        private String _id;
        private String _name;
        private String _timeStamp;
        private Device[] _deviceList;

        public Scan(String id, String name, String timeStamp, Device[] deviceList) {
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

        public Device[] getDevices() { return _deviceList; }

        // Setters
        public void setId(String id) { _id = id; }

        public void setName(String name) { _name = name; }

        public void setTimeStamp(String timeStamp) { _timeStamp = timeStamp; }

        public void setDeviceList(Device[] deviceList) { _deviceList = deviceList; }


}
