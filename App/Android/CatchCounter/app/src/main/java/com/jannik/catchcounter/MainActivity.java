package com.jannik.catchcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.garmin.android.connectiq.ConnectIQ;
import com.garmin.android.connectiq.IQApp;
import com.garmin.android.connectiq.IQDevice;
import com.garmin.android.connectiq.exception.InvalidStateException;
import com.garmin.android.connectiq.exception.ServiceUnavailableException;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String CIQ_APP_ID = "a3421feed289106a538cb9547ab12096";
    public static final String IQ_DEVICE = "IQDevice";

    private ConnectIQ connectIQ;
    private CIQDeviceAdapter deviceAdapter;
    private boolean sdkIsReady = false;

    private ConnectIQ.IQDeviceEventListener deviceEventListener = new ConnectIQ.IQDeviceEventListener() {
        @Override
        public void onDeviceStatusChanged(IQDevice iqDevice, IQDevice.IQDeviceStatus iqDeviceStatus) {
            deviceAdapter.updateDeviceStatus(iqDevice, iqDeviceStatus);
        }
    };

    private ConnectIQ.ConnectIQListener connectIQListener = new ConnectIQ.ConnectIQListener() {
        @Override
        public void onSdkReady() {
            Log.i("MYLOG", "sdk ready");
            loadDevices();
            sdkIsReady = true;
        }

        @Override
        public void onInitializeError(ConnectIQ.IQSdkErrorStatus iqSdkErrorStatus) {
            Log.i("MYLOG", "sdk init failed");
            String msg = "Failed to initialize Sdk with Error: " + iqSdkErrorStatus.name();
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            sdkIsReady = false;
        }

        @Override
        public void onSdkShutDown() {
            Log.i("MYLOG", "sdk shutdown");
            sdkIsReady = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView deviceListView = (ListView) findViewById(R.id.deviceListView);
        deviceAdapter = new CIQDeviceAdapter(this, android.R.layout.simple_list_item_2);
        deviceListView.setAdapter(deviceAdapter);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IQDevice device = deviceAdapter.getItem(position);
                forwardToCounter(device);
            }
        });

        Button buttonRefresh = (Button) findViewById(R.id.buttonRefresh);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDevices();
            }
        });

        connectIQ = ConnectIQ.getInstance(this, ConnectIQ.IQConnectType.WIRELESS);
        Log.i("MYLOG", "right before initialize");
        connectIQ.initialize(this, true, connectIQListener);
        Log.i("MYLOG", "right after initialize");

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sdkIsReady) {
            loadDevices();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            connectIQ.unregisterAllForEvents();
            connectIQ.shutdown(this);
        } catch (InvalidStateException e) {
            e.printStackTrace();
        }
    }

    public void loadDevices() {
        Log.i("MYLOG", "running loaddevices");
        try {
            List<IQDevice> devices = connectIQ.getKnownDevices();

            if (devices != null) {
                deviceAdapter.setDevices(devices);

                for(IQDevice device : devices) {
                    connectIQ.unregisterForDeviceEvents(device); // needed for refresh
                    connectIQ.registerForDeviceEvents(device, deviceEventListener);
                }
            }
        } catch (InvalidStateException | ServiceUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void forwardToCounter(IQDevice device) {
        IQDevice.IQDeviceStatus status = device.getStatus();
        if (status == IQDevice.IQDeviceStatus.CONNECTED) {
            Toast.makeText(MainActivity.this, "Yes, connected device!", Toast.LENGTH_SHORT).show();
            try {
                connectIQ.getApplicationInfo(CIQ_APP_ID, device, new ConnectIQ.IQApplicationInfoListener() {
                    @Override
                    public void onApplicationInfoReceived(IQApp iqApp) {
                        Intent intent = new Intent(MainActivity.this, CounterActivity.class);
                        intent.putExtra(MainActivity.IQ_DEVICE, device);
                        startActivity(intent);
                    }

                    @Override
                    public void onApplicationNotInstalled(String s) {
                        Toast.makeText(MainActivity.this, "Install the CIQ App first!", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (InvalidStateException | ServiceUnavailableException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(MainActivity.this, "Connect your device first!", Toast.LENGTH_SHORT).show();
        }
    }
}