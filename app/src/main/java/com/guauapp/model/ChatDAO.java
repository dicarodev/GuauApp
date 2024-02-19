package com.guauapp.model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChatDAO {
    DatabaseReference mDatabase;

    public ChatDAO() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    public DatabaseReference getChatroomReference(String chatroomId) {
        return mDatabase.child("chatrooms").child(chatroomId);
    }
    public String getChatroomId(String senderId, String receiverId) {
        if (senderId.compareTo(receiverId) > 0) {
            return senderId + "_" + receiverId;
        } else {
            return receiverId + "_" + senderId;
        }
    }

    public DatabaseReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).child("messages");
    }

    public CompletableFuture<List<ChatMessage>> getChatMessagesAsync(String chatroomId) {
        List<ChatMessage> chatMessageList = new ArrayList<>();
        CompletableFuture<List<ChatMessage>> future = new CompletableFuture<>();
        getChatroomMessageReference(chatroomId).orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessageList.clear(); // Limpiar la lista antes de agregar los nuevos mensajes
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    ChatMessage chatMessage = productSnapshot.getValue(ChatMessage.class);
                    chatMessageList.add(chatMessage);
                }
                future.complete(chatMessageList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return future;
    }



}
