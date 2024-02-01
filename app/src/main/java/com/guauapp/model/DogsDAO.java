package com.guauapp.model;

import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DogsDAO {
    DatabaseReference mDatabase;

    public DogsDAO() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public List<Province> getProvinces() {
        List<Province> provinceList = new ArrayList<>();
        mDatabase.child("provinces").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Province province = productSnapshot.getValue(Province.class);
                    provinceList.add(province);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return provinceList;
    }

    public CompletableFuture<List<String>> getBreedsAsync() {
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
}
