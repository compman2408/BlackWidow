package com.example.blackwidow;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ActivityNmapTest extends Activity {
    private static final String TAG = "ActivityNmapTest";
    private static final String LINE_BEGINNING = "user@android:~$ ";

    private TextView _lblDisplay;
    private EditText _txtNmapParameters;
    //private Button _btnRunNmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmap_test);

        _lblDisplay = (TextView) findViewById(R.id.lblDisplay);
        _txtNmapParameters = (EditText) findViewById(R.id.txtNmapParameters);
        //_btnRunNmap = (Button) findViewById(R.id.btnRunNmap);

        _lblDisplay.setText(LINE_BEGINNING);
    }

    public void btnRunNmap_OnClick(View view) {
        String filesDir = getApplicationContext().getFilesDir().getPath();
//        InputStream test = this.getResources().openRawResource(R.raw.nmap);
        File file = new File(filesDir + "/res/raw");
        _lblDisplay.setText("Current File: " + file.toString() + "\nExists: " + String.valueOf(file.exists()));
//        _lblDisplay.setText(_lblDisplay.getText() + "nmap " + _txtNmapParameters.getText() + "\n" + LINE_BEGINNING);
//        _txtNmapParameters.setText("");
//        try {
//            // Executes the command.
//            Process process = Runtime.getRuntime().exec("/system/bin/ls " + filesDir);
//
//            // Reads stdout.
//            // NOTE: You can write to stdin of the command using
//            //       process.getOutputStream().
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(process.getInputStream()));
//            int read;
//            char[] buffer = new char[4096];
//            StringBuffer output = new StringBuffer();
//            while ((read = reader.read(buffer)) > 0) {
//                output.append(buffer, 0, read);
//            }
//            reader.close();
//
//            // Waits for the command to finish.
//            process.waitFor();
//
//            _lblDisplay.setText(output.toString());
//        } catch (IOException e) {
//            Toast.makeText(this, "IOException ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        } catch (InterruptedException e) {
//            Toast.makeText(this, "InterruptedException ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            Toast.makeText(this, "Exception ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
    }
}
