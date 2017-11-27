package com.example.blackwidow;

/**
 * Created by Ryan on 10/10/2017.
 */

public class Device {
    private String _hostName;
    private String _ipAddress;
    private String _operatingSystem;
    private String _openPorts;
    private Exploit[] _exploits;

    public Device(String hostName, String ipAddress, String operatingSystem, String openPorts, Exploit[] exploits) {
        _hostName = hostName;
        _ipAddress = ipAddress;
        _operatingSystem = operatingSystem;
        _openPorts = openPorts;
        _exploits = exploits;

    }

    public String getHostName() {
        return _hostName;
    }

    public String getIpAddress() {
        return _ipAddress;
    }

    public String getOperatingSystem() { return _operatingSystem; }

    public String getOpenPorts() { return _openPorts; }

    public Exploit[] getExploits() { return _exploits; }

}
