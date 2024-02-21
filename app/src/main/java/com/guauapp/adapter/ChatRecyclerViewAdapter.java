package com.guauapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guauapp.R;
import com.guauapp.model.ChatMessage;
import com.guauapp.ui.chat.ChatActivity;
import com.guauapp.ui.logIn.LogInFragment;

import java.util.List;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {
    private List<ChatMessage> chatMessageList;


    public ChatRecyclerViewAdapter(List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatMessage, rightChatMessage;
        TextView leftChatMessageText, rightChatMessageText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatMessage = itemView.findViewById(R.id.left_chat_layout);
            rightChatMessage = itemView.findViewById(R.id.right_chat_layout);
            leftChatMessageText = itemView.findViewById(R.id.left_chat_textview);
            rightChatMessageText = itemView.findViewById(R.id.right_chat_textview);
        }
    }

    @NonNull
    @Override
    public ChatRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_recycler_row, parent, false);

        return new ChatRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRecyclerViewAdapter.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessageList.get(position);

        if (chatMessage.getSenderId().equals(LogInFragment.user.getUid())) {
            holder.rightChatMessage.setVisibility(View.VISIBLE);
            holder.leftChatMessage.setVisibility(View.GONE);
            holder.rightChatMessageText.setText(chatMessage.getMessage());
        } else {
            holder.leftChatMessage.setVisibility(View.VISIBLE);
            holder.rightChatMessage.setVisibility(View.GONE);
            holder.leftChatMessageText.setText(chatMessage.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    public void updateData(List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
        notifyDataSetChanged();
    }

    public void addData() {
        notifyDataSetChanged();
    }
}