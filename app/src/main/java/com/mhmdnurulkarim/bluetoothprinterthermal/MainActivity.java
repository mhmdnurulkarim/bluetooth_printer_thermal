package com.mhmdnurulkarim.bluetoothprinterthermal;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter deviceAdapter;

    private static final String PRINTER_NAME = "Your Printer Name";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        Button buttonPrint = findViewById(R.id.buttonPrint);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        deviceAdapter = new RecyclerViewAdapter(new ArrayList<BluetoothDevice>());
        recyclerView.setAdapter(deviceAdapter);

        buttonPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToPrinter();
            }
        });

        loadPairedDevices();
    }

    private void loadPairedDevices() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                deviceAdapter.addDevice(device);
            }
        }
    }

    private void connectToPrinter() {
        progressDialog = ProgressDialog.show(this, "Connecting", "Please wait...", true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bluetoothDevice = deviceAdapter.getSelectedDevice();
                    if (bluetoothDevice == null) {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "No device selected", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }

                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                    bluetoothSocket.connect();
                    outputStream = bluetoothSocket.getOutputStream();
                    printData();
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Connected to Printer", Toast.LENGTH_SHORT).show();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    private void printData() {
        try {
            JSONObject jsonData = new JSONObject();
            jsonData.put("Item Number", "21-00046");
            jsonData.put("Batch Number", "15-595-1649");
            jsonData.put("Expired Date", "");
            jsonData.put("Cost Date", "2024-04-01 08:30");
            jsonData.put("Warehouse", "WH-STR02");
            jsonData.put("Rack", "FLOOR");
            jsonData.put("Org", "BPI");
            jsonData.put("Site", "BTNG");

            String printData = jsonData.toString();
            outputStream.write(printData.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (outputStream != null) outputStream.close();
            if (bluetoothSocket != null) bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}