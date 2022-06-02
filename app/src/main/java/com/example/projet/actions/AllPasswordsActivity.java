package com.example.projet.actions;

import static com.example.projet.main.MainActivity.db;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projet.R;
import com.example.projet.data.Data;

import java.util.ArrayList;

public class AllPasswordsActivity extends AppCompatActivity {

    private ListView listPasswords;

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_passwords);

        listPasswords = findViewById(R.id.listPasswords);

        String username = getIntent().getStringExtra("username");
        list(username);
    }

    //display all passwords of current user
    public void list(String username){

        final ArrayList<Data> pwd = new ArrayList<>();
        final ArrayAdapter<Data> adapter;

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, pwd);
        Cursor query = db.rawQuery("SELECT website, login, pwd FROM USER, PASSWORD WHERE USER.username = ? AND USER.id = PASSWORD.user_id", new String[]{username});

        listPasswords.setAdapter(adapter);



        while (query.moveToNext()){
            pwd.add(new Data(query.getString(0), query.getString(1), query.getString(2)));
            adapter.notifyDataSetChanged();
        }


        listPasswords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object obj = listPasswords.getItemAtPosition(i);
                Data data = (Data) obj;
                passwordsActions(data, username, adapter);

            }
        });
        query.close();

    }

    //actions to do with choosed password
    public void passwordsActions(Data data, String username, final ArrayAdapter<Data> adapter){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        //final ArrayList<String> logins = new ArrayList<>();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        Intent changePassword = new Intent(this, ChangePwdActivity.class);
        final String[] actions = {getResources().getString(R.string.displod), getResources().getString(R.string.copin), getResources().getString(R.string.copord), getResources().getString(R.string.chord), getResources().getString(R.string.deletord)};
        builder.setTitle(getResources().getString(R.string.chooseaction)).setItems(actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        builder1.setTitle(getResources().getString(R.string.yord))
                                .setMessage(data.getPassword())
                                //.setIcon
                                .setPositiveButton(getResources().getString(R.string.copord), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ClipData clip = ClipData.newPlainText("text", data.getPassword());
                                        clipboard.setPrimaryClip(clip);
                                        toast(getResources().getString(R.string.copyd));
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                        builder1.show();
                        //toast("it works");
                        break;
                    case 1:
                        ClipData clipLogin = ClipData.newPlainText("text", data.getLogin());
                        clipboard.setPrimaryClip(clipLogin);
                        toast(getResources().getString(R.string.copyd));
                        break;
                    case 2:
                        ClipData clipPassword = ClipData.newPlainText("text", data.getPassword());
                        clipboard.setPrimaryClip(clipPassword);
                        toast(getResources().getString(R.string.copyd));
                        break;
                    case 3:
                        //toast("NOT YET IMPLEMENTED");
                        changePassword.putExtra("username", username);
                        changePassword.putExtra("pwdID", getPasswordID(data, username));
                        changePassword.putExtra("website", data.getWebsite());
                        changePassword.putExtra("login", data.getLogin());
                        changePassword.putExtra("password", data.getPassword());
                        startActivity(changePassword);
                        list(username);
                        break;
                    case 4:
                        builder2.setTitle(getResources().getString(R.string.Confirmation))
                                .setMessage(getResources().getString(R.string.ays))
                                //.setIcon
                                .setPositiveButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.del), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        db.delete("PASSWORD", "id = ?", new String[]{getPasswordID(data, username)});
                                        list(username);
                                        toast(getResources().getString(R.string.deletd));
                                    }
                                });
                        builder2.show();
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }

    //getting password id to facilitate some things in future
    public String getPasswordID(Data data, String username){

        Cursor query = db.rawQuery("SELECT PASSWORD.id FROM PASSWORD, USER WHERE USER.id = PASSWORD.user_id AND USER.username = ?" +
                "AND PASSWORD.website = ?" +
                "AND PASSWORD.login = ?" +
                "AND PASSWORD.pwd = ?", new String[]{username, data.getWebsite(), data.getLogin(), data.getPassword()});

        while(query.moveToNext()){
            return query.getString(0);
        }
        query.close();
        return "";

    }



    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.all_passwords);

        listPasswords = findViewById(R.id.listPasswords);

        String username = getIntent().getStringExtra("username");
        list(username);
    }

}
