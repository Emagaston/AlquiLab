package com.example.alquilab;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alquilab.model.Casa;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private Toolbar mToolbar;
    private Double latitudP;
    private Double longitudP;
    private Uri uri, uriG;
    private EditText nombre, descripcion, direccion, barrio, habitaciones, precio;
    private String nom, des, dir, bar, hab, pre;
    private Button btn_add,btn_map;
    private ImageView view_img;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, myref;
    private FirebaseAuth mAuth;
    String idUser;
    private Casa casa;
    String nomp, desp,dirp,barp,habp,prep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_alquiler);

        btn_add = (Button)findViewById(R.id.btn_add);
        btn_map = (Button)findViewById(R.id.btn_map);
        nombre = (EditText) findViewById(R.id.edit_nom);
        descripcion = (EditText) findViewById(R.id.edit_des);
        direccion = (EditText) findViewById(R.id.edit_dir);
        barrio = (EditText) findViewById(R.id.edit_bar);
        habitaciones = (EditText) findViewById(R.id.edit_hab);
        precio = (EditText) findViewById(R.id.edit_pre);
        view_img = (ImageView) findViewById(R.id.view_img);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //recupero parametros del map
        Bundle bundle  = getIntent().getExtras();
        if(bundle !=null){
            latitudP = bundle.getDouble("latitud");
            longitudP = bundle.getDouble("longitud");
            nomp =  bundle.getString("nomp");
            desp =  bundle.getString("desp");
            dirp =  bundle.getString("dirp");
            barp =  bundle.getString("barp");
            habp =  bundle.getString("habp");
            prep =  bundle.getString("prep");
        }
        else{
            latitudP = 0.0;
            longitudP = 0.0;
            nomp = "";
            desp = "";
            dirp = "";
            barp = "";
            habp = "";
            prep = "";
        }
        nombre.setText(nomp);
        descripcion.setText(desp);
        direccion.setText(dirp);
        barrio.setText(barp);
        habitaciones.setText(habp);
        precio.setText(prep);


        inicializarFirebase();

        //Listeners
        View.OnClickListener addListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nom = nombre.getText().toString();
                des = descripcion.getText().toString();
                dir = direccion.getText().toString();
                bar = barrio.getText().toString();
                hab = habitaciones.getText().toString();
                pre = precio.getText().toString();

                if (nom.equals("") || des.equals("") || dir.equals("")|| bar.equals("") || hab.equals("") ){//latitud =0
                    validacion();
                }else {
                    if (uriG != null){
                        crearCasa();
                        databaseReference.child("Casa").child(casa.getId()).setValue(casa);
                        Toast.makeText(NuevoAlquiler.this, "Alquiler agregado!!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(NuevoAlquiler.this, MainActivity.class));
                    }
                    else{
                        Toast.makeText(NuevoAlquiler.this, "Debe cargar una imagen!!", Toast.LENGTH_LONG).show();
                    }
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
                //if latitud =0;

            }
        };

        View.OnClickListener imgListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (longitudP == 0.0){
                    Toast.makeText(NuevoAlquiler.this, "Primero el mapa!!", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(NuevoAlquiler.this, "Cargar imagen!!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/jpg");
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    galleryLauncher.launch(intent);
                }
            };
        };

        View.OnClickListener mapListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nom = nombre.getText().toString();
                des = descripcion.getText().toString();
                dir = direccion.getText().toString();
                bar = barrio.getText().toString();
                hab = habitaciones.getText().toString();
                pre = precio.getText().toString();
                Intent intent =new Intent(NuevoAlquiler.this, MapsActivity.class);
                intent.putExtra("nomp",nom);//es el contenido del edit
                intent.putExtra("desp", des);
                intent.putExtra("dirp", dir);
                intent.putExtra("barp", bar);
                intent.putExtra("habp", hab);
                intent.putExtra("prep", pre);
                startActivity(intent);
            }
        };

        btn_add.setOnClickListener(addListener);
        view_img.setOnClickListener(imgListener);
        btn_map.setOnClickListener(mapListener);
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

    //launcher de la galeria, par subir fotos
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

                    //este es el problema
                    //databaseReference.child("Casa").child(casa.getId()).setValue(casa);
                    uriG=uri1;
                }));
            }
        }
    });

    private void crearCasa() {
        nom = nombre.getText().toString();
        des = descripcion.getText().toString();
        dir = direccion.getText().toString();
        bar = barrio.getText().toString();
        hab = habitaciones.getText().toString();
        pre = precio.getText().toString();
        casa = new Casa();
        casa.setId(UUID.randomUUID().toString());
        casa.setNombre(nom);
        casa.setDescripcion(des);
        casa.setDireccion(dir);
        casa.setBarrio(bar);
        casa.setHabitaciones(hab);
        casa.setPrecio(pre);
        casa.setIdUser(idUser);
        casa.setLatitud(String.valueOf(latitudP));
        casa.setLongitud(String.valueOf(longitudP));
        casa.setUrlFoto(String.valueOf(uriG));
    }

}

