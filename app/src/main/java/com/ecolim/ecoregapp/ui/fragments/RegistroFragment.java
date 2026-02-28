package com.ecolim.ecoregapp.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.ecolim.ecoregapp.R;
import com.ecolim.ecoregapp.data.local.entity.Residuo;
import com.ecolim.ecoregapp.utils.FileManager;
import com.ecolim.ecoregapp.utils.SessionManager;
import com.ecolim.ecoregapp.viewmodel.ResiduoViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegistroFragment extends Fragment {

    private ResiduoViewModel viewModel;
    private SessionManager session;

    private ChipGroup chipGroupTipo;
    private TextInputEditText etPeso, etUbicacion, etZona, etObservaciones;
    private TextInputLayout tilPeso;
    private LinearLayout layoutEPP;
    private CheckBox cbGuantes, cbMascarilla, cbLentes;
    private Button btnGuardar, btnGuardarNuevo;
    private Spinner spinnerTipo;

    private String tipoSeleccionado = "plastico";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new SessionManager(requireContext());
        viewModel = new ViewModelProvider(requireActivity()).get(ResiduoViewModel.class);

        bindViews(view);
        setupChipsTipo();
        setupBotones(view);
        observeViewModel();
    }

    private void bindViews(View v) {
        chipGroupTipo   = v.findViewById(R.id.chip_group_tipo);
        etPeso          = v.findViewById(R.id.et_peso);
        etUbicacion     = v.findViewById(R.id.et_ubicacion);
        etZona          = v.findViewById(R.id.et_zona);
        etObservaciones = v.findViewById(R.id.et_observaciones);
        tilPeso         = v.findViewById(R.id.til_peso);
        layoutEPP       = v.findViewById(R.id.layout_epp);
        cbGuantes       = v.findViewById(R.id.cb_guantes);
        cbMascarilla    = v.findViewById(R.id.cb_mascarilla);
        cbLentes        = v.findViewById(R.id.cb_lentes);
        btnGuardar      = v.findViewById(R.id.btn_guardar);
        btnGuardarNuevo = v.findViewById(R.id.btn_guardar_nuevo);
    }

    private void setupChipsTipo() {
        chipGroupTipo.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            Chip chip = group.findViewById(checkedIds.get(0));
            if (chip == null) return;
            tipoSeleccionado = chip.getTag().toString();
            // Mostrar EPP si es peligroso
            if (tipoSeleccionado.equals("peligroso")) {
                layoutEPP.setVisibility(View.VISIBLE);
            } else {
                layoutEPP.setVisibility(View.GONE);
                cbGuantes.setChecked(false);
                cbMascarilla.setChecked(false);
                cbLentes.setChecked(false);
            }
        });
    }

    private void setupBotones(View v) {
        btnGuardar.setOnClickListener(x -> {
            if (validar()) {
                guardarResiduo(false, v);
            }
        });
        btnGuardarNuevo.setOnClickListener(x -> {
            if (validar()) {
                guardarResiduo(true, v);
            }
        });
    }

    private boolean validar() {
        String pesoStr = etPeso.getText() != null ? etPeso.getText().toString().trim() : "";
        if (TextUtils.isEmpty(pesoStr)) {
            tilPeso.setError("Ingresa el peso");
            return false;
        }
        tilPeso.setError(null);
        try {
            double peso = Double.parseDouble(pesoStr.replace(",", "."));
            if (peso <= 0) {
                tilPeso.setError("El peso debe ser mayor a 0");
                return false;
            }
        } catch (NumberFormatException e) {
            tilPeso.setError("Peso inválido");
            return false;
        }
        // Validar EPP si es peligroso
        if (tipoSeleccionado.equals("peligroso")) {
            if (!cbGuantes.isChecked() || !cbMascarilla.isChecked() || !cbLentes.isChecked()) {
                Toast.makeText(requireContext(),
                        "Debes confirmar el uso de todos los EPP para residuos peligrosos",
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private void guardarResiduo(boolean nuevoFormulario, View v) {
        double peso = Double.parseDouble(
                etPeso.getText().toString().trim().replace(",", "."));
        String ubicacion = etUbicacion.getText() != null ?
                etUbicacion.getText().toString().trim() : "";
        String zona = etZona.getText() != null ?
                etZona.getText().toString().trim() : "";
        String obs = etObservaciones.getText() != null ?
                etObservaciones.getText().toString().trim() : "";

        Residuo r = new Residuo(
                tipoSeleccionado, peso,
                FileManager.fechaActual(),
                ubicacion, zona,
                session.getOperarioId(),
                session.getOperarioNombre()
        );
        r.observaciones = obs;
        r.eppConfirmado = tipoSeleccionado.equals("peligroso") &&
                cbGuantes.isChecked() && cbMascarilla.isChecked() && cbLentes.isChecked();

        viewModel.guardarResiduo(r);

        if (nuevoFormulario) {
            limpiarFormulario();
            Toast.makeText(requireContext(), "✓ Guardado. Puedes registrar otro.", Toast.LENGTH_SHORT).show();
        }
        // El observer se encarga de navegar al éxito
    }

    private void limpiarFormulario() {
        etPeso.setText("");
        etUbicacion.setText("");
        etZona.setText("");
        etObservaciones.setText("");
        chipGroupTipo.check(R.id.chip_plastico);
        layoutEPP.setVisibility(View.GONE);
        cbGuantes.setChecked(false);
        cbMascarilla.setChecked(false);
        cbLentes.setChecked(false);
        tipoSeleccionado = "plastico";
    }

    private void observeViewModel() {
        viewModel.getGuardadoExitoso().observe(getViewLifecycleOwner(), ok -> {
            if (ok != null && ok) {
                viewModel.resetGuardado();
                Navigation.findNavController(requireView()).navigate(R.id.successFragment);
            }
        });
    }
}
