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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Inne_zmienne extends AppCompatActivity {

private EditText Edit1,Edit2,Edit3,Edit4,Edit5,Edit6;
private static String Edit_1,Edit_2,Edit_3,Edit_4,Edit_5,Edit_6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inne_zmienne_activity);
        Button btnSave = (Button)findViewById(R.id.btnsave);
        Edit1 = (EditText) findViewById(R.id.edittext1);
        Edit2 = (EditText) findViewById(R.id.edittext2);
        Edit3 = (EditText) findViewById(R.id.edittext3);
        Edit4 = (EditText) findViewById(R.id.edittext4);
        Edit5 = (EditText) findViewById(R.id.edittext5);
        Edit6 = (EditText) findViewById(R.id.edittext6);
        Edit1.setText(Edit_1);
        Edit2.setText(Edit_2);
        Edit3.setText(Edit_3);
        Edit4.setText(Edit_4);
        Edit5.setText(Edit_5);
        Edit6.setText(Edit_6);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Edit_1 = Edit1.getText().toString();
                Edit_2 = Edit2.getText().toString();
                Edit_3 = Edit3.getText().toString();
                Edit_4 = Edit4.getText().toString();
                Edit_5 = Edit5.getText().toString();
                Edit_6 = Edit6.getText().toString();

            }
        });


    }

    public static String getDana1() {

        Log.d("Inne_zmienne","Edit1");
        return Edit_1;
    }
    public static String getDana2() {
        Log.d("Inne_zmienne","Edit2");
        return Edit_2;
    }
    public static String getDana3() {
        return Edit_3;
    }
    public static String getDana4() {
        return Edit_4;
    }
    public static String getDana5() {
        return Edit_5;
    }
    public static String getDana6() {
        return Edit_6;
    }

}