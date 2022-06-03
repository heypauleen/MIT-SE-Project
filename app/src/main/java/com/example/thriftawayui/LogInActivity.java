package com.example.thriftawayui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LogInActivity extends AppCompatActivity {

    public Button btn_signin;
    TextView txt_signup, txt_email;
    TextInputEditText txt_pw;
    DatabaseReference databaseReference;
    ProgressBar progressBar2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_signin = findViewById(R.id.btnsignin);
        txt_signup = findViewById(R.id.signuptext);
        txt_email = findViewById(R.id.txtemail);
        txt_pw = (TextInputEditText)findViewById(R.id.txtpw);
        progressBar2 = findViewById(R.id.progressBar2);
        final FirebaseAuth firebaseAuth;

        firebaseAuth = FirebaseAuth.getInstance();



        btn_signin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                final String email = txt_email.getText().toString();
                final String password = txt_pw.getText().toString();
                if(TextUtils.isEmpty(email)){
                    txt_email.setError("Email is required.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    txt_email.setError("Password is required.");
                    return;
                }


                progressBar2.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        
                        if(task.isSuccessful()){
                            Toast.makeText(LogInActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainMenu.class));
                            finish();
                        }else{
                            Toast.makeText(LogInActivity.this, "Error " + task.getException(),Toast.LENGTH_SHORT).show();
                            progressBar2.setVisibility(View.GONE);
                        }
                    }

                });


            }
        });



        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }


}
