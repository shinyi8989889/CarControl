package com.corbishley.carcontrol;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DataActivity extends AppCompatActivity {

    private static final String TAG = "data";
    private Context context;
    private TextView textViewData;
    private Button buttonClean,buttonBack;
    private BTChatService mChatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        context = this;
        setTitle("Data Mode");

        textViewData = (TextView) findViewById(R.id.textView_serverdata);
        textViewData.setText("");

        buttonClean = (Button) findViewById(R.id.button_clean);
        buttonBack = (Button) findViewById(R.id.button_back);

        buttonClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewData.setText("");
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mChatService = new BTChatService(context, mHandler);
        Toast.makeText(context,"Enter Server mode",Toast.LENGTH_SHORT).show();
        mChatService.serverStart();

    }

    /*********************copy BTCode.java****down***********************************/
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

                    textViewData.append("remote : " + readMessage + "\n");   //display on TextView
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
    /*********************copy BTCode.java***up***********************************/
}
