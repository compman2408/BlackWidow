package com.example.blackwidow;

import java.util.HashMap;
import java.util.Map;

/**
 * Contributed to by Alex
 * A data type to store the device information from the parser of the nmap scan results
 */

public class NmapReturn {
    public String ip = "", optHost = "", bestOSGuess = "", deviceType = "";
    public Map<Integer, String> unfiltered;

    public NmapReturn() {
        this.unfiltered = new HashMap<Integer, String>();
    }

}
