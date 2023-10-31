package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.musicapp.Service.MyMusicService;
import com.example.musicapp.data.GlobalConstance;
import com.example.musicapp.data.Song;
import com.example.musicapp.util.TimeUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayActivity extends AppCompatActivity {
    private ImageView ivPlayOrPause, ivPre, ivNext;
    private TextView tvTitle, tvCurTime, tvTotalTime;
    private SeekBar mSeekBar;
    private ArrayList<Song> mSongArrayList;
    private int curSongIndex;
    private Song mCurSong;
    private MyMusicService.MyMusicBind mMusicBind;
    private boolean isSeekBarDragging;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //服务已经建立，传递信息
            mMusicBind = (MyMusicService.MyMusicBind) iBinder;
            mMusicBind.updateMusicList(mSongArrayList);
            mMusicBind.updateCurrentMusicIndex(curSongIndex);
            updateUI();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };
    private void updateUI(){
        //当前时间更新
        int curProgress = mMusicBind.getCurProgress();
        tvCurTime.setText(TimeUtil.millToTimeFormat(curProgress));

        //总时间更新
        int duration = mMusicBind.getDuration();
        tvTotalTime.setText(TimeUtil.millToTimeFormat(duration));

        //更新进度条
        mSeekBar.setMax(duration);
        mSeekBar.setProgress(curProgress);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int curProgress = mMusicBind.getCurProgress();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!isSeekBarDragging){
                            mSeekBar.setProgress(curProgress);
                        }
                    }
                });
            }
        }, 0, 200);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        initView();
        Intent intent = getIntent();
        curSongIndex = intent.getIntExtra(GlobalConstance.KEY_SONG_INDEX, 0);
        mSongArrayList = (ArrayList<Song>) intent.getSerializableExtra(GlobalConstance.KEY_SONG_LIST);
        mCurSong = mSongArrayList.get(curSongIndex);
        updateTitle();
        startMusicService();
    }
    private void updateTitle() {
        tvTitle.setText(mCurSong.getSongName());
    }
    private void updateCurTimeText(int progress){
        //当前时间更新
        tvCurTime.setText(TimeUtil.millToTimeFormat(progress));
    }
    private void initView(){
        ivPlayOrPause = findViewById(R.id.iv_play_pause);
        ivPre = findViewById(R.id.iv_previous);
        ivNext = findViewById(R.id.iv_next);
        tvTitle = findViewById(R.id.tv_music_title);
        tvCurTime =findViewById(R.id.tv_cur_time);
        tvTotalTime = findViewById(R.id.tv_total_time);
        mSeekBar = findViewById(R.id.seek_bar_music);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                updateCurTimeText(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarDragging = true;
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarDragging = false;
                int progress = seekBar.getProgress();
                mMusicBind.seekTo(progress);
            }
        });
    }
    private void startMusicService() {
        //通过bind形式启动Service
        Intent intent = new Intent(this, MyMusicService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }
    public void playOrPause(View view) {
        if(mMusicBind.isPlaying()){
            //暂停音乐
            mMusicBind.pause();
            //修改图标为即将播放状态
            ivPlayOrPause.setImageResource(android.R.drawable.ic_media_play);
        } else {
            //播放音乐
            mMusicBind.play();
            //修改图标为即将暂停状态
            ivPlayOrPause.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    public void preMusic(View view) {
        mMusicBind.previous();
        mCurSong = mMusicBind.getCurSong();
        updateTitle();
    }
    public void nextMusic(View view) {
        mMusicBind.next();
        mCurSong = mMusicBind.getCurSong();
        updateTitle();
    }
    public void stopMusic(View view) {
        mMusicBind.stop();
        //修改图标为即将播放状态
        ivPlayOrPause.setImageResource(android.R.drawable.ic_media_play);
    }
}