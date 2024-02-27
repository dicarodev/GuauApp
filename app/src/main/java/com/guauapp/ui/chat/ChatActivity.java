package com.guauapp.ui.chat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.guauapp.R;
import com.guauapp.adapter.ChatRecyclerViewAdapter;
import com.guauapp.model.ChatDAO;
import com.guauapp.model.ChatMessage;
import com.guauapp.model.Chatroom;
import com.guauapp.model.Dog;
import com.guauapp.ui.logIn.LogInFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private EditText chat_messageInput;
    private ImageButton btn_sendMessage;
    private RecyclerView chatMessages_recyclerView;
    private ChatRecyclerViewAdapter chatRecyclerViewAdapter;
    private ChatDAO chatDAO;
    private Chatroom chatroom;
    private String chatroomId;
    private Dog selectedDog;
    private NotificationCompat.Builder notificationBuilder;
    private int notificationId = 0;
    private List<String> loadedMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatDAO = new ChatDAO();

        selectedDog = (Dog) getIntent().getExtras().get("selectedUser"); //Recibir el usuario seleccionado para la conversación

        chatroomId = chatDAO.getChatroomId(LogInFragment.user.getUid(), selectedDog.getId()); //Obtiene el id de la sala de chat

        chat_messageInput = findViewById(R.id.chat_messageInput);
        btn_sendMessage = findViewById(R.id.btn_sendMessage);
        chatMessages_recyclerView = findViewById(R.id.chatMessages_recyclerView);


    }

    @Override
    protected void onStart() {
        super.onStart();

        // Evento de clic del botón para enviar un mensaje
        btn_sendMessage.setOnClickListener(v -> {
            String message = chat_messageInput.getText().toString().trim();

            if (message.isEmpty()) {
                return;
            } else {
                sendMessageToUser(message);
            }
        });

        getOrCreateChatroom(); //Recuperar o crea la sala de chat
        loadChatMessages(); //Cargar todos los mensajes de la sala de chat
        loadNewMessages(); //Cargar nuevos mensajes en la sala de chat
    }

    private void loadChatMessages() {
        chatDAO.getChatMessagesAsync(chatroomId).thenAccept(chatMessageList -> {
            if (chatRecyclerViewAdapter == null) {

                for (ChatMessage chatMessage : chatMessageList) {
                    loadedMessages.add(String.valueOf(chatMessage.getTimestamp()));
                }

                // Si el adaptador es nulo, crea uno nuevo y configura el RecyclerView
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setStackFromEnd(true);
                chatMessages_recyclerView.setLayoutManager(layoutManager);
                chatRecyclerViewAdapter = new ChatRecyclerViewAdapter(chatMessageList);
                chatMessages_recyclerView.setAdapter(chatRecyclerViewAdapter);

                chatRecyclerViewAdapter.addData();
            } else {
                // Si el adaptador ya existe, actualiza los datos
                chatRecyclerViewAdapter.updateData(chatMessageList);
            }
        });
    }

    private void loadNewMessages() {
        chatDAO.getChatroomMessageReference(chatroomId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Nuevo mensaje agregado
                ChatMessage newMessage = snapshot.getValue(ChatMessage.class);
                if (!loadedMessages.contains(String.valueOf(newMessage.getTimestamp()))) {
                    loadedMessages.add(String.valueOf(newMessage.getTimestamp())); // Agregar el ID del mensaje cargado

                    // Metodo para enviar notificaciones
                    /*if (newMessage.getSenderId().equalsIgnoreCase(selectedDog.getId())) {

                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("selectedUser", selectedDog);
                        intent.putExtra("userId", LogInFragment.user.getUid());
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

                        notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), MainActivity.CHANNEL_ID)
                                .setSmallIcon(R.drawable.home_icon)
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText(newMessage.getMessage())
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);

                        // Enviar notificación al usuario
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        notificationManager.notify(notificationId, notificationBuilder.build());
                        notificationId++;
                    }*/
                }

                chatRecyclerViewAdapter.addData();
                // Desplazar a la última posición
                chatMessages_recyclerView.smoothScrollToPosition(chatRecyclerViewAdapter.getItemCount() - 1);
            }
            // Otros métodos de ChildEventListener...
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
    }

    private void getOrCreateChatroom() {

        chatDAO.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            System.out.println(task.getResult().getValue());
            if (task.isSuccessful()) {
                chatroom = task.getResult().getValue(Chatroom.class);
                if (chatroom == null) {
                    chatroom = new Chatroom(
                            chatroomId,
                            Arrays.asList(LogInFragment.user.getUid(), selectedDog.getId()),
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
        //chatDAO.getChatroomReference(chatroomId).setValue(chatroom); //No se utiliza ya que resetea el nodo

        ChatMessage chatMessage = new ChatMessage(message, LogInFragment.user.getUid(), System.currentTimeMillis());

        chatDAO.getChatroomMessageReference(chatroomId).push().setValue(chatMessage).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chat_messageInput.setText("");
                chatMessages_recyclerView.scrollToPosition(chatMessages_recyclerView.getAdapter().getItemCount() - 1);
            }
        });
    }
}