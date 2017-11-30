package com.example.blackwidow;

import java.util.ArrayList;

/**
 * Created by Ryan on 10/10/2017.
 * A data type used to store information about hosts on the network
 */

public class Device {
    private String _id;
    private String _hostName;
    private String _ipAddress;
    private String _operatingSystem;
    private String _openPorts;
    private ArrayList<Exploit> _exploits;

    public Device(String id, String hostName, String ipAddress, String operatingSystem, String openPorts, ArrayList<Exploit> exploits) {
        _id = id;
        _hostName = hostName;
        _ipAddress = ipAddress;
        _operatingSystem = operatingSystem;
        _openPorts = openPorts;
        _exploits = exploits;

    }

    // Getters
    public String getId() { return _id; }

    public String getHostName() {
        return _hostName;
    }

    public String getIpAddress() {
        return _ipAddress;
    }

    public String getOperatingSystem() { return _operatingSystem; }

    public String getOpenPorts() { return _openPorts; }

    public ArrayList<Exploit> getExploits() { return _exploits; }

    // Setters
    public void setId(String id) { _id = id; }

    public void setHostName(String hostName) { _hostName = hostName; }

    public void setIpAddress(String ipAddress) { _ipAddress = ipAddress; }

    public void setOperatingSystem(String operatingSystem) { _operatingSystem = operatingSystem; }

    public void setOpenPorts(String openPorts) { _openPorts = openPorts; }

    public void setExploits(ArrayList<Exploit> exploits) { _exploits = exploits; }

}
