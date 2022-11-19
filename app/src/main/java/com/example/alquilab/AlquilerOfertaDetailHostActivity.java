package com.example.alquilab;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.alquilab.databinding.ActivityAlquilerofertaDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Locale;

public class AlquilerOfertaDetailHostActivity extends AppCompatActivity {

    Toolbar mToolbar;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAlquilerofertaDetailBinding binding = ActivityAlquilerofertaDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        cargarPreferencias();
    }

    private void cargarPreferencias() {
        sharedPreferences = getSharedPreferences("Opcion",Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("opcion","");
        if (language.equals("Español")) {
            Locale idiom_es = new Locale("es", "ES");
            Locale.setDefault(idiom_es);
            Configuration config_es = new Configuration();
            config_es.locale = idiom_es;
            getBaseContext().getResources().updateConfiguration(config_es, getBaseContext().getResources().getDisplayMetrics());
        }else{
            if (language.equals("English")) {
                Locale idiom_en = new Locale("en", "EN");
                Locale.setDefault(idiom_en);
                Configuration config_en = new Configuration();
                config_en.locale = idiom_en;
                getBaseContext().getResources().updateConfiguration(config_en, getBaseContext().getResources().getDisplayMetrics());
            } else {
                if (language.equals("French")) {
                    Locale idiom_fr = new Locale("fr", "FR");
                    Locale.setDefault(idiom_fr);
                    Configuration config_fr = new Configuration();
                    config_fr.locale = idiom_fr;
                    getBaseContext().getResources().updateConfiguration(config_fr, getBaseContext().getResources().getDisplayMetrics());
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);

        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return true;
            }
        };
        menu.findItem(R.id.btn_Menu_Search).setOnActionExpandListener(onActionExpandListener);
        SearchView searchView = (SearchView) menu.findItem(R.id.btn_Menu_Search).getActionView();
        searchView.setQueryHint(getResources().getString(R.string.hintSearch));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.btnAdd:
                startActivity(new Intent(this,NuevoAlquiler.class));
                break;
            case R.id.btnLogout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.btnSettings:
                startActivity(new Intent(this,Ajustes.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}