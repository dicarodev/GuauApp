package com.guauapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.guauapp.adapter.DogsRecyclerViewAdapter;
import com.guauapp.databinding.ActivityMainBinding;
import com.guauapp.model.Dog;
import com.guauapp.model.DogsDAO;
import com.guauapp.model.Province;
import com.guauapp.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //public static final String CHANNEL_ID = "GuauApp_NotificationChannel";
    //private ActivityResultLauncher<String> requestNotificationPermissionLauncher; // Permisos de lectura de contactos
    private ActivityMainBinding binding;


    private Button btnFiltrar;
    Button btnBorrarFiltros;
    Button btnCancelarFiltros;
    Spinner spinner_raza;
    Spinner spinner_edad;
    RadioGroup rg_genero;
    private Dialog dialogFilter;
    private List<Dog> filteredDogList = new ArrayList<>();

    private View dialogView;
    private DogsDAO dogsDAO = new DogsDAO();
    Spinner spinner_provincia;
    Spinner spinner_localidad;
    Switch switch_castrado;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }


    @Override
    protected void onStart() {
        super.onStart();

        //initializeLuncherPermission();
        //requestNotificationPermission();
        //createNotificationChannel();

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_chat, R.id.navigation_profile).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        navView.setItemIconTintList(null);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.btnFloat){
            dialogView = getLayoutInflater().inflate(R.layout.filter_dialog, null, false);
            builder = new AlertDialog.Builder(MainActivity.this);
            mostrardialogoPersonalizado();
        }

        return super.onOptionsItemSelected(item);
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
        HomeFragment.rvAdapter = new DogsRecyclerViewAdapter(filteredDogList);
        HomeFragment.recyclerView.setAdapter(HomeFragment.rvAdapter);
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


    private void setAges() {
        List<String> ageList = new ArrayList<>();
        ageList.add("cachorro");
        ageList.add("adulto");
        ageList.add("senior");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_activated_1, ageList);
        spinner_edad.setAdapter(adapter);
    }

    private void setBreeds() {
        dogsDAO.getBreedsIdAsync().thenAccept(breedList -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_activated_1, breedList);
            spinner_raza.setAdapter(adapter);
        });
    }

    private void setProvinces() {
        dogsDAO.getProvincesAsync().thenAccept(provinceList -> {
            spinner_provincia.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_activated_1, provinceList));
            spinner_provincia.setOnItemSelectedListener(this);
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Province province = (Province) spinner_provincia.getSelectedItem();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_activated_1, province.getCities());
        spinner_localidad.setAdapter(adapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    /*// Inicializa el launcher para solicitar permisos de lectura de contactos
    private void initializeLuncherPermission() {

        requestNotificationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Si se otorgan los permisos, solicitar la información de contactos
                requestNotificationPermission();
            } else {
                Log.d("MainActivity", "Permission denied, cannot display notification content");
            }
        });
    }
    // Metodo para verificar y solicitar permisos de notificacion si no se tienen
    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }*/
}