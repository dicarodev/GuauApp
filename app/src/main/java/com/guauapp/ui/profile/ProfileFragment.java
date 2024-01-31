package com.guauapp.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.guauapp.MainActivity;
import com.guauapp.databinding.FragmentProfileBinding;
import com.guauapp.model.Breed;
import com.guauapp.model.Dog;
import com.guauapp.model.DogsDAO;
import com.guauapp.model.Province;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private FragmentProfileBinding binding;
    private List<Province> provinceList;
    private DogsDAO dogsDAO;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

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
        dogsDAO = new DogsDAO();
        Button addDogBtn = binding.btnAddDog;
        addDogBtn.setOnClickListener(this::onClick);
        setProvinces();
        setBreeds();
    }

    private void setBreeds() {
        dogsDAO.getBreedsAsync().thenAccept(breedList -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, breedList);

            binding.spnBreed.setAdapter(adapter);
        }).exceptionally(exception -> {
            return null;
        });
    }


    public List<String> getBreeds() {
        List<String> breedList = new ArrayList<>();
        mDatabase.child("breed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Breed breed = productSnapshot.getValue(Breed.class);
                    breedList.add(breed.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, breedList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spnBreed.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return breedList;
    }



    private void setProvinces() {
        provinceList = dogsDAO.getProvinces();

        binding.spnProvince.setAdapter(new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_activated_1, provinceList));
        binding.spnProvince.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Province province = (Province) binding.spnProvince.getSelectedItem();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, province.getCities());
        binding.spnCities.setAdapter(adapter);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void onClick(View view) {
        EditText dogName =  binding.txvDogName;
        String breed = binding.spnBreed.getSelectedItem().toString();
        EditText descrption =    binding.txvDescrption;
        EditText owenerName =    binding.txvOwenerName;

        String province = binding.spnProvince.getSelectedItem().toString();
        String location = binding.spnCities.getSelectedItem().toString();
        ArrayList tags = new ArrayList();
        Dog dog = new Dog(4321,
                            dogName.getText().toString(),
                            owenerName.getText().toString(),
                            breed,
                            province,
                            location,
                            tags,
                            descrption.getText().toString());
        addDog(dog);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void addDog(Dog dog){
        // Crear un objeto Dog con información específica


        // Generar una clave única para el nuevo perro en la base de datos
        String key = mDatabase.child("dogs").push().getKey();
        FirebaseUser user = MainActivity.user;
        // Establecer el valor del nuevo perro en la base de datos utilizando la clave generada
        mDatabase.child("dogs").child(user.getUid()).setValue(dog)
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