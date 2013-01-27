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
import android.widget.Toast;

public class ClientOrServerActivity extends Activity {
	Button btn_create_group;
	Button btn_connect_to_group;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_or_server);

        btn_create_group = ((Button)findViewById(R.id.create_group));
        btn_connect_to_group = ((Button)findViewById(R.id.connect_to_group));
        
        btn_create_group.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// Create a WifiDirect group
				Intent i = new Intent(ClientOrServerActivity.this, CreateGroupActivity.class);
				startActivity(i);
			}
		});

        btn_connect_to_group.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// Connect to a WifiDirect group
				Intent i = new Intent(ClientOrServerActivity.this, SearchForGroupActivity.class);
				startActivity(i);
			}
		});
        
        ((MyApplication)getApplication()).addP2PEnabledListener(new MyApplication.OnWifiP2PEnabledChangedListener() {
			
			@Override
			public void onEnabledChanged(boolean enabled) {
				if(!enabled) {
					// Drop back to the enable wifi p2p screen
					Toast.makeText(ClientOrServerActivity.this, "WifiP2P was disabled", Toast.LENGTH_SHORT);
					Intent i = new Intent(ClientOrServerActivity.this, StartActivity.class);
					startActivity(i);
				}
			}
		});
    }
    
    
    private String TAG = "TUNETETHER";
}