package com.example.projet.checknchoose;

import static com.example.projet.main.MainActivity.db;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projet.R;
import com.example.projet.actions.AddPasswordActivity;
import com.example.projet.actions.AllPasswordsActivity;
import com.example.projet.actions.ChangeProfileActivity;
import com.google.android.material.textfield.TextInputLayout;

public class CheckAndActionActivity extends AppCompatActivity {

    private TextInputLayout code;
    private Button login;

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
        setContentView(R.layout.check_and_action);

        code = findViewById(R.id.checkCode);
        login = findViewById(R.id.buttonLogin);
        String username = getIntent().getStringExtra("username");

        checkPin(username);

    }

    public void checkPin(String username){
        String pass = getCodeFromDb(username);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pass.equals(code.getEditText().getText().toString())){
                    actionChoice(view, username);
                } else{
                    toast(getResources().getString(R.string.checkpin_error));
                    vibrate(750);
                }
            }
        });
    }

    private String getCodeFromDb(String username){
        Cursor query = db.rawQuery("SELECT username, code FROM USER", null);
        String pass = " ";
        while (query.moveToNext()){
            if (username.equals(query.getString(0))){
                pass = query.getString(1);
            }
        }
        query.close();
        return pass;
    }

    public void actionChoice(View view, String username){
        Intent newPass = new Intent(this, AddPasswordActivity.class);
        Intent allPass = new Intent(this, AllPasswordsActivity.class);
        Intent changePin = new Intent(this, ChangeProfileActivity.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] actions = {getResources().getString(R.string.add_NewPassword), getResources().getString(R.string.allords), getResources().getString(R.string.chgusrnm)};
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
    public void onResume() {
        super.onResume();
        setContentView(R.layout.check_and_action);

        code = findViewById(R.id.checkCode);
        login = findViewById(R.id.buttonLogin);
        String username = getIntent().getStringExtra("username");

        checkPin(username);
    }
}
