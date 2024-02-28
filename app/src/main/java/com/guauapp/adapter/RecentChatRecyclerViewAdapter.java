package com.guauapp.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guauapp.R;
import com.guauapp.model.ChatMessage;
import com.guauapp.model.Chatroom;
import com.guauapp.model.Dog;
import com.guauapp.ui.chat.ChatActivity;
import com.guauapp.ui.logIn.LogInFragment;

import java.util.List;

public class RecentChatRecyclerViewAdapter extends RecyclerView.Adapter<RecentChatRecyclerViewAdapter.ViewHolder> {
    private List<Chatroom> chatroomsList;
    private List<Dog> usersList;


    public RecentChatRecyclerViewAdapter(List<Chatroom> chatroomsList, List<Dog> usersList) {
        this.chatroomsList = chatroomsList;
        this.usersList = usersList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName_textView);
        }
    }

    @NonNull
    @Override
    public RecentChatRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_chat_recycler_row, parent, false);

        return new RecentChatRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentChatRecyclerViewAdapter.ViewHolder holder, int position) {
        Chatroom chatroom = chatroomsList.get(position);
        List<String> usersInChatroom = chatroom.getUsersId();
        String otherUserId;

        // Comprueba el indice de la lista donde se encuentra el usuario actual para obtener el id del otro usuario
        if (!usersInChatroom.get(0).equals(LogInFragment.user.getUid())) {
            otherUserId = usersInChatroom.get(0);
        }else {
            otherUserId = usersInChatroom.get(1);
        }

        // Busca el otro usuario en la lista de usuarios
        for (Dog user : usersList) {
            // Comprueba si el id del otro usuario coincide con el id del usuario de la lista de usuarios
            if (otherUserId.equals(user.getId())) {
                holder.userName.setText(user.getOwner_name()); //Si coincide, muestra el nombre del otro usuario de la sala de chat

                // Crea un intent para abrir la actividad ChatActivity cuando se hace clic en un usuario
                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(holder.itemView.getContext(), ChatActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("selectedUser", user);
                    holder.itemView.getContext().startActivity(intent);
                });

                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatroomsList.size();
    }

    public void updateData(List<Chatroom> chatroomsList, List<Dog> usersList) {
        this.chatroomsList = chatroomsList;
        this.usersList = usersList;
        notifyDataSetChanged();
    }

    public void addData() {
        notifyDataSetChanged();
    }
}