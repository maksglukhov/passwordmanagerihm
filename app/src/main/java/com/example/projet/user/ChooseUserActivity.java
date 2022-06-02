package com.example.projet.user;

import static com.example.projet.main.MainActivity.db;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projet.R;
import com.example.projet.checknchoose.CheckAndActionActivity;

import java.util.ArrayList;

public class ChooseUserActivity extends AppCompatActivity {
    private ListView users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_user);

        users = findViewById(R.id.listUsers);
        chooseUser();
    }

    public void chooseUser(){

        Cursor query = db.rawQuery("SELECT username FROM USER", null);
        final ArrayList<String> names = new ArrayList<>();
        final ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, names);
        users.setAdapter(adapter);
        while(query.moveToNext()){
            names.add(query.getString(0));
            adapter.notifyDataSetChanged();
        }

        //actionChoice();
        Intent check = new Intent(this, CheckAndActionActivity.class);
        users.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView selectedUsername = (TextView) view;
                String username = selectedUsername.getText().toString();
                check.putExtra("username", username);
                startActivity(check);
            }
        });
        query.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.choose_user);

        users = findViewById(R.id.listUsers);
        chooseUser();
    }


}
