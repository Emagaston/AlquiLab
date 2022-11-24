package com.example.alquilab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EditarAlquiler extends AppCompatActivity {

    String midp, nomp, desp, estp, prep;
    private EditText nombre, descripcion, precio;
    private Button btn_sav;
    private Spinner spinnerEstado;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference casaReference;
    private DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_alquiler);

        inicializarFirebase();

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
        switch (estp){
            case "Disponible":
                spinnerEstado.setSelection(0);
                break;
            case "Alquilado":
                spinnerEstado.setSelection(1);
                break;
            case "Pausado":
                spinnerEstado.setSelection(2);
                break;
        };

        View.OnClickListener savListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nombre.getText().equals("") || descripcion.getText().equals("")){
                    validacion();
                }else{
                    updateCasa(midp,nombre.getText().toString(),descripcion.getText().toString(),precio.getText().toString(), spinnerEstado.getSelectedItem().toString());
                }
            }
        };
        btn_sav.setOnClickListener(savListener);
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

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        casaReference = firebaseDatabase.getReference("Casa");
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
}