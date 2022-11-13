package com.example.alquilab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.alquilab.databinding.ActivityAlquilerofertaDetailBinding;

public class AlquilerOfertaDetailHostActivity extends AppCompatActivity {

    private Toolbar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        ActivityAlquilerofertaDetailBinding binding = ActivityAlquilerofertaDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_alquileroferta_detail);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    public void setActionBar(Toolbar actionBar) {
        this.actionBar = actionBar;
    }

    public void onClick(View view) {
        startActivity(new Intent(this,NuevoAlquiler.class));
    }
}