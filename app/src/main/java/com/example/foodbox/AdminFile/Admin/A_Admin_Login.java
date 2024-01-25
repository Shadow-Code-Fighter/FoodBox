package com.example.foodbox.AdminFile.Admin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodbox.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class A_Admin_Login extends AppCompatActivity {

    String username, pswd;
    Button Admin_Log_btn;
    CheckBox pass_show;
    ProgressDialog progressDialog;
    EditText password1, email1;
    TextView textView,forgot;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
//    private Task<Query> myRef;
//    private DatabaseReference databaseReference;

 @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent i = new Intent(getApplicationContext(), A_Admin_Dashboard.class);
            startActivity(i);
            finish();

        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_admin_login);

        Admin_Log_btn = findViewById(R.id.Admin_log_btn);
        email1 = findViewById(R.id.email);
        password1 = findViewById(R.id.password);
        textView = findViewById(R.id.user_alrdy_ac);
        progressDialog = new ProgressDialog(this);
        pass_show = findViewById(R.id.pass_show);
        forgot = findViewById(R.id.forgot);

        pass_show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    password1.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    password1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });


        mAuth = FirebaseAuth.getInstance();


        Admin_Log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage("Loading,please wait....");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);

                username = email1.getText().toString();
                pswd = password1.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    email1.setError("Enter Your Email");
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                    email1.setError("Enter Valid Email");
                    return;
                } else if (TextUtils.isEmpty(pswd)) {
                    password1.setError("Enter Your Password");
                    return;
                } else if (pswd.length() < 8) {
                    password1.setError(("Length should be >=8"));
                    return;
                }
                db.collection("admins").whereEqualTo("email",username ).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.getResult().size() > 0) {
                                    mAuth.signInWithEmailAndPassword(username, pswd)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(A_Admin_Login.this, "Authentication Successfully.",
                                                                Toast.LENGTH_SHORT).show();
                                                        Intent i = new Intent(getApplicationContext(), A_Admin_Dashboard.class);
                                                        startActivity(i);
                                                        finish();
                                                        progressDialog.dismiss();

                                                    } else {

                                                        Toast.makeText(A_Admin_Login.this, "Authentication failed.",
                                                                Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                    }
                                                }
                                            });
                                }else{
                                    Toast.makeText(A_Admin_Login.this, "Invalid Admin.",Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });


        //Go to already register page
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iHome = new Intent(A_Admin_Login.this, A_admin_register.class);
                Toast.makeText(A_Admin_Login.this, "Register Account Selected", Toast.LENGTH_SHORT).show();
                startActivity(iHome);
                finish();

            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iF= new Intent(A_Admin_Login.this, A_forgot_pswd.class);
                Toast.makeText(A_Admin_Login.this, "Forgot Password", Toast.LENGTH_SHORT).show();
                startActivity(iF);
                finish();

            }
        });
    }
}