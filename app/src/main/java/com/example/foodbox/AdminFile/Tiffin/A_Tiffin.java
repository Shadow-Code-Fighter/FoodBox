package com.example.foodbox.AdminFile.Tiffin;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodbox.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class A_Tiffin extends AppCompatActivity implements ScaleGestureDetector.OnScaleGestureListener {
    FirebaseAuth mAuth;
    ImageView menu_image;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private float scrollX, scrollY;
    TextView day;
    Button add_Tiffin,edit_Tiffin;
    private FirebaseUser currentUser;
    private ProgressDialog progressDialog;
    private static final int PICK_IMAGE = 1;
    Uri imageUri;
    private UploadTask uploadTask;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_tiffin);

        menu_image = findViewById(R.id.menu_image);
        scaleGestureDetector = new ScaleGestureDetector(this,this);
        add_Tiffin=findViewById(R.id.Add_Tiffin);
        edit_Tiffin=findViewById(R.id.Edit_Tiffin);
        //Zoom in/out
//        menu_image.setScaleX(2f);
//        menu_image.setScaleY(2f);
        // Show day
        day=findViewById(R.id.day);
        String day1 = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());

        day.setText(day1);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        storageReference = FirebaseStorage.getInstance().getReference().child("Admin Tiffin Menu");
        progressDialog = new ProgressDialog(this);
        db.collection("admins").document(mAuth.getCurrentUser().getUid()).collection("Tiffin").document("Menu").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Picasso.get().load(documentSnapshot.getString("Tiffin Image")).into(menu_image);

            }
        });

        edit_Tiffin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit_tiffin=new Intent(getApplicationContext(), A_Tiffin_Menu.class);
                startActivity(edit_tiffin);
                finish();
            }
        });

        add_Tiffin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TiffinMenu(A_Tiffin.this);
            }
        });
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        progressDialog.setTitle("Set Menu Image");
        progressDialog.setMessage("please wait..Your Tiffin image is updating");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
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
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        db.collection("admins").document(mAuth.getCurrentUser().getUid()).collection("Tiffin").document("Menu").update("Tiffin Image", mUri);
                        progressDialog.dismiss();
                    } else {
                        String massage = task.getException().toString();
                        Toast.makeText(getApplicationContext(), "" + massage, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "No Image Selected", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getApplicationContext(), "Upload in Progress", Toast.LENGTH_LONG).show();
            } else {
                uploadImage();
            }
        }
    }
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float deltaX = event.getX() - scrollX;
            float deltaY = event.getY() - scrollY;
            menu_image.scrollBy((int) -deltaX, (int) -deltaY);
        }
        scrollX = event.getX();
        scrollY = event.getY();
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        scaleFactor *= scaleGestureDetector.getScaleFactor();
        scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));
        menu_image.setScaleX(scaleFactor);
        menu_image.setScaleY(scaleFactor);
        return true;
    }
    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
    }
    private void TiffinMenu(A_Tiffin tiffin){
        AlertDialog.Builder builder= new AlertDialog.Builder(tiffin);
        builder.setTitle("Add Tiffin Menu");
        builder.setMessage("Are you sure to add Menu Image ? ");
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
}