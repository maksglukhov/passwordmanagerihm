package com.example.projet.actions;

import static com.example.projet.actions.AddPasswordActivity.APP_PREFERENCES;
import static com.example.projet.main.MainActivity.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projet.R;
import com.example.projet.generatepassword.GeneratePwdActivity;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePwdActivity extends AppCompatActivity {

    private TextInputLayout editWebsite;
    private TextInputLayout editLogin;
    private TextInputLayout editPassword;
    private Button buttonChange;
    private Button buttonCancel;
    private ProgressBar bar;
    private Button buttonGenerate;
    private SharedPreferences filledData;
    private SharedPreferences.Editor editor;

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
        setContentView(R.layout.change_password);

        editWebsite = findViewById(R.id.filledTextField1);
        editLogin = findViewById(R.id.filledTextField2);
        editPassword = findViewById(R.id.filledTextField3);
        buttonChange = findViewById(R.id.buttonChange);
        bar = findViewById(R.id.progressBar1);
        buttonCancel = findViewById(R.id.buttonCancel2);
        buttonGenerate = findViewById(R.id.buttonGen2);
        filledData = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = filledData.edit();

        String pwdID = getIntent().getStringExtra("pwdID");
        String website = getIntent().getStringExtra("website");
        String login = getIntent().getStringExtra("login");
        String password = getIntent().getStringExtra("password");


        editWebsite.getEditText().setText(website);
        editLogin.getEditText().setText(login);
        editPassword.getEditText().setText(password);
        String username = getIntent().getStringExtra("username");

        listeners();
        changePassword(pwdID);
    }

    public void changePassword(String pwdID){
        String username = getIntent().getStringExtra("username");
        int id = getId(username);
        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editWebsite.getEditText().getText().toString().isEmpty() || editLogin.getEditText().getText().toString().isEmpty() || editPassword.getEditText().getText().toString().isEmpty()) {
                    toast(getResources().getString(R.string.listener_error));
                    vibrate(750);
                } else if (!checkData(id, editWebsite.getEditText().getText().toString(), editLogin.getEditText().getText().toString(), editPassword.getEditText().getText().toString())){
                    toast(getResources().getString(R.string.listener_error2));
                    vibrate(750);
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("website", String.valueOf(editWebsite.getEditText().getText()));
                    contentValues.put("login", String.valueOf(editLogin.getEditText().getText()));
                    contentValues.put("pwd", String.valueOf(editPassword.getEditText().getText()));
                    db.update("PASSWORD", contentValues, "id = ?", new String[]{pwdID});
                    toast(getResources().getString(R.string.updated));
                    finish();
                }

            }
        });
    }

    //getting id of user with given username
    public int getId(String username){
        Cursor query = db.rawQuery("SELECT id FROM USER WHERE username = ?", new String [] {username});

        int id = 0;
        while(query.moveToNext()){
            id = query.getInt(0);
        }
        query.close();
        return id;
    }

    //check if the same website with the same login and password exists
    public boolean checkData(int id, String web, String login, String pwd){
        Cursor query = db.rawQuery("SELECT website, login, pwd, user_id FROM PASSWORD", null);
        while (query.moveToNext()){
            if (web.equals(query.getString(0)) && login.equals(query.getString(1)) && pwd.equals(query.getString(2)) && String.valueOf(id).equals(query.getString(3))){
                return false;
            }
        }
        query.close();
        return true;
    }


    public void listeners(){
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent gen = new Intent(this, GeneratePwdActivity.class);
        buttonGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("web", editWebsite.getEditText().getText().toString());
                editor.putString("login", editLogin.getEditText().getText().toString());
                editor.apply();
                startActivity(gen);
            }
        });

        editPassword.getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                int l = editPassword.getEditText().getText().toString().length();
                String p = editPassword.getEditText().getText().toString();
                if (l==0){
                    bar.setProgress(0);
                }
                if (l>0 && l<=2){
                    bar.setProgress(2);
                    bar.getProgressDrawable().setColorFilter(
                            Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
                }
                if (l>2 && l <= 6){
                    bar.setProgress(6);
                    bar.getProgressDrawable().setColorFilter(
                            Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
                }
                if (l>6){
                    bar.setProgress(8);
                    bar.getProgressDrawable().setColorFilter(
                            Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
                }
                if (p.contains("!") ){
                    bar.incrementProgressBy(1);

                }
                if (p.codePoints().filter(Character::isUpperCase)
                        .findFirst().isPresent() && l>=6){
                    bar.incrementProgressBy(1);
                }
                if (p.contains("!") && p.codePoints().filter(Character::isUpperCase)
                        .findFirst().isPresent() && l>7){
                    bar.getProgressDrawable().setColorFilter(
                            Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                }
                return false;
            }
        });
    }

    public void setFilledData(){
        if (filledData.contains("web")){
            editWebsite.getEditText().setText(filledData.getString("web", ""));
            editor.remove("web");
            editor.apply();
        }
        if (filledData.contains("login")){
            editLogin.getEditText().setText(filledData.getString("login", ""));
            editor.remove("login");
            editor.apply();
        }
        if (filledData.contains("password")){
            editPassword.getEditText().setText(filledData.getString("password", ""));
            editor.remove("password");
            editor.apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.change_password);

        editWebsite = findViewById(R.id.filledTextField1);
        editLogin = findViewById(R.id.filledTextField2);
        editPassword = findViewById(R.id.filledTextField3);
        buttonChange = findViewById(R.id.buttonChange);
        bar = findViewById(R.id.progressBar1);
        buttonCancel = findViewById(R.id.buttonCancel2);
        buttonGenerate = findViewById(R.id.buttonGen2);
        filledData = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = filledData.edit();

        String pwdID = getIntent().getStringExtra("pwdID");
        String website = getIntent().getStringExtra("website");
        String login = getIntent().getStringExtra("login");
        String password = getIntent().getStringExtra("password");


        editWebsite.getEditText().setText(website);
        editLogin.getEditText().setText(login);
        editPassword.getEditText().setText(password);
        listeners();
        setFilledData();

        changePassword(pwdID);
    }
}
