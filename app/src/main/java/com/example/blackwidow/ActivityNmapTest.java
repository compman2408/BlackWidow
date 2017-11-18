package com.example.blackwidow;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ActivityNmapTest extends Activity {
    private static final String TAG = "ActivityNmapTest";
    private static final String LINE_BEGINNING = "{{!user}}@android:~$ ";
    private String USER = "";


    private TextView _lblCLIOutput;
    private EditText _txtNmapParameters;
    private ScrollView _scrollCLI;
    //private Button _btnRunNmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmap_test);

        // Get References to views
        _lblCLIOutput = (TextView) findViewById(R.id.lblCLIOutput);
        _txtNmapParameters = (EditText) findViewById(R.id.txtNmapParameters);
        _scrollCLI = (ScrollView) findViewById(R.id.scrollCLI);
        //_btnRunNmap = (Button) findViewById(R.id.btnRunNmap);

        // Setup the activity
        USER = executeCommand("whoami").trim();
        _lblCLIOutput.setText(getCLIBeginning());
    }

    public void btnRunNmap_OnClick(View view) {
        if (_txtNmapParameters.getText().toString().length() == 0) {
            runCommandInCLI(ActivityMain.binaryFilesToCopy.get(R.raw.nmap2));
        } else {
            runCommandInCLI(ActivityMain.binaryFilesToCopy.get(R.raw.nmap2) + " " + _txtNmapParameters.getText().toString());
        }
    }

    public void btnTest_OnClick(View view) {
        runCommandInCLI("/system/bin/ls -l " + ActivityMain.binaryFileLocation);
    }

    private String getCLIBeginning() {
        return LINE_BEGINNING.replace("{{!user}}", USER);
    }

    private void runCommandInCLI(String cmdToExecute) {
        String cmdOutput = executeCommand(cmdToExecute);
        _lblCLIOutput.setText(_lblCLIOutput.getText() + cmdToExecute + "\n\n" +  cmdOutput + "\n" + getCLIBeginning());
        _txtNmapParameters.setText("");
        _scrollCLI.scrollTo(0, _scrollCLI.getBottom());
    }

    private String executeCommand(String cmdToExecute) {
        StringBuffer output = new StringBuffer();
        try {
            // Executes the command.
            Process process = Runtime.getRuntime().exec(cmdToExecute);

            // Reads stdout.
            // NOTE: You can write to stdin of the command using
            //       process.getOutputStream().
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();

            // Waits for the command to finish.
            process.waitFor();

            //Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
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
        return output.toString();
    }
}
