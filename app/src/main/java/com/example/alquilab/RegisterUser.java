package com.example.alquilab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener{

    private TextView banner, registerUser;
    private EditText editTextRegisterName, editTextRegisterEmail, editTextRegisterPassword, editTextRegisterNumber;
    private Spinner spinnerRol;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        banner = findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser = findViewById(R.id.btnRegister);
        registerUser.setOnClickListener(this);

        editTextRegisterName = findViewById(R.id.nameRegister);
        editTextRegisterEmail = findViewById(R.id.emailRegister);
        editTextRegisterPassword = findViewById(R.id.passwordRegister);
        editTextRegisterNumber = findViewById(R.id.NumberContactRegister);

        progressBar = findViewById(R.id.progressBar);

        spinnerRol = findViewById(R.id.spinnerRol);


        ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this,R.array.OpcionesRol, R.layout.spinnerstyle);
        adapter.setDropDownViewResource(R.layout.spinnerstyle);
        spinnerRol.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.btnRegister:
                registerUser();
                break;
        }

    }

    private void registerUser() {
        String nombre = editTextRegisterName.getText().toString().trim();
        String email = editTextRegisterEmail.getText().toString().trim();
        String password = editTextRegisterPassword.getText().toString().trim();
        String number = editTextRegisterNumber.getText().toString().trim();
        String rol = spinnerRol.getSelectedItem().toString();

        if (nombre.isEmpty()){
            editTextRegisterName.setError(getResources().getString(R.string.errorName));
            editTextRegisterName.requestFocus();
            return;
        }
        if (email.isEmpty()){
            editTextRegisterEmail.setError(getResources().getString(R.string.errorEmail));
            editTextRegisterEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextRegisterEmail.setError(getResources().getString(R.string.errorEmailValid));
            editTextRegisterEmail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            editTextRegisterPassword.setError(getResources().getString(R.string.errorPassword));
            editTextRegisterPassword.requestFocus();
            return;
        }
        if (password.length() < 6){
            editTextRegisterPassword.setError(getResources().getString(R.string.errorPasswordValid));
            editTextRegisterPassword.requestFocus();
            return;
        }
        if (number.isEmpty()){
            editTextRegisterNumber.setError(getResources().getString(R.string.errorNumber));
            editTextRegisterNumber.requestFocus();
            return;
        }
        if (number.length() < 8){
            editTextRegisterNumber.setError(getResources().getString(R.string.errorNumberValid));
            editTextRegisterNumber.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            User user = new User(nombre, email, rol, number);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(RegisterUser.this, getResources().getString(R.string.ToastRegister), Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                                editTextRegisterName.setText("");
                                                editTextRegisterEmail.setText("");
                                                editTextRegisterPassword.setText("");
                                                startActivity(new Intent(RegisterUser.this,MainActivity.class));
                                            }else {
                                                Toast.makeText(RegisterUser.this, getResources().getString(R.string.ToastRegisterError), Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                            FirebaseAuth.getInstance().signOut();
                        }else {
                            Toast.makeText(RegisterUser.this, getResources().getString(R.string.ToastRegisterExistent), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegisterUser.this,MainActivity.class));
    }
}
