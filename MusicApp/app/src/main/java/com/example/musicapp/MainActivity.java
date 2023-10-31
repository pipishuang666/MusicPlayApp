package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.musicapp.adapter.MySongListAdapter;
import com.example.musicapp.data.GlobalConstance;
import com.example.musicapp.data.Song;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRCVSongList;
    private MySongListAdapter mySongListAdapter;
    private ArrayList<Song> mSongArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initSongList();
    }

    private void initData() {
        mSongArrayList = new ArrayList<>();
        mSongArrayList.add(new Song("暗号.mp3"));
        mSongArrayList.add(new Song("交换余生.mp3"));
        mSongArrayList.add(new Song("枫.mp3"));
        mSongArrayList.add(new Song("黑色幽默.mp3"));
        mSongArrayList.add(new Song("像风一样.mp3"));
    }

    private void initSongList() {
        mySongListAdapter = new MySongListAdapter(mSongArrayList, this);
        mySongListAdapter.setmItemClickListener(new MySongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, "点击了"+position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MusicPlayActivity.class);
                intent.putExtra(GlobalConstance.KEY_SONG_LIST, mSongArrayList);
                intent.putExtra(GlobalConstance.KEY_SONG_INDEX, position);
                startActivity(intent);
            }
        });
        mRCVSongList.setAdapter(mySongListAdapter);
        mRCVSongList.setLayoutManager(new LinearLayoutManager(this));

    }

    private void initView() {
        mRCVSongList = findViewById(R.id.rcv_song_list);
    }
}