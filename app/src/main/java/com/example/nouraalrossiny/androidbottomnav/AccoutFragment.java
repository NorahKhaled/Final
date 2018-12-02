package com.example.nouraalrossiny.androidbottomnav;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccoutFragment extends Fragment implements View.OnClickListener{

    FirebaseAuth mAuth;
    EditText editTextEmail, editTextPassword;
    ProgressBar progressBar;

    public AccoutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View RootView= inflater.inflate(R.layout.fragment_accout, container, false);
        mAuth = FirebaseAuth.getInstance();

        editTextEmail =(EditText)RootView.findViewById(R.id.emailSignIn);
        editTextPassword =(EditText)RootView.findViewById(R.id.passwordlSignIn);
        progressBar =(ProgressBar)RootView.findViewById(R.id.progressbarSignIn);

        RootView.findViewById(R.id.textViewSignup).setOnClickListener(this);
        RootView.findViewById(R.id.buttonLogin).setOnClickListener(this);
        RootView.findViewById(R.id.textpass).setOnClickListener(this);

        return RootView;
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Minimum lenght of password should be 6");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //using FireBae authentication to sign in      TEST IT
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    getActivity().finish(); //after click button -->go to this activity again
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//clear all open activites and open it
                    startActivity(intent);
                }  else if(task.getException() instanceof FirebaseAuthUserCollisionException){

                    Toast.makeText(getApplicationContext(),"You are ALREADY sign in",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    @Override
    public void onStart() { //already loged in
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            getActivity().finish();//finish from this
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textViewSignup:
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                break;

            case R.id.textpass:
                startActivity(new Intent(getApplicationContext(), ForgetPassword.class));
                break;

            case R.id.buttonLogin:
                userLogin();
                break;
        }
    }
}
