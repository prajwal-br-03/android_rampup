package com.example.rampup;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Set;

public class Deviceselect extends AppCompatActivity {
    public static final String TAG="Deviceselect" ;
    ArrayList<BluetoothDevice> devicelists = new ArrayList<>();
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    BluetoothDevice slecteddevice = null;
    ListView devicelist ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //    EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deviceselect);
         devicelist = findViewById(R.id.devicelist);
        Log.d(TAG,"onCreate");
            BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (ActivityCompat.checkSelfPermission(Deviceselect.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(Deviceselect.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSIONS);
                Log.d(TAG, "inside if block");
                return;
            }
            Set<BluetoothDevice> paireddevices = bluetoothAdapter.getBondedDevices();
            if (paireddevices.size() > 0) {
                for (BluetoothDevice device : paireddevices) {
                    devicelists.add(device);
                }
                updateDeviceList();
            }

        devicelist.setOnItemClickListener((adapterView, view, i, l) -> {
            if(slecteddevice!=null) {
                slecteddevice = devicelists.get(i);
            }
            Intent intent = new Intent(Deviceselect.this, MainActivity.class);
            intent.putExtra("device", slecteddevice);
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    protected void updateDeviceList(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        for (BluetoothDevice device : devicelists) {
            if (ActivityCompat.checkSelfPermission(Deviceselect.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(Deviceselect.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSIONS);
                return;
            }
            adapter.add(device.getName() + "\n" + device.getAddress());

        }
        devicelist.setAdapter(adapter);
    }
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (ActivityCompat.checkSelfPermission(Deviceselect.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // Request the necessary permissions
                    ActivityCompat.requestPermissions(Deviceselect.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSIONS);
                    return;
                }
                devicelists.add(device);
                updateDeviceList();


            }
        }
    };

}