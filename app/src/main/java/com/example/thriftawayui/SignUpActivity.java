package com.example.thriftawayui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    TextInputEditText txt_pw;
    TextView txt_confirmpw, txt_email, txt_signintext;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        txt_pw = findViewById(R.id.txtpwSU);
        txt_confirmpw = findViewById(R.id.txtconfirmpasswordSU);
        txt_email = findViewById(R.id.txtemailSU);
        txt_signintext = findViewById(R.id.signIn_text);
        progressBar = findViewById(R.id.progressBar);

        Button btn_signup;
        final UserAccounts userAccounts = new UserAccounts();

        btn_signup = findViewById(R.id.btnsignup);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String password = txt_pw.getText().toString();
                String email = txt_email.getText().toString();
                String confirmpw = txt_confirmpw.getText().toString();
                //userAccounts.setPassword(password);
                // userAccounts.setEmail(email);

                if(TextUtils.isEmpty(email)){
                    txt_email.setError("Email is required.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    txt_email.setError("Password is required.");
                    return;
                }
                if(password.length() <6){
                    txt_pw.setError("Password must be >= 6 characters.");
                    return;
                }
                if(!confirmpw.equals(password)){
                    txt_confirmpw.setError("Incorrect password.");
                    return;
                }

               progressBar.setVisibility(View.VISIBLE );

                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this,"User successfully created", Toast.LENGTH_SHORT).show( );
                            startActivity(new Intent(getApplicationContext(), MainMenu.class));
                            finish();

                        } else {
                            Toast.makeText(SignUpActivity.this,"Error!" + task.getException(), Toast.LENGTH_SHORT).show( );

                        }
                    }
                });


            }
        });

        txt_signintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LogInActivity.class));
                finish();
            }
        });



    }
}
