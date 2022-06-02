package com.example.projet.actions;

import static com.example.projet.main.MainActivity.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projet.R;
import com.example.projet.user.ChooseUserActivity;
import com.google.android.material.textfield.TextInputLayout;

public class ChangeProfileActivity extends AppCompatActivity {

    private TextInputLayout login;
    private TextInputLayout password;
    private Button buttonChange;
    private Button buttonCancel;
    private Spinner spinner;

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
        setContentView(R.layout.change_pin);

        String username = getIntent().getStringExtra("username");

        login = findViewById(R.id.changeLogin);
        password = findViewById(R.id.changeCode);
        buttonChange = findViewById(R.id.buttonChange);
        buttonCancel = findViewById(R.id.buttonCancel);
        spinner = findViewById(R.id.SpinnerPin1);

        defaultData(username);
        change(username);

    }

    public int checkUsername(String username, String usernameActual){
        Cursor query = db.rawQuery("SELECT username FROM USER", null);
        if (username.equals(usernameActual)){
            query.close();
            return 0;
        }
        if (username.isEmpty()){
            query.close();
            return 1;
        } else {
            while (query.moveToNext()){
                if (username.equals(query.getString(0))){
                    query.close();
                    return 2;
                }
            }
        }
        query.close();
        return 0;
    }


    //setting up all data in textfields to easy change them
    public void defaultData(String username){
        login.getEditText().setText(username);
        Cursor query = db.rawQuery("SELECT code FROM USER WHERE username=?", new String[]{username});
        while(query.moveToNext()){
            password.getEditText().setText(query.getString(0));
        }
        query.close();
    }

    //changing data
    public void change(String username){

        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (login.getEditText().getText().toString().isEmpty() || password.getEditText().getText().toString().isEmpty()) {
                    toast(getResources().getString(R.string.chang));
                    vibrate(750);
                } else if (checkUsername(login.getEditText().getText().toString(), username) == 1) {
                    toast(getResources().getString(R.string.creat_user_error2));
                    vibrate(750);
                } else if (checkUsername(login.getEditText().getText().toString(), username) == 2) {
                    toast(getResources().getString(R.string.creat_user_error));
                    vibrate(750);
                } else {


                    spinnerCheck(login.getEditText().getText().toString(), password.getEditText().getText().toString());


                }
            }
        });


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void update(String user, String pin){
        String username = getIntent().getStringExtra("username");
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", user);
        contentValues.put("code", pin);
        db.update("USER", contentValues, "username = ?", new String[]{username});
        Intent chooseUser = new Intent(this, ChooseUserActivity.class);
        //finish(); // this caused freeze bug, tried to make onResume with finish to go to choose user
        startActivity(chooseUser);
    }

    public void spinnerCheck(String user, String pin){
        switch(spinner.getSelectedItemPosition()){
            case 0:
                if (pin.length() == 4 && pin.matches("[0-9]+")){
                    update(user, pin);
                    //check.putExtra("typePin", "num");
                } else {
                    toast(getResources().getString(R.string.pin_error3));
                }
                break;
            case 1:
                if (pin.length() == 6 && pin.matches("[0-9]+")){
                    update(user, pin);
                    //check.putExtra("typePin", "num");
                } else {
                    toast(getResources().getString(R.string.pin_error2));
                }
                break;
            case 2:
                if (pin.length() != 0 && pin.trim().length() > 0){
                    update(user, pin);
                    //check.putExtra("typePin", "abc");
                } else {
                    toast(getResources().getString(R.string.pin_error));
                }
                break;
            default:
                break;
        }
    }

}
