package com.tunetether.mobile;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivityStart extends Activity {
    
	Button btn_enable_wifidirect;
	Button btn_next;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        
        btn_enable_wifidirect = ((Button)findViewById(R.id.enable_wifi));
        btn_next = ((Button)findViewById(R.id.next));
        
        btn_enable_wifidirect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
                WifiWrapper.getInstance().startWifiSettings(ActivityStart.this);
			}
		});
        
        btn_next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent next = new Intent(ActivityStart.this, ActivityClientOrServer.class);
				startActivity(next);
			}
		});
        
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
        // Initialize Wifi Receiver
        WifiWrapper.getInstance().initializeWifiReceiver(getApplication());
    }
}