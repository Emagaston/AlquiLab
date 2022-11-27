package com.example.alquilab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    private TextView banner;
    private EditText emailReset;
    private Button btnReset;
    private ProgressBar progressBar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        banner = findViewById(R.id.banner);
        banner.setOnClickListener(this);

        emailReset = findViewById(R.id.emailReset);
        btnReset = findViewById(R.id.btnReset);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.banner:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }

    }

    private void resetPassword() {
        String email = emailReset.getText().toString().trim();

        if (email.isEmpty()){
            emailReset.setError(getResources().getString(R.string.errorEmail));
            emailReset.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailReset.setError(getResources().getString(R.string.errorEmailValid));
            emailReset.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this, getResources().getString(R.string.ToastForgot), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(ForgotPassword.this, LoginActivity.class));
                }else {
                    Toast.makeText(ForgotPassword.this, getResources().getString(R.string.ToastErrorForgot), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ForgotPassword.this, LoginActivity.class));
    }
}