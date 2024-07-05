package com.mhmdnurulkarim.bluetoothprinterthermal;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Printer;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;

public class MainActivity extends AppCompatActivity {

//    private BluetoothAdapter bluetoothAdapter;
//    private BluetoothDevice bluetoothDevice;
//    private BluetoothSocket bluetoothSocket;
//    private OutputStream outputStream;
//    private ProgressDialog progressDialog;
//    private RecyclerView recyclerView;
//    private RecyclerViewAdapter deviceAdapter;
//
//    private static final String PRINTER_NAME = "Your Printer Name";
//    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int PERMISSION_BLUETOOTH = 1;
    private BluetoothConnection selectedDevice;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        recyclerView = findViewById(R.id.recyclerView);
        Button buttonPrint = findViewById(R.id.buttonPrint);
//
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter == null) {
//            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
//            finish();
//        }
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        deviceAdapter = new RecyclerViewAdapter(new ArrayList<BluetoothDevice>());
//        recyclerView.setAdapter(deviceAdapter);

        buttonPrint.setOnClickListener(new View.OnClickListener() {
            //            public void onClick(View v) {
//                connectToPrinter();
//            }
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
                } else {
//                    print();
                    EscPosPrinter printer = null;
                    try {
                        printer = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32);
                    } catch (EscPosConnectionException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        printer.printFormattedText("[C]Hello World!\n[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, getResources().getDrawableForDensity(R.drawable.ic_launcher_foreground, 100)) + "</img>\n");
                    } catch (EscPosConnectionException e) {
                        throw new RuntimeException(e);
                    } catch (EscPosParserException e) {
                        throw new RuntimeException(e);
                    } catch (EscPosEncodingException e) {
                        throw new RuntimeException(e);
                    } catch (EscPosBarcodeException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

//        loadPairedDevices();
    }

//    private void loadPairedDevices() {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
//        if (pairedDevices.size() > 0) {
//            for (BluetoothDevice device : pairedDevices) {
//                deviceAdapter.addDevice(device);
//            }
//        }
//    }

//    private void connectToPrinter() {
//        progressDialog = ProgressDialog.show(this, "Connecting", "Please wait...", true);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    bluetoothDevice = deviceAdapter.getSelectedDevice();
//                    if (bluetoothDevice == null) {
//                        runOnUiThread(() -> {
//                            progressDialog.dismiss();
//                            Toast.makeText(MainActivity.this, "No device selected", Toast.LENGTH_SHORT).show();
//                        });
//                        return;
//                    }
//
//                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
//                    bluetoothSocket.connect();
//                    outputStream = bluetoothSocket.getOutputStream();
//                    printData();
//                    runOnUiThread(() -> {
//                        progressDialog.dismiss();
//                        Toast.makeText(MainActivity.this, "Connected to Printer", Toast.LENGTH_SHORT).show();
//                    });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    runOnUiThread(() -> {
//                        progressDialog.dismiss();
//                        Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
//                    });
//                }
//            }
//        }).start();
//    }
//
//    private void printData() {
//        try {
//            JSONObject jsonData = new JSONObject();
//            jsonData.put("Item Number", "21-00046");
//            jsonData.put("Batch Number", "15-595-1649");
//            jsonData.put("Expired Date", "");
//            jsonData.put("Cost Date", "2024-04-01 08:30");
//            jsonData.put("Warehouse", "WH-STR02");
//            jsonData.put("Rack", "FLOOR");
//            jsonData.put("Org", "BPI");
//            jsonData.put("Site", "BTNG");
//
//            String printData = jsonData.toString();
//            outputStream.write(printData.getBytes());
//            outputStream.flush();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        try {
//            if (outputStream != null) outputStream.close();
//            if (bluetoothSocket != null) bluetoothSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void print() {
        BluetoothConnection[] bluetoothDevicesList = new BluetoothPrintersConnections().getList();
        if (bluetoothDevicesList != null) {
            selectedDevice = bluetoothDevicesList[0]; // Pilih perangkat pertama yang ditemukan, Anda dapat menyesuaikannya
        }

        if (selectedDevice != null && selectedDevice.isConnected()) {
            Toast.makeText(this, "Bluetooth device already connected", Toast.LENGTH_SHORT).show();
        } else {
            try {
                selectedDevice.connect();
                EscPosPrinter printer = new EscPosPrinter(selectedDevice, 203, 48f, 32);
                printer.printFormattedText("[C]Hello World!\n[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, getResources().getDrawableForDensity(R.drawable.ic_launcher_foreground, 100)) + "</img>\n");
                selectedDevice.disconnect();
            } catch (EscPosConnectionException e) {
                Log.e("APP", "Unable to connect to bluetooth printer", e);
                Toast.makeText(this, "Unable to connect to bluetooth printer", Toast.LENGTH_SHORT).show();
            } catch (EscPosEncodingException e) {
                Log.e("APP", "Encoding error", e);
                Toast.makeText(this, "Encoding error", Toast.LENGTH_SHORT).show();
            } catch (EscPosBarcodeException e) {
                Log.e("APP", "Barcode error", e);
                Toast.makeText(this, "Barcode error", Toast.LENGTH_SHORT).show();
            } catch (EscPosParserException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_BLUETOOTH) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                print();
            } else {
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}