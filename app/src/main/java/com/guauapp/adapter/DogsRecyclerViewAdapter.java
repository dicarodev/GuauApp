package com.guauapp.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.guauapp.R;
import com.guauapp.model.Dog;

import java.io.File;
import java.io.IOException;
import java.util.List;

// Adaptador personalizado para gestionar la visualización de la lista de perros en un RecyclerView.
public class DogsRecyclerViewAdapter extends RecyclerView.Adapter<DogsRecyclerViewAdapter.ViewHolder> {

    private List<Dog> dogList; // Lista que contiene los datos de perros a mostrar en el RecyclerView
    private NavController navController; // NavController utilizado para la navegación entre fragmentos

    public DogsRecyclerViewAdapter(List<Dog> dogList, NavController navController) {
        this.dogList = dogList;
        this.navController = navController;
    }

    // Clase interna estática que representa la vista de cada elemento en el RecyclerView.
    // Extiende RecyclerView.ViewHolder para proporcionar una referencia a cada vista de ítem.
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgDog; // ImageView para mostrar la imagen del perro
        private final TextView txtLocationDog; // TextView para mostrar la ubicación del perro
        private final TextView txtNameDog; // TextView para mostrar el nombre del perro
        private final ImageView imgDogGender; // ImageView para mostrar el género del perro (icono)
        private final TextView txtBreedDog; // TextView para mostrar la raza del perro

        // Constructor que inicializa las vistas del ViewHolder
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Asignar las vistas del layout a las variables del ViewHolder
            this.imgDog = itemView.findViewById(R.id.img_cardImage);
            this.txtNameDog = itemView.findViewById(R.id.txv_cardName);
            this.imgDogGender = itemView.findViewById(R.id.img_cardImageGender);
            this.txtLocationDog = itemView.findViewById(R.id.txv_cardCity);
            this.txtBreedDog = itemView.findViewById(R.id.txv_cardBreed);
        }

        // Métodos para obtener las vistas desde fuera de la clase
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

        public ImageView getImgDogGender() {
            return imgDogGender;
        }
    }

    // Crea y devuelve una nueva instancia de ViewHolder cuando sea necesario,
    // inflando el diseño del elemento de la lista a partir de un recurso de diseño específico.
    @NonNull
    @Override
    public DogsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla la vista del elemento de la lista desde el archivo de diseño cardview_dog_profile
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_dog_profile, parent, false);

        // Devuelve una nueva instancia de ViewHolder asociada a la vista del elemento de la lista
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DogsRecyclerViewAdapter.ViewHolder holder, int position) {
        Dog dog = dogList.get(position);

        // Asigna los valores del perro a las vistas en el ViewHolder
        holder.getTxtNameDog().setText(dog.getDog_name());
        holder.getTxtLocationDog().setText(dog.getLocation());
        holder.getTxtBreedDog().setText(dog.getBreed());
        // Establece el sexo del perro
        if (dog.getGender() != null && !dog.getGender().isEmpty()) {
            if (dog.getGender().equalsIgnoreCase("Macho"))
                holder.getImgDogGender().setImageResource(R.drawable.male);
            else if (dog.getGender().equalsIgnoreCase("Hembra"))
                holder.getImgDogGender().setImageResource(R.drawable.female);
            else
                holder.getImgDogGender().setVisibility(View.INVISIBLE);
        } else {
            holder.getImgDogGender().setVisibility(View.INVISIBLE);
            // Establecer marginStart a 0 (cero)
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.getImgDogGender().getLayoutParams();
            params.setMarginStart(0);
        }

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
            // Si no hay imágenes, cambia el tamaño a wrap_content
            holder.getImgDog().getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        // Actualizar el tamaño de ImageView
        holder.getImgDog().requestLayout();

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

}
