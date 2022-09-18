package com.example.meetupapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * In this class user select his favourite game which directs user to main activity
 */
public class FavouriteGame extends AppCompatActivity {
    private ArrayList<String> game = new ArrayList<>();
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private DatabaseReference userRef,domain;
    private FirebaseAuth mAuth;
    private String recevierId, recevierEmail, recevierName;
    private HashMap<String,Object> map,map_domain;
    private Intent intent;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_game);
        listView = findViewById(R.id.listview);

        userRef = FirebaseDatabase.getInstance("https://meetup-app-9262f-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("Users");
        domain=FirebaseDatabase.getInstance("https://meetup-app-9262f-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("DomainName");
        map = new HashMap<>();
        map_domain=new HashMap<>();
        fab=findViewById(R.id.add_fab);
        recevierId = getIntent().getStringExtra("userId");
        recevierName = getIntent().getStringExtra("userName");
        recevierEmail = getIntent().getStringExtra("userEmail");


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FavouriteGame.this);
                alertDialogBuilder.setTitle("Enter your Favourite domain..");
                EditText editText=new EditText(FavouriteGame.this);
                alertDialogBuilder.setView(editText);

                    alertDialogBuilder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        if(!editText.getText().toString().equals("")) {
                            map_domain.put("domain", editText.getText().toString().toUpperCase(Locale.ROOT));
                            domain.child(editText.getText().toString().toUpperCase(Locale.ROOT)).
                                    updateChildren(map_domain);
                        }
                        else {
                            Toast.makeText(FavouriteGame.this, "Enter Something", Toast.LENGTH_SHORT).show();
                        }


                        }

                    });
                    alertDialogBuilder.show();

            }
        });
        domain.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                game.add(snapshot.child("domain").getValue().toString());
                arrayAdapter = new ArrayAdapter<String>(FavouriteGame.this,
                        android.R.layout.simple_list_item_1,
                        game);

                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        toolbar=findViewById(R.id.toolBarMain);
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                map.put("Name", recevierName);
                map.put("Email", recevierEmail);
                String value=game.get(i);
                userRef.child(game.get(i)).child(recevierId).
                        updateChildren(map);
                Intent intent=new Intent(FavouriteGame.this,MainActivity.class);
                intent.putExtra("userId", recevierId);
                intent.putExtra("userName", recevierName);
                intent.putExtra("userEmail", recevierEmail);
                Toast.makeText(FavouriteGame.this, ""+value, Toast.LENGTH_SHORT).show();
                intent.putExtra("Game",game.get(i));


                startActivity(intent);
                finish();

            }
        });

        /*game.add("Machine Learning");
        game.add("Artificial Intelligence");
        game.add("Web Development");
        game.add("App Development");
        game.add("Frontend Development");
        game.add("Backend Development");*/


        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    listView.setSelector(R.color.browser_actions_bg_grey);
                    FragmentSearch.game = "Machine Learning";
                    recevierId = getIntent().getStringExtra("userId");
                    recevierName = getIntent().getStringExtra("userName");
                    recevierEmail = getIntent().getStringExtra("userEmail");
                    map.put("Name", recevierName);
                    map.put("Email", recevierEmail);
                    userRef.child("Machine Learning").child(recevierId).
                            updateChildren(map);
                    Intent intent = new Intent(FavouriteGame.this, MainActivity.class);
                    intent.putExtra("userId", recevierId);
                    intent.putExtra("userName", recevierName);
                    intent.putExtra("userEmail", recevierEmail);


                    startActivity(intent);
                    finish();
                } else if (position == 1) {
                    listView.setSelector(R.color.browser_actions_bg_grey);
                    FragmentSearch.game = "Artificial Intelligence";
                    recevierId = getIntent().getStringExtra("userId");
                    recevierName = getIntent().getStringExtra("userName");
                    recevierEmail = getIntent().getStringExtra("userEmail");
                    map.put("Name", recevierName);
                    map.put("Email", recevierEmail);
                    userRef.child("Artificial Intelligence").child(recevierId).
                            updateChildren(map);


                    Intent intent = new Intent(FavouriteGame.this, MainActivity.class);
                    intent.putExtra("userId", recevierId);
                    intent.putExtra("userName", recevierName);
                    intent.putExtra("userEmail", recevierEmail);

                    startActivity(intent);
                    finish();
                } else if (position == 2) {
                    listView.setSelector(R.color.browser_actions_bg_grey);
                    FragmentSearch.game = "Web Development";
                    recevierId = getIntent().getStringExtra("userId");
                    recevierName = getIntent().getStringExtra("userName");
                    recevierEmail = getIntent().getStringExtra("userEmail");
                    map.put("Name", recevierName);
                    map.put("Email", recevierEmail);
                    userRef.child("Web Development").child(recevierId).
                            updateChildren(map);
                    Intent intent = new Intent(FavouriteGame.this, MainActivity.class);
                    intent.putExtra("userId", recevierId);
                    intent.putExtra("userName", recevierName);
                    intent.putExtra("userEmail", recevierEmail);

                    startActivity(intent);
                    finish();

                } else if (position == 3) {
                    listView.setSelector(R.color.browser_actions_bg_grey);
                    FragmentSearch.game = "App Development";
                    recevierId = getIntent().getStringExtra("userId");
                    recevierName = getIntent().getStringExtra("userName");
                    recevierEmail = getIntent().getStringExtra("userEmail");
                    map.put("Name", recevierName);
                    map.put("Email", recevierEmail);
                    userRef.child("App Development").child(recevierId).
                            updateChildren(map);
                    Intent intent = new Intent(FavouriteGame.this, MainActivity.class);
                    intent.putExtra("userId", recevierId);
                    intent.putExtra("userName", recevierName);
                    intent.putExtra("userEmail", recevierEmail);

                    startActivity(intent);
                    finish();
                } else if (position == 4) {
                    listView.setSelector(R.color.browser_actions_bg_grey);
                    FragmentSearch.game = "Frontend Development";
                    recevierId = getIntent().getStringExtra("userId");
                    recevierName = getIntent().getStringExtra("userName");
                    recevierEmail = getIntent().getStringExtra("userEmail");
                    map.put("Name", recevierName);
                    map.put("Email", recevierEmail);
                    userRef.child("Frontend Development").child(recevierId).
                            updateChildren(map);


                    Intent intent = new Intent(FavouriteGame.this, MainActivity.class);
                    intent.putExtra("userId", recevierId);
                    intent.putExtra("userName", recevierName);
                    intent.putExtra("userEmail", recevierEmail);

                    startActivity(intent);
                    finish();
                } else if (position == 5) {
                    listView.setSelector(R.color.browser_actions_bg_grey);
                    FragmentSearch.game = "Backend Development";
                    recevierId = getIntent().getStringExtra("userId");
                    recevierName = getIntent().getStringExtra("userName");
                    recevierEmail = getIntent().getStringExtra("userEmail");
                    map.put("Name", recevierName);
                    map.put("Email", recevierEmail);
                    userRef.child("Backend Development").child(recevierId).
                            updateChildren(map);


                    Intent intent = new Intent(FavouriteGame.this, MainActivity.class);
                    intent.putExtra("userId", recevierId);
                    intent.putExtra("userName", recevierName);
                    intent.putExtra("userEmail", recevierEmail);

                    startActivity(intent);
                    finish();
                }
            }
        });*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favourite, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            mAuth.signOut();
            Intent intent = new Intent(FavouriteGame.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}