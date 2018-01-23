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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.UUID;


public class
MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    BluetoothAdapter bluetoothAdapter;

    ArrayList<BluetoothDevice> pairedDeviceArrayList;
    TextView textInfo, textStatus, Ramka, Temperature, Counter,Cartoons;
    ListView listViewPairedDevice;
    RelativeLayout inputPane;
    EditText inputField;
    Button Database,btnSend, NewActivity, btnD1, btnD2, btnD3, btnD4, btnD5, btnD6, btnD7, btnD8,
            btnDD1, btnDD2, btnDD3, btnDD4, btnDD5, btnDD6, btnDD7, btnDD8 ;
    Switch Switch1;
    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    private UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP =
            "00001101-0000-1000-8000-00805F9B34FB";

    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;

    @Override
    public void onResume() {
        Log.d("Main", "onResume()");
        super.onResume();  // Always call the superclass method first
        String ramka_ = Inne_zmienne.getDana1() + " " + Inne_zmienne.getDana2() + " " + Inne_zmienne.getDana3() + " " + Inne_zmienne.getDana4() + " " + Inne_zmienne.getDana5() + " " + Inne_zmienne.getDana6();
        Ramka.setText(ramka_);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Main", "onCreate");
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
        Cartoons = (TextView) findViewById(R.id.cartoons);
        Switch1 = (Switch) findViewById(R.id.switch1);
        Database = (Button) findViewById(R.id.database);
        btnD1 = (Button) findViewById(R.id.dd1);
        btnD2 = (Button) findViewById(R.id.dd2);
        btnD3 = (Button) findViewById(R.id.dd3);
        btnD4 = (Button) findViewById(R.id.dd4);
        btnD5 = (Button) findViewById(R.id.dd5);
        btnD6 = (Button) findViewById(R.id.dd6);
        btnD7 = (Button) findViewById(R.id.dd7);
        btnD8 = (Button) findViewById(R.id.dd8);

        btnDD1 = (Button) findViewById(R.id.ddd1);
        btnDD2 = (Button) findViewById(R.id.ddd2);
        btnDD3 = (Button) findViewById(R.id.ddd3);
        btnDD4 = (Button) findViewById(R.id.ddd4);
        btnDD5 = (Button) findViewById(R.id.ddd5);
        btnDD6 = (Button) findViewById(R.id.ddd6);
        btnDD7 = (Button) findViewById(R.id.ddd7);
        btnDD8 = (Button) findViewById(R.id.ddd8);
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
                   String dataAAAA=  "*"+inputField.getText().toString()+"#";
                    byte[] bytesToSend =dataAAAA.getBytes();
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


        String receivedTemperatureString;
        String receivedCounterString;
        String receivedOutputString;
        String receivedInputString;
        String writeRegisterValue;
        String writeRegisterNumber;
        int cartoon=0;
        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;


            while (true) {
                try {
                    if (buffer != null && buffer.length != 0) {
                        bytes = connectedInputStream.read(buffer);

                        String strReceived = new String(buffer, 0, bytes);

                        //I580G102X100S3020#
                        //012345678901234567

                        char[] data_BT;
                        data_BT = strReceived.toCharArray();
                        if (data_BT[0] == 'I' && data_BT[4] == 'G' && data_BT[8] == 'X' && data_BT[12] == 'S' && data_BT[17] == '#') {

                            char[] A = new char[3];
                            char[] B = new char[3];
                            char[] C = new char[3];
                            char[] D = new char[4];

                            for (int i = 0; i < 3; i++) {
                                A[i] = data_BT[i + 1];
                                B[i] = data_BT[i + 5];
                                C[i] = data_BT[i + 9];
                            }
                            for (int i = 0; i < 4; i++) {

                                D[i] = data_BT[i + 13];
                            }
                           //Write communication declerate



                            receivedTemperatureString = new String(D);
                            double Dd = Double.valueOf(receivedTemperatureString);
                            Dd = (Dd/10) - 100;
                            //DecimalFormat Df= new DecimalFormat("#.##");
                            //Dd= Double.valueOf(Df.format(Dd));
                            receivedTemperatureString=String.valueOf(Dd);
                            Log.d("Main", "receivedTemperatureString " + receivedTemperatureString);

                            receivedCounterString = new String(C);
                            receivedCounterString.replaceAll(System.getProperty("line.separator"),"");
                            Integer Cc = Integer.parseInt(receivedCounterString);
                            Cc = Cc  - 100;
                            if(Cc==89) cartoon++;
                            receivedCounterString=String.valueOf(Cc);
                            Log.d("Main", " receivedCounterString " +  receivedCounterString);

                            receivedOutputString = new String(B);
                            Integer Bb = Integer.valueOf(receivedOutputString);
                            Bb = Bb - 100;
                            receivedOutputString=String.valueOf(Bb);
                            Log.d("Main", " receivedOutputString " +  receivedOutputString);

                            Log.d("Main", " RAMKA :" + receivedTemperatureString + " " + receivedCounterString + " " + receivedOutputString);
                            receivedInputString = new String(A);
                            Integer Aa = Integer.valueOf(receivedInputString);
                            Aa = Aa - 100;
                            receivedInputString=String.valueOf(Aa);


                        }
//                        char[] data_BT_write;
//                        data_BT_write = strReceived.toCharArray();
//
//                        if (data_BT[0] == '*' && data_BT[5] == '$' && data_BT[11] == '%' ) {
//                            char[] G = new char[4];
//                            char[] H = new char[5];
//
//                            for (int i = 0; i < 4; i++) {
//                                G[i] = data_BT_write[i + 1];
//                            }
//                            for (int i = 0; i < 5; i++) {
//                                H[i] = data_BT_write[i + 6];
//                            }
//                            writeRegisterNumber = new String(H);
//                            Double Ee = Double.parseDouble(writeRegisterNumber);
//                            Ee = Ee  - 10000;
//                            writeRegisterNumber=String.valueOf(Ee);
//
//                            writeRegisterValue = new String(G);
//                            Integer Ff = Integer.parseInt(writeRegisterValue);
//                            Ff = Ff  - 10000;
//                            writeRegisterValue=String.valueOf(Ff);
//                        }



                        runOnUiThread(new Runnable() {


                            @Override
                            public void run() {
                                // if (receivedCounter) {
                                Log.d("Main", " Counter.setText receivedCounterString); :" + receivedCounterString);
                                Counter.setText("Wartosc licznika: " + receivedCounterString);
                                Log.d("Main", " Temperature.setText:" + receivedTemperatureString);

                                Temperature.setText("Wartosc temperatury: " + receivedTemperatureString + "\n");
                                double currentBalanceDbl = Double.parseDouble(receivedTemperatureString);
                                if (lastX >= 20000) lastX = 0;
                                else lastX++;
                                series.appendData(new DataPoint(lastX, currentBalanceDbl), true, 10);

                                Cartoons.setText("Ilosc wykonanych kartonikow: "+ cartoon);

                                char[] printOutputInt = new char[8];
                                char[] printInputInt = new char[8];
                                Log.d("Main", " receivedOutputInt = Integer.valueOf(receivedOutputString);" + receivedTemperatureString);
                                int receivedOutputInt = Integer.valueOf(receivedOutputString);
                                int receivedInputInt = Integer.valueOf(receivedInputString);
                                for (int i = 0; i < 8; ++i){
                                    printOutputInt[i] = (char) (receivedOutputInt & (1 << i));
                                    printInputInt[i] = (char)  (receivedInputInt & (1 << i)); }
                                if (printOutputInt[0] > 0)
                                    btnD1.setBackgroundColor(Color.GREEN);
                                else btnD1.setBackgroundColor(Color.RED);
                                if (printOutputInt[1] > 0)
                                    btnD2.setBackgroundColor(Color.GREEN);
                                else btnD2.setBackgroundColor(Color.RED);
                                if (printOutputInt[2] > 0)
                                    btnD3.setBackgroundColor(Color.GREEN);
                                else btnD3.setBackgroundColor(Color.RED);
                                if (printOutputInt[3] > 0)
                                    btnD4.setBackgroundColor(Color.GREEN);
                                else btnD4.setBackgroundColor(Color.RED);
                                if (printOutputInt[4] > 0)
                                    btnD5.setBackgroundColor(Color.GREEN);
                                else btnD5.setBackgroundColor(Color.RED);
                                if (printOutputInt[5] > 0)
                                    btnD6.setBackgroundColor(Color.GREEN);
                                else btnD6.setBackgroundColor(Color.RED);
                                if (printOutputInt[6] > 0)
                                    btnD7.setBackgroundColor(Color.GREEN);
                                else btnD7.setBackgroundColor(Color.RED);
                                if (printOutputInt[7] > 0)
                                    btnD8.setBackgroundColor(Color.GREEN);
                                else btnD8.setBackgroundColor(Color.RED);

                                    if (printInputInt[0] > 0)
                                        btnDD1.setBackgroundColor(Color.GREEN);
                                    else btnDD1.setBackgroundColor(Color.RED);
                                    if (printInputInt[1] > 0)
                                        btnDD2.setBackgroundColor(Color.GREEN);
                                    else btnDD2.setBackgroundColor(Color.RED);
                                    if (printInputInt[2] > 0)
                                        btnDD3.setBackgroundColor(Color.GREEN);
                                    else btnDD3.setBackgroundColor(Color.RED);
                                    if (printInputInt[3] > 0)
                                        btnDD4.setBackgroundColor(Color.GREEN);
                                    else btnDD4.setBackgroundColor(Color.RED);
                                    if (printInputInt[4] > 0)
                                        btnDD5.setBackgroundColor(Color.GREEN);
                                    else btnDD5.setBackgroundColor(Color.RED);
                                    if (printInputInt[5] > 0)
                                        btnDD6.setBackgroundColor(Color.GREEN);
                                    else btnDD6.setBackgroundColor(Color.RED);
                                    if (printInputInt[6] > 0)
                                        btnDD7.setBackgroundColor(Color.GREEN);
                                    else btnDD7.setBackgroundColor(Color.RED);
                                    if (printInputInt[7] > 0)
                                        btnDD8.setBackgroundColor(Color.GREEN);
                                    else btnDD8.setBackgroundColor(Color.RED);

                                // }
                            }
                        });

                    }
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