package com.example.nouraalrossiny.androidbottomnav;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
public class ProfileActivity extends AppCompatActivity  implements View.OnClickListener{
    private static final int CHOOSE_IMAGE = 101;

    TextView UserNameDisplay;
    ProgressBar progressBar;

    FirebaseAuth mAuth;

    FirebaseDatabase database;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();


        progressBar = findViewById(R.id.profile_progressbar);
        progressBar.setVisibility(View.GONE);

        findViewById(R.id.deleteAccount_Profile).setOnClickListener(this);
        findViewById(R.id.user_Logout).setOnClickListener(this);
        findViewById(R.id.BacktoHome).setOnClickListener(this);

        loadUserInformation();

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }


    private void loadUserInformation() { //retriev userName and photo

    }


 /*   private void saveUserInformation() {   //if user change

        String displayName = UserNameDisplay.getText().toString();

        if (displayName.isEmpty()) { //GET NAME FROM USER
            UserNameDisplay.setError("Name required");
            UserNameDisplay.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && profileImageUrl != null) { //have name and photo -->update
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "تم تحديث الحساب بنجاح", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    */


    private void delete() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid= mAuth.getCurrentUser().getUid();
            DatabaseReference us = FirebaseDatabase.getInstance().getReference("Users").child(uid);
            us.removeValue();
            progressBar.setVisibility(View.VISIBLE);
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "تم حذف الحساب بنجاح", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deleteAccount_Profile:
                delete();
                finish();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                break;
            case R.id.BacktoHome:
                startActivity(new Intent(this, MainActivity.class));

            case R.id.user_Logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, MainActivity.class));

        }
    }
}


