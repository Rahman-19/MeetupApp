package com.example.meetupapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * This class used,
 * if it is current user it holds message right hand side
 * if it is another person having common interest it holds message left hand side
 * user can delete only his message by selecting his message.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context mcontext;
    private ArrayList<String> messages;
    private DatabaseReference chatRef;

    public MessageAdapter(Context mcontext,ArrayList<String> messages) {
        this.mcontext = mcontext;
        this.messages=messages;
        chatRef=FirebaseDatabase.getInstance("https://meetup-app-9262f-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("Chats");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mcontext).inflate(R.layout.message_item,parent,false);
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
         String pos=MessageActivity.messagePosition.get(position);
         if(pos.equals("0"))
         {
             holder.txtLeft.setVisibility(View.VISIBLE);
             holder.txtRight.setVisibility(View.GONE);
             holder.txtLeft.setText(messages.get(position));
         }
         else
         {
             holder.txtRight.setVisibility(View.VISIBLE);
             holder.txtLeft.setVisibility(View.GONE);
             holder.txtRight.setText(messages.get(position));
         }
         holder.txtRight.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 AlertDialog.Builder alert=new AlertDialog.Builder(mcontext);
                 alert.setTitle("Delete Message");
                 alert.setMessage("Are you sure? Wheather you need to delete this message "+messages.get(position));
                 alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                          holder.txtRight.setVisibility(View.GONE);
                          chatRef.child(MessageActivity.domain).child(MessageActivity.messageId.get(position)).removeValue();
                     }
                 });
                 alert.show();
             }
         });
         return;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtLeft,txtRight;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLeft=itemView.findViewById(R.id.txtLeft);
            txtRight=itemView.findViewById(R.id.txtRight);

        }
    }
}
