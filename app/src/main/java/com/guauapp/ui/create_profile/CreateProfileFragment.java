package com.guauapp.ui.create_profile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.guauapp.R;
import com.guauapp.adapter.ImageAdapter;
import com.guauapp.databinding.FragmentCreateProfileBinding;
import com.guauapp.model.Dog;
import com.guauapp.model.DogsDAO;
import com.guauapp.model.Province;
import com.guauapp.ui.logIn.LogInFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private static final int GALLERY_INTENT = 1;
    private FragmentCreateProfileBinding binding;
    private StorageReference mStrorage;
    private DogsDAO dogsDAO;
    private RecyclerView imgRecyclerView;
    private ArrayList<String> photosList;
    private HashMap<StorageReference, Uri> photosReferences;
    private ImageAdapter adapter;
    private ArrayList<String> userImages;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CreateProfileViewModel createProfileViewModel =
                new ViewModelProvider(this).get(CreateProfileViewModel.class);

        binding = FragmentCreateProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.txvDogName;
        createProfileViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        photosList = new ArrayList<>();
        photosReferences = new HashMap<>();
        userImages = new ArrayList<>();
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
        enableBottomBar(false);
        imgRecyclerView = binding.recyclerView;
        createCarousel();

        mStrorage = FirebaseStorage.getInstance().getReference();
        binding.floatbtn.setOnClickListener(this::addPhoto);

    }

    private void createCarousel() {
        adapter = new ImageAdapter(this.getContext(),photosList);
        imgRecyclerView.setAdapter(adapter);
    }

    private void addPhoto(View view) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,GALLERY_INTENT);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                photosList.add(uri.toString());
                adapter.notifyDataSetChanged();
                StorageReference filePath = mStrorage.child("img").child(uri.getLastPathSegment());
                photosReferences.put(filePath,uri);
            } else {
                Toast.makeText(getContext(), "URI de imagen nula", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void insertPhotosIntoStorage() {
        for(StorageReference filePath : photosReferences.keySet()) {
            userImages.add(filePath.getPath());
            filePath.putFile(photosReferences.get(filePath));
        }

    }

    private void enableBottomBar(boolean enable){
        BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);
        for (int i = 0; i < navView.getMenu().size(); i++) {
            navView.getMenu().getItem(i).setEnabled(enable);
        }
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
        String castrated = "" + binding.swCastrated.isChecked();
        String age = null;
        if (binding.rdAdult.isChecked()) {
            age = "Adulto";
        } else if (binding.rdPuppy.isChecked()) {
            age = "Cachorro";
        } else if (binding.rdSenior.isChecked()) {
            age = "Senior";
        }
        insertPhotosIntoStorage();
        Dog dog = new Dog(LogInFragment.user.getUid(),
                            dogName.getText().toString(),
                            owenerName.getText().toString(),
                            breed,
                            province,
                            location,
                            descrption.getText().toString(),
                            age,
                            castrated,
                            userImages);
        addDog(dog);
        enableBottomBar(true);
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_home);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void addDog(Dog dog){

        // Generar una clave única para el nuevo perro en la base de datos
        String key = mDatabase.child("dogs").push().getKey();
        FirebaseUser user = LogInFragment.user;
        // Establecer el valor del nuevo perro en la base de datos utilizando la clave generada
        mDatabase.child("dogs").child(user.getUid()).setValue(dog);
    }
}