package com.example.foodbox.AdminFile;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodbox.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class A_Gallery extends AppCompatActivity {

    private GridView mGridView;
    private List<String> mImageUrlList;
    private A_GalleryAdapter mGalleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_gallery);

        mGridView = findViewById(R.id.gridView);
        mImageUrlList = new ArrayList<>();
        mGalleryAdapter = new A_GalleryAdapter(this, mImageUrlList);
        mGridView.setAdapter(mGalleryAdapter);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("FoodBox");

        storageRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mImageUrlList.add(uri.toString());
                                    mGalleryAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(A_Gallery.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
