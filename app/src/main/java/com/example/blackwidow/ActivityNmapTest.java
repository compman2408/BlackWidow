package com.example.blackwidow;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ActivityNmapTest extends Activity {
    private static final String TAG = "ActivityNmapTest";
    private static final String LINE_BEGINNING = "user@android:~$ ";

    private Map<Integer, String> _binaryFilesToCopy;
    private String _appDataDirectory;
    private String _binaryFileLocation;

    private TextView _lblDisplay;
    private EditText _txtNmapParameters;
    //private Button _btnRunNmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmap_test);
        // Set the directories to the important stuff
        _appDataDirectory = getApplicationContext().getFilesDir().getPath();
        _binaryFileLocation = _appDataDirectory + "/bin/nmap";

        // Get References to views
        _lblDisplay = (TextView) findViewById(R.id.lblDisplay);
        _lblDisplay.setText(LINE_BEGINNING);
        _txtNmapParameters = (EditText) findViewById(R.id.txtNmapParameters);
        //_btnRunNmap = (Button) findViewById(R.id.btnRunNmap);

        // Add files to copy
        _binaryFilesToCopy = new HashMap<Integer, String>();
        _binaryFilesToCopy.put(R.raw.ncat, _binaryFileLocation + "/ncat");
        _binaryFilesToCopy.put(R.raw.ndiff, _binaryFileLocation + "/ndiff");
        _binaryFilesToCopy.put(R.raw.nmap2, _binaryFileLocation + "/nmap");
        _binaryFilesToCopy.put(R.raw.nping, _binaryFileLocation + "/nping");
        _binaryFilesToCopy.put(R.raw.uninstall_ndiff, _binaryFileLocation + "/uninstall_ndiff");


        // If the binary files don't currently exist copy them out of the raw resources onto the
        // internal storage of the device since the external storage won't allow us to run the
        // executable on the Android system
        Log.d(TAG, "Checking binary directory existance...");
        File file = new File(_binaryFileLocation);
        if (!file.exists()) {
            Log.d(TAG, "Binary directory doesn't exist. Creating directories...");
            // Create binary directory
            file.mkdirs();
            Log.d(TAG, "Directories created. Copying files...");
        } else {
            Log.d(TAG, "Binary directory already exists. Moving on to files.");
        }
        Log.d(TAG, "Checking binary files existance...");
        int count = 0;
        for (Map.Entry<Integer, String> item : _binaryFilesToCopy.entrySet()) {
            count++;
            file = new File(item.getValue());
            if (!file.exists()) {
                Log.d(TAG, "Binary " + count + " out of " + _binaryFilesToCopy.size() + " doesn't exist.");
                try {
                    Log.d(TAG, "Copying binary " + count + " out of " + _binaryFilesToCopy.size());
                    copyBinFilesToInternalStorage(item.getKey(), item.getValue());
                    Log.d(TAG, "File copy successful!");
                } catch (IOException ex) {
                    Log.d(TAG, "Error copying file: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                Log.d(TAG, "Binary " + count + " out of " + _binaryFilesToCopy.size() + " already exists. Skipping file copy...");
            }
        }
    }

    private void copyBinFilesToInternalStorage(int id, String path) throws IOException {
        InputStream in = this.getResources().openRawResource(R.raw.nmap2);
        FileOutputStream out = new FileOutputStream(path);
        byte[] buff = new byte[1024];
        int read = 0;
        try {
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } finally {
            in.close();
            out.close();
        }
    }

    public void btnRunNmap_OnClick(View view) {
        try {
            // Executes the command.
            //Process process = Runtime.getRuntime().exec("/system/bin/ls -l " + _binaryFileLocation);
            Process process;
            if (_txtNmapParameters.getText().toString().length() == 0) {
                process = Runtime.getRuntime().exec(_binaryFilesToCopy.get(R.raw.nmap2));
            } else {
                process = Runtime.getRuntime().exec(_binaryFilesToCopy.get(R.raw.nmap2) + " " + _txtNmapParameters.getText().toString());
            }

            // Reads stdout.
            // NOTE: You can write to stdin of the command using
            //       process.getOutputStream().
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();

            // Waits for the command to finish.
            process.waitFor();

            _lblDisplay.setText(output.toString());
            Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "IOException ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (InterruptedException e) {
            Toast.makeText(this, "InterruptedException ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Exception ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
