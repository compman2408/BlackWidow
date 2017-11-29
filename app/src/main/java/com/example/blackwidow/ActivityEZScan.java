package com.example.blackwidow;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static com.example.blackwidow.PhoneDB.*;



/**
 * Created by Bobak on 11/19/2017.
 */

public class ActivityEZScan extends Activity implements IAsyncCommandCallback, IShodanPostCallback {
    private static final String TAG = "ActivityEZScan";
    private static final String[] SCANTYPES = new String[]{"REGULAR SCAN", "TCP SYN SCAN", "TCP NULL SCAN"};
    private static final String[] SCANTYPEARGS = new String[]{"", "-sS", "-sN"};
    private static final String[] OPTIONTYPES = new String[]{"UDP SCAN", "OS DETECTION", "FAST", "OPEN PORT SCAN", "VERBOSE", "TIMING TEMPLATE", "TRACEROUTE"};
    private static final String[] OPTIONARGS = new String[]{"-sU", "-O", "-F", "-sV", "-v", "-T4", "-traceroute"};
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
    String latestResults;
    public String scanName;
    public String osQuery;
    public boolean osFingerPrinted = false;
    public Context activityContext = this;
    public DataHelper data;
    private ProgressDialog dlgLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ez_scan);

        // Set up the progress dialog to show the user that something is happening
        dlgLoading = new ProgressDialog(this);
        dlgLoading.setTitle("Please Wait...");
        dlgLoading.setCancelable(false);

        // initialize db
        data = new DataHelper(this);

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
                Log.d("CLICK", "GROUP CLICK " + groupPosition);
                return false;
            }
        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            ListItem _lastScanTypeSelected = (ListItem) listAdapter.getChild(SCANTYPEINDEX, 0); //initial scan type is regular (no args)

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
                } else if (groupPosition == OPTIONTYPEINDEX) { //for OPTIONS
                    optionsArr[childPosition] = !optionsArr[childPosition]; //update the option selection boolean here
                    ((ListItem) listAdapter.getChild(groupPosition, childPosition)).toggleSelected(); //update the option selection boolean in the data bound to the view
                }
                listAdapter.notifyDataSetChanged(); //refresh view
                return false;
            }
        });

        _btnSaveResults.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnSaveResults_OnClick(activityContext);
            }
        });
    }

    private void initData() {
        latestResults = "";
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
                Log.d("COMMAND", cmdString);
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

        latestResults = output;
        Log.wtf(TAG, "latestResults " + latestResults);
        Log.wtf(TAG, output);
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
        _btnSaveResults.setEnabled(clickable);
    }

    public void printOptionsArrayLog() {
        for (int i = 0; i < optionsArr.length; i++) {
            Log.d("Options", "" + optionsArr[i]);
        }
        Log.d("Options", " ");
    }

    public void btnScanNmap_OnClick(View view) {
        cmdString = generateCommandFromOptions();
        runCommandInCLIAsync(ActivityMain.executableFileLocations.get(Utils.Executable.NMAP) + " " + cmdString);
    }

    public void btnSaveResults_OnClick(Context context) {
        // pops up dialog to name scan
        saveScanName(context);
    }

    public String getNullOrString(String s) {
        if (s.length() < 1) {
            return null;
        } else {
            return s;
        }
    }

    /* Need to parse output and insert items into db */
    public void saveResults() {
        long scanID = InsertScanIntoDB(activityContext, scanName);

        // TODO use parser to fill the null values in. this will probably be done in a loop for all scanned hosts
        String os = null;
        String ip;
        String hostName;
        String openPorts;
        Log.wtf(TAG, "latestResults saveResults");
        Log.wtf(TAG, latestResults);
        LinkedList<NmapReturn> results = parse(latestResults.split("\\r?\\n"));
        /* while (output has next line)
            if (line)...
             host =
             ip =
             os =
             open_ports =
             // if OS is parsed from device --> run query for shodan exploits
          long hostID = InsertHostIntoDB(this,host,ip.....,null,null,scanID);
         */

        for (NmapReturn host : results) {
            Log.wtf(TAG, "MAP TO STRING OF PORTS");
            Log.wtf(TAG, host.ip);
            Log.wtf(TAG, host.optHost);
            Log.wtf(TAG, host.bestOSGuess);
            Log.wtf(TAG, host.unfiltered.toString());
            long hostID = InsertHostIntoDB(this, getNullOrString(host.optHost), getNullOrString(host.ip), getNullOrString(host.bestOSGuess),getNullOrString(host.unfiltered.toString()) , scanID);
            if (host.bestOSGuess.length() > 0) {
                getExploitsForOS(activityContext, host.bestOSGuess, hostID);
            }
            if (osFingerPrinted) {
              //  getExploitsForOS(activityContext, os, hostID);
            }
        }


        Log.d(TAG, "Scan Results Saved");
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
                saveResults();
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

    public static LinkedList<NmapReturn> parse(String[] input) {

        LinkedList<NmapReturn> results = new LinkedList<NmapReturn>();
        NmapReturn tmp = new NmapReturn();


        Log.wtf(TAG, "START OF PARSE");
        Log.wtf(TAG, Integer.toString(input.length));
        Log.wtf(TAG, Arrays.toString(input));
        String previousLine = "";
        int i = 0;
        while (i < input.length) {
            String raw = input[i];
            Log.wtf(TAG, raw);
            String[] line = raw.split(" +", 0);
            if (line.length > 0) {
                Log.wtf(TAG, "line is longer than 0");
                switch (line[0]) {
                    case "Nmap":
                        if (line[1].equals("scan")) {
                            Log.wtf(TAG, "ADDED TMP TO RESULTS IN NMAP SWITCH-CASE");
                            results.add(tmp); //MAYBE REMOVE
                            tmp = new NmapReturn();
                            if (line.length == 5) {
                                tmp.ip = line[4];
                            } else if (line.length == 6) {
                                tmp.ip = line[5].substring(1,
                                        line[5].length() - 1);
                                tmp.optHost = line[4];
                            }

                        } else if (!line[1].equals("done")) {
                            // are there other possibilities
                        }
                        break;
                    //case "Host":
                    // I only ever received "Host is up (XXXXXs latency)."
                    //break;
                    case "Device":
                        // a lot of "Device type: general purpose"
                        tmp.deviceType = raw.substring(13);
                        break;
                    case "Aggressive":
                        tmp.bestOSGuess = raw.substring(23);
                        break;

                    /*
                     * any other important info can be parsed in here
                     */

                    default:
                        if (line[0].length() == 0) {
                            break;
                        }
                        Log.wtf(TAG,"DEFAULT");
                        Log.wtf(TAG, "LINE[0] = " + line[0]);
                        if (line.length > 0 && Character.isDigit(line[0].charAt(0))) {
                            Log.wtf(TAG, "ADDED PORT: " + line[0].substring(0, line[0].length() - 4) + " " + line[2]);
                            tmp.unfiltered.put(Integer.parseInt(
                                    line[0].substring(0, line[0].length() - 4)),
                                    line[2]);
                        }
                        break;

                }
            } else if (tmp.ip.length() > 0) {
                Log.wtf(TAG, "ADDED TMP TO RESULTS IN ELSEIF");
                results.add(tmp);
            }
            previousLine = raw;
            i++;
        }
        Log.wtf(TAG, Integer.toString(results.toArray().length));
        return results;
    }


    public void getExploitsForOS(Context context, String os, long hostID) {
        Log.i(TAG, "Fetch exploits for OS from Shodan API...");
        dlgLoading.setMessage("Getting exploits for OS: " + os);
        dlgLoading.show();

        StringBuilder postData = new StringBuilder();

        try {

            postData.append(URLEncoder.encode("query", "UTF-8"));
            postData.append("=" + URLEncoder.encode(os, "UTF-8"));

            postData.append("&" + URLEncoder.encode("key", "UTF-8"));//
            postData.append("=" + URLEncoder.encode("ofqxN4bGWWDA5GwDEPZNMoEiddgBBZ9B", "UTF-8"));

            new GetExploitsPostTask().execute(new ShodanPostRequest(this, postData.toString(), hostID, this));
        } catch (Exception ex) {
            Log.i(TAG, "Error starting post: " + ex.getMessage());
        }
    }

    @Override
    public void ShodanPostCallback(String jsonResponse, long hostID) {
        // Dismiss the dialog box telling the user to wait
        if (dlgLoading.isShowing())
            dlgLoading.dismiss();

        // Do something with the response

        if (!jsonResponse.startsWith("ERROR")) {
            try {
                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONArray jsonArray = jsonObject.getJSONArray("matches");

                for (int i=0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject oneObject = jsonArray.getJSONObject(i);
                        // Pulling items from the array
                        String source = oneObject.getString("source");
                        String id = oneObject.getString("_id");
                        String description = oneObject.getString("description");
                        String name = source + id;

                        InsertExploitIntoDB(activityContext, name, description, hostID);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // There was an error. Just skip processing response.
            Log.i(TAG, "Error was returned. Processing is being skipped.");
        }

        Toast.makeText(this, "Response --> " + jsonResponse, Toast.LENGTH_SHORT).show();
    }
}
