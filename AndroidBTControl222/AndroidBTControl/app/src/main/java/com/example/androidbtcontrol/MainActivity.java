/*
Android Example to connect to and communicate with Bluetooth
In this exercise, the target is a Arduino Due + HC-06 (Bluetooth Module)

Ref:
- Make BlueTooth connection between Android devices
http://android-er.blogspot.com/2014/12/make-bluetooth-connection-between.html
- Bluetooth communication between Android devices
http://android-er.blogspot.com/2014/12/bluetooth-communication-between-android.html
 */

package com.example.androidbtcontrol;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.UUID;




public class
MainActivity extends ActionBarActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    BluetoothAdapter bluetoothAdapter;

    ArrayList<BluetoothDevice> pairedDeviceArrayList;
    TextView textInfo, textStatus,Ramka,Temperature,Counter;
    ListView listViewPairedDevice;
    RelativeLayout inputPane;
    EditText inputField;
    Button btnSend, NewActivity,btnD2,btnD5,btnD1,btnD3,btnD4,btnD6,btnD7,btnD8;
    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    private UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP =
            "00001101-0000-1000-8000-00805F9B34FB";

    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        String ramka_ = Inne_zmienne.getDana1() + " " + Inne_zmienne.getDana2() + " " + Inne_zmienne.getDana3() + " " + Inne_zmienne.getDana4() + " " + Inne_zmienne.getDana5() + " " + Inne_zmienne.getDana6();
        Ramka.setText(ramka_);
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                // we add 100 new entries
//                for (int i = 0; i < 100; i++) {
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            addEntry();
//                        }
//                    });
//
//                    // sleep to slow down the add of entries
//                    try {
//                        Thread.sleep(600);
//                    } catch (InterruptedException e) {
//                        // manage error ...
//                    }
//                }
//            }
//        }).start();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        //data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        //
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(200);
        viewport.setScrollable(true);

        textInfo = (TextView) findViewById(R.id.info);
        textStatus = (TextView) findViewById(R.id.status);
        listViewPairedDevice = (ListView) findViewById(R.id.pairedlist);
        NewActivity = (Button) findViewById(R.id.newactivity);
        inputPane = (RelativeLayout) findViewById(R.id.inputpane);
        inputField = (EditText) findViewById(R.id.input);
        btnSend = (Button) findViewById(R.id.send);
        Ramka = (TextView) findViewById(R.id.ramka);
        Counter = (TextView) findViewById(R.id.counter);
        Temperature = (TextView) findViewById(R.id.temperature);
        btnD1 = (Button) findViewById(R.id.dd1);
        btnD2 = (Button) findViewById(R.id.dd2);
        btnD3 = (Button) findViewById(R.id.dd3);
        btnD4 = (Button) findViewById(R.id.dd4);
        btnD5 = (Button) findViewById(R.id.dd5);
        btnD6 = (Button) findViewById(R.id.dd6);
        btnD7 = (Button) findViewById(R.id.dd7);
        btnD8 = (Button) findViewById(R.id.dd8);
        NewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Inne_zmienne.class);
                startActivity(intent);

            }
        });




        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (myThreadConnected != null) {

                    byte[] bytesToSend = inputField.getText().toString().getBytes();
                    myThreadConnected.write(bytesToSend);
                }
            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(this,
                    "FEATURE_BLUETOOTH NOT support",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //using the well-known SPP UUID
        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this,
                    "Bluetooth is not supported on this hardware platform",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String stInfo = bluetoothAdapter.getName() + "\n" +
                bluetoothAdapter.getAddress();
        textInfo.setText(stInfo);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Turn ON BlueTooth if it is OFF
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        setup();
    }

    private void setup() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            pairedDeviceArrayList = new ArrayList<BluetoothDevice>();

            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceArrayList.add(device);
            }

            pairedDeviceAdapter = new ArrayAdapter<BluetoothDevice>(this,
                    android.R.layout.simple_list_item_1, pairedDeviceArrayList);
            listViewPairedDevice.setAdapter(pairedDeviceAdapter);

            listViewPairedDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    BluetoothDevice device =
                            (BluetoothDevice) parent.getItemAtPosition(position);
                    Toast.makeText(MainActivity.this,
                            "Name: " + device.getName() + "\n"
                                    + "Address: " + device.getAddress() + "\n"
                                    + "BondState: " + device.getBondState() + "\n"
                                    + "BluetoothClass: " + device.getBluetoothClass() + "\n"
                                    + "Class: " + device.getClass(),
                            Toast.LENGTH_LONG).show();

                    textStatus.setText("start ThreadConnectBTdevice");
                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
                    myThreadConnectBTdevice.start();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (myThreadConnectBTdevice != null) {
            myThreadConnectBTdevice.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                setup();
            } else {
                Toast.makeText(this,
                        "BlueTooth NOT enabled",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //Called in ThreadConnectBTdevice once connect successed
    //to start ThreadConnected
    private void startThreadConnected(BluetoothSocket socket) {

        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
    }

    /*
    ThreadConnectBTdevice:
    Background Thread to handle BlueTooth connecting
    */
    private class ThreadConnectBTdevice extends Thread {

        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;


        private ThreadConnectBTdevice(BluetoothDevice device) {
            bluetoothDevice = device;

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
                textStatus.setText("bluetoothSocket: \n" + bluetoothSocket);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();

                final String eMessage = e.getMessage();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textStatus.setText("something wrong bluetoothSocket.connect(): \n" + eMessage);
                    }
                });

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            if (success) {
                //connect successful
                final String msgconnected = "connect successful:\n"
                        + "BluetoothSocket: " + bluetoothSocket + "\n"
                        + "BluetoothDevice: " + bluetoothDevice;

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textStatus.setText(msgconnected);

                        listViewPairedDevice.setVisibility(View.GONE);
                        inputPane.setVisibility(View.VISIBLE);
                    }
                });

                startThreadConnected(bluetoothSocket);
            } else {
                //fail
            }
        }

        public void cancel() {

            Toast.makeText(getApplicationContext(),
                    "close bluetoothSocket",
                    Toast.LENGTH_LONG).show();

            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    /*
    ThreadConnected:
    Background Thread to handle Bluetooth data communication
    after connected
     */
    private class ThreadConnected extends Thread {
        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;

        public ThreadConnected(BluetoothSocket socket) {
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;
        }

        // int twojamama = 0;
        String receivedTemperatureString;
        String receivedCounterString;
        String receivedOutputString;
        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = connectedInputStream.read(buffer);
                    String strReceived = new String(buffer, 0, bytes);
                    final boolean receivedTemperature = strReceived.contains("SK");
                    final boolean receivedCounter = strReceived.contains("XC");
                    final boolean receivedOutput = strReceived.contains("GO");

                    if(receivedTemperature) {
                        String[] receivedTemperatureArray = strReceived.split("K");
                        receivedTemperatureString = receivedTemperatureArray[1];
                    } else {
                        if (receivedCounter) {
                            String[] receivedCounterArray = strReceived.split("C");
                            receivedCounterString = receivedCounterArray[1];
                        } else {
                            if(receivedOutput){
                                String[] receivedOutputArray = strReceived.split("O");
                                receivedOutputString = receivedOutputArray[1];
                            }
                        }
                    }
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if(receivedCounter){

                                    Counter.setText("Wartosc licznika: " + receivedCounterString);
                                } else {
                                if(receivedTemperature){
                                    Temperature.setText("Wartosc temperatury: " + receivedTemperatureString + "\n");
                                    double currentBalanceDbl = Double.parseDouble(receivedTemperatureString);
                                    series.appendData(new DataPoint(lastX++, currentBalanceDbl), true, 10);
                                } // zle zle zle
                                } if (receivedOutput){
                                    int receivedOutputInt = Integer.parseInt(receivedOutputString);
                                    int printOutputInt1 = receivedOutputInt & 1;
                                    int printOutputInt2 = receivedOutputInt & 2;
                                    int printOutputInt3 = receivedOutputInt & 4;
                                    int printOutputInt4 = receivedOutputInt & 8;
                                    int printOutputInt5 = receivedOutputInt & 16;
                                    int printOutputInt6 = receivedOutputInt & 32;
                                    int printOutputInt7 = receivedOutputInt & 64;
                                    int printOutputInt8 = receivedOutputInt & 128;
                                    if(printOutputInt1 > 0) btnD1.setBackgroundColor(Color.GREEN);
                                    else btnD1.setBackgroundColor(Color.RED);
                                    if(printOutputInt2 > 0) btnD2.setBackgroundColor(Color.GREEN);
                                    else btnD2.setBackgroundColor(Color.RED);
                                    if(printOutputInt3 > 0) btnD3.setBackgroundColor(Color.GREEN);
                                    else btnD3.setBackgroundColor(Color.RED);
                                    if(printOutputInt4 > 0) btnD4.setBackgroundColor(Color.GREEN);
                                    else btnD4.setBackgroundColor(Color.RED);
                                    if(printOutputInt5 > 0) btnD5.setBackgroundColor(Color.GREEN);
                                    else btnD5.setBackgroundColor(Color.RED);
                                    if(printOutputInt6 > 0) btnD6.setBackgroundColor(Color.GREEN);
                                    else btnD6.setBackgroundColor(Color.RED);
                                    if(printOutputInt7 > 0) btnD7.setBackgroundColor(Color.GREEN);
                                    else btnD7.setBackgroundColor(Color.RED);
                                    if(printOutputInt8 > 0) btnD8.setBackgroundColor(Color.GREEN);
                                    else btnD8.setBackgroundColor(Color.RED);

                                }
                            }
                        });
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    final String msgConnectionLost = "Connection lost:\n"
                            + e.getMessage();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            textStatus.setText(msgConnectionLost);
                        }
                    });
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                connectedBluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private void addEntry() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        series.appendData(new DataPoint(lastX++, RANDOM.nextDouble() * 10d), true, 10);
    }
}