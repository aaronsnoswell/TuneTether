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

public class TuneTetherStartActivity extends Activity {
    
	Button btn_enable_wifidirect;
	Button btn_next;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        
        btn_enable_wifidirect = ((Button)findViewById(R.id.enable_wifidirect));
        btn_next = ((Button)findViewById(R.id.next));
        
        btn_enable_wifidirect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (((MyApplication)getApplication()).manager != null && ((MyApplication)getApplication()).channel != null) {
					
                    // Since this is the system wireless settings activity, it's
                    // not going to send us a result. We will be notified by
                    // WiFiDeviceBroadcastReceiver instead.
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                } else {
                    Log.e(TAG, "channel or manager is null");
                }
			}
		});
        
        btn_next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent next = new Intent(TuneTetherStartActivity.this, ClientOrServerActivity.class);
				startActivity(next);
			}
		});
        
        ((MyApplication)getApplication()).addP2PEnabledListener(new MyApplication.OnWifiP2PEnabledChangedListener() {
			
			@Override
			public void onEnabledChanged(boolean enabled) {
				btn_next.setEnabled(enabled);
		        
		        if(btn_next.isEnabled()) {
		        	btn_next.setText("Enabled! Click to continue.");
		        } else {
		        	btn_next.setText("Enable WifiDirect First");
		        }
			}
		});
        
    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    
    
    private String TAG = "TUNETETHER";
}