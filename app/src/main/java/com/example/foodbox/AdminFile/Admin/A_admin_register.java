package com.example.foodbox.AdminFile.Admin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
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

import com.example.foodbox.AdminFile.Account.A_IntroActivity;
import com.example.foodbox.AdminFile.Email.OtpVarification;
import com.example.foodbox.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class A_admin_register extends AppCompatActivity {
    TextView textView;
    Button Admin_reg_btn;
    CollectionReference clf;
    CheckBox pass_show;
    private Query query;
    EditText Admin_name, email1, Admin_phone, password1, rest_name, address;
    String Ad_name, Ad_mail, Ad_pass, Ad_phoneno, Rest_name, Address;
    private FirebaseAuth mAuth;
    private CollectionReference collectionRef;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog progressDialog;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent i = new Intent(getApplicationContext(), A_Admin_Dashboard.class);
            startActivity(i);
            finish();
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_admin_register);

        Admin_reg_btn = findViewById(R.id.admin_reg_btn);
        Admin_name = findViewById(R.id.admin_name);
        email1 = findViewById(R.id.email);
        rest_name = findViewById(R.id.rest_name);
        address = findViewById(R.id.Address);
        Admin_phone = findViewById(R.id.admin_phone);
        password1 = findViewById(R.id.password);
        textView = findViewById(R.id.admin_already_ac);
        progressDialog = new ProgressDialog(this);
        pass_show = findViewById(R.id.pass_show);


//        getSupportActionBar().setTitle("City");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        restref = db.collection("admins").document(mAuth.getCurrentUser().getUid()).collection("City");


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

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iHome = new Intent(A_admin_register.this, A_Admin_Login.class);
                Toast.makeText(A_admin_register.this, "Login Account Selected", Toast.LENGTH_SHORT).show();
                startActivity(iHome);
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
//            myRef = FirebaseDatabase.getInstance().getReference();


        Admin_reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ad_name = Admin_name.getText().toString();
                Ad_mail = email1.getText().toString();
                Ad_pass = password1.getText().toString();
                Ad_phoneno = Admin_phone.getText().toString();
                Rest_name = rest_name.getText().toString();
                Address = address.getText().toString();

                if (TextUtils.isEmpty(Ad_name)) {
                    Admin_name.setError("Enter Your Name");
                    return;
                } else if (TextUtils.isEmpty(Ad_mail)) {
                    email1.setError("Enter Your Email");
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(Ad_mail).matches()) {
                    email1.setError("Enter Valid Email");
                    return;
                } else if (TextUtils.isEmpty(Rest_name)) {
                    rest_name.setError("Enter Restaurant Name");
                    return;
                } else if (TextUtils.isEmpty(Address)) {
                    address.setError("Enter Your Address");
                    return;
                } else if (TextUtils.isEmpty(Ad_pass)) {
                    password1.setError("Enter Your Password");
                    return;
                } else if (Ad_pass.length() < 6) {
                    password1.setError(("Length should be >=6"));
                    return;
                } else if (Ad_phoneno.isEmpty()) {
                    Admin_phone.setError("Enter Your Mobile Number");
                    return;
                }else if (Ad_phoneno.length() != 10){
                    return;
                }
                createAdmin();
                progressDialog.setMessage("Loading,please wait....");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);
            }
        });
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(10);
        Admin_phone.setFilters(filters);
    }

    private void createAdmin() {

        mAuth.createUserWithEmailAndPassword(Ad_mail, Ad_pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Map<String, Object> admin = new HashMap<>();
                            admin.put("admin_name", Ad_name);
                            admin.put("email", Ad_mail);
                            admin.put("admin_phone", Ad_phoneno);
                            admin.put("Restaurant", Rest_name);
                            admin.put("Address", Address);

                            // Check if city document already exists
                            db.collection("City").whereEqualTo("CityName", Address)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                                    // City document already exists
                                                    DocumentSnapshot cityDocument = querySnapshot.getDocuments().get(0);
                                                    Intent intent=new Intent(getApplicationContext(), OtpVarification.class);
                                                    createAdminWithExistingCityDocument(admin, cityDocument.getReference());
                                                    Intent i = new Intent(getApplicationContext(), A_IntroActivity.class);
                                                    startActivity(i);
                                                    finish();
                                                    progressDialog.dismiss();
                                                } else {
                                                    // City document does not exist, create a new one
                                                    createNewCityDocumentAndAdmin(admin);

                                                }
                                            } else {
                                                // Handle query failure

                                            }
                                        }
                                    });
                        } else {
                            // Handle sign-up failure
                            Toast.makeText(A_admin_register.this, "Admin already present,Try other email", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                    }
                });

    }


    private void createAdminWithExistingCityDocument(Map<String, Object> admin, DocumentReference cityDocument) {
        db.collection("admins").document(mAuth.getCurrentUser().getUid()).set(admin)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // User created successfully
                        Toast.makeText(A_admin_register.this, "Admin Created", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                    }
                });
    }


    private void createNewCityDocumentAndAdmin(Map<String, Object> admin) {
        Map<String, Object> city = new HashMap<>();
        city.put("CityName", Address);
        db.collection("City").add(city)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        createAdminWithExistingCityDocument(admin, documentReference);
                        Intent intent = new Intent(A_admin_register.this, A_IntroActivity.class);
                        startActivity(intent);
                        finish();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                    }
                });
    }

}

