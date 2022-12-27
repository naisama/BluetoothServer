package com.example.bluetoothserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int REQUEST_ENABLE_BT = 1; //TODO

    TextView receivedEditText;
    EditText sendTextEditText;
    Button acceptConnectionButton;
    Button closeConnectionButton;
    Button sendTextButton;
    TextView BT_MAC_TextView;
    TextView BT_Name_TextView;
    TextView BT_ConnectionState_TextView;

    BluetoothAdapter mBluetoothAdapter;
    private Handler handlerNetworkExecutorResult;
    private BluetoothServerManager btServerManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.receivedEditText = (TextView) findViewById(R.id.receivedEditText);
        this.sendTextEditText = (EditText) findViewById(R.id.sendTextEditText);
        this.acceptConnectionButton = (Button) findViewById(R.id.acceptConnectionButton);
        this.closeConnectionButton = (Button) findViewById(R.id.closeConnectionButton);
        this.sendTextButton = (Button) findViewById(R.id.sendTextButton);
        this.BT_MAC_TextView = (TextView) findViewById(R.id.BT_MAC_TextView);
        this.BT_Name_TextView = (TextView) findViewById(R.id.BT_Name_TextView);
        this.BT_ConnectionState_TextView = (TextView) findViewById(R.id.BT_ConnectionState_TextView);

        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Device does not support Bluetooth",
                    Toast.LENGTH_SHORT).show();
        }

        acceptConnectionButton.setOnClickListener(v -> acceptConnectionButtonOnClick(v));
        closeConnectionButton.setOnClickListener(v -> closeConnectionButtonOnClick(v));



        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); //REQUEST_ENABLE_BT = 1;
        }

        //Initialize State Components
        this.acceptConnectionButton.setEnabled(true);
        this.closeConnectionButton.setEnabled(false);
        this.sendTextButton.setEnabled(false);
        this.sendTextEditText.setEnabled(false);
        this.BT_ConnectionState_TextView.setText("NOT CONNECTED");

        handlerNetworkExecutorResult = new Handler() { //Pasarla en el constructor del BluetoothServerManager como parametro
            @Override
            public void handleMessage(Message msg) {
                String message = (String) msg.obj;
                if (msg != null) {
                    if (msg.arg1 == 0) { //Datos recibidos por el Bluetooth desde el cliente
                        receivedEditText.append(message + "\n");
                    } else if (msg.arg1 == 1) { //Informacion del estado del socket
                        BT_ConnectionState_TextView.setText(message);
                    }
                }
            }
        };


        showBT_MAC_NAME();


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth enabled",
                        Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth NOT enabled",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showBT_MAC_NAME() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        }
        String BT_name = this.mBluetoothAdapter.getName();
        String BT_MAC = this.mBluetoothAdapter.getAddress();
        this.BT_MAC_TextView.setText("BT MAC:"+BT_MAC);
        this.BT_Name_TextView.setText("BT Name:"+BT_name);
    }

    private void initializeBlueetoothServerManager(){ //TODO
        //PASO 6
        if (this.btServerManager == null) {
            this.btServerManager = new BluetoothServerManager(this.mBluetoothAdapter, handlerNetworkExecutorResult);
            this.btServerManager.start();
        }else{
            this.btServerManager.exitCurrentConnection = true;
            //this.btServerManager.cancel();
        }
    }

    public void acceptConnectionButtonOnClick(View v) {
        initializeBlueetoothServerManager();
        this.acceptConnectionButton.setEnabled(false);
        this.closeConnectionButton.setEnabled(true);
        this.sendTextButton.setEnabled(true);
        this.sendTextEditText.setEnabled(true);
    }

    public void closeConnectionButtonOnClick(View v) {
        initializeBlueetoothServerManager();
        this.acceptConnectionButton.setEnabled(true);
        this.closeConnectionButton.setEnabled(false);
        this.sendTextButton.setEnabled(false);
        this.sendTextEditText.setEnabled(false);
        this.BT_ConnectionState_TextView.setText("NOT CONNECTED");
    }





}