// Copyright 2011 Google Inc. All Rights Reserved.

package com.tunetether.mobile;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class SendStringMessageService extends IntentService {

    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_MESSAGE = "com.example.android.wifidirect.SEND_MESSAGE";
    public static final String EXTRAS_MESSAGE_TEXT = "message_text";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";

    public SendStringMessageService(String name) {
        super(name);
    }

    public SendStringMessageService() {
        super("SendPlaySongMessageService");
    }

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_SEND_MESSAGE)) {
            
        	String messageText = intent.getExtras().getString(EXTRAS_MESSAGE_TEXT);
            
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

            try {
                Lg.d("Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                Lg.d("Client socket - " + socket.isConnected());
                OutputStream stream = socket.getOutputStream();

            	// Convert String into InputStream
            	InputStream is = new ByteArrayInputStream(messageText.getBytes());
                
                DeviceDetailFragment.copyStream(is, stream);
                Lg.d("Client: Data written");
            } catch (IOException e) {
                Lg.e(e.getMessage());
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }
}
