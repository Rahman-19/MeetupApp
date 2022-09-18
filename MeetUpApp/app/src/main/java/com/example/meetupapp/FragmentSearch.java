package com.example.meetupapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

/**
 * This fragment contains list of users and there is search box we can search particular user
 */

public class FragmentSearch extends Fragment {
    private TextView editTextSearch;
    private ArrayAdapter arrayAdapter;
    private ArrayList<String> userName, userId;
    private ListView editListView;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    public static String domain;
    public static String game;



    public FragmentSearch() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        editListView = view.findViewById(R.id.editListView);
        userRef = FirebaseDatabase.getInstance("https://meetup-app-9262f-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        ProgressDialog pg = new ProgressDialog(getContext());
        pg.setTitle("Getting Users");
        pg.setMessage("Please Wait");
        pg.show();
        domain=getActivity().getIntent().getStringExtra("Game");

        userName = new ArrayList<>();
        userId = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, userName);
        editListView.setAdapter(arrayAdapter);

        editListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), MessageActivity.class);
                intent.putExtra("RecevierName", userName.get(position));
                intent.putExtra("RecevierId", userId.get(position));
                intent.putExtra("Game",domain);
                startActivity(intent);
            }
        });


        userRef.child(domain).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists() && !snapshot.getKey().equals(mAuth.getCurrentUser().getUid())) {
                    userName.add(snapshot
                            .child("Name").getValue().toString());
                    userId.add(snapshot.getKey());
                }
                arrayAdapter.notifyDataSetChanged();
                pg.dismiss();
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


        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    private void searchUser(String s) {
        userName.clear();
        userId.clear();
        arrayAdapter.notifyDataSetChanged();
        userRef.child(domain)
                .orderByChild("Name").startAt(s).endAt(s + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userName.clear();
                        userId.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnap : snapshot.getChildren()) {
                                if (!userSnap.getKey().equals(mAuth.getCurrentUser().getUid())) {
                                    userName.add(userSnap.child("Name").getValue().toString());
                                    userId.add((userSnap.getKey()));
                                }

                            }
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}
