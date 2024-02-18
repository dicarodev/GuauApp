package com.guauapp.ui.chat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.guauapp.ChatRecyclerViewAdapter;
import com.guauapp.R;
import com.guauapp.model.ChatDAO;
import com.guauapp.model.ChatMessage;
import com.guauapp.model.Chatroom;
import com.guauapp.ui.logIn.LogInFragment;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {
    EditText chat_messageInput;
    ImageButton btn_sendMessage;
    RecyclerView chatMessages_recyclerView;
    ChatRecyclerViewAdapter chatRecyclerViewAdapter;
    ChatDAO chatDAO;
    Chatroom chatroom;
    String chatroomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatDAO = new ChatDAO();
        chatroomId = chatDAO.getChatroomId(LogInFragment.user.getUid(), "j9A1HUxBmDhgYIj4F0euK1HfvDh2");

        chat_messageInput = findViewById(R.id.chat_messageInput);
        btn_sendMessage = findViewById(R.id.btn_sendMessage);
        chatMessages_recyclerView = findViewById(R.id.chatMessages_recyclerView);

    }

    @Override
    protected void onStart() {
        super.onStart();
        btn_sendMessage.setOnClickListener(v -> {
            String message = chat_messageInput.getText().toString().trim();

            if (message.isEmpty()) {
                return;
            }else {
                sendMessageToUser(message);
            }
        });

        getOrCreateChatroom();

        // Llamar al método para cargar los mensajes
        loadChatMessages();
    }

    private void loadChatMessages() {
        chatDAO.getChatMessagesAsync(chatroomId).thenAccept(chatMessageList -> {
            if (chatRecyclerViewAdapter == null) {
                // Si el adaptador es nulo, crea uno nuevo y configura el RecyclerView
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setStackFromEnd(true);
                chatMessages_recyclerView.setLayoutManager(layoutManager);
                chatRecyclerViewAdapter = new ChatRecyclerViewAdapter(chatMessageList);
                chatMessages_recyclerView.setAdapter(chatRecyclerViewAdapter);
            } else {
                // Si el adaptador ya existe, actualiza los datos
                chatRecyclerViewAdapter.updateData(chatMessageList);
            }
        });
    }

    private void getOrCreateChatroom() {

        chatDAO.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            System.out.println(task.getResult().getValue());
            if (task.isSuccessful()) {
                chatroom = task.getResult().getValue(Chatroom.class);
                if (chatroom == null) {
                    chatroom = new Chatroom(
                            chatroomId,
                            Arrays.asList(LogInFragment.user.getUid(), "j9A1HUxBmDhgYIj4F0euK1HfvDh2"),
                            System.currentTimeMillis(),
                            ""
                    );

                    chatDAO.getChatroomReference(chatroomId).setValue(chatroom);
                }
            }
        });
    }

    private void sendMessageToUser(String message) {

        chatroom.setLastMessageTimestamp(System.currentTimeMillis());
        chatroom.setLastMessageSenderId(LogInFragment.user.getUid());
        //chatDAO.getChatroomReference(chatroomId).setValue(chatroom);

        ChatMessage chatMessage = new ChatMessage(message, LogInFragment.user.getUid(), System.currentTimeMillis());

        chatDAO.getChatroomMessageReference(chatroomId).push().setValue(chatMessage).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chat_messageInput.setText("");
                chatMessages_recyclerView.scrollToPosition(chatMessages_recyclerView.getAdapter().getItemCount() - 1);
            }
        });
    }
}