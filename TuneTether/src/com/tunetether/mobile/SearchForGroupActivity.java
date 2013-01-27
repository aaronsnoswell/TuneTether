package com.tunetether.mobile;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tunetether.mobile.MyApplication.OnDeviceConnectedListener;
import com.tunetether.mobile.MyApplication.OnPeerListChangedListener;

public class SearchForGroupActivity extends Activity {
	LinearLayout lyt_device_list;
	Button btn_cancel;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_for_group);
        
        lyt_device_list = ((LinearLayout) findViewById(R.id.device_list));
        btn_cancel = ((Button) findViewById(R.id.cancel));
        
        btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((MyApplication)getApplication()).stopPeerDiscovery();
				finish();
			}
		});
        
        ((MyApplication)getApplication()).addPeerListChangedListener(new OnPeerListChangedListener() {
			
			@Override
			public void onPeerListChanged(WifiP2pDeviceList peers) {
				populateDeviceListUI(peers);
			}
		});
        
        ((MyApplication)getApplication()).addDeviceConnectedListener(new OnDeviceConnectedListener() {
			
			@Override
			public void onDeviceConnected() {
				// The user connected to a device
				Intent i = new Intent(SearchForGroupActivity.this, ClientPlayingActivity.class);
				startActivity(i);
			}
		});
        
        ((MyApplication)getApplication()).initiatePeerDiscovery();
    }
    
    /**
     * Updates the Device List UI
     * @param peers
     */
	private void populateDeviceListUI(WifiP2pDeviceList peers) {
		lyt_device_list.removeAllViews();
		
		for(WifiP2pDevice device : peers.getDeviceList()) {
			View inflatedView = View.inflate(SearchForGroupActivity.this, R.layout.device_list_item, null);
			inflatedView.setTag(device);
			
			((TextView)inflatedView.findViewById(R.id.device_name)).setText(device.deviceName);
			inflatedView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// Connect to group
					WifiP2pDevice device = ((WifiP2pDevice) v.getTag());
					
					((MyApplication) getApplication()).connectToDevice(device);
				}
			});
			
			lyt_device_list.addView(inflatedView);
		}
	}
    
    private String TAG = "TUNETETHER";
}
