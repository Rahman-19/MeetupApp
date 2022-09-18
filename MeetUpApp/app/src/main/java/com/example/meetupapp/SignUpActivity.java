package com.example.meetupapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;

/**
 * In this class user enter his name,email and password to create account
 * if the user already have account he will select already have account option
 * will direct user to login page.
 */
public class SignUpActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    DatabaseReference userRef= FirebaseDatabase.getInstance("https://meetup-app-9262f-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        EditText editTextName=findViewById(R.id.editTextName);
        EditText editTextEmail=findViewById(R.id.editTextEmail);
        EditText editTextPassword=findViewById(R.id.editTextPassword);
        TextView alreadyHaveAccount=findViewById(R.id.editAlreadyAccount);
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button regBtn=findViewById(R.id.registerBtn);
        mAuth=FirebaseAuth.getInstance();
        DatabaseReference userRef= FirebaseDatabase.getInstance("https://meetup-app-9262f-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("Users");
        ProgressDialog dialog=new ProgressDialog(SignUpActivity.this);
        dialog.setTitle("Authentication");
        dialog.setMessage("Please wait, until Authentication finishes");
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextName.getText().toString().equals("") || editTextEmail.getText().toString().equals("")
                || editTextPassword.getText().toString().equals(""))
                {
                    Toast.makeText(SignUpActivity.this,"All Fields are Required",Toast.LENGTH_SHORT)
                    .show();
                }
                else {
                    dialog.show();
                    mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(),editTextPassword.
                            getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                String email=editTextEmail.getText().toString().toLowerCase(Locale.ROOT);
                                HashMap<String,Object> map=new HashMap<>();
                                map.put("Name",editTextName.getText().toString());
                                map.put("Email",email);

                                userRef.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
                                Toast.makeText(SignUpActivity.this,"Register Successful",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(SignUpActivity.this,FavouriteGame.class);
                                intent.putExtra("userId",mAuth.getCurrentUser().getUid());
                                intent.putExtra("userName",editTextName.getText().toString());
                                intent.putExtra("userEmail",email);
                                startActivity(intent);
                                finish();
                                dialog.dismiss();
                            }
                            else {
                                Toast.makeText(SignUpActivity.this,"Error: "+task.getException(),
                                        Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

 @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null)
        {
            userRef.child(mAuth.getCurrentUser()
                    .getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                    String Name=snapshot.child("Name").getValue().toString();
                    String email=snapshot.child("Email").getValue().toString();


                    Intent intent=new Intent(SignUpActivity.this,FavouriteGame.class);
                    intent.putExtra("userId",mAuth.getCurrentUser().getUid());
                    intent.putExtra("userName",Name);
                    intent.putExtra("userEmail",email);
                    startActivity(intent);
                    finish();}
                }
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
 }