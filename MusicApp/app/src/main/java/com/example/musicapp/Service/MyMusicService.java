package com.example.musicapp.Service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.musicapp.data.Song;

import java.io.IOException;
import java.util.ArrayList;

public class MyMusicService extends Service {

    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> mSongArrayList;
    private int curSongIndex;

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mSongArrayList = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void updateMusicList(ArrayList<Song> songArrayList) {
        this.mSongArrayList = songArrayList;
    }

    public void updateCurrentMusicIndex(int index){
        if(index < 0 || index >= mSongArrayList.size()){
            return;
        }
        this.curSongIndex = index;
        //播放该条歌曲
        Song song = mSongArrayList.get(curSongIndex);
        String songName = song.getSongName();

        AssetManager assetManager = getAssets();
        try {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            AssetFileDescriptor fileDescriptor = assetManager.openFd(songName);
            mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyMusicBind(this);
    }

    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }

    public void pause(){
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }
    }

    public void play(){
        if(mMediaPlayer.isPlaying()){
            return;
        }
        mMediaPlayer.start();
    }

    public void previous(){
        int preIndex = curSongIndex - 1;
        if(preIndex < 0){
            preIndex = mSongArrayList.size() - 1;
        }
        updateCurrentMusicIndex(preIndex);
    }

    public void next(){
        int nextIndex = curSongIndex + 1;
        if(nextIndex > mSongArrayList.size() - 1){
            nextIndex = 0;
        }
        updateCurrentMusicIndex(nextIndex);
    }

    public void stop(){
        mMediaPlayer.stop();
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Song getCurSong(){
        if(curSongIndex < 0 || curSongIndex >= mSongArrayList.size()){
            return null;
        }
        return mSongArrayList.get(curSongIndex);
    }

    public int getCurProgress(){
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDuration(){
        return mMediaPlayer.getDuration();
    }

    private void seekTo(int progress) {
        mMediaPlayer.seekTo(progress);
    }


    public class MyMusicBind extends Binder{

        private MyMusicService mMusicService;

        public MyMusicBind(MyMusicService musicService) {
            mMusicService = musicService;
        }


        public void startPlay(){

        }

        public void updateMusicList(ArrayList<Song> mSongArrayList){
            mMusicService.updateMusicList(mSongArrayList);

        }

        public void updateCurrentMusicIndex(int index){
            mMusicService.updateCurrentMusicIndex(index);
        }

        public boolean isPlaying(){
            return mMusicService.isPlaying();
        }

        public void pause(){
            mMusicService.pause();
        }

        public void play(){
            mMusicService.play();
        }
        public void previous(){
            mMusicService.previous();
        }

        public void next(){
            mMusicService.next();
        }

        public void stop(){
            mMusicService.stop();
        }

        public Song getCurSong(){
            return mMusicService.getCurSong();
        }

        public int getCurProgress(){
            return mMusicService.getCurProgress();
        }

        public int getDuration(){
            return mMusicService.getDuration();
        }

        public void seekTo(int progress) {
            mMusicService.seekTo(progress);
        }
    }
}
