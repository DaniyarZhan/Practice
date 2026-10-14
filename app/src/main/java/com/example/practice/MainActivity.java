package com.example.practice;

import android.app.Activity;
import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.provider.MediaStore;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.io.IOException;

public class MainActivity extends Activity implements OnPreparedListener,
        OnCompletionListener {

    final String LOG_TAG = "myLogs";

    final String DATA_HTTP = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-16.mp3";
    final String DATA_STREAM = "https://icecast-vgtrk.cdnvideo.ru/mayakfm_mp3_192kbps";
    final String DATA_SD = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            + "/a-dessa_-_zhenshchiny_ya_ne_tantsuyu_(zaycev.net).mp3";
    final Uri DATA_URI = ContentUris
            .withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    1000030661);

    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    CheckBox checkBoxLoop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        checkBoxLoop = (CheckBox) findViewById(R.id.checkboxLoop);
        checkBoxLoop.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (mediaPlayer != null)
                    mediaPlayer.setLooping(isChecked);
            }
        });

        android.database.Cursor cursor = getContentResolver().query(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { android.provider.MediaStore.Audio.Media._ID, android.provider.MediaStore.Audio.Media.DISPLAY_NAME },
                null, null, null
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                android.util.Log.d("myLogs", "Песня: " + name + " | ID: " + id);
            }
            cursor.close();
        }

    }

    public void onClickStart(View view){
        releaseMP();

        try {
            int id = view.getId();

            if (id == R.id.buttonStartHttp) {
                Log.d(LOG_TAG, "start HTTP");
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(DATA_HTTP);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                Log.d(LOG_TAG, "prepareAsync");
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.prepareAsync();
            }
            else if (id == R.id.buttonStartStream) {
                Log.d(LOG_TAG, "start Stream");
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(DATA_STREAM);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                Log.d(LOG_TAG, "prepareAsync");
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.prepareAsync();
            }
            else if (id == R.id.buttonStartSD) {
                Log.d(LOG_TAG, "start SD");
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(DATA_SD);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
            else if (id == R.id.buttonStartUri) {
                Log.d(LOG_TAG, "start Uri");
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(this, DATA_URI);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
            else if (id == R.id.buttonStartRaw) {
                Log.d(LOG_TAG, "start Raw");
                mediaPlayer = MediaPlayer.create(this, R.raw.music);
                mediaPlayer.start();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        if (mediaPlayer == null)
            return;

        mediaPlayer.setLooping(checkBoxLoop.isChecked());
        mediaPlayer.setOnCompletionListener(this);
    }

    private void releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void onClick(View view) {
        if (mediaPlayer == null)
            return;
        int id = view.getId();

        if (id == R.id.buttonPause) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
        else if (id == R.id.buttonResume) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }
        else if (id == R.id.buttonStop) {
            mediaPlayer.stop();
        }
        else if (id == R.id.buttonBackward) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
        }
        else if (id == R.id.buttonForward) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
        }
        else if (id == R.id.buttonInfo) {
            Log.d(LOG_TAG, "Playing " + mediaPlayer.isPlaying());
            Log.d(LOG_TAG, "Time " + mediaPlayer.getCurrentPosition() +
                    " / " + mediaPlayer.getDuration());
            Log.d(LOG_TAG, "Looping " + mediaPlayer.isLooping());
            Log.d(LOG_TAG, "Volume " +
                    audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d(LOG_TAG, "onPrepared");
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d(LOG_TAG, "onCompletion");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMP();
    }
}