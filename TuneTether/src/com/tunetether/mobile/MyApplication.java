package com.tunetether.mobile;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.util.Log;
import android.widget.Toast;


public class MyApplication extends Application {

    /**
     * An interface for interested parties to be notified about the
     * wifi p2p enabled state
     */
	public interface OnWifiP2PEnabledChangedListener {
		public void onEnabledChanged(boolean enabled);
	}

    /**
     * An interface for interested parties to be notified when a wifi p2p
     * group is created
     */
    public interface OnGroupCreatedListener {
		public void onGroupCreated();
	}

    /**
     * An interface for interested parties to be notified when a the list of
     * peers is changed
     */
    public interface OnPeerListChangedListener {
		public void onPeerListChanged(WifiP2pDeviceList peers);
	}

    /**
     * An interface for interested parties to be notified when a connection
     * is made to a device
     */
    public interface OnDeviceConnectedListener {
		public void onDeviceConnected();
	}

	private boolean isWifiP2pEnabled = false;
    public final IntentFilter intentFilter = new IntentFilter();
    public BroadcastReceiver receiver = null;
    public Channel channel;
    public WifiP2pManager manager;

    List<OnWifiP2PEnabledChangedListener> p2pEnabledListeners = new ArrayList<OnWifiP2PEnabledChangedListener>();
    List<OnGroupCreatedListener> groupCreatedListeners = new ArrayList<OnGroupCreatedListener>();
    List<OnPeerListChangedListener> peerListChangedListeners = new ArrayList<OnPeerListChangedListener>();
    List<OnDeviceConnectedListener> deviceConnectedListeners = new ArrayList<OnDeviceConnectedListener>();
 
	@Override
	public void onCreate() {
		super.onCreate();
		
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), new ChannelListener() {
			
			@Override
			public void onChannelDisconnected() {
				Log.d(TAG, "Connection lost!");
			}
		});
        
        /*
        // Listen to Activity State changes
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			
			@Override
			public void onActivityStopped(Activity activity) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onActivityStarted(Activity activity) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onActivityResumed(Activity activity) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onActivityPaused(Activity activity) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onActivityDestroyed(Activity activity) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
				// TODO Auto-generated method stub
				
			}
		});
		*/
        
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
	}
 
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
 
	@Override
	public void onTerminate() {
		super.onTerminate();
		
        unregisterReceiver(receiver);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	/**
	 * Adds a Wifi P2P Enabled Listener
	 * @param listener
	 */
	public void addP2PEnabledListener(OnWifiP2PEnabledChangedListener listener) {
		p2pEnabledListeners.add(listener);
	}
	
	/**
	 * Adds a 'group created' listener
	 * @param listener
	 */
	public void addGroupCreatedListener(OnGroupCreatedListener listener) {
		groupCreatedListeners.add(listener);
	}
	
	/**
	 * Adds a 'group created' listener
	 * @param listener
	 */
	public void addPeerListChangedListener(OnPeerListChangedListener listener) {
		peerListChangedListeners.add(listener);
	}
	
	/**
	 * Adds a 'group created' listener
	 * @param listener
	 */
	public void addDeviceConnectedListener(OnDeviceConnectedListener listener) {
		deviceConnectedListeners.add(listener);
	}
    
    /**
     * Sets the 'wifi enabled' flag and notifies any listeners
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
        
        for(OnWifiP2PEnabledChangedListener listener : p2pEnabledListeners) {
        	listener.onEnabledChanged(isWifiP2pEnabled);
        }
    }
    
    /**
     * Attempts to create a WifiP2P Group
     */
	public void createGroup() {
		manager.createGroup(
			channel,
			new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Log.d(MyApplication.this.TAG, "Group Created!");
                    
                    for(OnGroupCreatedListener listener : groupCreatedListeners) {
                    	listener.onGroupCreated();
                    }
                }

                @Override
                public void onFailure(int reasonCode) {
                    Toast.makeText(getApplicationContext(), "Couldn't create group! Please try again", Toast.LENGTH_SHORT);
                    Log.d(MyApplication.this.TAG, "Group Creation Failed!");
                }
            }
		);
	}
	
	public void initiatePeerDiscovery() {
		// Initiates discovery of peers
		manager.discoverPeers(channel, new ActionListener() {
			
			@Override
			public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Searching for Group Members...", Toast.LENGTH_SHORT);
				Log.d(MyApplication.this.TAG, "Peers Discovery Started!");
			}
			
			@Override
			public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "Couldn't find other devices! Please try again", Toast.LENGTH_SHORT);
                Log.d(MyApplication.this.TAG, "Peer Discovery Failed!");
			}
		});
	}
	
	/**
	 * Called from the WifiDirectBroadcastReceiver to indicate the peer list
	 * has changed. Notifies listeners
	 * @param peers
	 */
	public void peersAreAvailable(WifiP2pDeviceList peers) {
		for(OnPeerListChangedListener listener : peerListChangedListeners) {
			listener.onPeerListChanged(peers);
		}
	}

	/**
	 * Called from the WifiDirectBroadcastReceiver to indicate that new
	 * connection information is available
	 * @param peers
	 */
	public void connectionInfoIsAvailable(WifiP2pInfo info) {
		// TODO Auto-generated method stub
		
	}
    
    /**
     * Attempts to initiate a connection to the given device
     * @param device The device of interest
     */
	public void connectToDevice(WifiP2pDevice device) {
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = device.deviceAddress;
		
		manager.connect(channel, config, new ActionListener() {
			
			@Override
			public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Connected to Device!", Toast.LENGTH_SHORT);
                Log.d(MyApplication.this.TAG, "Device Connected!");
                
                for(OnDeviceConnectedListener listener : deviceConnectedListeners) {
                	listener.onDeviceConnected();
                }
			}
			
			@Override
			public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "Couldn't connect to device!", Toast.LENGTH_SHORT);
                Log.d(MyApplication.this.TAG, "Device Connection Failed!");
			}
		});
	}
	
	public void stopPeerDiscovery() {
		// Stops discovery of peers
		// XXX ajs 26/jan/13 Not available until API level 16
	}

    /**
     * Disconnects this device from any connections
     */
	public void disconnect() {
		manager.cancelConnect(channel, new ActionListener() {
			
			@Override
			public void onSuccess() {
				Log.d(TAG, "Disconnected from device");
			}
			
			@Override
			public void onFailure(int reason) {
				Log.d(TAG, "Couldn't disconnect from device");
			}
		});
	}

    private String TAG = "TUNETETHER";
    
}