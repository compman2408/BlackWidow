package com.example.blackwidow;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 11/28/2017.
 */

public class NmapReturn {
    public String ip, optHost, bestOSGuess, deviceType = "";
    public Map<Integer, String> unfiltered;

    public NmapReturn() {
        this.unfiltered = new HashMap<Integer, String>();
    }

}
