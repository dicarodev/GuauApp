package com.guauapp.ui.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.guauapp.carousel.ImageAdapter;
import com.guauapp.carousel.ImageProfileAdapter;
import com.guauapp.databinding.FragmentProfileBinding;
import com.guauapp.model.Dog;
import com.guauapp.model.DogsDAO;
import com.guauapp.ui.chat.ChatViewModel;
import com.guauapp.ui.logIn.LogInFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    DogsDAO dogsDAO;
    Dog dog;
    private ArrayList<Bitmap> photosList;
    private ImageProfileAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        photosList = new ArrayList<>();
        View root = binding.getRoot();
        dogsDAO = new DogsDAO();
        getDog();
        return root;
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
        binding.txvProfileCastrated.setText(dog.getCastrated());
        getImages();
        createCarousel();
    }

    private void getImages() {
        dog.getImages().forEach(image -> {
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
    }
    private void createCarousel() {
        adapter = new ImageProfileAdapter(this.getContext(),photosList);
        binding.profileRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
