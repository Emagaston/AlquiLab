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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class NuevoAlquiler extends AppCompatActivity {

    Toolbar mToolbar;

    public static int RC_PHOTO_PICKER = 0;
    private EditText description, address;
    private Button btn_add;
    private ImageView view_img;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_alquiler);

        btn_add = (Button)findViewById(R.id.btn_add);
        view_img = (ImageView) findViewById(R.id.view_img);
        description = (EditText) findViewById(R.id.description);
        address = (EditText) findViewById(R.id.address);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        inicializarFirebase();
        
        View.OnClickListener addListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = description.getText().toString();
                String addr = address.getText().toString();
                //Image img = view_img.get
                if (desc.equals("") || addr.equals("")){
                    validacion();
                }else {
                    Casa casa = new Casa();
                    casa.setId(UUID.randomUUID().toString());
                    casa.setDescription(desc);
                    casa.setAddress(addr);
                    //casa.setImagen();

                    databaseReference.child("Casa").child(casa.getId()).setValue(casa);
                    //Muestro mensaje de agregado
                    Toast.makeText(NuevoAlquiler.this, "Agregado!!", Toast.LENGTH_LONG).show();
                    limpiarCajas();
                }

            }

            private void limpiarCajas() {
                description.setText("");
                address.setText("");
            }

            private void validacion() {
                String desc = description.getText().toString();
                String addr = address.getText().toString();
                if (desc.equals("")){
                    description.setError("Requerido");
                }
                if (addr.equals("")){
                    address.setError("Requerido");
                }
            }
        };

        View.OnClickListener imgListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] opciones ={"Tomar foto","Cargar imagen","Cancelar"};
                final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(NuevoAlquiler.this);
                alertOpciones.setTitle("Seleccione una opción:");
                alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (opciones[i].equals("Tomar foto")) {
                            Toast.makeText(NuevoAlquiler.this, "Tomar foto!!", Toast.LENGTH_LONG).show();
                            camaraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
                        } else {
                            if (opciones[i].equals("Cargar imagen")) {
                                Toast.makeText(NuevoAlquiler.this, "Cargar imagen!!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/jpg");
                                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                                galleryLauncher.launch(intent);

                            } else {
                                dialogInterface.dismiss();
                            }
                        }
                    }
                });
                alertOpciones.show();
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
        }
        return super.onOptionsItemSelected(item);
    }


    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    ActivityResultLauncher<Intent> camaraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode()== RESULT_OK){
                Bundle extras = result.getData().getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
                view_img.setImageBitmap(bitmap);
            }
        }
    });

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode()== RESULT_OK){
                Intent data = result.getData();
                Uri uri = data.getData();
                view_img.setImageURI(uri);
            }
        }
    });

}