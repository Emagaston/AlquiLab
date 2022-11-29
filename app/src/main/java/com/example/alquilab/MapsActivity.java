package com.example.alquilab;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.alquilab.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.OutputStream;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int REQUEST_CODE = 1;
    private Toolbar mToolbar;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private double latitud;
    private double longitud;
    private Button btn_save;
    String nomp, desp,dirp,barp,habp,prep;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //recupero los datos enviados por NuevoAlquiler
        //para setearlos de nuevo al seleccionar la ubicacion
        Bundle bundle  = getIntent().getExtras();
        if(bundle !=null){
            nomp =  bundle.getString("nomp");
            desp =  bundle.getString("desp");
            dirp =  bundle.getString("dirp");
            barp =  bundle.getString("barp");
            habp =  bundle.getString("habp");
            prep =  bundle.getString("prep");
        }

        mToolbar = findViewById(R.id.toolbar);

        //maps
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cargarPreferencias();

        //Listener del boton guardar
        //con los datos, vuelvo a NuevoAlquiler
        View.OnClickListener saveUbication = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, NuevoAlquiler.class);
                intent.putExtra("latitud",latitud);
                intent.putExtra("longitud", longitud);
                intent.putExtra("nomp", nomp);
                intent.putExtra("desp", desp);
                intent.putExtra("dirp", dirp);
                intent.putExtra("barp", barp);
                intent.putExtra("habp", habp);
                intent.putExtra("prep", prep);
                startActivity(intent);
                finish();
            }
        };

        btn_save = (Button)findViewById(R.id.btn_save);
        btn_save.setOnClickListener(saveUbication);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //verifico si tengo los permisos concedidos.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
           && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //no tengo el permiso
           return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        LocationManager  locationManager = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                //recupero latitud y longitud
                latitud = location.getLatitude();
                longitud = location.getLongitude();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MapsActivity.this,NuevoAlquiler.class));
        finish();
    }

}