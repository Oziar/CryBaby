package com.crybaby;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.util.Log;

public class MainActivity extends Activity implements OnClickListener {
    private static final String TAG = "MainActivity";
	Button receiverButton;
	Button monitorButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        receiverButton = (Button) findViewById(R.id.receiverButton);
        receiverButton.setOnClickListener(this);
        monitorButton = (Button) findViewById(R.id.monitorButton);
        monitorButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        Intent i;
        switch(v.getId()) {
        case R.id.receiverButton:
            Log.d(TAG, "receiverButton onClicked");
            i = new Intent(this, ReceiverActivity.class);
            startActivity(i);
            break;
        case R.id.monitorButton:
            Log.d(TAG, "monitorButton onClicked");
            i = new Intent(this, MonitorActivity.class);
            startActivity(i);
           	break;
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
}
