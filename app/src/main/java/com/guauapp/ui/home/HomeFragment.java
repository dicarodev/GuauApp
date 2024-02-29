package com.guauapp.ui.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.guauapp.MainActivity;
import com.guauapp.adapter.DogsRecyclerViewAdapter;
import com.guauapp.R;
import com.guauapp.adapter.DogsRecyclerViewAdapter;
import com.guauapp.databinding.FragmentHomeBinding;
import com.guauapp.model.Dog;
import com.guauapp.model.DogsDAO;
import com.guauapp.model.Province;
import com.guauapp.ui.logIn.LogInFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener {

    private FragmentHomeBinding binding;  // Objeto de enlace para el diseño de HomeFragment
    private NavController navController;
    private List<Dog> dogsList = new ArrayList<>();  // Lista para almacenar nombres de perros
    private List<Dog> filteredDogList = new ArrayList<>();// Lista para almacenar perros filtrados
    private List<Dog> listaPerrosAfiltrar = new ArrayList<>();
    private List<String> listCities = new ArrayList<>();
    private List<Province> provinceListClon = new ArrayList<>();

    private DogsDAO dogsDAO = new DogsDAO();
    private RecyclerView recyclerView;  // RecyclerView para mostrar la lista de perros
    private RecyclerView.LayoutManager rvLayoutManager;  // LayoutManager para el RecyclerView
    private RecyclerView.Adapter rvAdapter;  // Adaptador para el RecyclerView
    private View dialogView;
    private Dialog dialogFilter;
    private AlertDialog.Builder builder;
    private Button btnFiltrar;
    private ImageButton imageButtonFiltros;
    private ImageButton imageButtonCancelar;


    RadioGroup rg_genero;
    RadioButton rb_male;
    RadioButton rb_female;

    Spinner spinner_provincia;
    Spinner spinner_localidad;
    Switch switch_castrado;

    Province provinceSelected;

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

        setHasOptionsMenu(true);

        return root;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btnFloat) {
            dialogView = getLayoutInflater().inflate(R.layout.filter_dialog, null, false);
            builder = new AlertDialog.Builder(getContext());
            mostrardialogoPersonalizado();
        }
        return super.onOptionsItemSelected(item);

    }

    // Este método se llama cuando el fragmento está a punto de hacerse visible
    @Override
    public void onStart() {
        super.onStart();

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);

        getDogs();
        //configureRecyclerView();  // Configura y prepara el RecyclerView
    }

    public void getDogs() {
        dogsDAO.getDogsAsync().thenAccept(dogs -> {
            dogsList.clear();
            dogs.forEach(dog -> {
                if (!dog.getId().equals(LogInFragment.user.getUid()))
                    dogsList.add(dog);
            });
            configureRecyclerView(dogsList);
        });
    }


    // Método para configurar y preparar el RecyclerView
    private void configureRecyclerView(List<Dog> dogsList) {
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

    private void mostrardialogoPersonalizado() {
        initFilter();
        btnFiltrar.setOnClickListener(this::filterListener);
        imageButtonFiltros.setOnClickListener(this::deleteFilterListener);
        imageButtonCancelar.setOnClickListener(this::cancelFilterListener);
        setProvinces();

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
        int id_genero = rg_genero.getCheckedRadioButtonId();
        String gender;
        if (id_genero == 0) {
            gender = "Macho";
        } else {
            if (id_genero == 1) {
                gender = "Hembra";
            } else {
                gender = "";
            }
        }


        String provincia = spinner_provincia.getSelectedItem().toString();
        String localidad = spinner_localidad.getSelectedItem().toString();
        boolean castrado = switch_castrado.isChecked();
        String castradoStr = castrado ? "true" : "";

        dogsDAO.getDogsAsync().thenAccept(dogs -> {
            listaPerrosAfiltrar.clear();
            listaPerrosAfiltrar.addAll(dogs);
            filteredDogList = dogsDAO.getfilterListDog(listaPerrosAfiltrar, gender, castradoStr, provincia, localidad);
            configureRecyclerView(filteredDogList);

            dialogFilter.dismiss();

        });

    }

    private void deleteFilterListener(View view) {
        rg_genero.clearCheck();
        spinner_provincia.setSelection(0);
        spinner_localidad.setSelection(0);
        switch_castrado.setChecked(false);
    }

    private void initFilter() {
        btnFiltrar = dialogView.findViewById(R.id.btn_filtrar);
        imageButtonFiltros = dialogView.findViewById(R.id.btn_borrarFiltros);
        imageButtonCancelar = dialogView.findViewById(R.id.btn_FilterCancel);
        rg_genero = dialogView.findViewById(R.id.radioGroup);
        rb_male = dialogView.findViewById(R.id.rb_Macho);
        rb_female = dialogView.findViewById(R.id.rb_Hembra);


        spinner_provincia = dialogView.findViewById(R.id.spinner_Provincia);
        spinner_localidad = dialogView.findViewById(R.id.spinner_Localidad);
        switch_castrado = dialogView.findViewById(R.id.switch_Castrado);
        rg_genero.setOnCheckedChangeListener(this);
        //Borro todos los filtros antes de entrar
        deleteFilterListener(dialogView.getRootView());

    }

    private void setProvinces() {
        dogsDAO.getProvincesAsync().thenAccept(provinceList -> {
            //Limpio el clon de la lista
            provinceListClon.clear();
            //Al clon le añado la lista original
            provinceListClon.addAll(provinceList);
            //Creo una provincia vacía
            Province emptyProvince = new Province();
            //Le asigno el nombre vacío
            emptyProvince.setProvince("");
            //Lo añado al clon
            provinceListClon.add(0, emptyProvince);
            //Creo una lista con los nombres de las provincias
            List<String> provinceNames = new ArrayList<>();
            //Recorro la lista de provincias
            for (Province province : provinceListClon) {
                //Añado el nombre de la provincia a la lista
                provinceNames.add(province.getProvince());
            }
            //Creo un adaptador para el spinner y le asigno la lista de nombres
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_activated_1, provinceNames);
            spinner_provincia.setAdapter(adapter);
            spinner_provincia.setOnItemSelectedListener(this);
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        provinceSelected = provinceListClon.get(position);
        listCities.clear();
        if (provinceSelected.getProvince().isEmpty()) {
            listCities.add("Selecciona provincia");
        } else {
            listCities.addAll(provinceSelected.getCities());
            listCities.add(0, "");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_list_item_activated_1, listCities);
        spinner_localidad.setAdapter(adapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rb_Hembra) {
            rb_female.setTextColor(getResources().getColor(R.color.pink));
        } else {
            rb_female.setTextColor(Color.BLACK);
        }

            if (checkedId == R.id.rb_Macho) {
            rb_male.setTextColor(getResources().getColor(R.color.blue));
        }else{
            rb_male.setTextColor(Color.BLACK);
            }
    }
}