package com.example.blackwidow;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Bobak on 11/19/2017.
 */

public class ActivityEZScan extends Activity implements IAsyncCommandCallback {
    private static final String TAG = "ActivityEZScan";
    private static final String[] SCANTYPES = new String[]{"REGULAR SCAN", "TCP SYN SCAN", "TCP NULL SCAN"};
    private static final String[] SCANTYPEARGS = new String[]{"", "-sS", "-sN"};
    private static final String[] OPTIONTYPES = new String[]{"UDP SCAN", "OS DETECTION", "FAST", "OPEN PORT SCAN", "VERBOSE", "TIMING TEMPLATE", "TRACEROUTE"};
    private static final String[] OPTIONARGS = new String[]{"-sU", "-O", "-F", "-sV", "-v", "-T4", "-traceroute"};

    EditText _txtIPAddress;
    Button _btnScanNmap;
    Button _btnSaveResults;
    ScrollView _scrollResults;
    TextView _lblResults;
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;
    public static HashMap<String, String> argMap;
    boolean[] optionsArr;
    String scanType;
    String cmdString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ez_scan);

        // Get References to views
        _txtIPAddress = (EditText) findViewById(R.id.txtIPAddress);
        _btnScanNmap = (Button) findViewById(R.id.btnScanNmap);
        _btnSaveResults = (Button) findViewById(R.id.btnSaveResults);
        _scrollResults = (ScrollView) findViewById(R.id.scrollResults);
        _lblResults = (TextView) findViewById(R.id.lblResults);

        listView = (ExpandableListView) findViewById(R.id.lvExpOptions);
        initData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listHash);
        listView.setAdapter(listAdapter);
        listView.expandGroup(0);

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            View _lastScanTypeSelected;

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.wtf("CLICK", "CLICK " + groupPosition + " " + childPosition);
                if (groupPosition == 0) { //for SCAN TYPES
                    scanType = SCANTYPES[childPosition]; //set the new option

                    Log.wtf("CLICK", "CLICK " + v);
                    if (_lastScanTypeSelected != null) {
                        ((LinearLayout) _lastScanTypeSelected).getChildAt(0).setBackgroundColor(Color.WHITE);
                    }
                    ((LinearLayout) v).getChildAt(0).setBackgroundColor(Color.parseColor("#ff202f"));
                    _lastScanTypeSelected = v;

                } else if (groupPosition == 1) { //for OPTIONS
                    optionsArr[childPosition] = !optionsArr[childPosition];
                    printOptionsArrayLog();

                    if (optionsArr[childPosition]) {
                        ((LinearLayout) v).getChildAt(0).setBackgroundColor(Color.parseColor("#ff202f"));
                    } else {
                        ((LinearLayout) v).getChildAt(0).setBackgroundColor(Color.WHITE);
                    }
                }
                return false;
            }
        });
    }

    private void initData() {
        optionsArr = new boolean[7]; //initialize options to false
        scanType = SCANTYPES[0];

        argMap = new HashMap<>();

        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("SCAN TYPE");
        listDataHeader.add("OPTIONS");

        List<String> lstScanTypes = new ArrayList<>();
        for (int i = 0; i < SCANTYPES.length; i++) {
            lstScanTypes.add(SCANTYPES[i]);
            argMap.put(SCANTYPES[i], SCANTYPEARGS[i]);
        }

        List<String> lstScanOptions = new ArrayList<>();
        for (int i = 0; i < OPTIONTYPES.length; i++) {
            lstScanOptions.add(OPTIONTYPES[i]);
            argMap.put(OPTIONTYPES[i], OPTIONARGS[i]);
        }

        listHash.put(listDataHeader.get(0), lstScanTypes);
        listHash.put(listDataHeader.get(1), lstScanOptions);
    }

    public String generateCommandFromOptions() {
        cmdString = argMap.get(scanType) + " ";
        for (int i = 0; i < OPTIONTYPES.length; i++) {
            if (optionsArr[i]) {
                cmdString = cmdString + OPTIONARGS[i] + " ";
                Log.wtf("COMMAND", cmdString);
            }
        }
        cmdString = cmdString + _txtIPAddress.getText().toString();
        return cmdString;
    }

    private void runCommandInCLIAsync(String cmdToExecute) {
        //String cmdOutput = executeCommand(cmdToExecute);
        // Execute the command
        Utils.ExecuteCommandAsync(this, cmdToExecute, this);
        Toast.makeText(this, "Command \'nmap " + cmdString + "\'  running in background...", Toast.LENGTH_SHORT).show();
        // Disable the buttons so a second command can't be completed at the same time
        setButtonsClickable(false);
    }

    @Override
    public void CommandCompletedCallback(boolean isError, String output) {
        if (isError)
            Log.e(TAG, output);
        else
            Log.d(TAG, output);

        _lblResults.setText(_lblResults.getText() + output + "\n");
        // Re-enable the buttons after the command has completed to allow the next command to execute
        setButtonsClickable(true);
        // Automatically scroll to the bottom of the output text
        _scrollResults.post(scrollToBottom);
    }

    private Runnable scrollToBottom = new Runnable() {
        @Override
        public void run() {
            _scrollResults.fullScroll(ScrollView.FOCUS_DOWN);
        }
    };

    private void setButtonsClickable(boolean clickable) {
        _btnScanNmap.setEnabled(clickable);
    }

    public void printOptionsArrayLog() {
        for (int i = 0; i < optionsArr.length; i++) {
            Log.wtf("Options", "" + optionsArr[i]);
        }
        Log.wtf("Options", " " );
    }

    public void btnScanNmap_OnClick(View view) {
        cmdString = generateCommandFromOptions();
        runCommandInCLIAsync(ActivityMain.executableFileLocations.get(Utils.Executable.NMAP) + " " + cmdString);
    }
}