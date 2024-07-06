package com.mhmdnurulkarim.bluetoothprinterthermal;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_BLUETOOTH = 100;
    private static final int PERMISSION_BLUETOOTH_ADMIN = 101;
    private static final int PERMISSION_BLUETOOTH_CONNECT = 102;
    private static final int PERMISSION_BLUETOOTH_SCAN = 103;
    private static final int PERMISSION_FINE_LOCATION = 104;
    private static final int PERMISSION_COARSE_LOCATION = 105;
    private BluetoothConnection selectedDevice;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPrint = findViewById(R.id.btnPrint);

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkpermission();
            }
        });
    }

    private void checkpermission() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH}, MainActivity.PERMISSION_BLUETOOTH);
        } else if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, MainActivity.PERMISSION_BLUETOOTH_ADMIN);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, MainActivity.PERMISSION_BLUETOOTH_CONNECT);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, MainActivity.PERMISSION_BLUETOOTH_SCAN);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MainActivity.PERMISSION_FINE_LOCATION);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MainActivity.PERMISSION_COARSE_LOCATION);
        } else {
            showDeviceSelectionDialog();
        }
    }

    private void showDeviceSelectionDialog() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth tidak aktif", Toast.LENGTH_SHORT).show();
            return;
        }

        BluetoothConnection[] bluetoothDevicesList = new BluetoothPrintersConnections().getList();
        if (bluetoothDevicesList != null && bluetoothDevicesList.length > 0) {
            final List<BluetoothConnection> devices = new ArrayList<>();
            final List<String> deviceNames = new ArrayList<>();
            for (BluetoothConnection device : bluetoothDevicesList) {
                devices.add(device);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // Request missing permissions
                    return;
                }
                deviceNames.add(device.getDevice().getName() + "\n" + device.getDevice().getAddress());
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Pilih Perangkat Bluetooth")
                    .setItems(deviceNames.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedDevice = devices.get(which);
                            print();
                        }
                    })
                    .show();
        } else {
            Toast.makeText(this, "Tidak ada perangkat Bluetooth yang ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void print() {
        try {
//            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
//                Toast.makeText(this, "Bluetooth tidak aktif", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            BluetoothConnection[] bluetoothDevicesList = new BluetoothPrintersConnections().getList();
//            if (bluetoothDevicesList != null && bluetoothDevicesList.length > 0) {
//                selectedDevice = bluetoothDevicesList[0]; // Pilih perangkat pertama yang ditemukan, Anda dapat menyesuaikannya
//            } else {
//                Toast.makeText(this, "No Bluetooth devices found", Toast.LENGTH_SHORT).show();
//                return;
//            }

            // Coba untuk terhubung ke perangkat Bluetooth
            if (selectedDevice != null) {
                selectedDevice.connect();
                EscPosPrinter printer = new EscPosPrinter(selectedDevice, 203, 48f, 32);
                printer.printFormattedText("[C]Hello World!\n[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, getResources().getDrawableForDensity(R.drawable.ic_launcher_foreground, 100)) + "</img>\n");
                selectedDevice.disconnect();
                Toast.makeText(this, "Pencetakan berhasil", Toast.LENGTH_SHORT).show();
            }

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
            Log.e("APP", "Parser error", e);
            Toast.makeText(this, "Parser error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_BLUETOOTH || requestCode == PERMISSION_BLUETOOTH_ADMIN || requestCode == PERMISSION_BLUETOOTH_CONNECT || requestCode == PERMISSION_BLUETOOTH_SCAN || requestCode == PERMISSION_FINE_LOCATION || requestCode == PERMISSION_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                print();
            } else {
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}