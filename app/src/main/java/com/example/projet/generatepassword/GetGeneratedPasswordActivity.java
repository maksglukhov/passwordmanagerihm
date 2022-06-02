package com.example.projet.generatepassword;



import static com.example.projet.actions.AddPasswordActivity.APP_PREFERENCES;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projet.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GetGeneratedPasswordActivity extends AppCompatActivity {

    private TextView res;
    private PasswordRequest passwordRequest;
    private Button buttonCopy;
    private Button buttonBack;
    private SharedPreferences filledData;
    private SharedPreferences.Editor editor;

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_password);
        res = findViewById(R.id.textResPassword);
        buttonCopy = findViewById(R.id.buttonCopy);
        buttonBack = findViewById(R.id.buttonBack2);


        boolean numbers = getIntent().getExtras().getBoolean("numbers");
        boolean smallLetters = getIntent().getExtras().getBoolean("lowercase");
        boolean capLetters = getIntent().getExtras().getBoolean("uppercase");
        boolean symbols = getIntent().getExtras().getBoolean("symbols");
        int nb = getIntent().getExtras().getInt("nb");

        filledData = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = filledData.edit();


        passwordRequest = new PasswordRequest(numbers, smallLetters, capLetters, symbols, nb);
        passwordRequest.execute();
        //ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        buttonCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ClipData clip = ClipData.newPlainText("text", res.getText());
                editor.putString("password", res.getText().toString());
                editor.apply();
                //clipboard.setPrimaryClip(clip);
                toast(getResources().getString(R.string.choose_it));
                finish();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    class PasswordRequest extends AsyncTask<Void, Integer, Void> {

        String passwordRes;
        boolean numbers;
        boolean smallLetters;
        boolean capLetters;
        boolean symbols;
        int nb;

        public PasswordRequest(boolean numbers, boolean smallLetters, boolean capLetters, boolean symbols, int nb) {
            this.numbers = numbers;
            this.smallLetters = smallLetters;
            this.capLetters = capLetters;
            this.symbols = symbols;
            this.nb = nb;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
            Connection.Response pwdForm = null;
            try {
                pwdForm = Jsoup.connect("https://www.motdepasse.xyz/")
                        .method(Connection.Method.POST)
                        .userAgent(USER_AGENT)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FormElement form = null;
            try {
                form = (FormElement) pwdForm.parse().select("form").first();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Element nbm = form.select("#gdmdp_preselect_pwd_number").first();
            Element lowercase = form.select("#gdmdp_preselect_pwd_lowercase").first();
            Element uppercase = form.select("#gdmdp_preselect_pwd_uppercase").first();
            Element special = form.select("#gdmdp-preselect-pwd-special").first();
            Elements elems = form.getElementsByAttributeValue("id", "gdmdp_pwd_length");
            //loginField.removeAttr("checked");
            if (numbers){
                nbm.attr("checked", "checked");
            } else {
                nbm.removeAttr("checked");
            }
            if(smallLetters){
                lowercase.attr("checked", "checked");
            } else {
                lowercase.removeAttr("checked");
            }
            if(capLetters){
                uppercase.attr("checked", "checked");
            } else {
                uppercase.removeAttr("checked");
            }
            if(symbols){
                special.attr("checked", "checked");
            } else {
                special.removeAttr("checked");
            }
            elems.forEach(elem->{
                Element e9 = elem.child(5);//deleting default value
                e9.removeAttr("selected");
                Element e = elem.child(nb);
                e.attr("selected", "selected");
            });


            Connection.Response formResponse = null;
            try {
                formResponse = form.submit()
                        .cookies(pwdForm.cookies())
                        .userAgent(USER_AGENT)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements response = null;
            try {
                response = formResponse.parse().getElementsByAttributeValue("class", "gdmdp-pwd");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Element res = response.first();
            passwordRes = res.attr("value");

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // Inutile ici, cf doc
            super.onProgressUpdate(values);
        }



        @Override
        protected void onPostExecute(Void aVoid) { // S'exécute sur le ThreadUI après doInBackground
            super.onPostExecute(aVoid);
            GetGeneratedPasswordActivity.this.res.setText(passwordRes);
        }
    }
}
