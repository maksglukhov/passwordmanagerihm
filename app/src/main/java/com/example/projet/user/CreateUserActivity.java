package com.example.projet.user;

import static com.example.projet.main.MainActivity.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.projet.R;
import com.google.android.material.textfield.TextInputLayout;

public class CreateUserActivity extends AppCompatActivity {

    private Button btnCreate;
    private Spinner spinner;

    private TextInputLayout username;
    private TextInputLayout password;

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    public void vibrate(long duration_ms) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(duration_ms < 1)
            duration_ms = 1;
        if(v != null && v.hasVibrator()) {
            if(Build.VERSION.SDK_INT >= 26) {
                v.vibrate(VibrationEffect.createOneShot(duration_ms,
                        VibrationEffect.DEFAULT_AMPLITUDE));
            }
            else {
                v.vibrate(duration_ms);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user);

        username = findViewById(R.id.inputUsername);
        password = findViewById(R.id.inputCode);
        spinner = findViewById(R.id.SpinnerPin);
        btnCreate = findViewById(R.id.buttonCreateUser);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser(view);
            }
        });

    }

    public void createUser(View view){
        String user = username.getEditText().getText().toString();
        String pin = password.getEditText().getText().toString();
        if (checkUsername(user) == 1){
            toast(getResources().getString(R.string.creat_user_error2));
            vibrate(750);
        } else if (checkUsername(user) == 2){
            toast(getResources().getString(R.string.creat_user_error));
            vibrate(750);
        } else if (checkUsername(user) == 0){
            spinnerCheck(user, pin);
        }



    }
    public int checkUsername(String username){
        Cursor query = db.rawQuery("SELECT username FROM USER", null);
        if (username.isEmpty()){
            return 1;
        } else {
            while (query.moveToNext()){
                if (username.equals(query.getString(0))){
                    return 2;
                }
            }
        }
        query.close();
        return 0;
    }

    public void spinnerCheck(String user, String pin){
        switch(spinner.getSelectedItemPosition()){
            case 0:
                if (pin.length() == 4 && pin.matches("[0-9]+")){
                    registerUser(user, pin);
                    //check.putExtra("typePin", "num");
                } else {
                    toast(getResources().getString(R.string.pin_error3));
                }
                break;
            case 1:
                if (pin.length() == 6 && pin.matches("[0-9]+")){
                    registerUser(user, pin);
                    //check.putExtra("typePin", "num");
                } else {
                    toast(getResources().getString(R.string.pin_error2));
                }
                break;
            case 2:
                if (pin.length() != 0 && pin.trim().length() > 0){
                    registerUser(user,pin);
                    //check.putExtra("typePin", "abc");
                } else {
                    toast(getResources().getString(R.string.pin_error));
                }
                break;
            default:
                break;
        }
    }

    public void registerUser(String user, String pin){
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", user);
        contentValues.put("code", pin);
        db.insert("USER", null, contentValues);
        Log.v("TAG", "user " + user + " is created");
        toast(user + getResources().getString(R.string.user_created));
        Intent chooseUser = new Intent(this, ChooseUserActivity.class);
        startActivity(chooseUser);
    }
}
