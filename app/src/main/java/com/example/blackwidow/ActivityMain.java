package com.example.blackwidow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

public class ActivityMain extends Activity {

    Button btnScanNetwork;
    AnimationDrawable btnAnimation;

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        // Start the scan button animation
        btnScanNetwork = (Button) findViewById(R.id.btnScanNetwork);
        btnAnimation = (AnimationDrawable) btnScanNetwork.getBackground();

//        btnAnimation = new AnimationScan(this, R.drawable.btn_scan_frame0);
//        btnScanNetwork.setBackground(btnAnimation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!btnAnimation.isRunning())
            btnAnimation.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (btnAnimation.isRunning())
            btnAnimation.stop();
    }

    public void btnScanNetwork_OnClick(View view) {
        startActivity(new Intent(this, ActivityScan.class));
    }
}
