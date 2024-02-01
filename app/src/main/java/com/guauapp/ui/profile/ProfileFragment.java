package com.guauapp.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
        dogsDAO.getBreedsIdAsync().thenAccept(breedList -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, breedList);
            binding.spnBreed.setAdapter(adapter);
        });
    }

    private void setProvinces() {
         dogsDAO.getProvincesAsync().thenAccept(provinceList ->{
             binding.spnProvince.setAdapter(new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_activated_1, provinceList));
             binding.spnProvince.setOnItemSelectedListener(this);
         });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Province province = (Province) binding.spnProvince.getSelectedItem();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, province.getCities());
        binding.spnCities.setAdapter(adapter);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void onClick(View view) {
        EditText dogName =  binding.txvDogName;
        String breed = binding.spnBreed.getSelectedItem().toString();
        EditText descrption = binding.txvDescrption;
        EditText owenerName = binding.txvOwenerName;

        String province = binding.spnProvince.getSelectedItem().toString();
        String location = binding.spnCities.getSelectedItem().toString();
        ArrayList tags = new ArrayList();
        Dog dog = new Dog(MainActivity.user.getUid(),
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