package com.example.blackwidow;

/**
 * Created by Ryan on 10/10/2017.
 */

public class Device {
    private String _hostName;
    private String _ipAddress;

    public Device(String hostName, String ipAddress) {
        _hostName = hostName;
        _ipAddress = ipAddress;
    }

    public String getHostName() {
        return _hostName;
    }

    public String getIpAddress() {
        return _ipAddress;
    }
}
