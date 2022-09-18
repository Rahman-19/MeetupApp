package com.example.meetupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    /**
     * In this class user enter his email and password, if user have account already.
     * and click login button which directs user to favourite game activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText editTextEmail=findViewById(R.id.editTextEmail);
        EditText editTextPassword=findViewById(R.id.editTextPassword);
        FirebaseAuth mAuth=FirebaseAuth.getInstance();


        DatabaseReference userRef= FirebaseDatabase.
                getInstance("https://meetup-app-9262f-default-rtdb.asia-southeast1.firebasedatabase.app").
                getReference().child("Users");
        ProgressDialog dialog=new ProgressDialog(LoginActivity.this);
        dialog.setTitle("Authentication");
        dialog.setMessage("Please wait, until Authentication finishes");
        Button loginBtn=findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextEmail.getText().toString().equals("") || editTextPassword.getText().toString()
                .equals(""))
                {
                    Toast.makeText(LoginActivity.this, "All Fields are Requiered",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog.show();
                    mAuth.signInWithEmailAndPassword(editTextEmail.getText().toString(),
                            editTextPassword.getText().toString()).addOnCompleteListener
                            (new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                String email=editTextEmail.getText().toString().toLowerCase(Locale.ROOT);
                                userRef.child(mAuth.getCurrentUser()
                                .getUid()).child("Name").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                         String Name=snapshot.getValue().toString();

                                        dialog.dismiss();

                                        Intent intent=new Intent(LoginActivity.this,FavouriteGame.class);
                                        intent.putExtra("userId",mAuth.getCurrentUser().getUid());
                                        intent.putExtra("userName",Name);
                                        intent.putExtra("userEmail",email);

                                        startActivity(intent);
                                        finish();


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                            else {

                                Toast.makeText(LoginActivity.this, "Error: "+task.getException(),
                                        Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            }
                        }
                    });
                }
            }
        });


    }
}