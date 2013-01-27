package com.tunetether.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ClientPlayingScreen extends Activity {
	Button btn_disconnect;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_playing_screen);
        
        btn_disconnect = ((Button) findViewById(R.id.disconnect));
        
        btn_disconnect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((MyApplication)getApplication()).disconnect();
				finish();
			}
		});
        
    }
	
}
