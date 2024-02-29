package com.guauapp.ui.friend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.guauapp.R;
import com.guauapp.adapter.ImageProfileAdapter;
import com.guauapp.databinding.FragmentFriendBinding;
import com.guauapp.model.Dog;
import com.guauapp.ui.chat.ChatActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment {

    private FriendViewModel mViewModel;
    private FragmentFriendBinding binding;
    private NavController navController;
    private ArrayList<Bitmap> photosList;
    private Dog selectedDog;
    private ImageProfileAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentFriendBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        photosList = new ArrayList<>();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Obtener información del perro del Bundle
        if (getArguments() != null) {
            selectedDog = (Dog) getArguments().getSerializable("selectedDog");

            // Actualizar la interfaz de usuario con la información del perro seleccionado
            if (selectedDog != null) {
                updateUIWithDogInfo(selectedDog);
            }
        }

        binding.floatingOpenChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //AQUI SE ABRE EL CHAT CON EL USUARIO SELECCIONADO EN EL FRAGMENTO
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("selectedUser", selectedDog);
                getContext().startActivity(intent);

            }
        });

    }

    // Método para actualizar la interfaz de usuario con la información de un perro amigo
    private void updateUIWithDogInfo(Dog friendDog) {
        // Establece el nombre del perro
        binding.txvFriendDogName.setText(friendDog.getDog_name());
        // Establece el nombre del dueño del perro
        binding.txvFriendOwnerName.setText(friendDog.getOwner_name());
        // Establece la raza del perro
        binding.txvFriendBreed.setText(friendDog.getBreed());
        // Establece la provincia del perro
        binding.txvFriendProvince.setText(friendDog.getProvince());
        // Establece la ubicación del perro
        binding.txvFriendLocation.setText(friendDog.getLocation());
        // Establece la descripción del perro
        binding.txvFriendDescription.setText(friendDog.getDescription());
        // Establece la edad del perro amigo
        binding.txvFriendAge.setText(friendDog.getAge());
        // Establece si el perro amigo está castrado
        String castrated = friendDog.getCastrated().toString().equalsIgnoreCase("true") ? "Castrado" : "No castrado";
        binding.txvFriendCastrated.setText(castrated);

        // Obtiene imágenes asociadas al perro amigo y actualiza el carrusel de imágenes
        getImages();

        // Crea y configura el carrusel de imágenes en la interfaz de usuario
        createCarousel();
    }


    // Método para obtener imágenes asociadas a un perro seleccionado
    private void getImages() {
        // Limpiar la lista de fotos antes de agregar nuevas imágenes
        photosList.clear();

        // Obtiene la lista de imágenes del perro seleccionado
        List<String> images = selectedDog.getImages();

        // Verifica si la lista de imágenes es nula o está vacía
        if (images != null && !images.isEmpty()) {
            // Itera sobre las imágenes del perro seleccionado
            images.forEach(image -> {
                // Crea una referencia al almacenamiento de Firebase para la imagen actual
                StorageReference msStorageReference = FirebaseStorage.getInstance().getReference().child(image);

                // Obtiene metadatos de la imagen actual y realiza acciones cuando se tenga éxito
                msStorageReference.getMetadata().addOnSuccessListener(storageMetadata -> {
                    // Obtiene el tipo de contenido de la imagen y extrae la extensión del tipo
                    String type = storageMetadata.getContentType().split("/")[1];
                    System.out.println(type);

                    // Intenta crear un archivo temporal para la imagen en el dispositivo
                    try {
                        // Crea un archivo temporal con el nombre y tipo de la imagen
                        File localFile = File.createTempFile(msStorageReference.getName(), type);

                        // Descarga la imagen desde Firebase Storage al archivo temporal
                        msStorageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                            // Convierte el archivo en un objeto Bitmap
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());

                            // Agrega el objeto Bitmap a la lista de fotos
                            photosList.add(bitmap);

                            // Notifica al adaptador que los datos han cambiado
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } catch (IOException e) {
                        // Lanza una excepción en caso de error al crear el archivo temporal
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

    // Método para crear y configurar un carrusel de imágenes en la interfaz de usuario
    private void createCarousel() {
        // Crea un nuevo adaptador de imágenes, proporcionándole el contexto actual y la lista de fotos
        adapter = new ImageProfileAdapter(requireContext(), photosList);

        // Configura el RecyclerView del diseño vinculado para utilizar el adaptador recién creado
        binding.friendProfileRecyclerView.setAdapter(adapter);
    }

}