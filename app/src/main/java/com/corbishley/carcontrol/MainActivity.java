package com.corbishley.carcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 100;
    private static final String TAG = "main";
    private Context context;
    private ListView btListView;
    private BluetoothAdapter btAdapter;
    private Set<BluetoothDevice> allBTDevice;
    private ArrayList<String> btDeviceList;
    private ArrayAdapter<String> adapter;
    private int mode;
    private final int ButtonMode=1;
    private final int SensorMode=2;
    private final int ControlMode=3;
    private final int DataMode=4;
    private String itemData;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context =this;
        mode = ButtonMode;

        btListView =(ListView) findViewById(R.id.listView_id);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter == null){
            Toast.makeText(context,"There is no bluetooth device.",Toast.LENGTH_SHORT).show();
            finish();
        }else{
            if(!btAdapter.isEnabled()){

                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            }
        }
        allBTDevice = btAdapter.getBondedDevices();

        btDeviceList = new ArrayList<String>();

        if (allBTDevice.size() >0){
            for (BluetoothDevice device : allBTDevice){
                btDeviceList.add("paired : "+device.getName()+"\n"+device.getAddress());
                Log.d(TAG,"name = "+device.getName());
                Log.d(TAG,"address = "+device.getAddress());
            }
            adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,btDeviceList);
            btListView.setAdapter(adapter);
        }

        btListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemData = parent.getItemAtPosition(position).toString();

                if (mode == ButtonMode){
                    intent = new Intent(context,CarActivity.class);
                    intent.putExtra("btdata",itemData);
                    startActivity(intent);
                }else if (mode == SensorMode){
                    intent = new Intent(context,SensorActivity.class);
                    intent.putExtra("btdata",itemData);
                    startActivity(intent);

                }else if (mode == ControlMode){
                    intent = new Intent(context,ControlActivity.class);
                    intent.putExtra("btdata",itemData);
                    startActivity(intent);

                }
            }
        });
    } // end of onCreate()

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT){
            if (resultCode == RESULT_CANCELED){
                Toast.makeText(context,"BT enable fails.",Toast.LENGTH_SHORT).show();
                finish();
            }else if(resultCode == RESULT_OK){
                Toast.makeText(context,"Turn on BT.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.bt_search:
                break;

            case R.id.bt_update:
                allBTDevice = btAdapter.getBondedDevices(); //被註冊的devices要再讓他讀一次
                btDeviceList.clear();

                if (allBTDevice.size() >0){   //檢查,避免當掉
                    for (BluetoothDevice device : allBTDevice){
                        btDeviceList.add("paired : "+device.getName()+"\n"+device.getAddress());
                    }
                    //adapter.notifyDataSetChanged();  只寫這行會當掉
                    adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,btDeviceList);
                    btListView.setAdapter(adapter);
                }
                break;

            case R.id.car_buttonmode:
                mode = ButtonMode;
                break;

            case R.id.car_sensormode:
                mode = SensorMode;
                break;

            case R.id.control_mode:
                mode = ControlMode;
                break;

            case R.id.data_mode:
                mode = DataMode;
                Intent intent = new Intent(context, DataActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
