package com.guauapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guauapp.model.Dog;

import java.util.List;

public class DogsRecyclerViewAdapter extends RecyclerView.Adapter<DogsRecyclerViewAdapter.ViewHolder> {

    private List<Dog> dogList;

    public DogsRecyclerViewAdapter(List<Dog> dogList) {
        this.dogList = dogList;
        this.dogList.add(new Dog(55, "Tobi", "", "Golden", "Ciudad Real", "Alcazar", null, ""));
        this.dogList.add(new Dog(56, "Lana", "", "Labrador", "Ciudad Real", "Herencia", null, ""));
        this.dogList.add(new Dog(57, "Rulo", "", "Caniche", "Ciudad Real", "Tomelloso", null, ""));
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

        // Aquí asignas los valores del perro a las vistas en el ViewHolder
        //holder.getImgDog().setImageResource(dog.get);
        holder.getTxtNameDog().setText(dog.getDog_name());
        holder.getTxtLocationDog().setText(dog.getLocation());
        holder.getTxtBreedDog().setText(dog.getBreed());
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
