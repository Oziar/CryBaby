package com.crybaby;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;
import android.support.v4.app.NavUtils;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class MonitorActivity extends Activity implements OnClickListener{
    private static final String TAG = "MonitorActivity";
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int RECORDER_SAMPLERATE = 8000;
    private int bufferSize = 0;
    private ToggleButton recordButton;
    private Thread dataReadThread = null;
    private AudioRecord recorder = null;
    Boolean isRecording = false;
    File recorded = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/monitor.pcm");
        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        
        recordButton = (ToggleButton) findViewById(R.id.recordButton);
        recordButton.setOnClickListener(this);
        
        bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                                                  RECORDER_CHANNELS,
                                                  RECORDER_ENCODING);
        Log.d(TAG, "bufferSize = " + bufferSize);
     }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
        case R.id.recordButton:
            Log.d(TAG, "recordButton OnClicked");
            if (recordButton.isChecked()) {
                Log.d(TAG, "recordButton is checked");
                startRecord();
            }
            else {
                Log.d(TAG, "recordButton is not checked");
                stopRecord();
            }
            break;
        }
    }
    
    private void startRecord() {
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_ENCODING,
                bufferSize);
        if (recorded.exists()) {
            Log.d(TAG, "A recording already exists, deleting");
            recorded.delete();
        }
        
        try {
            recorded.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        dataReadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeToFile();
            }
        }, "Data Writer Thread");

        recorder.startRecording();
        isRecording = true;       
        dataReadThread.start();
     }
    
    private void writeToFile() {
        DataOutputStream dataOut = null;
        try {
            try {
                dataOut = new DataOutputStream(
                                  new BufferedOutputStream(
                                          new FileOutputStream(recorded)));
                byte[] buffer = new byte[bufferSize];

                while (isRecording) {
                    int bufferReadResult = recorder.read(buffer, 0, bufferSize);
                    Log.d(TAG, "bufferReadResult = " + String.valueOf(bufferReadResult));
                    dataOut.write(buffer, 0, bufferReadResult);
                }
            } finally {
                Log.d(TAG, "Closing dataOut");
                dataOut.close();
            }
        } catch(IOException e) {
            Log.e(TAG, e.toString());
        }
    }
    
    private void stopRecord() {
        Log.d(TAG, "stopRecord");
        isRecording = false;
        recorder.stop();
        recorder.release();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_monitor, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
