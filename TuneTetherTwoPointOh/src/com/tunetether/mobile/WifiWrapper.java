package com.tunetether.mobile;

import com.tunetether.mobile.WiFiDirectBroadcastReceiver.OnDeviceChangedListener;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class WifiWrapper {
	
    public WifiP2pManager manager;
    public boolean isWifiP2pEnabled = false;
    public boolean retryChannel = false;

    public final IntentFilter intentFilter = new IntentFilter();
    public Channel channel;
    public WiFiDirectBroadcastReceiver receiver = null;
    public WifiP2pDevice device = null;
    
    private static WifiWrapper instance = null;
    public static WifiWrapper getInstance() {
    	if(instance == null) {
    		instance = new WifiWrapper();
    	}
    	
    	return instance;
    }
    
    private WifiWrapper() {
        // Add necessary intent values to be matched
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }
    
    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }
	
	/**
	 * Initializes the Wifi Broadcast Receiver - should be called prior to all
	 * Wifi APIs
	 */
	public void initializeWifiReceiver(Application application) {
		if(manager == null) {
	        manager = (WifiP2pManager) application.getSystemService(Context.WIFI_P2P_SERVICE);
	        channel = manager.initialize(application, application.getMainLooper(), new ChannelListener() {
				
				@Override
				public void onChannelDisconnected() {
					// TODO Auto-generated method stub
					
				}
			});
	        
	        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
		}
		
		application.registerReceiver(receiver, intentFilter);
	}
	
	/**
	 * Unregisters the Wifi Broadcast Receiver - should be called when a Wifi
	 * Connection is no longer needed
	 */
	public void shutdownWifiReceiver(Application application) {
		application.unregisterReceiver(receiver);
	}
	
	public void setOnDeviceChangedListener(OnDeviceChangedListener listener) {
		receiver.onDeviceChangedListener = listener;
	}

	public void startWifiSettings(final Context ctx) {
		if (manager != null && channel != null) {

            /* Since this is the system wireless settings activity, it's
             * not going to send us a result. We will be notified by
             * WiFiDeviceBroadcastReceiver instead.
             */

			/* TODO ajs 27/jan/13 On some Android Devices there is no
			 * dedicated WifiDirect setting - instead, the user simply has
			 * to enable Wifi. We need to catalogue these cases and change
			 * this intent accordingly
			 */
			ctx.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        } else {
            Lg.e("channel or manager is null - cannot start settings intent");
        }
	}

	public void setDevice(WifiP2pDevice device) {
		this.device = device;
	}
}
