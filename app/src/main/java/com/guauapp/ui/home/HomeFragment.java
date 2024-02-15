package com.guauapp.ui.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.guauapp.R;
import com.guauapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private View dialogView;
    private Dialog dialogFilter;
    private AlertDialog.Builder builder;
    private FloatingActionButton fab;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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