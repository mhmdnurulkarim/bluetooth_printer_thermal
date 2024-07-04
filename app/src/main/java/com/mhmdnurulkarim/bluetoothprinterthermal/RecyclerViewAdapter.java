package com.mhmdnurulkarim.bluetoothprinterthermal;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.DeviceViewHolder> {

    private List<BluetoothDevice> devices;
    private BluetoothDevice selectedDevice;
    private RadioGroup radioGroup;

    public RecyclerViewAdapter(List<BluetoothDevice> devices) {
        this.devices = devices;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.DeviceViewHolder holder, int position) {
        BluetoothDevice device = devices.get(position);
        holder.deviceName.setText(device.getName());
        holder.deviceAddress.setText(device.getAddress());
        holder.radioButton.setChecked(device.equals(selectedDevice));
        holder.radioButton.setOnClickListener(v -> {
            selectedDevice = device;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void addDevice(BluetoothDevice device) {
        devices.add(device);
        notifyDataSetChanged();
    }

    public BluetoothDevice getSelectedDevice() {
        return selectedDevice;
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        RadioButton radioButton;

        DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.deviceName);
            deviceAddress = itemView.findViewById(R.id.deviceAddress);
            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }
}
