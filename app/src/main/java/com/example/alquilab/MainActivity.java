package com.example.alquilab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.database.core.Tag;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register, forgotPassword;
    private EditText editEmailLogin, editpasswordLogin;
    private Button btnLogin;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ProgressBar progressBar;
    private static final String TAG = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = findViewById(R.id.register);
        register.setOnClickListener(this);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        editEmailLogin = findViewById(R.id.emailLogin);
        editpasswordLogin = findViewById(R.id.passwordLogin);

        progressBar = findViewById(R.id.progressBar);

        forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);
        
        inicialize();
    }

    private void inicialize() {
        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    Log.w(TAG, "onAuthStateChanged -Logueado");
                    startActivity(new Intent(MainActivity.this, HomePropietario.class));
                }else{
                    Log.w(TAG,"onAuthStateChanged - Cerro sesion");
                    startActivity(new Intent(MainActivity.this,MainActivity.class));
                }

            }
        };
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
            editEmailLogin.setError("Ingrese un correo electrónico!");
            editEmailLogin.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailLogin).matches()){
            editEmailLogin.setError("Ingrese un correo electrónico válido!");
            editEmailLogin.requestFocus();
            return;
        }
        if (passLogin.isEmpty()){
            editpasswordLogin.setError("Ingrese una contraseña!");
            editpasswordLogin.requestFocus();
            return;
        }
        if (passLogin.length() < 6){
            editpasswordLogin.setError("La contraseña debe contener mas de 6 caracteres!");
            editpasswordLogin.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(emailLogin,passLogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                
                if (task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this, HomePropietario.class));
                    progressBar.setVisibility(View.GONE);
                    editEmailLogin.setText("");
                    editpasswordLogin.setText("");
                    
                }else {
                    Toast.makeText(MainActivity.this, "Error al iniciar sesion!, Por favor verifique sus datos! ", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);

                }
            }
        });
    }
}