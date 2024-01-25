package com.example.foodbox.AdminFile.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foodbox.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class A_forgot_pswd extends AppCompatActivity {
    private Button login, reset;
    private EditText txtEmail;

    private String mail;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_forgot_pswd);
        txtEmail=findViewById(R.id.Ad_email);
        login=findViewById(R.id.loginbtn);
        reset=findViewById(R.id.resetlink);

        mAuth=FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(A_forgot_pswd.this, A_Admin_Login.class);
                startActivity(i);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void validateData() {
        mail=txtEmail.getText().toString();
        if(mail.isEmpty()){
            txtEmail.setError("It is Required Fields");
        }else{
            forgetpass();
        }
    }

    private void forgetpass() {
        mAuth.sendPasswordResetEmail(mail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(A_forgot_pswd.this,"Check Your Email",Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(A_forgot_pswd.this, A_Admin_Login.class);
                            startActivity(i);
                            finish();
                        }else {
                            Toast.makeText(A_forgot_pswd.this, "Error :"+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}