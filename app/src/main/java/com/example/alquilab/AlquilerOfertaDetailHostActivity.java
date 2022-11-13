package com.example.alquilab;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.example.alquilab.databinding.ActivityAlquilerofertaDetailBinding;
import com.google.firebase.auth.FirebaseAuth;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void setActionBar(Toolbar actionBar) {
        this.actionBar = actionBar;
    }

    public void onClick(View view) {

        switch (view.getId()){
            case R.id.add_btn:
                startActivity(new Intent(this,NuevoAlquiler.class));
                break;
            case R.id.logout_btn:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this,MainActivity.class));
                break;
    }
}
}