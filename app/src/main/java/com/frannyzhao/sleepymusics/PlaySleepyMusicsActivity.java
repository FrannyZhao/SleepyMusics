package com.frannyzhao.sleepymusics;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class PlaySleepyMusicsActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    private static final String TAG = "PlaySleepyMusics";
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 0;

    private static String musicDir = "/storage/emulated/0/night/";

    private int playingMusicIndex;

    MediaPlayer mp = new MediaPlayer();
    File[] musicFiles;

    RelativeLayout activityLayout;
    ImageView playBtn;
    TextView noPermissionTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_sleepy_musics);
        activityLayout = (RelativeLayout) findViewById(R.id.activity_play_sleepy_musics);
        playBtn = (ImageView) findViewById(R.id.img_play_button);
        noPermissionTxt = (TextView) findViewById(R.id.txt_no_permission);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请READ_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            playBtn.setVisibility(View.VISIBLE);
            noPermissionTxt.setVisibility(View.INVISIBLE);
        }

        mp.setOnCompletionListener(this);

        activityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PlaySleepyMusicsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请READ_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(PlaySleepyMusicsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EXTERNAL_STORAGE_REQUEST_CODE);
                }
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    stopPlayMusic();
                } else {
                    startPlayMusic();
                }
                changePlayBtn();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                    playBtn.setVisibility(View.VISIBLE);
                    noPermissionTxt.setVisibility(View.INVISIBLE);
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    playBtn.setVisibility(View.INVISIBLE);
                    noPermissionTxt.setVisibility(View.VISIBLE);
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private File[] listFiles(String rootDir) {
        File[] files = new File(rootDir).listFiles();
        Log.d(TAG, rootDir + "files = null?" + (files == null));
        if (files != null) {
            Arrays.sort(files);
            Log.d(TAG, "total " + files.length + " files");
            /*
            for (File file : files) {
                Log.d(TAG, file.getName());
            } */
        }
        return files;
    }

    private void changePlayBtn() {
        if (mp != null && mp.isPlaying()) {
            playBtn.setImageResource(R.drawable.stop_button);
        } else {
            playBtn.setImageResource(R.drawable.play_button);
        }
    }

    private void playMusic(String path) {
        try {
            Log.d(TAG, "playing file： " + path);
            mp.reset();
            mp.setDataSource(path);
            mp.prepare();
            mp.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startPlayMusic() {
        Log.d(TAG, "startPlayMusic");
        musicFiles = listFiles(musicDir);
        if (musicFiles == null) {
            return;
        } else {
            playingMusicIndex = 0;
            playMusic(musicFiles[playingMusicIndex].getPath());
        }
    }

    private void stopPlayMusic() {
        Log.d(TAG, "stopPlayMusic");
        mp.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (playingMusicIndex + 1 == musicFiles.length) { // 整个列表都播放完了
            stopPlayMusic();
            changePlayBtn();
        } else {
            playingMusicIndex = (playingMusicIndex + 1) % musicFiles.length;
            playMusic(musicFiles[playingMusicIndex].getPath());
        }
    }
}
