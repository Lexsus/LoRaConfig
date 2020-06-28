package com.example.simpleserverble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// class MyScanCallback extends ScanCallback{
//     private String TAG="BLE";
//     private BluetoothAdapter mBluetoothAdapter;
//     private List<String> devices = new ArrayList<String>();
//
//     public List<String> getDevices() {
//         return devices;
//     }
//
//     public void setDevices(List<String> devices) {
//         this.devices = devices;
//     }
//
//     public MyScanCallback(BluetoothAdapter mBluetoothAdapter) {
//         this.mBluetoothAdapter = mBluetoothAdapter;
//     }
//
//
//    @Override
//    public void onScanResult(int callbackType, ScanResult result) {
//        super.onScanResult(callbackType, result);
//        Log.d(TAG,"on Scan result");
//        processScanResult(result);
//    }
//
//     private void processScanResult(ScanResult result) {
//         BluetoothDevice bluetoothDevice = result.getDevice();
//         Log.i(TAG, "device name "+ bluetoothDevice.getName()+ " with address "+ bluetoothDevice.getAddress());
//         devices.add(bluetoothDevice.getName());
//         stopBleScan();
//
////        lamp_state_switcher.setOnCheckedChangeListener({ _: CompoundButton, state: Boolean ->
////                Log.d(TAG, "changing the lamp state")
////        setLampState(state)
////        })
//
////        mBluetoothGatt = bluetoothDevice.connectGatt(BlueToothActivity@this,false,mBleGattCallBack)
//     }
//
//     private void stopBleScan() {
//         mBluetoothAdapter.getBluetoothLeScanner().stopScan(this);
//     }
//
//     @Override
//
//    public void onBatchScanResults(List<ScanResult> results) {
//        super.onBatchScanResults(results);
//        Log.i(TAG,"on Scan Batch");
//    }
//
//    @Override
//    public void onScanFailed(int errorCode) {
//        super.onScanFailed(errorCode);
//        Log.e(TAG,"on Scan Failed");
//    }
//}

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;
    ListView mDeviceList;
    Button btnScan;
    int BLUETOOTH_REQUEST_CODE = 1;
    private String TAG = "BLE";
    private Set<String> deviceNames = new HashSet<>();
    private Set<BluetoothDevice> devices = new LinkedHashSet<>();
    Handler handler = new Handler();
    TextView statusTextView;
    BluetoothGattCharacteristic m_characteristicWrite;
    LoraLink loraLink;
    @Override
    protected void onResume() {
        super.onResume();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLUETOOTH_REQUEST_CODE);
        } else {
            startBleScan();
            listPairedDevices();
        }
    }

    private void listPairedDevices() {
//        mPairedDevices = mBluetoothAdapter.getBondedDevices();
//        ArrayList list = new ArrayList();
//        list.add("One");
//        list.add("two");
//        list.add("Three");
//        if (mPairedDevices.size()>0)
//        {
//            for(BluetoothDevice bt : mPairedDevices)
//            {
//                list.add(bt.getName() + "\n" + bt.getAddress());
//            }
//        }
//        else
//        {
//            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
//        }
        if (deviceNames.size() > 0) {
            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceNames.toArray());

            mDeviceList.setAdapter(adapter);
            mDeviceList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked
        }
    }

    private void stopBleScan() {
        mBluetoothAdapter.getBluetoothLeScanner().stopScan(newCallBack);
    }

    private void processScanResult(ScanResult result) {
        BluetoothDevice bluetoothDevice = result.getDevice();
        Log.i(TAG, "device name " + bluetoothDevice.getName() + " with address " + bluetoothDevice.getAddress());
        deviceNames.add(bluetoothDevice.getName() + " " + bluetoothDevice.getAddress());
        devices.add(bluetoothDevice);
        stopBleScan();

//        lamp_state_switcher.setOnCheckedChangeListener({ _: CompoundButton, state: Boolean ->
//                Log.d(TAG, "changing the lamp state")
//        setLampState(state)
//        })

//        mBluetoothGatt = bluetoothDevice.connectGatt(BlueToothActivity@this,false,mBleGattCallBack)
    }

    private ScanCallback newCallBack = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.d(TAG, "on Scan result");
            processScanResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.i(TAG, "on Scan Batch");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG, "on Scan Failed");
        }
    };
    //TODO добавить доп информацию в List
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            if (arg2 <= devices.size()) {
                for (BluetoothDevice dev : devices
                ) {
                    if (dev.getAddress().equals(address)) {
                        Log.d(TAG, "Connect device " + dev.getName());
                        //bluetoothGatt = dev.connectGatt(av.getContext(), true, gattCallback);
                        loraLink.connectGatt(dev);
                        if (loraLink.isConnected())
                            statusTextView.setText("Connected");
                    }

                }

            }
            // Make an intent to start next activity.
//            Intent i = new Intent(MainActivity.this, MyCommunicationsActivity.class);
//            //Change the activity.
//            i.putExtra(EXTRA_ADDRESS, address); //this will be received at CommunicationsActivity
//            startActivity(i);
        }
    };



//    private void broadcastUpdate(final String action) {
//        final Intent intent = new Intent(action);
//        sendBroadcast(intent);
//    }

    private void startBleScan() {

        //devices = new HashSet<>();
        ScanFilter filter = new ScanFilter.Builder().build();
        List<ScanFilter> filters = new ArrayList<>();
        filters.add(filter);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();
        Log.i(TAG, "start ble scan");
        mBluetoothAdapter.getBluetoothLeScanner().startScan(
                filters,
                settings, newCallBack);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        mDeviceList = (ListView) findViewById(R.id.listView);
        btnScan = (Button) findViewById(R.id.buttonScan);
        btnScan.setOnClickListener(OnClickListener);

        mBluetoothAdapter = ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE))
                .getAdapter();

        statusTextView = (TextView) findViewById(R.id.statusTextView);

        loraLink = new LoraLink(this);

    }

    private View.OnClickListener OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "OnClick");
//            mBluetoothAdapter = ((BluetoothManager)getSystemService(BLUETOOTH_SERVICE))
//                    .getAdapter();
//            if (!mBluetoothAdapter.isEnabled())
//            {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, BLUETOOTH_REQUEST_CODE);
//            }
//            else
//            {
//                startBleScan();
//                listPairedDevices();
//            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void OnSend(View view) {
        if (loraLink.isConnected())
        {
            loraLink.writeValue("Covid-19");
        }
    }
}
