package com.example.alquilab;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alquilab.model.Casa;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.UUID;

public class NuevoAlquiler extends AppCompatActivity {

    Toolbar mToolbar;

//    public static int RC_PHOTO_PICKER = 0;
    public static final int File = 1;

    private Uri uri;
    private EditText nombre, descripcion, direccion, barrio, habitaciones, precio;
    private String nom,des,dir,bar,hab,pre;
    private Button btn_add;
    private ImageView view_img;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, myref;
    private FirebaseAuth mAuth;
    String idUser;
    private Casa casa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_alquiler);

        btn_add = (Button)findViewById(R.id.btn_add);

        nombre = (EditText) findViewById(R.id.edit_nom);
        descripcion = (EditText) findViewById(R.id.edit_des);
        direccion = (EditText) findViewById(R.id.edit_dir);
        barrio = (EditText) findViewById(R.id.edit_bar);
        habitaciones = (EditText) findViewById(R.id.edit_hab);
        precio = (EditText) findViewById(R.id.edit_pre);

        view_img = (ImageView) findViewById(R.id.view_img);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        inicializarFirebase();
        
        View.OnClickListener addListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nom = nombre.getText().toString();
                des = descripcion.getText().toString();
                dir = direccion.getText().toString();
                bar = barrio.getText().toString();
                hab = habitaciones.getText().toString();
                pre = precio.getText().toString();

                if (nom.equals("") || des.equals("") || dir.equals("")|| bar.equals("") || hab.equals("") ){
                    validacion();
                }else {
                    Toast.makeText(NuevoAlquiler.this, "Agregado!!", Toast.LENGTH_LONG).show();
                    limpiarCajas();
                    startActivity(new Intent(NuevoAlquiler.this, MainActivity.class));
                }
            }

            private void limpiarCajas() {
                nombre.setText("");
                descripcion.setText("");
                direccion.setText("");
                barrio.setText("");
                habitaciones.setText("");
                precio.setText("");
                uri =null;
            }

            private void validacion() {
                String nom = nombre.getText().toString();
                String des = descripcion.getText().toString();
                String dir = direccion.getText().toString();
                String bar = barrio.getText().toString();
                String hab = habitaciones.getText().toString();
                String pre = precio.getText().toString();
                if (nom.equals("")){
                    nombre.setError("Requerido");
                }
                if (des.equals("")){
                    descripcion.setError("Requerido");
                }
                if (dir.equals("")){
                    direccion.setError("Requerido");
                }
                if (bar.equals("")){
                    barrio.setError("Requerido");
                }
                if (hab.equals("")){
                    habitaciones.setError("Requerido");
                }
                if (pre.equals("")){
                    precio.setError("Requerido");
                }

            }
        };

        View.OnClickListener imgListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NuevoAlquiler.this, "Cargar imagen!!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                galleryLauncher.launch(intent);
            };
        };
        btn_add.setOnClickListener(addListener);
        view_img.setOnClickListener(imgListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.btnAdd:
                startActivity(new Intent(this,NuevoAlquiler.class));
                finish();
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

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();//myref
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
    }

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode()== RESULT_OK){
                uri = result.getData().getData();
                view_img.setImageURI(uri);
                //definimos el path
                StorageReference Folder = FirebaseStorage.getInstance().getReference().child("fotos");
                //definimos el nombre de la imagen
                final StorageReference file_name = Folder.child("file"+uri.getLastPathSegment());
                file_name.putFile(uri).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(uri1 -> {
                    crearCasa();
                    casa.setUrlFoto(String.valueOf(uri1));
                    databaseReference.child("Casa").child(casa.getId()).setValue(casa);
                }));
            }
        }
    });

    private void crearCasa() {
        casa = new Casa();
        casa.setId(UUID.randomUUID().toString());
        casa.setNombre(nom);
        casa.setDescripcion(des);
        casa.setDireccion(dir);
        casa.setBarrio(bar);
        casa.setHabitaciones(hab);
        casa.setPrecio(pre);
        casa.setIdUser(idUser);
    }

}

