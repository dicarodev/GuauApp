package com.guauapp.ui.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.guauapp.DogsRecyclerViewAdapter;
import com.guauapp.R;
import com.guauapp.databinding.FragmentHomeBinding;
import com.guauapp.model.Dog;
import com.guauapp.model.DogsDAO;
import com.guauapp.model.Province;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FragmentHomeBinding binding;  // Objeto de enlace para el diseño de HomeFragment
    private NavController navController;
    private List<Dog> dogList = new ArrayList<>();  // Lista para almacenar nombres de perros
    private List<Dog> filteredDogList = new ArrayList<>();// Lista para almacenar perros filtrados
    private DogsDAO dogsDAO = new DogsDAO();
    private RecyclerView recyclerView;  // RecyclerView para mostrar la lista de perros
    private RecyclerView.LayoutManager rvLayoutManager;  // LayoutManager para el RecyclerView
    private RecyclerView.Adapter rvAdapter;  // Adaptador para el RecyclerView
    private View dialogView;
    private Dialog dialogFilter;
    private AlertDialog.Builder builder;
    private FloatingActionButton fab;
    private Button btnFiltrar;
    Button btnBorrarFiltros;
    Button btnCancelarFiltros;
    Spinner spinner_raza;
    Spinner spinner_edad;
    RadioGroup rg_genero;
    Spinner spinner_provincia;
    Spinner spinner_localidad;
    Switch switch_castrado;


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


        fab = binding.fabFiltros;
        fab.setOnClickListener(view -> mostrardialogoPersonalizado());

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
        initFilter();
        btnFiltrar.setOnClickListener(this::filterListener);
        btnBorrarFiltros.setOnClickListener(this::deleteFilterListener);
        btnCancelarFiltros.setOnClickListener(this::cancelFilterListener);
        setProvinces();
        setAges();
        setBreeds();

        //Con esto puedes cerrar el dialogo y volverlo a abrir
        ViewGroup parentViewGroup = (ViewGroup) dialogView.getParent();
        if (parentViewGroup != null) {
            parentViewGroup.removeView(dialogView);
        }

        // Ahora puedes agregar tu vista al contenedor
        dialogFilter = builder.setView(dialogView).show();

    }

    private void cancelFilterListener(View view) {
        dialogFilter.dismiss();
    }

    private void filterListener(View view) {
        String raza = spinner_raza.getSelectedItem().toString();

        int id_genero = rg_genero.getCheckedRadioButtonId();
        String edad = spinner_edad.getSelectedItem().toString();
        String provincia = spinner_provincia.getSelectedItem().toString();
        String localidad = spinner_localidad.getSelectedItem().toString();
        boolean castrado = switch_castrado.isChecked();
        String castradoStr = castrado ? "true" : "false";
        rvAdapter = new DogsRecyclerViewAdapter(filteredDogList);
        recyclerView.setAdapter(rvAdapter);
        dialogFilter.dismiss();
    }

    private void deleteFilterListener(View view) {
        spinner_raza.setSelection(0);
        spinner_edad.setSelection(0);
        rg_genero.clearCheck();
        spinner_provincia.setSelection(0);
        spinner_localidad.setSelection(0);
        switch_castrado.setChecked(false);
    }

    private void initFilter() {
        btnFiltrar = dialogView.findViewById(R.id.btn_filtrar);
        btnBorrarFiltros = dialogView.findViewById(R.id.btn_borrarFiltros);
        btnCancelarFiltros = dialogView.findViewById(R.id.btn_FilterCancel);
        spinner_raza = dialogView.findViewById(R.id.spinner_Breed);
        spinner_edad = dialogView.findViewById(R.id.spinner_Age);
        rg_genero = dialogView.findViewById(R.id.radioGroup);

        spinner_provincia = dialogView.findViewById(R.id.spinner_Provincia);
        spinner_localidad = dialogView.findViewById(R.id.spinner_Localidad);
        switch_castrado = dialogView.findViewById(R.id.switch_Castrado);
    }

    private void setAges() {
        List<String> ageList = new ArrayList<>();
        ageList.add("cachorro");
        ageList.add("adulto");
        ageList.add("senior");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_activated_1, ageList);
        spinner_edad.setAdapter(adapter);
    }

    private void setBreeds() {
        dogsDAO.getBreedsIdAsync().thenAccept(breedList -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_activated_1, breedList);
            spinner_raza.setAdapter(adapter);
        });
    }

    private void setProvinces() {
        dogsDAO.getProvincesAsync().thenAccept(provinceList -> {
            spinner_provincia.setAdapter(new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_activated_1, provinceList));
            spinner_provincia.setOnItemSelectedListener(this);
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Province province = (Province) spinner_provincia.getSelectedItem();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_activated_1, province.getCities());
        spinner_localidad.setAdapter(adapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
