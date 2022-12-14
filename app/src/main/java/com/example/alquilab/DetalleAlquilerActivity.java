package com.example.alquilab;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class DetalleAlquilerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 1;
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
        asignamosComponentes();
        recuperamosParametros();

        //Listener IMG
        view_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermission();
                new AlertDialog.Builder(DetalleAlquilerActivity.this)
                        .setTitle(R.string.TitleAlertDescarga)
                        .setMessage(R.string.TextAlertDescarga)
                        .setPositiveButton(R.string.AlertDeleteYes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (ContextCompat.checkSelfPermission(DetalleAlquilerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    testsave();
                                    crearCanalNotificacion();
                                    crearNotifcacion();
                                }
                            }
                        })
                        .setNegativeButton(R.string.AlertDeleteNo, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d("Mensaje", "Se cancelo la accio??n");
                            }
                        })
                        .show();
            }
        });
    }

    private void asignamosComponentes() {
        nombre = findViewById(R.id.detail_nom);
        descripcion = findViewById(R.id.detail_des);
        direccion = findViewById(R.id.detail_dir);
        barrio = findViewById(R.id.detail_bar);
        habitaciones = findViewById(R.id.detail_hab);
        precio = findViewById(R.id.detail_pre);
        view_img = findViewById(R.id.detail_view_img);

        //toolbar 1 Detalle
        textToolbar = findViewById(R.id.titleToolbar);
        String titleToolbar = getString(R.string.toolbartitledetail);
        textToolbar.setText(titleToolbar);

        imageToolbar = findViewById(R.id.imageToolbar1);
        imageToolbar.setOnClickListener(view -> {
            startActivity(new Intent(DetalleAlquilerActivity.this, HomePropietarioActivity.class));
        });

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    private void recuperamosParametros() {
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
    }

    //descarga de imagen
    private void crearCanalNotificacion () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Descarga de imagen";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
    private void crearNotifcacion() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/SaveImage/";
        Uri uri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri,"*/*");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(LoginActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_ONE_SHOT);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_image);
        builder.setContentTitle(getResources().getString(R.string.TitleDescargaNotificacion));
        builder.setContentText(getResources().getString(R.string.TextNotificacion));
        builder.setContentInfo("4");
        builder.setTicker("Descarga!");
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(IDunica, builder.build());
    }

    //solicitamos permiso (no necesario, por flujo, primero NuevoAlquiler)
    private void askPermission () {
        ActivityCompat.requestPermissions(DetalleAlquilerActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
    }

    //obtenemos la respuesta de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
    @NonNull int[] grantResults){
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               //testsave();
            } else {
                Toast.makeText(this, getResources().getString(R.string.RequierePermisos), Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //guardamos la imagen
    private void testsave(){
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File dir = new File(file.getAbsolutePath() + "/SaveImage");
        if (!dir.exists()) {
            dir.mkdir();
        }
        BitmapDrawable draw = (BitmapDrawable)  view_img.getDrawable();
        Bitmap bitmap = draw.getBitmap();

        String fileName = String.format("%d.png", System.currentTimeMillis());
        File outFile = new File(dir, fileName);
        try {
            outputStream = new FileOutputStream(outFile);
        }catch (Exception e){
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        Toast.makeText(this, getResources().getString(R.string.TextDescargaCompletada), Toast.LENGTH_SHORT).show();
        try {
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //MAPA
    @Override
    public void onMapReady (@NonNull GoogleMap googleMap){
        mMap = googleMap;
        Bundle bundle1 = getIntent().getExtras();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        LocationManager locationManager = (LocationManager) DetalleAlquilerActivity.this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitudMap = Double.parseDouble(bundle1.getString("latitud1"));
                longitudMap = Double.parseDouble(bundle1.getString("longitud1"));
                LatLng miUbicacion = new LatLng(latitudMap, longitudMap);
                mMap.addMarker(new MarkerOptions().position(miUbicacion).title("Ac?? estoy!"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(miUbicacion));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(miUbicacion)
                        .zoom(15)
                        .bearing(0)
                        .tilt(0)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
}


