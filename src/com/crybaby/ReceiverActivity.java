package com.crybaby;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.support.v4.app.NavUtils;

public class ReceiverActivity extends Activity implements OnClickListener{
    private static final String TAG = "ReceiverActivity";
    private static final int PLAYER_CHANNELS = AudioFormat.CHANNEL_OUT_MONO;
    private static final int PLAYER_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int PLAYER_SAMPLERATE = 8000;
    private int bufferSize = 0;
    private Button playButton;
    File recorded = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/monitor.pcm");
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        
        playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(this);
        
        bufferSize = AudioTrack.getMinBufferSize(PLAYER_SAMPLERATE,
                                                 PLAYER_CHANNELS,
                                                 PLAYER_ENCODING);
        Log.d(TAG, "bufferSize = " + bufferSize);
    }

    @Override
    public void onClick(View v) {
        DataInputStream dataIn = null;
        byte[] audioData = new byte[bufferSize];
        Log.d(TAG, "playButton onClicked");
        if (recorded.exists()) {
            Log.d(TAG, "File exists, creating AudioTrack");
            AudioTrack player = new AudioTrack(AudioManager.STREAM_MUSIC,
                                               PLAYER_SAMPLERATE,
                                               PLAYER_CHANNELS,
                                               PLAYER_ENCODING,
                                               bufferSize,
                                               AudioTrack.MODE_STREAM);
            try {
                Log.d(TAG, "Creating DataInputStream");
                dataIn = new DataInputStream(
                                 new BufferedInputStream(
                                         new FileInputStream(recorded)));
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.toString());
                return;
            }
            
            player.play();
            try {
                int dataReadResult = 0;
                while((dataReadResult = dataIn.read(audioData, 0, bufferSize)) != -1) {
                    Log.d(TAG, "dataReadResult = " + dataReadResult);
                    player.write(audioData, 0, dataReadResult);
                }
                Log.d(TAG, "Reached end of file, closing dataIn");
                dataIn.close();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return;
            }
            player.release();
        }
        else {
            Log.d(TAG, "File does not exist");
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_receiver, menu);
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
