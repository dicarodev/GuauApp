package com.guauapp.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.guauapp.model.Dog;
import com.guauapp.ui.chat.ChatActivity;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DogsRecyclerViewAdapter extends RecyclerView.Adapter<DogsRecyclerViewAdapter.ViewHolder> {

    private List<Dog> dogList;
    private NavController navController;

    public DogsRecyclerViewAdapter(List<Dog> dogList) {
        this.dogList = dogList;
    }

    public DogsRecyclerViewAdapter(List<Dog> dogList, NavController navController) {
        this.dogList = dogList;
        this.navController = navController;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgDog;
        private final TextView txtLocationDog;
        private final TextView txtNameDog;
        private final TextView txtBreedDog;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.imgDog = itemView.findViewById(R.id.img_cardImage);
            this.txtNameDog = itemView.findViewById(R.id.txv_cardName);
            this.txtLocationDog = itemView.findViewById(R.id.txv_cardCity);
            this.txtBreedDog = itemView.findViewById(R.id.txv_cardBreed);
        }

        public ImageView getImgDog() {
            return imgDog;
        }

        public TextView getTxtNameDog() {
            return txtNameDog;
        }

        public TextView getTxtLocationDog() {
            return txtLocationDog;
        }

        public TextView getTxtBreedDog() {
            return txtBreedDog;
        }

    }

    @NonNull
    @Override
    public DogsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_dog_profile, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DogsRecyclerViewAdapter.ViewHolder holder, int position) {
        Dog dog = dogList.get(position);

        // Asigna los valores del perro a las vistas en el ViewHolder
        holder.getTxtNameDog().setText(dog.getDog_name());
        holder.getTxtLocationDog().setText(dog.getLocation());
        holder.getTxtBreedDog().setText(dog.getBreed());

        // Verificar si hay imágenes asociadas al perro
        if (dog.getImages() != null && !dog.getImages().isEmpty()) {
            // Obtener la primera imagen del perro
            String firstImage = dog.getImages().get(0);

            // Crear una referencia a Firebase Storage para la imagen actual
            StorageReference msStorageReference = FirebaseStorage.getInstance().getReference().child(firstImage);

            // Obtiene metadatos de la imagen actual y realiza acciones cuando se tenga éxito
            msStorageReference.getMetadata().addOnSuccessListener(storageMetadata -> {
                // Obtiene el tipo de contenido de la imagen y extrae la extensión del tipo
                String type = storageMetadata.getContentType().split("/")[1];

                // Intenta crear un archivo temporal para la imagen en el dispositivo
                try {
                    // Crea un archivo temporal con el nombre y tipo de la imagen
                    File localFile = File.createTempFile(msStorageReference.getName(), type);

                    // Descarga la imagen desde Firebase Storage al archivo temporal
                    msStorageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                        // Convierte el archivo en un objeto Bitmap
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());

                        // Agrega el objeto Bitmap a la foto
                        holder.getImgDog().setImageBitmap(bitmap);

                    });
                } catch (IOException e) {
                    // Lanza una excepción en caso de error al crear el archivo temporal
                    throw new RuntimeException(e);
                }
            });

        } else {
            // Si no hay imágenes, mostrar la imagen predeterminada
            holder.getImgDog().setImageResource(R.drawable.image_not_found);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crear un Bundle para enviar datos al FriendFragment
                Bundle bundle = new Bundle();
                bundle.putSerializable("selectedDog", dog);

                // Navegar al FriendFragment con el Bundle
                navController.navigate(R.id.navigation_friend, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dogList.size();
    }

    public void updateData(List<Dog> dogList) {
        this.dogList = dogList;
        notifyDataSetChanged();
    }

    // Método que devuelve la URL de la primera imagen del perro, o null si no hay imágenes
    private String getFirstImage(Dog dog) {
        List<String> images = dog.getImages();
        if (images != null && !images.isEmpty()) {
            return images.get(0); // Devuelve la primera imagen
        } else {
            return null; // No hay imágenes disponibles
        }
    }
}
