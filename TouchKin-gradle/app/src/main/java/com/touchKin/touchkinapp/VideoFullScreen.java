package com.touchKin.touchkinapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.touchKin.touckinapp.R;

public class VideoFullScreen extends ActionBarActivity implements
		OnClickListener, Runnable {
	Uri videopath;
	VideoView videoscreen;
	Bitmap thumbnail;
	ProgressBar progressbarofvideo;
	// private SeekBar seekBar;
	// private Button startMedia;
	// private Button pauseMedia;
	private MediaPlayer mp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_preview);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		MediaController ctlr = new MediaController(this);
		// AudioControl();

		progressbarofvideo = (ProgressBar) findViewById(R.id.progressbarofvideo);

		videoscreen = (VideoView) findViewById(R.id.videopreview);
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
		mTitle.setText("");
		toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
		toolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NavUtils.navigateUpFromSameTask(VideoFullScreen.this);
			}
		});
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		videopath = (Uri) bundle.get("videopath");

		videoscreen.setVideoURI(videopath);
		ctlr.setMediaPlayer(videoscreen);
		videoscreen.setMediaController(ctlr);

		videoscreen.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub

				progressbarofvideo.setVisibility(View.INVISIBLE);
				videoscreen.start();
			}
		});

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	// public void AudioControl() {
	// seekBar = (SeekBar) findViewById(R.id.seekBar1);
	// startMedia = (Button) findViewById(R.id.button2);
	// pauseMedia = (Button) findViewById(R.id.button1);
	// startMedia.setOnClickListener(this);
	// pauseMedia.setOnClickListener(this);
	// }
	//
	// public void run() {
	// int currentPosition = 0;
	// int total = mp.getDuration();
	// while (mp != null && currentPosition < total) {
	// try {
	// Thread.sleep(1000);
	// currentPosition = mp.getCurrentPosition();
	// } catch (InterruptedException e) {
	// return;
	// } catch (Exception e) {
	// return;
	// }
	// seekBar.setProgress(currentPosition);
	// }
	// }
	//
	// public void onClick(View v) {
	// if (v.equals(startMedia)) {
	// if (mp != null && mp.isPlaying())
	// return;
	// if (seekBar.getProgress() > 0) {
	// mp.start();
	// return;
	// }
	// videoscreen.start();
	// mp = MediaPlayer.create(VideoFullScreen.this, videopath);
	// mp.start();
	// seekBar.setProgress(0);
	// seekBar.setMax(mp.getDuration());
	// new Thread(this).start();
	// }
	//
	// if (v.equals(pauseMedia) && mp != null) {
	// mp.pause();
	// }
	//
	// }
	//
	// public void onStartTrackingTouch(SeekBar seekBar) {
	// }
	//
	// public void onStopTrackingTouch(SeekBar seekBar) {
	// }
	//
	// public void onProgressChanged(SeekBar seekBar, int progress,
	// boolean fromUser) {
	// if (fromUser)
	// mp.seekTo(progress);
	//
	// }

}
