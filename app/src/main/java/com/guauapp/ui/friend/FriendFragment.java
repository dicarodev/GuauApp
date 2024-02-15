package com.guauapp.ui.friend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.guauapp.R;
import com.guauapp.databinding.FragmentFriendBinding;
import com.guauapp.model.Dog;
import com.guauapp.model.DogsDAO;
import com.guauapp.ui.home.HomeFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FriendFragment extends Fragment {

    private FriendViewModel mViewModel;
    private FragmentFriendBinding binding;
    private NavController navController;
    private ArrayList<Bitmap> photosList;
    //private ImageProfileAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentFriendBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        photosList = new ArrayList<>();

        // Obtener información del perro del Bundle
        if (getArguments() != null) {
            Dog selectedDog = (Dog) getArguments().getSerializable("selectedDog");

            // Actualizar la interfaz de usuario con la información del perro seleccionado
            if (selectedDog != null) {
                updateUIWithDogInfo(selectedDog);
            }
        }

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
    }

    private void updateUIWithDogInfo(Dog friendDog) {
        binding.txvProfileDogName.setText(friendDog.getDog_name());
        binding.txvProfileOwnerName.setText(friendDog.getOwner_name());
        binding.txvProfileBreed.setText(friendDog.getBreed());
        binding.txvProfileProvince.setText(friendDog.getProvince());
        binding.txvProfileLocation.setText(friendDog.getLocation());
        binding.txvProfileDescription.setText(friendDog.getDescription());
        binding.txvProfileAge.setText(friendDog.getAge());
        binding.txvProfileCastrated.setText(friendDog.getCastrated());
        /*getImages();
        createCarousel();*/
    }

    /*private void getImages() {
        friendDog.getImages().forEach(image -> {
            StorageReference msStorageReference = FirebaseStorage.getInstance().getReference().child(image);
            msStorageReference.getMetadata().addOnSuccessListener(storageMetadata -> {
                String type =  storageMetadata.getContentType().split("/")[1];
                System.out.println(type);
                try {
                    File localFile = File.createTempFile(msStorageReference.getName(), type);
                    msStorageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        photosList.add(bitmap);
                        if(adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }*/

    /*private void createCarousel() {
        adapter = new ImageProfileAdapter(this.getContext(),photosList);
        binding.profileRecyclerView.setAdapter(adapter);
    }*/
}