package com.example.alquilab;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.OutputStream;
import java.util.HashMap;

public class EditarAlquiler extends AppCompatActivity {

    String midp, nomp, desp, estp, prep;
    private EditText nombre, descripcion, precio;
    private Button btn_sav;
    private Spinner spinnerEstado;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference casaReference;

    private Toolbar mToolbar;
    private TextView textToolbar;
    private ImageView imageToolbar;
    private OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_alquiler);

        configurarToolbar();
        inicializarFirebase();

        asociarIniciarComponentes();

        //Listener del SAVE
        View.OnClickListener savListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nombre.getText().equals("") || descripcion.getText().equals("")){
                    validacion();
                }else{
                    updateCasa(midp,nombre.getText().toString(),descripcion.getText().toString(),precio.getText().toString(), String.valueOf(spinnerEstado.getSelectedItemId()));
                }
            }
        };
        btn_sav.setOnClickListener(savListener);
    }

    private void configurarToolbar() {
        //toolbar 1 EditarAlquiler
        textToolbar = findViewById(R.id.titleToolbar);
        String titleToolbar = getString(R.string.edit_alq);
        textToolbar.setText(titleToolbar);

        imageToolbar = findViewById(R.id.imageToolbar1);
        imageToolbar.setOnClickListener(view -> {
            startActivity(new Intent(EditarAlquiler.this, HomePropietario.class));
        });
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        casaReference = firebaseDatabase.getReference("Casa");
    }

    private void asociarIniciarComponentes() {
        btn_sav = (Button) findViewById(R.id.btn_sav);
        nombre = (EditText) findViewById(R.id.edit_nom);
        descripcion = (EditText) findViewById(R.id.edit_des);
        precio = findViewById(R.id.edit_prec);
        spinnerEstado = findViewById(R.id.spinnerEst);

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.OpcionesEstado, R.layout.spinnerstyle);
        adapter.setDropDownViewResource(R.layout.spinnerstyle);
        spinnerEstado.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            midp = bundle.getString("Mid");
            nomp = bundle.getString("nom");
            desp = bundle.getString("des");
            estp = bundle.getString("est");
            prep = bundle.getString("precioM");

        } else {
            midp = "";
            nomp = "";
            desp = "";
            estp = "";
        }
        nombre.setText(nomp);
        descripcion.setText(desp);
        precio.setText(prep);
        //Estado

        switch (estp){
            case "0":
                spinnerEstado.setSelection(0);
                break;
            case "1":
                spinnerEstado.setSelection(1);
                break;
            case "2":
                spinnerEstado.setSelection(2);
                break;
        }
    }

    private void validacion() {
        String nom = nombre.getText().toString();
        String des = descripcion.getText().toString();
        if (nom.equals("")){
            nombre.setError("Requerido");
        }
        if (des.equals("")){
            descripcion.setError("Requerido");
        }
    }

    private void updateCasa(String id, String nombre, String descripcion, String precio, String estado) {
        HashMap casa = new HashMap();
        casa.put("nombre",nombre);
        casa.put("descripcion",descripcion);
        casa.put("estado",estado);
        casa.put("precio",precio);

        casaReference.child(id).updateChildren(casa).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Toast.makeText(EditarAlquiler.this, "Alquiler editado!!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(EditarAlquiler.this, HomePropietario.class));
                }else{
                    Toast.makeText(EditarAlquiler.this, "Error:Alquiler NO editado.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.btnAdd:
                startActivity(new Intent(this,NuevoAlquiler.class));
                finish();
                break;
            case R.id.btnLogout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.btnSettings:
                startActivity(new Intent(this,Ajustes.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EditarAlquiler.this,HomePropietario.class));
        finish();
    }

}