package com.example.projet.generatepassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projet.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class GeneratePwdActivity extends AppCompatActivity {
    private SwitchMaterial numbers;
    private SwitchMaterial capLetters;
    private SwitchMaterial smallLetters;
    private SwitchMaterial symbols;
    private Spinner spinner;
    private Button buttonGenerate;
    private Button buttonBack;


    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_password);

        //button = findViewById(R.id.button);

        numbers = findViewById(R.id.switchNumbers);
        capLetters = findViewById(R.id.switchCapLetters);
        smallLetters = findViewById(R.id.switchSmallLetters);
        symbols = findViewById(R.id.switchSymbols);
        spinner = findViewById(R.id.spinnerLength);
        buttonGenerate = findViewById(R.id.buttonGenerate);
        buttonBack = findViewById(R.id.buttonBack);




        List<Integer> list = new ArrayList<>();//setting spinner
        for (int i=4; i<=50; i++){
            list.add(i);
        }
        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        generate();
    }

    public void generate(){
        Intent gen = new Intent(this, GetGeneratedPasswordActivity.class);
        buttonGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int nb = spinner.getSelectedItemPosition();
                gen.putExtra("numbers", numbers.isChecked());
                gen.putExtra("lowercase", smallLetters.isChecked());
                gen.putExtra("uppercase", capLetters.isChecked());
                gen.putExtra("symbols", symbols.isChecked());
                gen.putExtra("nb", nb);
                startActivity(gen);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.generate_password);

        //button = findViewById(R.id.button);

        numbers = findViewById(R.id.switchNumbers);
        capLetters = findViewById(R.id.switchCapLetters);
        smallLetters = findViewById(R.id.switchSmallLetters);
        symbols = findViewById(R.id.switchSymbols);
        spinner = findViewById(R.id.spinnerLength);
        buttonGenerate = findViewById(R.id.buttonGenerate);
        buttonBack = findViewById(R.id.buttonBack);



        List<Integer> list = new ArrayList<>();//setting spinner
        for (int i=4; i<=50; i++){
            list.add(i);
        }
        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        generate();

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
