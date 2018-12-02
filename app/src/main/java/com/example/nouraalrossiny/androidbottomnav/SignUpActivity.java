package com.example.nouraalrossiny.androidbottomnav;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.nouraalrossiny.androidbottomnav.FireBaseMethods;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignUpActivity";

    private Context mContext;
    private EditText editTextName, editTextEmail, editTextPassword, editTextPhone;
    String email,name,password;
    long phone;
    private ProgressBar progressBar;
    Button btnSignUp;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference myRef;

    private FireBaseMethods firebaseMethods;

    private FirebaseAuth.AuthStateListener mAuthListener;
    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mContext = SignUpActivity.this;
        firebaseMethods = new FireBaseMethods(mContext);

        editTextName = findViewById(R.id.nameSignUp);
        editTextEmail = findViewById(R.id.emailSignUp);
        editTextPassword = findViewById(R.id.passwordSignUp);
        editTextPhone = findViewById(R.id.phoneSignUp);
        btnSignUp = findViewById(R.id.buttonSignUp);
        Log.d(TAG, "onCreate: started.");


        progressBar = findViewById(R.id.progressbarSignUp);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.buttonSignUp).setOnClickListener(this);
        findViewById(R.id.textViewLogin).setOnClickListener(this);
        setupFirebaseAuth();
    }

    public void registerUser(){
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("ادخل اسم المستخدم");
            editTextName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("ادخل البريد الإلكتروني");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("ادخل كلمة المرور");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Minimum lenght of password should be 6");
            editTextPassword.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            editTextPhone.setError("Phone is required");
            editTextPhone.requestFocus();
            return;
        }

        if (phone.length() != 10) {
            editTextPhone.setError("enter valid phone#");
            editTextPhone.requestFocus();
            return;
        }


            progressBar.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());


                            if (task.isSuccessful()) {
                                uid= mAuth.getCurrentUser().getUid();
                                Log.d(TAG, "onComplete: Authstate changed: " + uid);
                                progressBar.setVisibility(View.INVISIBLE);
                            }else if(task.getException() instanceof FirebaseAuthUserCollisionException){

                                Toast.makeText(getApplicationContext(),"You are ALREADY registered",Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }//end Account
                    });

        }


    private boolean checkInputs(String email, String username, String password){
        Log.d(TAG, "checkInputs: checking inputs for null values.");
        if(email.equals("") || username.equals("") || password.equals("")){
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            uid = mAuth.getCurrentUser().getUid();
        }
    }


     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mfirebaseDatabase=FirebaseDatabase.getInstance(); //authintication to DB
        myRef=mfirebaseDatabase.getReference();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //add new user to DB
                            firebaseMethods.addNewUser(email,name,phone);

                            //add new user user_account_settings to DB

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSignUp:
                registerUser();
                break;

            case R.id.textViewLogin:
                finish();
           //     startActivity(new Intent(this, MainActivity.class));
                break;

        }
    }
}