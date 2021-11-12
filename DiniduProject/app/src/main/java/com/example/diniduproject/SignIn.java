package com.example.diniduproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diniduproject.model.UserDetail;
import com.example.diniduproject.util.CustomToast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SignIn extends AppCompatActivity {

    EditText emaiId,password;
    Button btnSignIn;
    TextView tvSignUp;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    private  FirebaseAuth.AuthStateListener mAuthStateListener;

    private TextView info;
    private ImageView profile;
    private LoginButton login;

    CallbackManager callbackManager;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emaiId= findViewById(R.id.editTextEmailSignIn);
        password= findViewById(R.id.editTextPasswordSignIn);
        btnSignIn= findViewById(R.id.signInButton);
        tvSignUp=findViewById(R.id.textViewSignIn);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        info =findViewById(R.id.info);
        profile=findViewById(R.id.profile);
        login =findViewById(R.id.login);

         callbackManager =CallbackManager.Factory.create();

        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess( LoginResult loginResult ) {
                 info.setText("User Id :" +loginResult.getAccessToken().getUserId());
                 String imageURL= "https://graph.facebook.com" + loginResult.getAccessToken().getUserId() +"/picture?return_ssl_resources=1";
                Picasso.get().load(imageURL).into(profile);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError( @NonNull FacebookException e ) {

            }
        });

        mAuthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged( @NonNull FirebaseAuth firebaseAuth ) {
                FirebaseUser mFirebaseUser= firebaseAuth.getCurrentUser();
                if(mFirebaseUser!=null){
                    moveToHomeActivity(mFirebaseUser);
                }else{
                    CustomToast.creatToast(SignIn.this,"Please Login",false);
                }

            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                String email = emaiId.getText().toString();
                String pwd = password.getText().toString();


                if(email.isEmpty()){
                    emaiId.setError("Please Provide email");
                    emaiId.requestFocus();
                }
                else if(pwd.isEmpty()){
                    password.setError("Please Provide Password");
                    password.requestFocus();

                }else if(email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(SignIn.this,"Files are empty",Toast.LENGTH_LONG).show();

                }else  if(!(email.isEmpty() && pwd.isEmpty())){
                    firebaseAuth.signInWithEmailAndPassword(email,pwd)
                            .addOnCompleteListener(SignIn.this,
                                    new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete( @NonNull Task<AuthResult> task ) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(SignIn.this, "Login Error , Please Login In", Toast.LENGTH_LONG).show();
                                            }
                                            else{
                                                moveToHomeActivity(task.getResult().getUser());
                                            }
                                        }
                                    });

                }else{
                    Toast.makeText(SignIn.this,"Error Occurred !",Toast.LENGTH_LONG).show();

                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                Intent inSignUp = new Intent(SignIn.this,MainActivity.class);
                inSignUp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(inSignUp);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void moveToHomeActivity( FirebaseUser mFirebaseUser ) {
        firebaseDatabase.getReference().child(mFirebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange( @NonNull DataSnapshot snapshot ) {
                        UserDetail userDetail= snapshot.getValue(UserDetail.class);
                        String name = userDetail.getFirstName()+" "+userDetail.getLastName();
                        Intent i = new Intent(getApplicationContext(),HomeActivity.class);
                        CustomToast.creatToast(getApplicationContext(),"Login Successful",false);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.putExtra("name",name);
                        startActivity(i);
                    }

                    @Override
                    public void onCancelled( @NonNull DatabaseError error ) {

                    }
                });
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}