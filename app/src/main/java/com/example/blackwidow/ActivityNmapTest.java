package com.example.blackwidow;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
        _lblDisplay.setText(_lblDisplay.getText() + "nmap " + _txtNmapParameters.getText() + "\n" + LINE_BEGINNING);
        _txtNmapParameters.setText("");
    }
}
