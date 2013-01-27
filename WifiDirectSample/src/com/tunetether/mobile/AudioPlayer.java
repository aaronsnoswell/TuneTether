package com.tunetether.mobile;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

public class AudioPlayer {
	
	MediaPlayer player;
	
	public AudioPlayer() {
		player = new MediaPlayer();
	}
	
	public void play_file(Activity activity, String filename) {
		AssetFileDescriptor afd;
		try {
			afd = activity.getAssets().openFd(filename);
			
			player.setDataSource(
				afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength()
			);
			
			player.prepare();
			player.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stopAll() {
		player.stop();
	}
	
}
