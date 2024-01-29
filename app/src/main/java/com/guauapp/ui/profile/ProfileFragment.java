package com.guauapp.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.guauapp.databinding.FragmentProfileBinding;
import com.guauapp.model.Dog;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://guauapp-29679-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.txvDogName;
        profileViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;


    }

    @Override
    public void onStart() {
        super.onStart();
        Button addDogBtn = binding.btnAddDog;
        addDogBtn.setOnClickListener(this::onClick);
    }

    private void onClick(View view) {
        addDog();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void addDog(){
        // Crear un objeto Dog con información específica
        Dog dog = new Dog(4321, "tobi","manolo","Pastor alemán","Ciudad real","Alcázar", new ArrayList<>(), "vacia");

        // Generar una clave única para el nuevo perro en la base de datos
        String key = mDatabase.child("dogs").push().getKey();
        // Establecer el valor del nuevo perro en la base de datos utilizando la clave generada
        mDatabase.child("dogs").child(key).setValue(dog)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Perro añadido", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Fallo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}