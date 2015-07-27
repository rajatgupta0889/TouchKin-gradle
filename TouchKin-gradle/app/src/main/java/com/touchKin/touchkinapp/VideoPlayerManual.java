package com.touchKin.touchkinapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import com.touchKin.touckinapp.R;

public class VideoPlayerManual extends Activity {

	VideoView videoplayer;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_player_manual);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		videoplayer = (VideoView) findViewById(R.id.video_player);
		videoplayer.setVideoURI((Uri) bundle.get("videouri"));
		// videoView.requestFocus();
		videoplayer.setMediaController(new MediaController(VideoPlayerManual.this));
		//
		// String url =
		// "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
		// Toast.makeText(context, String.valueOf(position),
		// Toast.LENGTH_SHORT).show();

		videoplayer.start();
	}

}
