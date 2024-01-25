package com.example.foodbox.AdminFile.Admin;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodbox.AdminFile.Model.A_MenuNote;
import com.example.foodbox.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class A_AddMenu extends AppCompatActivity {
   ImageView Add_image;
   EditText menu,price;
   Button selectImage,add;
   FirebaseFirestore db = FirebaseFirestore.getInstance();
   CollectionReference addref;
   Query query;
   static final int IMAGE_REQUEST = 1;
   String userid,rest_name;
   Uri ImageUri;
   FirebaseStorage firebaseStorage;
   StorageReference storageReference;
   private FirebaseAuth mAuth=FirebaseAuth.getInstance();
   StorageTask uploadTask;
   String mUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_add_menu);
//
        menu = findViewById(R.id.food_name);
        price = findViewById(R.id.food_price);
        add = findViewById(R.id.add_button);
        selectImage = findViewById(R.id.select_img);
        Add_image = findViewById(R.id.add_image);
//        getSupportActionBar().setTitle("Add Menu");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        check Internet Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infos = connectivityManager.getActiveNetworkInfo();
        if (null != infos) {
//            final Intent data = getIntent();
//            userid = data.getStringExtra("userid");
//            rest_name = data.getStringExtra("rest_name");
            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference().child("Menu Images" );
            addref = db.collection("admins").document(mAuth.getCurrentUser().getUid()).collection("Menu");

//        selecting image from file
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menu.getText().toString().isEmpty()) {
                    menu.setError("Please Enter Food Name");
                    menu.requestFocus();
                    return;
                } else if (price.getText().toString().isEmpty()) {
                    price.setError("Please Enter Price");
                    price.requestFocus();
                    return;
                } else if (ImageUri == null) {
                    Toast.makeText(getApplicationContext(), "Please Select Image", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();

                }
            }
        });
    }else{
        Toast.makeText(getApplicationContext(), "Network Error: Please Check your Connection!!", Toast.LENGTH_SHORT).show();
     }
}

    private void uploadImage() {
        final  ProgressDialog progressDialog = new ProgressDialog(A_AddMenu.this);
       progressDialog.setTitle("Uploading...");
       progressDialog.setMessage("Please Wait...");
       progressDialog.show();
        final StorageReference fileReference = storageReference.child(menu.getText().toString()+"." + getFileExtension(ImageUri));
        uploadTask = fileReference.putFile(ImageUri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
//                        progressDialog.setCanceledOnTouchOutside(false);
                    Uri downloadUri = task.getResult();
                    mUri = downloadUri.toString();
                    String foodName = menu.getText().toString();
                    String foodPrice = price.getText().toString();
                    Integer fprice = Integer.parseInt(foodPrice);
                    A_MenuNote menuNote = new A_MenuNote(foodName,fprice,mUri);
                    addref.add(menuNote);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Menu added successfully", Toast.LENGTH_LONG).show();
//                    Intent i = new Intent(getApplicationContext(), AddMenu.class);
//                    startActivity(i);
//                    finish();
                    addMenu(A_AddMenu.this);


                } else {
                    String message = task.getException().toString();
                    Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_LONG).show();

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getFileExtension(Uri Uri) {
        ContentResolver contentResolver=getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(Uri));
    }

    private void imageChooser() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

//    call after chooses image
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_REQUEST && resultCode == RESULT_OK && data!=null &&data.getData()!=null){
            ImageUri=data.getData();
            Picasso.get().load(ImageUri).into(Add_image);
            selectImage.setText("Change Image");
        }

    }
    private void addMenu(A_AddMenu addMenu) {
        AlertDialog.Builder builder = new AlertDialog.Builder(addMenu);
        builder.setTitle("Menu");
        builder.setMessage("do you add more menu ? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(getApplicationContext(), A_AddMenu.class);
                startActivity(i);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(getApplicationContext(), A_Dhaba.class);
                startActivity(i);
                finish();
                dialog.dismiss();
            }
        });
        builder.show();
    }
}