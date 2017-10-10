package com.example.blackwidow;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ActivityScan extends Activity {

    private ListView _lstViewDevices;
    private List<Device> _lstDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        _lstViewDevices = (ListView) findViewById(R.id.lstViewDevices);
        _lstDevices = new ArrayList();
        _lstDevices.add(new Device("WIN10-DESKTOP", "192.168.1.21"));
        _lstDevices.add(new Device("SANTA'S IPHONE", "192.168.1.111"));
        _lstDevices.add(new Device("UR-NEIGHBORHOOD-HAKR", "0.0.0.0"));

        final DeviceListArrayAdapter aa = new DeviceListArrayAdapter(this, GetDeviceArray());
        _lstViewDevices.setAdapter(aa);
    }

    private Device[] GetDeviceArray() {
        Device[] output = new Device[_lstDevices.size()];

        for (int i = 0; i < _lstDevices.size(); i++) {
            output[i] = _lstDevices.get(i);
        }

        return output;
    }
}
