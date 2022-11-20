package com.example.alquilab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetalleAlquiler extends AppCompatActivity implements OnMapReadyCallback {

    Toolbar mToolbar;
    private GoogleMap mMap;
    private TextView nombre, descripcion, direccion, barrio, habitaciones, precio;
    private ImageView view_img;
    private Button btn_map;
    private Double latitudMap, longitudMap;

    String nom,barr,pre,img,det,direc,hab;

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

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            longitudMap = bundle.getDouble("longitud");
            latitudMap = bundle.getDouble("latitud");
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        LocationManager locationManager = (LocationManager) DetalleAlquiler.this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitudMap = location.getLatitude();
                longitudMap = location.getLongitude();
                LatLng miUbicacion = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(miUbicacion).title("Acá estoy!"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(miUbicacion));
                CameraPosition cameraPosition =new CameraPosition.Builder()
                        .target(miUbicacion)
                        .zoom(14)
                        .bearing(90)
                        .tilt(45)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
    }
}

