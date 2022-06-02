package com.example.projet.actions;

import static com.example.projet.main.MainActivity.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projet.R;
import com.example.projet.generatepassword.GeneratePwdActivity;
import com.google.android.material.textfield.TextInputLayout;

public class AddPasswordActivity extends AppCompatActivity {

    private TextInputLayout website;
    private TextInputLayout login;
    private TextInputLayout password;
    private Button btnSave;
    private Button btnCancel;
    private ProgressBar bar;
    private Button btnGen;
    public static final String APP_PREFERENCES = "data";
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
        setContentView(R.layout.add_password);

        String username = getIntent().getStringExtra("username");

        website = findViewById(R.id.website);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        btnSave = findViewById(R.id.buttonAdd);
        bar = findViewById(R.id.progressBar);
        btnCancel = findViewById(R.id.buttonCancel1);
        btnGen = findViewById(R.id.buttonGen);
        filledData = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = filledData.edit();

        setFilledData(); //set up filled data if user has generated password and comme back
        listeners(username); //set up listeners
    }


    public void setFilledData(){
        if (filledData.contains("web")){
            website.getEditText().setText(filledData.getString("web", ""));
            editor.remove("web");
            editor.apply();
        }
        if (filledData.contains("login")){
            login.getEditText().setText(filledData.getString("login", ""));
            editor.remove("login");
            editor.apply();
        }
        if (filledData.contains("password")){
            password.getEditText().setText(filledData.getString("password", ""));
            editor.remove("password");
            editor.apply();
        }
    }

    public void listeners(String username){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = getId(username);
                if (website.getEditText().getText().toString().isEmpty() || login.getEditText().getText().toString().isEmpty() || password.getEditText().getText().toString().isEmpty()) {
                    toast(getResources().getString(R.string.listener_error));
                    vibrate(750);
                } else if (!checkData(id, website.getEditText().getText().toString(), login.getEditText().getText().toString(), password.getEditText().getText().toString())){
                    toast(getResources().getString(R.string.listener_error2));
                    vibrate(750);
                } else {
                    insert(id, website.getEditText().getText().toString(), login.getEditText().getText().toString(), password.getEditText().getText().toString());
                    actionChoice(view, username);
                    toast(getResources().getString(R.string.listener_error3));
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        password.getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                int l = password.getEditText().getText().toString().length();
                String p = password.getEditText().getText().toString();
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

        Intent genPassword = new Intent(this, GeneratePwdActivity.class);
        btnGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("web", website.getEditText().getText().toString());
                editor.putString("login", login.getEditText().getText().toString());
                editor.apply();
                startActivity(genPassword);
            }
        });

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

    //insert new password
    public void insert(int id, String website, String login, String pwd){
        ContentValues contentValues = new ContentValues();
        contentValues.put("website", website);
        contentValues.put("login", login);
        contentValues.put("pwd", pwd);
        contentValues.put("user_id", id);
        db.insert("PASSWORD", null, contentValues);
        //return true;
    }

    //choosing next action
    public void actionChoice(View view, String username){
        Intent newPass = new Intent(this, AddPasswordActivity.class);
        Intent allPass = new Intent(this, AllPasswordsActivity.class);
        Intent changePin = new Intent(this, ChangeProfileActivity.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] actions = {getResources().getString(R.string.add_NewPassword), getResources().getString(R.string.allmy), getResources().getString(R.string.chgusrnm)};
        builder.setTitle(getResources().getString(R.string.chooseaction)).setItems(actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        newPass.putExtra("username", username);
                        startActivity(newPass);
                        break;
                    case 1:
                        allPass.putExtra("username", username);
                        startActivity(allPass);
                        break;
                    case 2:
                        changePin.putExtra("username", username);
                        startActivity(changePin);
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.add_password);

        String username = getIntent().getStringExtra("username");

        website = findViewById(R.id.website);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        btnSave = findViewById(R.id.buttonAdd);
        bar = findViewById(R.id.progressBar);
        btnCancel = findViewById(R.id.buttonCancel1);
        btnGen = findViewById(R.id.buttonGen);
        filledData = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = filledData.edit();

        setFilledData(); //set up filled data if user has generated password and comme back
        listeners(username); //set up listeners

    }
}
