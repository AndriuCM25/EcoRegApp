package com.ecolim.ecoregapp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.ecolim.ecoregapp.R;
import com.ecolim.ecoregapp.data.local.entity.Residuo;
import com.ecolim.ecoregapp.utils.FileManager;
import com.ecolim.ecoregapp.viewmodel.ResiduoViewModel;
import com.google.android.material.chip.ChipGroup;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportesFragment extends Fragment {

    private ResiduoViewModel viewModel;
    private ChipGroup chipPeriodo, chipTipo;
    private TextView tvTotalKg, tvTotalReg, tvTotalTipos;
    private Button btnExportarPDF, btnExportarCSV;
    private ProgressBar progressExport;

    private List<Residuo> listaActual = new ArrayList<>();
    private String periodoSeleccionado = "mes";
    private String tipoSeleccionado = "todos";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reportes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ResiduoViewModel.class);

        chipPeriodo     = view.findViewById(R.id.chip_group_periodo);
        chipTipo        = view.findViewById(R.id.chip_group_tipo_reporte);
        tvTotalKg       = view.findViewById(R.id.tv_total_kg);
        tvTotalReg      = view.findViewById(R.id.tv_total_reg);
        tvTotalTipos    = view.findViewById(R.id.tv_total_tipos);
        btnExportarPDF  = view.findViewById(R.id.btn_exportar_pdf);
        btnExportarCSV  = view.findViewById(R.id.btn_exportar_csv);
        progressExport  = view.findViewById(R.id.progress_export);

        chipPeriodo.setOnCheckedStateChangeListener((g, ids) -> aplicarFiltros());
        chipTipo.setOnCheckedStateChangeListener((g, ids) -> aplicarFiltros());

        btnExportarPDF.setOnClickListener(v -> exportarPDF());
        btnExportarCSV.setOnClickListener(v -> exportarCSV());

        observeData();
    }

    private void observeData() {
        viewModel.todosLosResiduos.observe(getViewLifecycleOwner(), lista -> {
            listaActual = lista != null ? lista : new ArrayList<>();
            aplicarFiltros();
        });
    }

    private void aplicarFiltros() {
        List<Residuo> filtrada = new ArrayList<>(listaActual);
        // Filtro por período
        String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Calendar cal = Calendar.getInstance();

        if (chipPeriodo.getCheckedChipId() == R.id.chip_hoy) {
            filtrada.removeIf(r -> r.fecha == null || !r.fecha.startsWith(hoy));
        } else if (chipPeriodo.getCheckedChipId() == R.id.chip_semana) {
            cal.add(Calendar.DAY_OF_YEAR, -7);
            String hace7 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
            filtrada.removeIf(r -> r.fecha == null || r.fecha.compareTo(hace7) < 0);
        } else if (chipPeriodo.getCheckedChipId() == R.id.chip_mes) {
            String mesActual = hoy.substring(0, 7);
            filtrada.removeIf(r -> r.fecha == null || !r.fecha.startsWith(mesActual));
        }

        // Filtro por tipo
        if (chipTipo.getCheckedChipId() != R.id.chip_tipo_todos) {
            String tipoFiltro = getTipoDeChip(chipTipo.getCheckedChipId());
            filtrada.removeIf(r -> !tipoFiltro.equalsIgnoreCase(r.tipo));
        }

        listaActual = filtrada;
        actualizarResumen(filtrada);
    }

    private String getTipoDeChip(int id) {
        if (id == R.id.chip_tipo_plastico)  return "plastico";
        if (id == R.id.chip_tipo_organico)  return "organico";
        if (id == R.id.chip_tipo_papel)     return "papel";
        if (id == R.id.chip_tipo_metal)     return "metal";
        if (id == R.id.chip_tipo_peligroso) return "peligroso";
        return "todos";
    }

    private void actualizarResumen(List<Residuo> lista) {
        double totalKg = lista.stream().mapToDouble(r -> r.pesoKg).sum();
        Set<String> tipos = new HashSet<>();
        for (Residuo r : lista) if (r.tipo != null) tipos.add(r.tipo);

        tvTotalKg.setText(String.format("%.1f kg", totalKg));
        tvTotalReg.setText(String.valueOf(lista.size()));
        tvTotalTipos.setText(String.valueOf(tipos.size()));
    }

    private void exportarPDF() {
        if (listaActual.isEmpty()) {
            Toast.makeText(requireContext(), "No hay datos para exportar", Toast.LENGTH_SHORT).show();
            return;
        }
        progressExport.setVisibility(View.VISIBLE);
        btnExportarPDF.setEnabled(false);
        new Thread(() -> {
            try {
                String periodo = obtenerPeriodoTexto();
                File pdf = FileManager.exportarPDF(requireContext(), listaActual, "Reporte de Residuos", periodo);
                requireActivity().runOnUiThread(() -> {
                    progressExport.setVisibility(View.GONE);
                    btnExportarPDF.setEnabled(true);
                    compartirArchivo(pdf, "application/pdf");
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    progressExport.setVisibility(View.GONE);
                    btnExportarPDF.setEnabled(true);
                    Toast.makeText(requireContext(), "Error al generar PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void exportarCSV() {
        if (listaActual.isEmpty()) {
            Toast.makeText(requireContext(), "No hay datos para exportar", Toast.LENGTH_SHORT).show();
            return;
        }
        progressExport.setVisibility(View.VISIBLE);
        btnExportarCSV.setEnabled(false);
        new Thread(() -> {
            try {
                File csv = FileManager.exportarCSV(requireContext(), listaActual, "residuos_ecolim");
                requireActivity().runOnUiThread(() -> {
                    progressExport.setVisibility(View.GONE);
                    btnExportarCSV.setEnabled(true);
                    compartirArchivo(csv, "text/csv");
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    progressExport.setVisibility(View.GONE);
                    btnExportarCSV.setEnabled(true);
                    Toast.makeText(requireContext(), "Error al exportar CSV: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void compartirArchivo(File file, String mimeType) {
        android.net.Uri uri = FileProvider.getUriForFile(
                requireContext(),
                "com.ecolim.ecoregapp.fileprovider",
                file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Reporte EcoRegApp - ECOLIM S.A.C.");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Compartir reporte"));
    }

    private String obtenerPeriodoTexto() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (chipPeriodo.getCheckedChipId() == R.id.chip_hoy) return "Hoy " + sdf.format(new Date());
        if (chipPeriodo.getCheckedChipId() == R.id.chip_semana) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -7);
            return sdf.format(cal.getTime()) + " - " + sdf.format(new Date());
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return sdf.format(cal.getTime()) + " - " + sdf.format(new Date());
    }
}
