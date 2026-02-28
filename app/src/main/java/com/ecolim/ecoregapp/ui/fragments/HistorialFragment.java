package com.ecolim.ecoregapp.ui.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ecolim.ecoregapp.R;
import com.ecolim.ecoregapp.data.local.entity.Residuo;
import com.ecolim.ecoregapp.ui.adapters.ResiduoAdapter;
import com.ecolim.ecoregapp.viewmodel.ResiduoViewModel;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class HistorialFragment extends Fragment {

    private ResiduoViewModel viewModel;
    private ResiduoAdapter adapter;
    private RecyclerView recyclerView;
    private ChipGroup chipGroupFiltro;
    private EditText etBuscar;
    private TextView tvVacio;
    private List<Residuo> listaCompleta = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_historial, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ResiduoViewModel.class);

        recyclerView    = view.findViewById(R.id.rv_historial);
        chipGroupFiltro = view.findViewById(R.id.chip_group_filtro);
        etBuscar        = view.findViewById(R.id.et_buscar);
        tvVacio         = view.findViewById(R.id.tv_vacio);

        adapter = new ResiduoAdapter(residuo -> mostrarDetalle(residuo));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Filtros por chip
        chipGroupFiltro.setOnCheckedStateChangeListener((g, ids) -> filtrar());

        // Búsqueda en tiempo real
        etBuscar.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            public void onTextChanged(CharSequence s, int i, int b, int c) { filtrar(); }
            public void afterTextChanged(android.text.Editable s) {}
        });

        viewModel.todosLosResiduos.observe(getViewLifecycleOwner(), lista -> {
            listaCompleta = lista != null ? lista : new ArrayList<>();
            filtrar();
        });
    }

    private void filtrar() {
        List<Residuo> resultado = new ArrayList<>(listaCompleta);
        String query = etBuscar.getText().toString().toLowerCase().trim();

        // Filtro por tipo
        int chipId = chipGroupFiltro.getCheckedChipId();
        if (chipId == R.id.chip_f_plastico)  filtrarPorTipo(resultado, "plastico");
        else if (chipId == R.id.chip_f_organico)  filtrarPorTipo(resultado, "organico");
        else if (chipId == R.id.chip_f_papel)     filtrarPorTipo(resultado, "papel");
        else if (chipId == R.id.chip_f_metal)     filtrarPorTipo(resultado, "metal");
        else if (chipId == R.id.chip_f_peligroso) filtrarPorTipo(resultado, "peligroso");
        else if (chipId == R.id.chip_f_pendiente) resultado.removeIf(r -> r.sincronizado);

        // Búsqueda por texto
        if (!query.isEmpty()) {
            resultado.removeIf(r ->
                    (r.tipo == null || !r.tipo.toLowerCase().contains(query)) &&
                    (r.zona == null || !r.zona.toLowerCase().contains(query)) &&
                    (r.ubicacion == null || !r.ubicacion.toLowerCase().contains(query)) &&
                    (r.operarioNombre == null || !r.operarioNombre.toLowerCase().contains(query))
            );
        }

        adapter.submitList(resultado);
        tvVacio.setVisibility(resultado.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void filtrarPorTipo(List<Residuo> lista, String tipo) {
        lista.removeIf(r -> r.tipo == null || !r.tipo.equalsIgnoreCase(tipo));
    }

    private void mostrarDetalle(Residuo r) {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Detalle del Registro")
                .setMessage(
                        "Tipo: " + r.tipo + "\n" +
                        "Peso: " + r.pesoKg + " kg\n" +
                        "Volumen: " + String.format("%.4f m³", r.volumenM3) + "\n" +
                        "Fecha: " + r.fecha + "\n" +
                        "Ubicación: " + r.ubicacion + "\n" +
                        "Zona: " + r.zona + "\n" +
                        "Operario: " + r.operarioNombre + "\n" +
                        "EPP: " + (r.eppConfirmado ? "✓ Confirmado" : "N/A") + "\n" +
                        "Obs: " + (r.observaciones != null ? r.observaciones : "-") + "\n" +
                        "Sync: " + (r.sincronizado ? "✓" : "⏳ Pendiente")
                )
                .setPositiveButton("Cerrar", null)
                .setNegativeButton("Eliminar", (d, w) -> {
                    viewModel.eliminar(r);
                    Toast.makeText(requireContext(), "Registro eliminado", Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
