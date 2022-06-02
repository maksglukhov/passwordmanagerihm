package com.example.projet.main;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.projet.R;
import com.example.projet.user.ChooseUserActivity;
import com.example.projet.user.CreateUserActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnStart;
    private Button btnExit;

    public static SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.buttonStart);
        btnExit = findViewById(R.id.buttonExit);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.close();
                finish();
            }
        });

        createBD();

    }


    //creating BD if not exists
    public void createBD(){
        db = getBaseContext().openOrCreateDatabase("users.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS USER(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username VARCHAR(25) UNIQUE," +
                "code VARCHAR(25))");
        db.execSQL("CREATE TABLE IF NOT EXISTS PASSWORD(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "website VARCHAR(50)," +
                "login VARCHAR(50)," +
                "pwd VARCHAR(50)," +
                "user_id INTEGER,FOREIGN KEY (user_id) REFERENCES USER(id))");
        Log.v("TAG", "database created or opened SUCC");
        Cursor query = db.rawQuery("SELECT * FROM USER", null);
        start(query);
    }

    public void start(Cursor query){
        Intent noUser = new Intent(this, CreateUserActivity.class);
        Intent listUser = new Intent (this, ChooseUserActivity.class);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!query.moveToFirst()){
                    Log.v("TAG", "no users, starting createUser activity");
                    startActivity(noUser);
                } else {
                    chooseStart(noUser, listUser);
                }
            }
        });
    }


    public void chooseStart(Intent noUser, Intent listUser){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] actions = {getResources().getString(R.string.new_user), getResources().getString(R.string.log_in)};
        builder.setTitle(getResources().getString(R.string.question1))
               .setNegativeButton(getResources().getString(R.string.cancel), null)
               .setItems(actions, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0){
                            Log.v("TAG", "User chose to create new user");
                            startActivity(noUser);
                            Log.v("TAG", "Create user activity is called");
                        }
                        if (i == 1){
                            Log.v("TAG", "User chose to log in");
                            startActivity(listUser);
                            Log.v("TAG", "Choose user activity is called");
                        }
                   }
               });
        builder.show();
    }

}