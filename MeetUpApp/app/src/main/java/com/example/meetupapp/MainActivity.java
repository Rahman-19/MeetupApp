package com.example.meetupapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * In main activity there are two tabs chats and profile
 * Chats display the list of users having common interest
 * Profile display user name
 */

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    String domain;
    private TextView appName;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private TabAdapter tabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        domain=getIntent().getStringExtra("Game");
        toolbar = findViewById(R.id.toolBarMain);
        appName = findViewById(R.id.profileName);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager2);
        tabAdapter = new TabAdapter(getSupportFragmentManager(), getLifecycle());
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance("https://meetup-app-9262f-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("Users");



        appName.setText("HYPERSTREAM APP");


        setSupportActionBar(toolbar);
        viewPager2.setAdapter(tabAdapter);


        tabLayout.addTab(tabLayout.newTab().setText("CHATS"));
        tabLayout.addTab(tabLayout.newTab().setText("PROFILE"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Logout) {
            HashMap<String,Object> map=new HashMap<>();
            map.put("Offline", ServerValue.TIMESTAMP);
            userRef.child(domain).child(mAuth.getCurrentUser().getUid()).child("Status").setValue(map);
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.FavouriteGame) {
            userRef.child("Users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Intent intent = new Intent(MainActivity.this, FavouriteGame.class);
                    intent.putExtra("userId", getIntent().getStringExtra("userId"));
                    intent.putExtra("userName", getIntent().getStringExtra("userName"));
                    intent.putExtra("userEmail", getIntent().getStringExtra("userEmail"));
                    intent.putExtra("Game",getIntent().getStringExtra("Game"));
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        HashMap<String,Object> map=new HashMap<>();
        map.put("Online", ServerValue.TIMESTAMP);
        userRef.child(domain).child(mAuth.getCurrentUser().getUid())
                .child("Status").setValue(map);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAuth.getUid()!=null){
            HashMap<String,Object> map=new HashMap<>();
            map.put("Offline", ServerValue.TIMESTAMP);
            userRef.child(domain).child(mAuth.getCurrentUser().getUid())
                    .child("Status").setValue(map);
        }
    }
}