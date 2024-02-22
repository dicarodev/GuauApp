package com.guauapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.guauapp.adapter.DogsRecyclerViewAdapter;
import com.guauapp.R;
import com.guauapp.databinding.FragmentHomeBinding;
import com.guauapp.model.Dog;
import com.guauapp.model.DogsDAO;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;  // Objeto de enlace para el diseño de HomeFragment
    private NavController navController;
    private List<Dog> dogsList = new ArrayList<>();  // Lista para almacenar nombres de perros
    private DogsDAO dogsDAO = new DogsDAO();
    private RecyclerView recyclerView;  // RecyclerView para mostrar la lista de perros
    private RecyclerView.LayoutManager rvLayoutManager;  // LayoutManager para el RecyclerView
    private RecyclerView.Adapter rvAdapter;  // Adaptador para el RecyclerView
    public static Dog selectedDog = null; // Perro seleccionado en el RecyclerView


    // Este método se llama cuando se crea el fragmento
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Crea una instancia de HomeViewModel utilizando ViewModelProvider
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Infla el diseño para este fragmento usando enlace de datos
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;  // Devuelve la vista raíz del fragmento
    }

    // Este método se llama cuando el fragmento está a punto de hacerse visible
    @Override
    public void onStart() {
        super.onStart();

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);

        getDogs();
    }

    public void getDogs() {
        dogsDAO.getDogsAsync().thenAccept(dogs -> {
            dogsList.clear();
            dogs.forEach(dog -> {
                dogsList.add(dog);
            });
            configureRecyclerView();
        });
    }

    // Método para configurar y preparar el RecyclerView
    private void configureRecyclerView() {
        recyclerView = binding.recyclerViewDogs;  // Obtiene la referencia al RecyclerView desde el enlace

        // Crea un LinearLayoutManager para el RecyclerView
        rvLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(rvLayoutManager);

        // Crea un adaptador para el RecyclerView y establece el adaptador en el RecyclerView
        rvAdapter = new DogsRecyclerViewAdapter(dogsList, navController);
        recyclerView.setAdapter(rvAdapter);

    }

    // Este método se llama cuando el fragmento está a punto de ser destruido
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Establece el objeto de enlace como nulo para evitar pérdidas de memoria
    }

}
