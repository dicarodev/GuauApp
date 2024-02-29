package com.guauapp.model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.guauapp.ui.logIn.LogInFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DogsDAO {
    DatabaseReference mDatabase;

    public DogsDAO() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public CompletableFuture<List<Province>> getProvincesAsync() {
        List<Province> provinceList = new ArrayList<>();
        CompletableFuture<List<Province>> future = new CompletableFuture<>();
        mDatabase.child("provinces").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Province province = productSnapshot.getValue(Province.class);
                    provinceList.add(province);
                }
                future.complete(provinceList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return future;
    }

    public CompletableFuture<List<String>> getBreedsIdAsync() {
        List<String> breedList = new ArrayList<>();
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        mDatabase.child("breed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Breed breed = productSnapshot.getValue(Breed.class);
                    breedList.add(breed.getName());
                }
                future.complete(breedList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return future;
    }

    // Obetener los id de los perros para comprobar si existen
    public CompletableFuture<List<String>> getDogsIdAsync() {
        List<String> dogsList = new ArrayList<>();
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        mDatabase.child("dogs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Dog dog = productSnapshot.getValue(Dog.class);
                    dogsList.add(dog.getId());
                }
                future.complete(dogsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return future;
    }

    // Obetener los perros
    public CompletableFuture<List<Dog>> getDogsAsync() {
        List<Dog> dogsList = new ArrayList<>();
        CompletableFuture<List<Dog>> future = new CompletableFuture<>();
        mDatabase.child("dogs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Dog dog = productSnapshot.getValue(Dog.class);
                    dogsList.add(dog);
                }
                future.complete(dogsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return future;
    }

    // Obetener perro por id
    public CompletableFuture<Dog> getDogByIdAsync(String id) {
        CompletableFuture<Dog> future = new CompletableFuture<>();
        mDatabase.getDatabase().getReference("dogs").orderByChild("id").equalTo(id).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            Dog dog = productSnapshot.getValue(Dog.class);
                            future.complete(dog);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        return future;
    }

    public List<Dog> getfilterListDog(List<Dog> allDogs, String gender, String castrado, String provincia, String localidad) {
        List<Dog> filterDogList = new ArrayList<>();
        for (Dog dog : allDogs) {
            if ((gender.isEmpty() || gender.equalsIgnoreCase(dog.getGender()))
                    && (castrado.isEmpty() || castrado.equalsIgnoreCase(String.valueOf(dog.getCastrated())))
                    && (provincia.isEmpty() || provincia.equalsIgnoreCase(dog.getProvince()))
                    && (localidad.equalsIgnoreCase("Selecciona provincia") || localidad.equalsIgnoreCase(dog.getLocation()) || localidad.isEmpty())
                    && (!dog.getId().equals(LogInFragment.user.getUid()))
            ) {
                filterDogList.add(dog);
            }
        }
        return filterDogList;
    }


}

