package com.example.alquilab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.alquilab.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register, forgotPassword;
    private EditText editEmailLogin, editpasswordLogin;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private FirebaseDatabase db =FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private String rol="";
    private User user2;
    SharedPreferences sharedPreferences;
    String passLogin,emailLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        register = findViewById(R.id.register);
        forgotPassword = findViewById(R.id.forgotPassword);
        btnLogin = findViewById(R.id.btnLogin);

        forgotPassword.setOnClickListener(this);
        register.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        editEmailLogin = findViewById(R.id.emailLogin);
        editpasswordLogin = findViewById(R.id.passwordLogin);
        progressBar = findViewById(R.id.progressBar);

        cargarPreferencias();
    }

    //SharedPreferences
    //manejo de idioma
    private void cargarPreferencias() {
        sharedPreferences = getSharedPreferences("Opcion",Context.MODE_PRIVATE);
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

    //opciones Registrarse/loguearse/recuperar constraseña
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register:
                startActivity(new Intent(this,RegisterUser.class));
                break;
            case R.id.btnLogin:
                userLogin();//
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
        }
    }

    //loguearse
    private void userLogin() {

        validacionLogueo();

        //logueo firebase
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
                                //capturamos el usuario logueado para verificar el rol
                                //si es propietario, cargamos el homepropietario
                                user2 = task.getResult().getValue(User.class);
                                rol = user2.getRol();
                                if (rol.equals("2")){
                                    startActivity(new Intent(LoginActivity.this, HomePropietario.class));
                                    finish();
                                }else{
                                    //si no es propietario se desloguea y cierra.
                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.ToastPropietario), Toast.LENGTH_LONG).show();
                                    FirebaseAuth.getInstance().signOut();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                    editEmailLogin.setText("");
                    editpasswordLogin.setText("");

                }else {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.ToastLogin), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    private void validacionLogueo() {
        emailLogin = editEmailLogin.getText().toString().trim();
        passLogin = editpasswordLogin.getText().toString().trim();

        //validacion de campos email y password
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
    }

    //Manejo de sesion iniciada
    //redirecciona a home propietario
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            startActivity(new Intent(LoginActivity.this,HomePropietario.class));
            finish();
        }
    }
}