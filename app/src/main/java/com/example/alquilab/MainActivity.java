package com.example.alquilab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Tag;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register, forgotPassword;
    private EditText editEmailLogin, editpasswordLogin;
    private Button btnLogin;

    private FirebaseAuth mAuth;

    private ProgressBar progressBar;

    private FirebaseDatabase db =FirebaseDatabase.getInstance();
    private DatabaseReference users = db.getReference().child("Users");
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private String rol="", rol2;
    private User user2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        register = findViewById(R.id.register);
        register.setOnClickListener(this);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);


        editEmailLogin = findViewById(R.id.emailLogin);
        editpasswordLogin = findViewById(R.id.passwordLogin);

        progressBar = findViewById(R.id.progressBar);

        forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);

        cargarPreferencias();
    }


    private void cargarPreferencias() {
        SharedPreferences sharedPreferences = getSharedPreferences("Opcion",Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("opcion","");
        if (language.equals("Español")) {
            Locale idiom_es = new Locale("es", "ES");
            Locale.setDefault(idiom_es);
            Configuration config_es = new Configuration();
            config_es.locale = idiom_es;
            getBaseContext().getResources().updateConfiguration(config_es, getBaseContext().getResources().getDisplayMetrics());
        }else{
            if (language.equals("English")) {
                Locale idiom_en = new Locale("en", "EN");
                Locale.setDefault(idiom_en);
                Configuration config_en = new Configuration();
                config_en.locale = idiom_en;
                getBaseContext().getResources().updateConfiguration(config_en, getBaseContext().getResources().getDisplayMetrics());
            } else {
                if (language.equals("French")) {
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register:
                startActivity(new Intent(this,RegisterUser.class));
                break;
            case R.id.btnLogin:
                userLogin();
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
        }
    }

    private void userLogin() {
        String emailLogin = editEmailLogin.getText().toString().trim();
        String passLogin = editpasswordLogin.getText().toString().trim();

        if (emailLogin.isEmpty()){
            editEmailLogin.setError(getResources().getString(R.string.errorEmail));
            editEmailLogin.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailLogin).matches()){
            editEmailLogin.setError(getResources().getString(R.string.errorEmailValid));
            editEmailLogin.requestFocus();
            return;
        }
        if (passLogin.isEmpty()){
            editpasswordLogin.setError(getResources().getString(R.string.errorPassword));
            editpasswordLogin.requestFocus();
            return;
        }
        if (passLogin.length() < 6){
            editpasswordLogin.setError(getResources().getString(R.string.errorPasswordValid));
            editpasswordLogin.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(emailLogin,passLogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    mDatabase.child("Users").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            }
                            else {
                                user2 = task.getResult().getValue(User.class);
                                rol = user2.getRol();
                                if (rol.equals("2")){
                                    startActivity(new Intent(MainActivity.this, HomePropietario.class));
                                    finish();
                                }else{
                                    Toast.makeText(MainActivity.this, getResources().getString(R.string.ToastPropietario), Toast.LENGTH_LONG).show();
                                    FirebaseAuth.getInstance().signOut();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                    /*progressBar.setVisibility(View.GONE);*/
                    editEmailLogin.setText("");
                    editpasswordLogin.setText("");
                    //finish();
                }else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.ToastLogin), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
//        if ((user != null)&(rol == "")){
//            mDatabase.child("Users").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DataSnapshot> task) {
//                    if (!task.isSuccessful()) {
//                        Log.e("firebase", "Error getting data", task.getException());
//                    }
//                    else {
//                        user2 = task.getResult().getValue(User.class);
//                        rol2 = user2.getRol();
//                        if (rol2.equals("2")){
//                            startActivity(new Intent(MainActivity.this, HomePropietario.class));
//                        }else{
//                            Toast.makeText(MainActivity.this, getResources().getString(R.string.ToastPropietario), Toast.LENGTH_LONG).show();
//                            FirebaseAuth.getInstance().signOut();
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    }
//                }
//            });
//        }
        if (user != null){
            startActivity(new Intent(MainActivity.this,HomePropietario.class));
            finish();
        }
    }
}