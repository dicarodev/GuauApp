package com.guauapp.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.guauapp.adapter.RecentChatRecyclerViewAdapter;
import com.guauapp.databinding.FragmentChatBinding;
import com.guauapp.model.ChatDAO;
import com.guauapp.model.Dog;
import com.guauapp.model.DogsDAO;
import com.guauapp.ui.logIn.LogInFragment;

import java.util.List;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private RecyclerView recentChatsRecyclerView;
    private RecentChatRecyclerViewAdapter recentChatRecyclerViewAdapter;
    private String userId;
    private ChatDAO chatDAO;
    private DogsDAO dogsDAO;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        chatDAO = new ChatDAO();
        dogsDAO = new DogsDAO();
        userId = LogInFragment.user.getUid();

        recentChatsRecyclerView = binding.recyclerViewChats;

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        getUsers();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Carga los usuarios de la base de datos y recupera las salas de chat recientes del usuario
    private void getUsers(){
        dogsDAO.getDogsAsync().thenAccept(this::loadRecentChatroom);
    }

    // Recupera las salas de chat recientes y las muestra en el RecyclerView
    private void loadRecentChatroom(List<Dog> usersList) {

        chatDAO.getChatroomsAsync(userId).thenAccept(chatroomList -> {
            if (recentChatRecyclerViewAdapter == null) {

                // Si el adaptador es nulo, crea uno nuevo y configura el RecyclerView
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                recentChatsRecyclerView.setLayoutManager(layoutManager);
                recentChatRecyclerViewAdapter = new RecentChatRecyclerViewAdapter(chatroomList, usersList);
                recentChatsRecyclerView.setAdapter(recentChatRecyclerViewAdapter);

                recentChatRecyclerViewAdapter.addData();
            } else {
                // Si el adaptador ya existe, actualiza los datos
                recentChatRecyclerViewAdapter.updateData(chatroomList, usersList);
            }
        });
    }
}