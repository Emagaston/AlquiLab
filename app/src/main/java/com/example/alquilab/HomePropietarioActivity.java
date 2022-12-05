package com.example.alquilab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alquilab.model.Casa;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class HomePropietarioActivity extends AppCompatActivity {

    Toolbar mToolbar;
    SharedPreferences sharedPreferences;

    private RecyclerView recyclerView;
    private FirebaseDatabase db =FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Casa");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private AlquilerAdapter adapter;
    private ArrayList<Casa> list;
    private TextView TextListVacia;
    FirebaseUser user1 = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_propietario);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TextListVacia = findViewById(R.id.textListVacia);

        //creo el listado de alquileres
        list = new ArrayList<>();
        adapter = new AlquilerAdapter(this,list);

        recyclerView.setAdapter(adapter);

        root.addValueEventListener(new ValueEventListener() {
            @SuppressLint("")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                //lista completa de los alquileres
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //verificamos que el userid sea el propio.
                    Casa casa = dataSnapshot.getValue(Casa.class);
                    if (user1.getUid().contains(casa.getIdUser())) {
                        list.add(casa);
                        TextListVacia.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }else if (list.isEmpty()) {
                        listaVacia();
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        cargarPreferencias();
    }


    private void listaVacia() {
        String sms = getString(R.string.listVacia);
        TextListVacia.setText(sms);
        TextListVacia.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }


    private void cargarPreferencias() {
        sharedPreferences = getSharedPreferences("Opcion", Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("opcion","");
        if (language.equals(getString(R.string.idiomaES))) {
            Locale idiom_es = new Locale("es", "ES");
            Locale.setDefault(idiom_es);
            Configuration config_es = new Configuration();
            config_es.locale = idiom_es;
            getBaseContext().getResources().updateConfiguration(config_es, getBaseContext().getResources().getDisplayMetrics());
        }else{
            if (language.equals(getString(R.string.idiomaEN))) {
                Locale idiom_en = new Locale("en", "EN");
                Locale.setDefault(idiom_en);
                Configuration config_en = new Configuration();
                config_en.locale = idiom_en;
                getBaseContext().getResources().updateConfiguration(config_en, getBaseContext().getResources().getDisplayMetrics());
            } else {
                if (language.equals(getString(R.string.idiomaFR))) {
                    Locale idiom_fr = new Locale("fr", "FR");
                    Locale.setDefault(idiom_fr);
                    Configuration config_fr = new Configuration();
                    config_fr.locale = idiom_fr;
                    getBaseContext().getResources().updateConfiguration(config_fr, getBaseContext().getResources().getDisplayMetrics());
                }
            }
        }
    }

    //búsqueda
    private void filter(String text) {
        ArrayList<Casa> filteredlist = new ArrayList<Casa>();
        for (Casa item: list){
            if (item.getNombre().toLowerCase().contains(text.toLowerCase())){
                filteredlist.add(item);
            }
            else if (item.getBarrio().toLowerCase().contains(text.toLowerCase())){
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()){
            Toast.makeText(this, getString(R.string.Searchempty), Toast.LENGTH_SHORT).show();
        }else {
            adapter.filterList(filteredlist);
        }

    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.btn_Menu_Search).getActionView();
        searchView.setQueryHint(getResources().getString(R.string.hintSearch));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    //menu agregar,logout,filter
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.btnAdd:
                startActivity(new Intent(this, NuevoAlquilerActivity.class));
                break;
            case R.id.btnLogout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.btnSettings:
                startActivity(new Intent(this, AjustesActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
