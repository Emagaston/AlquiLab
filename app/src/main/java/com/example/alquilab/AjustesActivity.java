package com.example.alquilab;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;


public class AjustesActivity extends AppCompatActivity {

    Toolbar mToolbar;
    TextView textToolbar, textLanguage;
    ImageView imageToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        setearToolbar();

        //Listener del lenguaje
        textLanguage=findViewById(R.id.textLenguaje);
        textLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] opciones = {getString(R.string.idiomaES), getString(R.string.idiomaEN), getString(R.string.idiomaFR)};

                final AlertDialog.Builder alertaOpciones = new AlertDialog.Builder(AjustesActivity.this);

                alertaOpciones.setTitle(getString(R.string.selectIdioma));
                alertaOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (opciones[i].equals(getString(R.string.idiomaES))) {
                                    Locale idiom_es = new Locale("es", "ES");
                                    Locale.setDefault(idiom_es);
                                    Configuration config_es = new Configuration();
                                    config_es.locale = idiom_es;
                                    getBaseContext().getResources().updateConfiguration(config_es, getBaseContext().getResources().getDisplayMetrics());
                                    finish();
                                    startActivity(getIntent());
                                } else {
                                    if (opciones[i].equals(getString(R.string.idiomaEN))) {
                                        Locale idiom_en = new Locale("en", "EN");
                                        Locale.setDefault(idiom_en);
                                        Configuration config_en = new Configuration();
                                        config_en.locale = idiom_en;
                                        getBaseContext().getResources().updateConfiguration(config_en, getBaseContext().getResources().getDisplayMetrics());
                                        finish();
                                        startActivity(getIntent());
                                    } else {
                                        if (opciones[i].equals(getString(R.string.idiomaFR))) {
                                            Locale idiom_fr = new Locale("fr", "FR");
                                            Locale.setDefault(idiom_fr);
                                            Configuration config_fr = new Configuration();
                                            config_fr.locale = idiom_fr;
                                            getBaseContext().getResources().updateConfiguration(config_fr, getBaseContext().getResources().getDisplayMetrics());
                                            finish();
                                            startActivity(getIntent());
                                        } else {
                                            dialogInterface.dismiss();
                                        }
                                    }
                                }
                                SharedPreferences preferences=getSharedPreferences("Opcion",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("opcion", (String) opciones[i]);
                                editor.apply();
                            }
                        });
                alertaOpciones.show();
            }
        });
}

    private void setearToolbar() {
        //toolbar
        mToolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(mToolbar);
        textToolbar = findViewById(R.id.titleToolbar);
        String titleToolbar = getString(R.string.settings);
        textToolbar.setText(titleToolbar);
        imageToolbar=findViewById(R.id.imageToolbar1);
        imageToolbar.setOnClickListener(view -> {
            startActivity(new Intent(AjustesActivity.this, HomePropietarioActivity.class));
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AjustesActivity.this, HomePropietarioActivity.class));
    }
}