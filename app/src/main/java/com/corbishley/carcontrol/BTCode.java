package com.corbishley.carcontrol;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

public class BTCode extends AppCompatActivity {

    private Context context;
    private TextView BTText , songText;
    private ImageButton buttonTop,buttonDown,buttonLeft,buttonRight,buttonStop,imageStop;
    private String remoteDeviceInfo;
    private Button ButtonLink;
    private ImageView imageTop,imageDown,imageLeft,imageRight;
    private BluetoothAdapter btAdapter;
    private BTChatService mChatService = null;
    private String remoteMacAddress;
    private final int ButtonMode = 1;
    private final int SensorMode = 2;
    private int mode,linkFlag;

    private SensorManager sensor_manager;

    private static final String TAG = "button";

    private static final String GO_FORWARD ="f" ;
    private static final String GO_BACKWARD ="b" ;
    private static final String TURN_LEFT ="l" ;
    private static final String TURN_RIGHT ="r" ;
    private static final String CAR_STOP ="p" ;
    private static final String Song_OFF="0";
    private static final String Song_1="1";
    private static final String Song_2="2";
    private static final String Song_3="3";
    private static final String Song_4="4";
    private String directionCmd, songCmd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    } // end of onCreate()


    // Sends a Command to remote BT device.
    private void sendCMD(String message) {
        // Check that we're actually connected before trying anything
        int mState = mChatService.getState();
        Log.d(TAG, "btstate in sendMessage =" + mState);

        if (mState != BTChatService.STATE_CONNECTED) {
            Log.d(TAG, "btstate =" + mState);
           // Toast.makeText(context, "Bluetooth device is not connected. ", Toast.LENGTH_SHORT).show();
            return;

        } else {
            // Check that there's actually something to send
            if (message.length() > 0) {
                // Get the message bytes and tell the BluetoothChatService to write
                byte[] send = message.getBytes();
                mChatService.BTWrite(send);

            }
        }

    }


    // The Handler that gets information back from the BluetoothChatService
    //There is no message queue leak problem
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    songText.append("remote : " + readMessage + "\n");   //display on TextView
                    Log.d(TAG,"Receive data : "+readMessage);

                    break;

                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String mConnectedDevice = msg.getData().getString(Constants.DEVICE_NAME);
                    Toast.makeText(context, "Connected to "+ mConnectedDevice, Toast.LENGTH_SHORT).show();
                    break;

                case Constants.MESSAGE_TOAST:
                    Toast.makeText(context, msg.getData().getString(Constants.TOAST),Toast.LENGTH_SHORT).show();
                    break;

                case Constants.MESSAGE_ServerMode:
                   // Toast.makeText(context,"Enter Server accept state.",Toast.LENGTH_SHORT).show();   //display on TextView
                    break;

                case Constants.MESSAGE_ClientMode:
                  //  Toast.makeText(context,"Enter Client connect state.",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            Log.d(TAG,"CarActivity onDestry()");
            mChatService.stop();
            mChatService=null;
        }
    }
}
