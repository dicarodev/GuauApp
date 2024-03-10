package com.guauapp.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.guauapp.R;
import com.guauapp.adapter.ImageProfileAdapter;
import com.guauapp.databinding.FragmentProfileBinding;
import com.guauapp.model.Dog;
import com.guauapp.model.DogsDAO;
import com.guauapp.ui.logIn.LogInFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private DogsDAO dogsDAO;
    private Dog dog;
    private ArrayList<Bitmap> photosList;
    private ImageProfileAdapter adapter;
    private static final int GALLERY_INTENT = 1;
    private HashMap<StorageReference, Uri> photosReferences;
    private StorageReference mStrorage;

    private ArrayList<String> userImages;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        photosList = new ArrayList<>();
        View root = binding.getRoot();
        dogsDAO = new DogsDAO();
        getDog();
        binding.btnAddPhoto.setOnClickListener(this::addPhoto);
        mStrorage = FirebaseStorage.getInstance().getReference();
        photosReferences = new HashMap<>();
        userImages = new ArrayList<>();
        return root;
    }

    private void addPhoto(View view) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, GALLERY_INTENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    photosList.add(bitmap);

                    if (adapter == null) {
                        adapter = new ImageProfileAdapter(this.getContext(), photosList);
                        binding.profileRecyclerView.setAdapter(adapter);
                    }
                    adapter.notifyDataSetChanged();

                    StorageReference filePath = mStrorage.child("img").child(uri.getLastPathSegment());
                    photosReferences.put(filePath, uri);
                    insertPhotosIntoStorage();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "URI de imagen nula", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void insertPhotosIntoStorage() {
        for (StorageReference filePath : photosReferences.keySet()) {
            userImages.add(filePath.getPath());
            filePath.putFile(photosReferences.get(filePath));
        }
        List<String> localImages = dog.getImages();
        try {
            localImages.forEach(i -> userImages.add(i));
        } catch (Exception e) {

        }
        dog.setImages(userImages);
        addPhotos();
    }

    public void addPhotos() {
        // Generar una clave única para el nuevo perro en la base de datos
        FirebaseUser user = LogInFragment.user;
        // Establecer el valor del nuevo perro en la base de datos utilizando la clave generada
        mDatabase.child("dogs").child(user.getUid()).setValue(dog);
    }


    private void getDog() {
        dogsDAO.getDogByIdAsync(LogInFragment.user.getUid()).thenAccept(dog -> {
            this.dog = dog;
            if (this.dog != null) {
                updateUIWithDogInfo();
            }
        });
    }

    private void updateUIWithDogInfo() {
        binding.txvProfileDogName.setText(dog.getDog_name());
        binding.txvProfileOwnerName.setText(dog.getOwner_name());
        binding.txvProfileBreed.setText(dog.getBreed());
        binding.txvProfileProvince.setText(dog.getProvince());
        binding.txvProfileLocation.setText(dog.getLocation());
        binding.txvProfileDescription.setText(dog.getDescription());
        binding.txvProfileAge.setText(dog.getAge());
        String castrated = dog.getCastrated().equalsIgnoreCase("true") ? "Castrado" : "No castrado";
        binding.txvProfileCastrated.setText(castrated);
        if (dog.getGender() != null && !dog.getGender().isEmpty()) {
            if (dog.getGender().equalsIgnoreCase("Macho"))
                binding.imgDogGender.setImageResource(R.drawable.male);
            else if (dog.getGender().equalsIgnoreCase("Hembra"))
                binding.imgDogGender.setImageResource(R.drawable.female);
            else
                binding.imgDogGender.setVisibility(View.INVISIBLE);
        } else
            binding.imgDogGender.setVisibility(View.INVISIBLE);
        getImages();
        createCarousel();
    }

    private void getImages() {
        // Obtiene la lista de imágenes del perro seleccionado
        List<String> images = dog.getImages();

        // Verifica si la lista de imágenes es nula o está vacía
        if (images != null && !images.isEmpty()) {
            images.forEach(image -> {
                StorageReference msStorageReference = FirebaseStorage.getInstance().getReference().child(image);
                msStorageReference.getMetadata().addOnSuccessListener(storageMetadata -> {
                    String type = storageMetadata.getContentType().split("/")[1];
                    System.out.println(type);
                    try {
                        File localFile = File.createTempFile(msStorageReference.getName(), type);
                        msStorageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            photosList.add(bitmap);
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        } else {
            // Si el perro no tiene imágenes, carga la imagen predeterminada desde drawable
            Bitmap defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.logo_discord);
            photosList.add(defaultImage);

            // Notifica al adaptador que los datos han cambiado
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void createCarousel() {
        adapter = new ImageProfileAdapter(this.getContext(), photosList);
        binding.profileRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
