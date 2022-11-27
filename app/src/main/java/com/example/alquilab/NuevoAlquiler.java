package com.example.alquilab;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.alquilab.model.Casa;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.OutputStream;
import java.util.UUID;

public class NuevoAlquiler extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private Toolbar mToolbar;
    private Double latitudP;
    private Double longitudP;
    private Uri uri, uriG;
    private EditText nombre, descripcion, direccion, barrio, habitaciones, precio;
    private Button btn_add;
    private TextView btn_map;
    private ImageView view_img;
    private String nom, des, dir, bar, hab, pre;
    private String idUser;
    private String nomp, desp,dirp,barp,habp,prep;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private Casa casa;

    private TextView textToolbar;
    private ImageView imageToolbar;
    private OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_alquiler);

        //recupero parametros del mapa
        recuperarDatos();
        configurarToolbar();
        inicializarFirebase();

        //Listener ADD
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
                        startActivity(new Intent(NuevoAlquiler.this, LoginActivity.class));
                    }
                    else{
                        Toast.makeText(NuevoAlquiler.this, "Debe cargar una imagen!!", Toast.LENGTH_LONG).show();
                    }
                }
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

        //Listener IMG
        View.OnClickListener imgListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (longitudP == 0.0){
                    Toast.makeText(NuevoAlquiler.this, "Primero el mapa!!", Toast.LENGTH_LONG).show();
                }else {
                    askPermission ();

                    Toast.makeText(NuevoAlquiler.this, "Cargar imagen!!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/jpg");
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    galleryLauncher.launch(intent);
                }
            };
        };

        //Listener MAP
        View.OnClickListener mapListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(NuevoAlquiler.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(NuevoAlquiler.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //no tengo el permiso
                    ActivityCompat.requestPermissions(NuevoAlquiler.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
                    //return;
                }else{
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
            }
        };

        //asociamos los listener a los
        btn_add.setOnClickListener(addListener);
        view_img.setOnClickListener(imgListener);
        btn_map.setOnClickListener(mapListener);
    }

    //respuesta de solicitud de permisos
    @Override
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults){
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //
            } else {
                Toast.makeText(this, "Requiere permisos", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //menu
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
                startActivity(new Intent(this, LoginActivity.class));
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
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
    }

    //launcher de la galeria, para subir fotos
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

    private void recuperarDatos(){
        btn_add = (Button)findViewById(R.id.btn_add);
        btn_map = (TextView) findViewById(R.id.btn_map);
        nombre = (EditText) findViewById(R.id.edit_nom);
        descripcion = (EditText) findViewById(R.id.edit_des);
        direccion = (EditText) findViewById(R.id.edit_dir);
        barrio = (EditText) findViewById(R.id.edit_bar);
        habitaciones = (EditText) findViewById(R.id.edit_hab);
        precio = (EditText) findViewById(R.id.edit_pre);
        view_img = (ImageView) findViewById(R.id.view_img);

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
    }

    private void configurarToolbar() {
        //toolbar 1 NuevoAlquiler
        textToolbar = findViewById(R.id.titleToolbar);
        String titleToolbar = getString(R.string.toolbartitleNewRent);
        textToolbar.setText(titleToolbar);

        imageToolbar = findViewById(R.id.imageToolbar1);
        imageToolbar.setOnClickListener(view -> {
            startActivity(new Intent(NuevoAlquiler.this, HomePropietario.class));
        });
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    //desde el onclic IMG
    private void askPermission () {
        ActivityCompat.requestPermissions(NuevoAlquiler.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
    }

    //desde el onclic ADD
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(NuevoAlquiler.this,HomePropietario.class));
        finish();
    }
}

