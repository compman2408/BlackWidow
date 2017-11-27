package com.example.blackwidow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
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

import static com.example.blackwidow.PhoneDB.*;

/**
 * Created by Bobak on 11/19/2017.
 */

public class ActivityEZScan extends Activity implements IAsyncCommandCallback {
    private static final String TAG = "ActivityEZScan";
    private static final String[] SCANTYPES = new String[]{"REGULAR SCAN", "TCP SYN SCAN", "TCP NULL SCAN"};
    private static final String[] SCANTYPEARGS = new String[]{"", "-sS", "-sN"};
    private static final String[] OPTIONTYPES = new String[]{"UDP SCAN", "OS DETECTION", "FAST", "OPEN PORT SCAN", "VERBOSE", "TIMING TEMPLATE", "TRACEROUTE"};
    private static final String[] OPTIONARGS = new String[]{"-sU", "-A", "-F", "-sV", "-v", "-T4", "-traceroute"};
    private static final int SCANTYPEINDEX = 0;
    private static final int OPTIONTYPEINDEX = 1;

    EditText _txtIPAddress;
    Button _btnScanNmap;
    Button _btnSaveResults;
    ScrollView _scrollResults;
    TextView _lblResults;
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<ListItem>> listHash;
    public static HashMap<String, String> argMap;
    boolean[] optionsArr;
    String scanType;
    String cmdString;
    public String scanName;
    public boolean osFingerPrinted = false;

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
        listView.expandGroup(SCANTYPEINDEX);
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Log.wtf("CLICK", "GROUP CLICK " + groupPosition);
                return false;
            }
        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            ListItem _lastScanTypeSelected = (ListItem) listAdapter.getChild(SCANTYPEINDEX,0); //initial scan type is regular (no args)
             @Override
             public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                 Log.wtf("CLICK", "CHILD CLICK " + groupPosition + " " + childPosition);
                 if (groupPosition == SCANTYPEINDEX) { //for SCAN TYPES
                     scanType = SCANTYPES[childPosition]; //update the type of scan to perform
                     if (_lastScanTypeSelected != null) {
                         _lastScanTypeSelected.toggleSelected();
                     }
                     ((ListItem) listAdapter.getChild(groupPosition, childPosition)).setSelected(true);
                     _lastScanTypeSelected = (ListItem) listAdapter.getChild(groupPosition, childPosition);
                 } else if  (groupPosition == OPTIONTYPEINDEX) { //for OPTIONS
                     optionsArr[childPosition] = !optionsArr[childPosition]; //update the option selection boolean here
                     ((ListItem) listAdapter.getChild(groupPosition, childPosition)).toggleSelected(); //update the option selection boolean in the data bound to the view
                 }
                 listAdapter.notifyDataSetChanged(); //refresh view
                 return false;
             }
        });
    }

    private void initData() {
        optionsArr = new boolean[7]; //initialize options to false
        scanType = SCANTYPES[0]; //Set scan type to Regular scan

        argMap = new HashMap<>();

        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("SCAN TYPE");
        listDataHeader.add("OPTIONS");

        List<ListItem> lstScanTypes = new ArrayList<>();
        for (int i = 0; i < SCANTYPES.length; i++) {
            if (i == 0) {
                lstScanTypes.add(new ListItem(SCANTYPES[i], true));
            } else {
                lstScanTypes.add(new ListItem(SCANTYPES[i], false));
            }
            argMap.put(SCANTYPES[i], SCANTYPEARGS[i]);
        }

        List<ListItem> lstScanOptions = new ArrayList<>();
        for (int i = 0; i < OPTIONTYPES.length; i++) {
            lstScanOptions.add(new ListItem(OPTIONTYPES[i], false));
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

    public void btnSaveResults_OnClick(View view) {
        //TODO: Parse Nmap output and feed into DB classes

        // pops up dialog to name scan
        saveScanName(this);

        String scanID = InsertScanIntoDB(this,scanName);
        // TODO use parser to fill the null values in. this will probably be done in a loop for all scanned hosts
        String hostID = InsertHostIntoDB(this,null,null,null,null,scanID);
        if (osFingerPrinted) {
            InsertExploitIntoDB(this, null, null, hostID);
        }


        Log.d(TAG,"Scan Results Saved");
    }

    // Names scan
    public void saveScanName(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Save Results -- choose name");

        final EditText input = new EditText(context);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scanName = input.getText().toString();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
