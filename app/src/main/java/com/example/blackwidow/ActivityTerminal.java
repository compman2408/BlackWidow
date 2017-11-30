package com.example.blackwidow;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Contributed to by Ryan, Alex, Bobak
 * A terminal-style activity that allows the user to execute nmap by manually typing in arguments (-v, -O, etc.) and IP addresses
 */

public class ActivityTerminal extends Activity implements IAsyncCommandCallback {
    private static final String TAG = "ActivityTerminal";
    private static final String LINE_BEGINNING = "{{!user}}@android:~$ ";
    private String USER = "";


    private TextView _lblCLIOutput;
    private EditText _txtNmapParameters;
    private Button _btnRunCommand;
    private Button _btnTestCommand;
    private ScrollView _scrollCLI;
    //private Button _btnRunNmap;

    private Runnable scrollToBottom = new Runnable() {
        @Override
        public void run() {
            _scrollCLI.fullScroll(ScrollView.FOCUS_DOWN);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        // Get References to views
        _lblCLIOutput = (TextView) findViewById(R.id.lblCLIOutput);
        _txtNmapParameters = (EditText) findViewById(R.id.txtNmapParameters);
        _scrollCLI = (ScrollView) findViewById(R.id.scrollCLI);
        _btnRunCommand = (Button) findViewById(R.id.btnRunNmap);
        _btnTestCommand = (Button) findViewById(R.id.btnTest);
        //_btnRunNmap = (Button) findViewById(R.id.btnRunNmap);

        // Setup the activity
        //USER = executeCommand("whoami").trim();
        USER = "root";
        _lblCLIOutput.setText(getCLIBeginning());
    }

    public void btnRunNmap_OnClick(View view) {
        if (_txtNmapParameters.getText().toString().length() == 0) {
            Log.v("COMMAND", "SOMETHING");
            //runCommandInCLI(ActivityMain.executableFileLocations.get(Utils.Executable.NMAP));
            runCommandInCLIAsync(ActivityMain.executableFileLocations.get(Utils.Executable.NMAP));
        } else {
            //runCommandInCLI(ActivityMain.executableFileLocations.get(Utils.Executable.NMAP) + " " + _txtNmapParameters.getText().toString());
            runCommandInCLIAsync(ActivityMain.executableFileLocations.get(Utils.Executable.NMAP) + " " + _txtNmapParameters.getText().toString());
        }
    }

    public void btnTest_OnClick(View view) {
        //runCommandInCLI("/system/bin/ls -l " + ActivityMain.binaryFileLocation);
        runCommandInCLIAsync("/system/bin/ls -l " + ActivityMain.binaryFileLocation);
    }

    private String getCLIBeginning() {
        return LINE_BEGINNING.replace("{{!user}}", USER);
    }

    private void runCommandInCLIAsync(String cmdToExecute) {
        //String cmdOutput = executeCommand(cmdToExecute);
        // Execute the command
        Utils.ExecuteCommandAsync(this, cmdToExecute, this);
        _lblCLIOutput.setText(_lblCLIOutput.getText() + cmdToExecute + "\n\n");
        _txtNmapParameters.setText("");
        Toast.makeText(this, "Command running in background...", Toast.LENGTH_SHORT).show();
        // Disable the buttons so a second command can't be completed at the same time
        setButtonsClickable(false);
    }

    private void runCommandInCLI(String cmdToExecute) {
        String cmdOutput = executeCommand(cmdToExecute);
        _lblCLIOutput.setText(_lblCLIOutput.getText() + cmdToExecute + "\n\n" +  cmdOutput + "\n" + getCLIBeginning());
        _txtNmapParameters.setText("");
        _scrollCLI.scrollTo(0, _scrollCLI.getBottom());
    }

    private String executeCommand(String cmdToExecute) {
        StringBuffer output = new StringBuffer();
        Log.d("COMMAND", cmdToExecute);
        Process scanProcess;
        try {
            ProcessBuilder pBuilder = new ProcessBuilder("su");
            pBuilder.redirectErrorStream(true);
            scanProcess = pBuilder.start();
            // Executes the command.
        //    Process process = Runtime.getRuntime().exec(cmdToExecute);
     //       Process process = Runtime.getRuntime().exec(cmdToExecute,null, null);
            DataOutputStream dos = new DataOutputStream(scanProcess.getOutputStream());
            dos.writeBytes(cmdToExecute + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            dos.close();//*/
            // Reads stdout.
            // NOTE: You can write to stdin of the command using
            //       process.getOutputStream().
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(scanProcess.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();

            // Waits for the command to finish.
            scanProcess.waitFor();

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
        Log.d("OUTPUT", output.toString());
        return output.toString();
    }

    @Override
    public void CommandCompletedCallback(boolean isError, String output) {
        if (isError)
            Log.e(TAG, output);
        else
            Log.d(TAG, output);

        _lblCLIOutput.setText(_lblCLIOutput.getText() + output + "\n" + getCLIBeginning());
        // Re-enable the buttons after the command has completed to allow the next command to execute
        setButtonsClickable(true);
        // Automatically scroll to the bottom of the output text
        _scrollCLI.post(scrollToBottom);
    }

    private void setButtonsClickable(boolean clickable) {
        _btnRunCommand.setEnabled(clickable);
        _btnTestCommand.setEnabled(clickable);
    }
}
