/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tunetether.mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.tunetether.mobile.DeviceListFragment.DeviceActionListener;
import com.tunetether.mobile.WiFiDirectBroadcastReceiver.OnDeviceChangedListener;

/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 */
public class ActivityConnectToGroup extends Activity implements ChannelListener, DeviceActionListener {

	Button btn_cancel;
	Button btn_scan_again;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_to_group);

        btn_cancel = ((Button) findViewById(R.id.cancel));
        btn_scan_again = ((Button) findViewById(R.id.scan_again));
        
        btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
        btn_scan_again.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				initiateDiscovery();
			}
		});
        

    	DeviceListFragment fragment = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
        fragment.updateThisDevice(WifiWrapper.getInstance().device);
        
        initiateDiscovery();
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }
    
    @Override
    public void finish() {
    	super.finish();
    }

	@Override
	public void showDetails(WifiP2pDevice device) {
		DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
        fragment.showDetails(device);
	}

	@Override
	public void cancelDisconnect() {
        /* A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (WifiWrapper.getInstance().manager != null) {
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
            
            if (fragment.getDevice() == null || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
            	
                disconnect();
                
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE || fragment.getDevice().status == WifiP2pDevice.INVITED) {

            	WifiWrapper.getInstance().manager.cancelConnect(WifiWrapper.getInstance().channel, new ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(
                    		ActivityConnectToGroup.this,
                    		"Aborting connection",
                    		Toast.LENGTH_SHORT
                    	).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(
                    		ActivityConnectToGroup.this,
                    		"Connect abort request failed - error # " + reasonCode,
                    		Toast.LENGTH_SHORT
                    	).show();
                    }
                });
            }
        }
	}

	@Override
	public void connect(WifiP2pConfig config) {
		WifiWrapper.getInstance().manager.connect(WifiWrapper.getInstance().channel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(ActivityConnectToGroup.this, "Connecting failed - please retry", Toast.LENGTH_SHORT).show();
            }
        });
	}

	@Override
	public void disconnect() {
		WifiWrapper.getInstance().manager.removeGroup(WifiWrapper.getInstance().channel, new ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Lg.d("Disconnect failed - error # " + reasonCode);
            }

            @Override
            public void onSuccess() {
            	// Disconnected - pass
            }

        });
		
        //final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
        //fragment.resetViews();
        //fragment.getView().setVisibility(View.GONE);
	}

	@Override
	public void onChannelDisconnected() {
		// Try reconnecting once
        if (WifiWrapper.getInstance().manager != null && !WifiWrapper.getInstance().retryChannel) {
            Toast.makeText(this, "Channel connection lost - trying again", Toast.LENGTH_LONG).show();
            
            WifiWrapper.getInstance().retryChannel = true;
            WifiWrapper.getInstance().manager.initialize(this, getMainLooper(), this);
        } else {
        	Toast.makeText(this, "Warning - could not reconnect to channel. Try Disabling & Re-Enabling WifiDirect in the Wifi Settings Page.", Toast.LENGTH_LONG).show();
        }
		
        clearDeviceList();
	}
	
	private void enableWifi() {
		WifiWrapper.getInstance().startWifiSettings(this);
	}
	
	private void initiateDiscovery() {
        final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
        fragment.onInitiateDiscovery();
		
        if (!WifiWrapper.getInstance().isWifiP2pEnabled) {
            fragment.cancelDialog();
            
            new AlertDialog.Builder(this)
	            .setTitle("WifiDirect is disabled")
	            .setMessage("WifiDirect must be enabled in the Wifi settings page before continuing.")
	            .setPositiveButton("Go to Wifi Settings", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						WifiWrapper.getInstance().startWifiSettings(ActivityConnectToGroup.this);
					}
				})
	            .show();
            
            return;
        }
		
        WifiWrapper.getInstance().manager.discoverPeers(WifiWrapper.getInstance().channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(ActivityConnectToGroup.this, "Started device discovery", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(ActivityConnectToGroup.this, "Couldn't start device discovery - error # " + reasonCode, Toast.LENGTH_SHORT).show();
            }
        });
	}
    
    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void clearDeviceList() {
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }

        //DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
        //if (fragmentDetails != null) {
        //    fragmentDetails.resetViews();
        //}
    }
}
