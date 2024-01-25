package com.example.foodbox.AdminFile.Fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.foodbox.AdminFile.Admin.A_Admin_Login;
import com.example.foodbox.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class A_AccountFragment extends Fragment {
    Button button;
    TextView textView;
    FirebaseAuth mauth;
    DatabaseReference myRef;
    FirebaseUser currentUser;
    String adminId;
    TextView acc_name,acc_phone,acc_email,acc_rest,acc_address,editName;
    FirebaseAuth mAuth;
    ImageView acc_image;
    private ProgressDialog progressDialog;
    private  static final int PICK_IMAGE=1;
    Uri imageUri;
    private UploadTask uploadTask;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.a_fragment_account, container, false);
//        textView = v.findViewById(R.id.ac_name);
        acc_name = v.findViewById(R.id.ac_name);
        acc_email = v.findViewById(R.id.ac_email);
        acc_phone =v. findViewById(R.id.ac_phone);
        acc_rest = v.findViewById(R.id.ac_rest);
        acc_address = v.findViewById(R.id.ac_address);
        acc_image =v. findViewById(R.id.Ac_image);
        TextView del_ac = v.findViewById(R.id.del_ac);
//        editName=v.findViewById(R.id.editName);
      

        del_ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteAccountdoc(A_AccountFragment.this);

            }
        });


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        storageReference= FirebaseStorage.getInstance().getReference().child("Admin Profile Image");
        progressDialog=new ProgressDialog(getContext());
        db.collection("admins").document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                acc_name.setText(documentSnapshot.getString("admin_name"));
                acc_phone.setText(documentSnapshot.getString("admin_phone"));
                acc_email.setText(documentSnapshot.getString("email"));
                acc_rest.setText(documentSnapshot.getString("Restaurant"));
                acc_address.setText(documentSnapshot.getString("Address"));
                Picasso.get().load(documentSnapshot.getString("AdminImage")).into(acc_image);

            }
        });
        acc_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileImage(A_AccountFragment.this);
            }
        });
        return v;
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(){
        progressDialog.setTitle("Set Profile Image");
        progressDialog.setMessage("please wait..Your profile image is updating");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri!=null){
            final StorageReference fileReference=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask=fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        String mUri=downloadUri.toString();
                        db.collection("admins").document(mAuth.getCurrentUser().getUid()).update("AdminImage",mUri);
                        progressDialog.dismiss();
                    }
                    else {
                        String massage=task.getException().toString();
                        Toast.makeText(getContext(),""+massage,Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            });

        }else {
            Toast.makeText(getContext(),"No Image Selected",Toast.LENGTH_LONG).show();
        }

    }
    @Override
    public   void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getContext(), "Upload in Progress", Toast.LENGTH_LONG).show();
            } else {
                uploadImage();
            }
        }

        myRef = FirebaseDatabase.getInstance().getReference("admins").child(currentUser.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (datasnapshot.exists() && (datasnapshot.hasChild("admin_name"))) {
                    String retrivename = datasnapshot.child("admin_name").getValue().toString();
                    acc_name.setText(retrivename);
                } else if (datasnapshot.exists() && (datasnapshot.hasChild("admin_name"))) {
                    String retrivename = datasnapshot.child("admin_name").getValue().toString();
                    acc_name.setText(retrivename);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        firebaseFirestore = FirebaseFirestore.getInstance();
//
//        adminId = mauth.getCurrentUser().getUid();

//        DocumentReference documentReference = firebaseFirestore.collection("admins").document(adminId);
//        documentReference.addSnapshotListener((Executor) this, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
//                assert documentSnapshot != null;
//                textView.setText(documentSnapshot.getString("admin_name"));
//            }
//        });


//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i= new Intent(getActivity(), Account_in.class);
//                startActivity(i);
//
//            }
//        });


    }
    private void ProfileImage(A_AccountFragment accountfragment){
        AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
        builder.setTitle("Add Profile Image");
        builder.setMessage("Are you sure to add Profile Image ? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();}


    private void deleteAccount() {
        DocumentReference df= db.collection("admins").document(mAuth.getCurrentUser().getUid());
            df.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Account deletion successful
                                currentUser.delete();
                                Toast.makeText(getContext(), "Account deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                // Account deletion failed
                                Toast.makeText(getContext(), "Failed to delete account", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    }
    private void deleteAccountdoc(A_AccountFragment accountfragment){
        AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
        builder.setTitle("Data Delete");
        builder.setMessage("Are you sure to delete account, all the data are deleted ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAccount();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();}

}