package com.example.foodbox.AdminFile.Tiffin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodbox.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

public class A_Tiffin_Menu extends AppCompatActivity {
    CheckBox check1,check2,check3;
    Button tiffin_upd;
    EditText b_price,l_price,d_price;
    CollectionReference addref;
    ProgressDialog pd;
    Query query;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage fstore=FirebaseStorage.getInstance();
//    StorageReference sf= storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_tiffin_menu);

        b_price=findViewById(R.id.b_price);
        l_price=findViewById(R.id.l_price);
        d_price=findViewById(R.id.d_price);

//        check1=findViewById(R.id.check1);
//        check2=findViewById(R.id.check2);
//        check3=findViewById(R.id.check3);
        tiffin_upd=findViewById(R.id.tiffin_upd);
        pd =new ProgressDialog(this);

        tiffin_upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Loading,please wait....");
                pd.show();
                insertdata();
            }
        });

        // Fetch the menu data from the database and populate the EditText fields
        fetchMenuData();
    }

    public void insertdata(){
        String breakfast = b_price.getText().toString();
        String lunch = l_price.getText().toString().trim();
        String dinner = d_price.getText().toString().trim();

        // Check if any of the fields is empty
//        if (TextUtils.isEmpty(breakfast) || TextUtils.isEmpty(lunch) || TextUtils.isEmpty(dinner)) {
//            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // Create a Map to hold the data
        Map<String,Object> menu = new HashMap<>();
        menu.put("Breakfast", breakfast);
        menu.put("Lunch", lunch);
        menu.put("Dinner", dinner);

        // Get the current user ID
        String userId = mAuth.getCurrentUser().getUid();

        // Add the menu data to the database
        db.collection("admins").document(userId)
                .collection("Tiffin").document("Menu").update(menu)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Tiffin updated successfully", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        db.collection("admins").document(userId)
                        .collection("Tiffin").document("Menu").set(menu);
                        Toast.makeText(getApplicationContext(), "Tiffin Added", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
    }

    public void fetchMenuData() {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference menuRef = db.collection("admins").document(userId)
                .collection("Tiffin").document("Menu");

        menuRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String breakfast = documentSnapshot.getString("Breakfast");
                    String lunch = documentSnapshot.getString("Lunch");
                    String dinner = documentSnapshot.getString("Dinner");

                    // Populate the EditText fields with the fetched data
                    b_price.setText(breakfast);
                    l_price.setText(lunch);
                    d_price.setText(dinner);
                }
            }
        });
    }

    private void photodel(){

    }
}
