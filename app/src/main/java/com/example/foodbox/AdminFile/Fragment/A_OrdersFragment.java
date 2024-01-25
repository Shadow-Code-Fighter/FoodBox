package com.example.foodbox.AdminFile.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodbox.AdminFile.Adapter.A_OrderAdapter;
import com.example.foodbox.AdminFile.Email.A_JavaMailAPI;
import com.example.foodbox.AdminFile.Model.A_OrderDhabaNote;
import com.example.foodbox.AdminFile.A_WrapContentLinearLayoutManager;
import com.example.foodbox.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class A_OrdersFragment extends Fragment {

    RecyclerView order_recyclerview;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference restref, useref;
    private Query query;
    private CollectionReference colref;
    private String userid, u_email;
    private A_OrderAdapter adapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentuser = mAuth.getCurrentUser();
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.a_fragment_orders, container, false);
        order_recyclerview = view.findViewById(R.id.order_recyclerview);
        order_recyclerview.setLayoutManager(new A_WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        Intent intent = getActivity().getIntent();
        userid = intent.getStringExtra("userid");
//            Toolbar toolbar = view.findViewById(R.id.toolbar);
//            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Order Details");
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //check Internet Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infos = connectivityManager.getActiveNetworkInfo();
        if (null != infos) {
            //show order details in recylerview
            colref = db.collection("admins").document(mAuth.getCurrentUser().getUid()).collection("orderdetails");
            query = colref.orderBy("timestamp", Query.Direction.DESCENDING);
            FirestoreRecyclerOptions<A_OrderDhabaNote> options = new FirestoreRecyclerOptions.Builder<A_OrderDhabaNote>()
                    .setQuery(query, A_OrderDhabaNote.class)
                    .build();

            restref = (CollectionReference) db.collection("admins").document(mAuth.getCurrentUser().getUid()).collection("orderdetails");
            colref.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                    showNotification();
                }
            });


            adapter = new A_OrderAdapter(options);
//                order_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
            order_recyclerview.setAdapter(adapter);

            //click on cancel for cancel the order
            adapter.setOnItemClickListener(new A_OrderAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                }

                //change status after clicking accept button
                @Override
                public void onAccept(DocumentSnapshot documentSnapshot, int position) {
                    A_OrderDhabaNote orderNote = documentSnapshot.toObject(A_OrderDhabaNote.class);
                    String path = documentSnapshot.getReference().getPath();
                    final String user_uid = orderNote.getUserid();
                    u_email = orderNote.getU_email();
                    final String order_id = orderNote.getOrder_id();
                    String sts = orderNote.getStatus();
                    if (sts.equals("Pending")) {
                        //change status in hotel collection
                        db.document(path).update("status", "Accepted");
                        //change staus in user collection
                        db.collection("users").document(user_uid).collection("orderdetails")
                                .whereEqualTo("order_id", order_id)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                            A_OrderDhabaNote orderDhabaNote = documentSnapshot1.toObject(A_OrderDhabaNote.class);
                                            String path2 = documentSnapshot1.getReference().getPath();
                                            db.document(path2).update("status", "Accepted");
                                            Toast.makeText(getContext(), "Order is accepted", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        //sending updated info on user mail
                        String message = "Your Order " + order_id + " is accepted";
                        //sendmail function
                        sendmail(message, u_email);
                        //create notification
                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        HashMap<String, String> chatnotification = new HashMap<>();
                        chatnotification.put("from", currentuser.getUid());
                        chatnotification.put("type", "Accept Order");
                        databaseReference.child("Notification").child(user_uid).push().setValue(chatnotification);
                    }
                }

                @Override
                public void onCancel(DocumentSnapshot documentSnapshot, int position) {
                    A_OrderDhabaNote orderNote = documentSnapshot.toObject(A_OrderDhabaNote.class);
                    String path = documentSnapshot.getReference().getPath();
                    final String user_uid = orderNote.getUserid();
                    u_email = orderNote.getU_email();

                    final String order_id = orderNote.getOrder_id();
                    String sts = orderNote.getStatus();
                    if (sts.equals("Pending") | sts.equals("Accept")) {
                        //change status in hotel collection
                        db.document(path).update("status", "Cancelled");
                        //change status in user collection
                        db.collection("users").document(user_uid).collection("orderdetails")
                                .whereEqualTo("order_id", order_id)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                            A_OrderDhabaNote orderDhabaNote = documentSnapshot1.toObject(A_OrderDhabaNote.class);
                                            String path2 = documentSnapshot1.getReference().getPath();
                                            db.document(path2).update("status", "Cancelled");
                                            Toast.makeText(getContext(), "Order is Cancelled", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        //sending updated info on user mail
                        String message = "Sorry, Your Order " + order_id + " is Cancel because foods are not available";
                        //sendmail function
                        sendmail(message, u_email);
                        //create notification
                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        HashMap<String, String> chatnotification = new HashMap<>();
                        chatnotification.put("from", currentuser.getUid());
                        chatnotification.put("type", "Cancel Order");
                        databaseReference.child("Notification").child(user_uid).push().setValue(chatnotification);
                    }
                }

                @Override
                public void onDelivery(DocumentSnapshot documentSnapshot, int position) {
                    A_OrderDhabaNote orderNote = documentSnapshot.toObject(A_OrderDhabaNote.class);
                    String path = documentSnapshot.getReference().getPath();
                    final String user_uid = orderNote.getUserid();
                    u_email = orderNote.getU_email();

                    final String order_id = orderNote.getOrder_id();
                    String sts = orderNote.getStatus();
                    if (sts.equals("Accepted")) {
                        db.document(path).update("status", "Delivered");
                        db.collection("users").document(user_uid).collection("orderdetails")
                                .whereEqualTo("order_id", order_id)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                            A_OrderDhabaNote orderDhabaNote = documentSnapshot1.toObject(A_OrderDhabaNote.class);
                                            String path2 = documentSnapshot1.getReference().getPath();
                                            db.document(path2).update("status", "Delivered");
                                            Toast.makeText(getContext(), "Order is delivered", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        //sending updated info on user mail
                        String message = "Your Order " + order_id + " is delivered successfully\nThank You for Order";
                        //sendmail function
                        sendmail(message, u_email);
                        //create notification
                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        HashMap<String, String> chatnotification = new HashMap<>();
                        chatnotification.put("from", currentuser.getUid());
                        chatnotification.put("type", "Deliver Order");
                        databaseReference.child("Notification").child(user_uid).push().setValue(chatnotification);
                    }
                }

            });


        } else {
            Toast.makeText(getContext(), "Network Error: Please Check your Connection!!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void sendmail(String message, String u_email) {
        String subject = "Order Update Details";

        //Send Mail
        A_JavaMailAPI javaMailAPI = new A_JavaMailAPI(getContext(), u_email, subject, message);

        javaMailAPI.execute();
    }

//    private void showNotification() {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "default")
//                .setSmallIcon(R.drawable.a_notification)
//                .setContentTitle("New Order")
//                .setContentText("")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        notificationManager.notify(0, builder.build());
//     }


     @Override
        public void onStart() {
            super.onStart();
            try {
                adapter.startListening();
                order_recyclerview.smoothScrollToPosition(order_recyclerview.getAdapter().getItemCount());
            } catch (Exception e) {

            }
        }

        @Override
        public void onStop() {
            super.onStop();
            try {
                adapter.stopListening();
            } catch (Exception e) {

            }
        }
    }

