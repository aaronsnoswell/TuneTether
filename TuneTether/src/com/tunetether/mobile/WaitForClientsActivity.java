package com.tunetether.mobile;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class WaitForClientsActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_for_clients);
        
        ((MyApplication)getApplication()).addP2PEnabledListener(new MyApplication.OnWifiP2PEnabledChangedListener() {
			
			@Override
			public void onEnabledChanged(boolean enabled) {
				if(!enabled) {
					// Drop back to the enable wifi p2p screen
					Toast.makeText(WaitForClientsActivity.this, "WifiP2P was disabled", Toast.LENGTH_SHORT);
					Intent i = new Intent(WaitForClientsActivity.this, StartActivity.class);
					startActivity(i);
				}
			}
		});

        ((MyApplication)getApplication()).addPeerListChangedListener(new MyApplication.OnPeerListChangedListener() {

			@Override
			public void onPeerListChanged(WifiP2pDeviceList peers) {
				// Got some peers
				
				Log.d(TAG, "Got some peers, yo!" + peers.getDeviceList().size());
			}
		});
        
        //((MyApplication)getApplication()).initiatePeerDiscovery();
    }
    
    
    private String TAG = "TUNETETHER";
}
