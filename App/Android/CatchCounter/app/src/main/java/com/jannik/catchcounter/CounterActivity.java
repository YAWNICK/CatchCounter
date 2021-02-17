package com.jannik.catchcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.garmin.android.connectiq.ConnectIQ;
import com.garmin.android.connectiq.IQApp;
import com.garmin.android.connectiq.IQDevice;
import com.garmin.android.connectiq.exception.InvalidStateException;
import com.garmin.android.connectiq.exception.ServiceUnavailableException;

import java.util.List;

public class CounterActivity extends AppCompatActivity {

    private IQDevice device;
    private IQApp ciqApp;
    private ConnectIQ connectIQ;

    private TextView deviceNameTextView;
    private TextView catchCountTextView;
    private Button resetButton;
    private Button openAppButton;

    // for counting catches
    private int catches;
    private int last1;
    private int last2;
    private int sinceLastCatch;

    private ConnectIQ.IQOpenApplicationListener openApplicationListener = new ConnectIQ.IQOpenApplicationListener() {
        @Override
        public void onOpenApplicationResponse(IQDevice iqDevice, IQApp iqApp, ConnectIQ.IQOpenApplicationStatus iqOpenApplicationStatus) {
            if (iqOpenApplicationStatus == ConnectIQ.IQOpenApplicationStatus.PROMPT_SHOWN_ON_DEVICE) {
                Toast.makeText(getApplicationContext(), "Look at your watch", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        Intent intent = getIntent();
        device = (IQDevice) intent.getParcelableExtra(MainActivity.IQ_DEVICE);
        ciqApp = new IQApp(MainActivity.CIQ_APP_ID);

        deviceNameTextView = (TextView) findViewById(R.id.deviceNameTextView);
        catchCountTextView = (TextView) findViewById(R.id.catchCountTextView);
        resetButton = (Button) findViewById(R.id.resetButton);
        openAppButton = (Button) findViewById(R.id.openAppButton);

        catches = 0;
        sinceLastCatch = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();

        connectIQ = ConnectIQ.getInstance();

        deviceNameTextView.setText(device.getFriendlyName());
        if (device.getStatus() != IQDevice.IQDeviceStatus.CONNECTED) {
            finish();
        }

        try {
            connectIQ.registerForDeviceEvents(device, new ConnectIQ.IQDeviceEventListener() {
                @Override
                public void onDeviceStatusChanged(IQDevice iqDevice, IQDevice.IQDeviceStatus iqDeviceStatus) {
                    if (iqDeviceStatus != IQDevice.IQDeviceStatus.CONNECTED) {
                        finish();
                    }
                }
            });
        } catch (InvalidStateException e) {
            e.printStackTrace();
        }

        openAppOnDevice();

        openAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppOnDevice();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catches = 0;
                sinceLastCatch = 0;
                catchCountTextView.setText("0");
            }
        });

        try {
            connectIQ.registerForAppEvents(device, ciqApp, new ConnectIQ.IQApplicationEventListener() {
                @Override
                public void onMessageReceived(IQDevice iqDevice, IQApp iqApp, List<Object> list, ConnectIQ.IQMessageStatus iqMessageStatus) {
                    // I should only ever receive one message
                    String msg = null;
                    if (list.size() > 0) {
                        for (Object o : list) {
                            if (msg == null) {
                                msg = o.toString();
                            }
                        }
                    } else {
                        return;
                    }

                    String[] valStrings = msg.split(",");
                    int[] newZs = new int[valStrings.length];
                    for (int i = 0; i < valStrings.length; i++) {
                        newZs[i] = Integer.parseInt(valStrings[i]);
                    }
                    for (int z : newZs) {
                        if (sinceLastCatch > 4) {
                            // check for feature
                            if (last1 - z > 1000 || last2 - z > 1000) {
                                catches = catches + 2;
                                sinceLastCatch = 0;
                            }
                        }
                        last2 = last1;
                        last1 = z;
                        sinceLastCatch++;
                    }
                    catchCountTextView.setText(String.valueOf(catches));
                }
            });
        } catch (InvalidStateException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            connectIQ.unregisterForDeviceEvents(device);
            connectIQ.unregisterForApplicationEvents(device, ciqApp);
        } catch (InvalidStateException e) {
            e.printStackTrace();
        }
    }

    private void openAppOnDevice() {
        try {
            connectIQ.openApplication(device, ciqApp, openApplicationListener);
        } catch (InvalidStateException | ServiceUnavailableException e) {
            e.printStackTrace();
        }
    }
}