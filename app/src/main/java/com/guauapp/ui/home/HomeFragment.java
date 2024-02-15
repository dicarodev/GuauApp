package com.guauapp.ui.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.guauapp.DogsRecyclerViewAdapter;
import com.guauapp.R;
import com.guauapp.databinding.FragmentHomeBinding;
import com.guauapp.model.Dog;
import com.guauapp.model.DogsDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;  // Objeto de enlace para el diseño de HomeFragment
    private NavController navController;
    private List<Dog> dogList = new ArrayList<>();  // Lista para almacenar nombres de perros
    private DogsDAO dogsDAO = new DogsDAO();
    private RecyclerView recyclerView;  // RecyclerView para mostrar la lista de perros
    private RecyclerView.LayoutManager rvLayoutManager;  // LayoutManager para el RecyclerView
    private RecyclerView.Adapter rvAdapter;  // Adaptador para el RecyclerView
    private FragmentHomeBinding binding;
    private View dialogView;
    private Dialog dialogFilter;
    private AlertDialog.Builder builder;
    private FloatingActionButton fab;


    // Este método se llama cuando se crea el fragmento
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Crea una instancia de HomeViewModel utilizando ViewModelProvider
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Infla el diseño para este fragmento usando enlace de datos
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dialogView = inflater.inflate(R.layout.filter_dialog, null, false);
        builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater_dialog = getLayoutInflater();


        fab= binding.fabFiltros;
        fab.setOnClickListener(view -> mostrardialogoPersonalizado());

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    // Este método se llama cuando el fragmento está a punto de hacerse visible
    @Override
    public void onStart() {
        super.onStart();

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        configureRecyclerView();  // Configura y prepara el RecyclerView
    }

    // Método para configurar y preparar el RecyclerView
    private void configureRecyclerView() {
        recyclerView = binding.recyclerViewDogs;  // Obtiene la referencia al RecyclerView desde el enlace

        // Crea un LinearLayoutManager para el RecyclerView
        rvLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(rvLayoutManager);

        // Crea un adaptador para el RecyclerView y establece el adaptador en el RecyclerView
        rvAdapter = new DogsRecyclerViewAdapter(dogList);
        recyclerView.setAdapter(rvAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                int position = rv.getChildAdapterPosition(child);

                if (child != null && position != RecyclerView.NO_POSITION) {
                    // Obtener el perro
                    Dog dogProfile = dogList.get(position);

                    navController.navigate(R.id.navigation_profile);
                }

                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    // Este método se llama cuando el fragmento está a punto de ser destruido
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Establece el objeto de enlace como nulo para evitar pérdidas de memoria
    }

    private void mostrardialogoPersonalizado() {

        Button btnFiltrar = dialogView.findViewById(R.id.btn_filtrar);
        Button btnBorrarFiltros = dialogView.findViewById(R.id.btn_borrarFiltros);
        Spinner spinner_raza = dialogView.findViewById(R.id.spinner_Breed);
        EditText et_edad = dialogView.findViewById(R.id.et_Age);
        RadioGroup rg_genero = dialogView.findViewById(R.id.radioGroup);
        Spinner spinner_provincia = dialogView.findViewById(R.id.spinner_Provincia);
        Spinner spinner_localidad = dialogView.findViewById(R.id.spinner_Localidad);
        Switch switch_castrado = dialogView.findViewById(R.id.switch_Castrado);

        String raza = spinner_raza.getSelectedItem().toString();
        int edad= Integer.parseInt(et_edad.getText().toString());
        int id = rg_genero.getCheckedRadioButtonId();
        String provincia = spinner_provincia.getSelectedItem().toString();
        String localidad = spinner_localidad.getSelectedItem().toString();
        boolean castrado = switch_castrado.isChecked();



        btnFiltrar.setOnClickListener(view -> {
            dialogFilter.dismiss();
        });




        dialogFilter = builder.setView(dialogView).show();

    }
}
