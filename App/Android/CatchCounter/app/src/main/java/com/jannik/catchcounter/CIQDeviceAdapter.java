package com.jannik.catchcounter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.garmin.android.connectiq.IQDevice;

import java.util.List;

public class CIQDeviceAdapter extends ArrayAdapter<IQDevice> {

    private LayoutInflater inflater;

    public CIQDeviceAdapter(@NonNull Context context, int resource) {
        super(context, resource);

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        IQDevice device = getItem(position);
        String deviceName = device.getFriendlyName();
        if (deviceName == null) {
            deviceName = String.valueOf(device.getDeviceIdentifier());
        }
        ((TextView)convertView.findViewById(android.R.id.text1)).setText(deviceName);
        ((TextView)convertView.findViewById(android.R.id.text2)).setText(device.getStatus().name());
        return convertView;
    }

    public void setDevices(List<IQDevice> devices) {
        clear();
        addAll(devices);
        notifyDataSetChanged();
    }

    public synchronized void updateDeviceStatus(IQDevice deviceToUpdate, IQDevice.IQDeviceStatus status) {
        for(int i = 0; i < this.getCount(); i++) {
            IQDevice device = getItem(i);
            if (device.getDeviceIdentifier() == deviceToUpdate.getDeviceIdentifier()) {
                device.setStatus(status);
                notifyDataSetChanged();
                return;
            }
        }
    }
}
