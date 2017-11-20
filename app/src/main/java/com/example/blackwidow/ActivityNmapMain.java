package com.example.blackwidow;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Bobak on 11/19/2017.
 */

public class ActivityNmapMain extends Activity {
    Spinner _spinnerCmdType;
    EditText _txtIP;
    Button _btnRunNmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmap_main);

        // Get References to views
    }
}
