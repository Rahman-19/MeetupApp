package com.example.meetupapp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * In this class user can chat with another common interest user
 */
public class MessageActivity extends AppCompatActivity {
    private TextView textViewName,lastSeen;
    private DatabaseReference userRef,chatRef;
    private FirebaseAuth mAuth;
    private String recevierId,recevierName;
    private EditText editSendMessage;
    private ImageButton imageSendButton;
    private RecyclerView mrecyclerView;
    private MessageAdapter messageAdapter;
    public static String domain;
    public static ArrayList<String> messages,messagePosition,messageId;
    private ChildEventListener childevent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MessageActivity", "On create loaded");
        setContentView(R.layout.activity_message);
        textViewName=findViewById(R.id.textViewName);
        lastSeen=findViewById(R.id.lastSeen);
        mrecyclerView=findViewById(R.id.recyclerView);
        userRef=FirebaseDatabase.getInstance("https://meetup-app-9262f-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("Users");
        chatRef=FirebaseDatabase.getInstance("https://meetup-app-9262f-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("Chats");
        mAuth=FirebaseAuth.getInstance();
        messages=new ArrayList<>();
        messagePosition=new ArrayList<>();
        messageId=new ArrayList<>();
        messageAdapter=new MessageAdapter(MessageActivity.this,messages);
        editSendMessage=findViewById(R.id.editSendMessage);
        imageSendButton=findViewById(R.id.imageSendButton);

        mrecyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
        mrecyclerView.setAdapter(messageAdapter);

       Intent intent=getIntent();
       recevierId=intent.getStringExtra("RecevierId");
       recevierName=intent.getStringExtra("RecevierName");
       domain=intent.getStringExtra("Game");
       textViewName.setText(recevierName);



       userRef.child(domain).child(recevierId).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot!=null && snapshot.hasChild("Status"))
               {
                   if(snapshot.child("Status").hasChild("Online")){
                       lastSeen.setText("Online");

                   }
                   else{
                       Object objTimeStamp=snapshot.child("Status").child("Offline").getValue();
                       SimpleDateFormat df=new SimpleDateFormat("EEE,dd-MM-yyyy");
                       String lastSeenString=df.format(objTimeStamp);
                       String currentDateString=df.format(new Date());
                       Date currentDate=null,lastSeenDate=null;
                       try {
                           currentDate = df.parse(currentDateString);
                           lastSeenDate=df.parse(lastSeenString);
                       }
                       catch (ParseException e){
                           e.printStackTrace();
                       }
                       if(currentDate.compareTo(lastSeenDate)==0){
                           String time=new SimpleDateFormat("h:mm a").format(objTimeStamp);
                           lastSeen.setText("last Seen : "+time);
                       }
                       else {
                           String date=new SimpleDateFormat("yyyy-MM-dd").format(objTimeStamp);
                           lastSeen.setText("last Seen : "+date);
                       }
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });


       imageSendButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Log.i("MessageActivity", "Called on click");
               if(editSendMessage.getText().toString().equals(""))
               {
                   Toast.makeText(MessageActivity.this, "Enter something", Toast.LENGTH_SHORT).show();
               }
               else
               {
                   HashMap<String,String> map=new HashMap<>();
                   map.put("RecevierId",recevierId);
                   map.put("Messages",editSendMessage.getText().toString());
                   map.put("SenderId",mAuth.getCurrentUser().getUid());
                   chatRef.child(domain).push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           /*userRef.child(mAuth.getCurrentUser().getUid()).child("ChatLists").
                                   child(recevierId).setValue(ServerValue.TIMESTAMP);
                           userRef.child(recevierId).child("ChatLists").child(mAuth.getCurrentUser().getUid())
                                   .setValue(ServerValue.TIMESTAMP);*/
                           editSendMessage.setText("");
                       }
                   });
                   Log.i("MessageActivity", "Sent msg success");
               }
           }
       });
        Log.i("MessageActivity", "Going to call getMessage");
            getMessage();

    }


    public void getMessage()
    {
         childevent = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    if (snapshot.child("RecevierId").getValue().toString().equals(mAuth.getCurrentUser().getUid())
                            && snapshot.child("SenderId").getValue().equals(recevierId)) {
                        messages.add(snapshot.child("Messages").getValue().toString());
                        messagePosition.add("0");
                        messageId.add(snapshot.getKey());
                        messageAdapter.notifyItemInserted(messages.size() - 1);
                        mrecyclerView.smoothScrollToPosition(messages.size() - 1);


                    } else if (snapshot.child("RecevierId").getValue().toString().equals(recevierId)
                            && snapshot.child("SenderId").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
                        messages.add(snapshot.child("Messages").getValue().toString());
                        messagePosition.add("1");
                        messageId.add(snapshot.getKey());
                        messageAdapter.notifyItemInserted(messages.size() - 1);
                        mrecyclerView.smoothScrollToPosition(messages.size() - 1);
                    }
                }
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
        };

        chatRef.child(domain).addChildEventListener(childevent);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatRef.child(domain).removeEventListener(childevent);
    }
}