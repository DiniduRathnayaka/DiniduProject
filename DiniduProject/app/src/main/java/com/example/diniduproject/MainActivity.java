package com.example.diniduproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.diniduproject.model.UserDetail;
import com.example.diniduproject.util.CustomToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText emailId,firstName,lastName,password;
    Button btnSignUp;
    TextView tvSignIn;

    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailId= findViewById(R.id.editTextEmail);
        firstName= findViewById(R.id.editTextFirstName);
        lastName= findViewById(R.id.editTextLastName);
        password= findViewById(R.id.editTextPassword);
        btnSignUp =findViewById(R.id.signUpButton);
        tvSignIn= findViewById(R.id.textViewSignIn);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
              final String email= emailId.getText().toString();
              final String fname= firstName.getText().toString();
                final String lname= lastName.getText().toString();
                final String pwd= password.getText().toString();

                if(email.isEmpty()) {
                    emailId.setError("Please Provide emial id");
                    emailId.requestFocus();
                }
                else if(fname.isEmpty()){
                    firstName.setError("Please Provide your first name");
                    firstName.requestFocus();

                }
                else if (lname.isEmpty()){
                    lastName.setError("Please Provide your last name");
                    lastName.requestFocus();
                }
                else if (pwd.isEmpty()) {
                    password.setError("Please Provide password");
                    password.requestFocus();
                }else if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.createUserWithEmailAndPassword(email,pwd)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete( @NonNull Task<AuthResult> task ) {

                                    if ((!task.isSuccessful())){
                                        CustomToast.creatToast(MainActivity.this,
                                                "SignUp Unsuccessful, Please Try Again !"
                                        + task.getException().getMessage(),true);
                                    }else{
                                        UserDetail userDetail = new UserDetail(fname,lname);
                                        String uid= task.getResult().getUser().getUid();
                                        firebaseDatabase.getReference(uid).setValue(userDetail)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess( Void unused ) {
                                                        Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        intent.putExtra("name",fname+" \n "+lname);
                                                        startActivity(intent);
                                                    }
                                                });
                                    }
                                }
                            });
                }else{
                    CustomToast.creatToast(MainActivity.this,"Error Occurred !",true);
                }


            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                Intent i = new Intent(MainActivity.this,SignIn.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

    }
}