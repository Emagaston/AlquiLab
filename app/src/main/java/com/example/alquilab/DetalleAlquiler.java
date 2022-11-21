package com.example.alquilab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.IconCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DetalleAlquiler extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 100;
    private static final String CHANNEL_ID = "NOTIFICACION";
    private final static int IDunica = 0;
    Toolbar mToolbar;
    private GoogleMap mMap;
    private TextView nombre, descripcion, direccion, barrio, habitaciones, precio;
    private ImageView view_img;
    private Double latitudMap, longitudMap;

    TextView textToolbar;
    ImageView imageToolbar;
    OutputStream outputStream;

    String nom, barr, pre, img, det, direc, hab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_alquiler);

        nombre = findViewById(R.id.detail_nom);
        descripcion = findViewById(R.id.detail_des);
        direccion = findViewById(R.id.detail_dir);
        barrio = findViewById(R.id.detail_bar);
        habitaciones = findViewById(R.id.detail_hab);
        precio = findViewById(R.id.detail_pre);
        view_img = findViewById(R.id.detail_view_img);

        textToolbar = findViewById(R.id.titleToolbar);
        String titleToolbar = getString(R.string.toolbartitledetail);
        textToolbar.setText(titleToolbar);

        imageToolbar = findViewById(R.id.imageToolbar1);
        imageToolbar.setOnClickListener(view -> {
            startActivity(new Intent(DetalleAlquiler.this, HomePropietario.class));
        });

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            longitudMap = bundle.getDouble("longitud1");
            latitudMap = bundle.getDouble("latitud1");
            nom = bundle.getString("nom");
            barr = bundle.getString("barrio");
            pre = bundle.getString("precio");
            img = bundle.getString("imagen");
            det = bundle.getString("descripcion");
            direc = bundle.getString("direccion");
            hab = bundle.getString("habitaciones");
        }
        nombre.setText(nom);
        barrio.setText(barr);
        precio.setText(pre);
        descripcion.setText(det);
        direccion.setText(direc);
        habitaciones.setText(hab);
        Glide.with(this).load(img).into(view_img);

        view_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(DetalleAlquiler.this)
                        .setTitle("Descargar imagen")
                        .setMessage("¿Desea guardar la imagen?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (ContextCompat.checkSelfPermission(DetalleAlquiler.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    saveImage();
                                } else {
                                    askPermission();
                                }
                                crearCanalNotificacion();
                                crearNotifcacion();
                            }

                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d("Mensaje", "Se cancelo la accioón");
                            }
                        })
                        .show();
            }
        });
    }

    private void crearCanalNotificacion () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Descarga de imagen";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void crearNotifcacion() {
//        Intent intent = new Intent();
//        Uri pathUri= Uri.parse(Environment.getExternalStorageDirectory().getPath()
//                + "/SaveImage/");
//        intent.setDataAndType(pathUri, "file/*");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            builder.setSmallIcon(R.drawable.ic_image);
            builder.setContentTitle("Descarga de imagen");
            builder.setContentText("Se ha descargado la imagen!");
            builder.setContentInfo("4");
            builder.setTicker("Descarga!");
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(IDunica, builder.build());
        }

        private void askPermission () {
            ActivityCompat.requestPermissions(DetalleAlquiler.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

        }

        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            if (requestCode == REQUEST_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImage();
                } else {
                    Toast.makeText(this, "Requiere permisos", Toast.LENGTH_SHORT).show();
                }
            }

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        private void saveImage () {
            File dir = new File(Environment.getExternalStorageDirectory(), "SaveImage");
            if (!dir.exists()) {
                dir.mkdir();
            }
            BitmapDrawable drawable = (BitmapDrawable) view_img.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            File file = new File(dir, System.currentTimeMillis() + ".jpg");
            try {
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            Toast.makeText(this, "Se guardó exitosamente", Toast.LENGTH_SHORT).show();

            try {
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onMapReady (@NonNull GoogleMap googleMap){
            mMap = googleMap;
            Bundle bundle1 = getIntent().getExtras();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            LocationManager locationManager = (LocationManager) DetalleAlquiler.this.getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    latitudMap = Double.parseDouble(bundle1.getString("latitud1"));
                    longitudMap = Double.parseDouble(bundle1.getString("longitud1"));
                    LatLng miUbicacion = new LatLng(latitudMap, longitudMap);
                    mMap.addMarker(new MarkerOptions().position(miUbicacion).title("Acá estoy!"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(miUbicacion));
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(miUbicacion)
                            .zoom(15)
                            .bearing(90)
                            .tilt(0)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
}


