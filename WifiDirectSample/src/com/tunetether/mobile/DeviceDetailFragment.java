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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.tunetether.mobile.DeviceListFragment.DeviceActionListener;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    
    AudioPlayer audio = new AudioPlayer();
    private boolean playing_song = false;
    

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true
//                        new DialogInterface.OnCancelListener() {
//
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
//                            }
//                        }
                        );
                ((DeviceActionListener) getActivity()).connect(config);

            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceActionListener) getActivity()).disconnect();
                    }
                });
        
        return mContentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	/*
        // User has picked an image. Transfer it to group owner i.e peer using
        // FileTransferService.
        Uri uri = data.getData();
        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
        statusText.setText("Sending: " + uri);
        Log.d(WiFiDirectActivity.TAG, "Intent----------- " + uri);
        Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        getActivity().startService(serviceIntent);
        */
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                        : getResources().getString(R.string.no)));

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
        	
        	new ReceiveStringMessageServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text)).execute();
        	
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
        	
        	/*
            mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
                    .getString(R.string.client_text));
                    */
        	
        	// Set up the play button
            ((Button)getActivity().findViewById(R.id.play_song)).setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				
    				if(playing_song) {
        				// Send start playing message
        		        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
        		        statusText.setText("Sending Stop Message");
        		        
        		        Intent serviceIntent = new Intent(getActivity(), SendStringMessageService.class);
        		        serviceIntent.setAction(SendStringMessageService.ACTION_SEND_MESSAGE);
        		        serviceIntent.putExtra(SendStringMessageService.EXTRAS_MESSAGE_TEXT, "STOPALL");
        		        serviceIntent.putExtra(SendStringMessageService.EXTRAS_GROUP_OWNER_ADDRESS, info.groupOwnerAddress.getHostAddress());
        		        serviceIntent.putExtra(SendStringMessageService.EXTRAS_GROUP_OWNER_PORT, 8988);
        		        getActivity().startService(serviceIntent);
        		        
        		        audio.stopAll();
        		        
        		        ((Button)getActivity().findViewById(R.id.play_song)).setText("Play Song");
        		        
        		        playing_song = false;
    				} else {
        				// Send start playing message
        		        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
        		        statusText.setText("Sending Play Message");
        		        
        		        String command = "PLAY:";
        		        String songname = ((Spinner)getActivity().findViewById(R.id.select_song)).getSelectedItem().toString();
        		        
        		        Intent serviceIntent = new Intent(getActivity(), SendStringMessageService.class);
        		        serviceIntent.setAction(SendStringMessageService.ACTION_SEND_MESSAGE);
        		        serviceIntent.putExtra(SendStringMessageService.EXTRAS_MESSAGE_TEXT, "PLAY:rickroll.mp3");
        		        serviceIntent.putExtra(SendStringMessageService.EXTRAS_GROUP_OWNER_ADDRESS, info.groupOwnerAddress.getHostAddress());
        		        serviceIntent.putExtra(SendStringMessageService.EXTRAS_GROUP_OWNER_PORT, 8988);
        		        getActivity().startService(serviceIntent);
        		        
        		        audio.play_file(getActivity(), "rickroll.mp3");
        		        
        		        ((Button)getActivity().findViewById(R.id.play_song)).setText("Stop Playing");
        		        
        		        playing_song = true;
    				}
    				
    		        
    			}
    		});
        	
        	// Enable the play song button
        	((Button)getActivity().findViewById(R.id.play_song)).setEnabled(true);
        }

        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }

    /**
     * Updates the UI with device data
     * 
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());

    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        this.getView().setVisibility(View.GONE);
    }

    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    public static class ReceiveStringMessageServerAsyncTask extends AsyncTask<Void, Void, String> {
    	
    	private AudioPlayer audio = new AudioPlayer();
        private Context context;
        private TextView statusText;

        /**
         * @param context
         * @param statusText
         */
        public ReceiveStringMessageServerAsyncTask(Context context, View statusText) {
            this.context = context;
            this.statusText = (TextView) statusText;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                ServerSocket serverSocket = new ServerSocket(8988);
                Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
                Socket client = serverSocket.accept();
                Log.d(WiFiDirectActivity.TAG, "Server: connection done");
                
                InputStream inputstream = client.getInputStream();
                
            	// Read message with BufferedReader
            	BufferedReader br = new BufferedReader(new InputStreamReader(inputstream));
            	
            	String total_msg = "";
            	String line;
            	while ((line = br.readLine()) != null) {
            		total_msg += line;
            	}
            	
            	serverSocket.close();
                return total_msg;
            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
                return null;
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String command) {
            if (command != null) {
            	
            	if(command.contains("PLAY:")) {
            		String filename = command.split("PLAY:")[1];
                	audio.play_file((Activity)context, filename);
            	} else if(command.contains("STOPALL")) {
            		audio.stopAll();
            	} else {
            		Log.d("FOFSODFOSDF", "Unsupported message");
            	}
            	
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            statusText.setText("Opening a server socket");
        }

    }

    public static boolean copyStream(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(WiFiDirectActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

}
