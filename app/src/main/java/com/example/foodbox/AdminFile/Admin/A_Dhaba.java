package com.example.foodbox.AdminFile.Admin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodbox.AdminFile.Adapter.A_MenuAdapter;
import com.example.foodbox.AdminFile.Model.A_MenuNote;
import com.example.foodbox.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class A_Dhaba extends AppCompatActivity {

    Button Add_button, Edit_button;
    private String userid, foodName,restPath, rest_name, u_email_id;
    RecyclerView menu_recyclerview;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference restref;
    private Query query;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    A_MenuAdapter adapter1;
//    A_Dhaba adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_dhaba);
        final Intent data = getIntent();
        userid = data.getStringExtra("userid");
        menu_recyclerview = findViewById(R.id.menu_recyclerview);
//        enableSwipeToDelete(menu_recyclerview,adapter1);

//        getSupportActionBar().setTitle("Menu");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Add_button = findViewById(R.id.add_btn);
        Edit_button = findViewById(R.id.edit_btn);
        restref = db.collection("admins").document(mAuth.getCurrentUser().getUid()).collection("Menu");


//        check Internet Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infos = connectivityManager.getActiveNetworkInfo();
        if (null != infos) {
            query = restref;
            FirestoreRecyclerOptions<A_MenuNote> options = new FirestoreRecyclerOptions.Builder<A_MenuNote>()
                    .setQuery(query, A_MenuNote.class)
                    .build();
            adapter1 = new A_MenuAdapter(options);
            menu_recyclerview.setLayoutManager(new LinearLayoutManager(this));
            menu_recyclerview.setAdapter(adapter1);

            Add_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), A_AddMenu.class);
                    startActivity(i);
                    finish();
                }
            });


            Edit_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //create view for Alertbox
                    View view= LayoutInflater.from(A_Dhaba.this).inflate(R.layout.a_edit_menu,null);
                    final EditText new_price = view.findViewById(R.id.new_price);
                    final Spinner menu_list = view.findViewById(R.id.menu_list);
                    final ArrayList<String> items = new ArrayList<String>();
                    items.add("select food name");
                    //fetch menu for firestore database
                    db.collection("admins").document(mAuth.getCurrentUser().getUid()).collection("Menu")
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        A_MenuNote note = documentSnapshot.toObject(A_MenuNote.class);
                                        String f_name = note.getMenu_name();
                                        items.add(f_name);
                                    }
                                    //set menu into spinner
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, items);
                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    menu_list.setAdapter(arrayAdapter);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                    //get selected menu item from spinner
                    menu_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            foodName = menu_list.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    AlertDialog.Builder builder = new AlertDialog.Builder(A_Dhaba.this);
                    builder.setTitle("Change/Edit Food Price")
                            .setView(view)
                            .setCancelable(false)
                            .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final Integer price = Integer.parseInt(new_price.getText().toString());
                                    if (new_price.getText().toString().isEmpty()) {
                                        Toast.makeText(getApplicationContext(), "New Price Empty,Please Try Again", Toast.LENGTH_SHORT).show();
                                    } else if (foodName.equals("select food name")) {
                                        Toast.makeText(getApplicationContext(), "Please select food", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //fetch path for selected food name
                                        db.collection("admins").document(mAuth.getCurrentUser().getUid()).collection("Menu")
                                                .whereEqualTo("menu_name", foodName).get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                            String path = documentSnapshot.getReference().getPath();
                                                            //Update selected food  Price
                                                            db.document(path).update("price", price);
                                                            Toast.makeText(getApplicationContext(), "Menu Update Successfully", Toast.LENGTH_LONG).show();
                                                        }

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(), "Please select food", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            })
                            .setNegativeButton("no", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Network Error: Please Check your Connection!!", Toast.LENGTH_SHORT).show();
        }
    }


//    public void enableSwipeToDelete(final RecyclerView recyclerView, final A_MenuAdapter adapter1) {
//        ItemTouchHelper.SimpleCallback swipeToDeleteCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                int position = viewHolder.getAdapterPosition();
//                adapter1.deleteItem(viewHolder.getAdapterPosition());
//                m_price();
//            }
//
//
//
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);
//    }



        @Override
    protected void onStart() {
        super.onStart();
        try {
            adapter1.startListening();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            adapter1.stopListening();
        } catch (Exception e) {

        }
    }

}