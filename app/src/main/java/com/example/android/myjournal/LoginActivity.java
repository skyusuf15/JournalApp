package com.example.android.myjournal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText, passEditText;
    Button loginbtn, moveToRegister;

    // string fields
    String emailEditString, passEditString;

    // Firebase Authentication fields
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginbtn = (Button) findViewById(R.id.loginbtn);
        moveToRegister = (Button) findViewById(R.id.moveToRegister);

        emailEditText = (EditText) findViewById(R.id.loginEmailEditText);
        passEditText = (EditText) findViewById(R.id.loginPasswordEditText);


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(LoginActivity.this, ViewEntriesActivity.class));
                } else {

                }
            }
        };


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailEditString = emailEditText.getText().toString().trim();
                passEditString = passEditText.getText().toString().trim();

                if (!TextUtils.isEmpty(emailEditString) && !TextUtils.isEmpty(passEditString)) {
                    mAuth.signInWithEmailAndPassword(emailEditString, passEditString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(LoginActivity.this, ViewEntriesActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "Sign in failed!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        moveToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}
