package com.ecolim.ecoregapp.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ecolim.ecoregapp.R;
import com.ecolim.ecoregapp.data.local.entity.Residuo;
import com.ecolim.ecoregapp.ui.adapters.ResiduoAdapter;
import com.ecolim.ecoregapp.utils.SafeNav;
import com.ecolim.ecoregapp.utils.SessionManager;
import com.ecolim.ecoregapp.viewmodel.ResiduoViewModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private ResiduoViewModel viewModel;
    private SessionManager session;
    private ResiduoAdapter adapter;

    private TextView tvSaludo, tvNombre, tvPesoHoy, tvRegistrosHoy;
    private TextView tvPendientes, tvPeligrosos, tvTurnoPlanta;
    private RecyclerView rvRecientes;
    private ProgressBar progressMeta;

    private static final String PREFS_PROFILE = "profile_prefs";
    private static final String KEY_NOMBRE    = "nombre_usuario";

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        session   = new SessionManager(requireContext());
        viewModel = new ViewModelProvider(requireActivity()).get(ResiduoViewModel.class);
        bindViews(view);
        setupRecycler();
        observeData();
        setupQuickActions(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        actualizarDesdePerfil();
    }

    private void bindViews(View v) {
        tvSaludo       = v.findViewById(R.id.tv_saludo);
        tvNombre       = v.findViewById(R.id.tv_nombre_operario);
        tvPesoHoy      = v.findViewById(R.id.tv_peso_hoy);
        tvRegistrosHoy = v.findViewById(R.id.tv_registros_hoy);
        tvPendientes   = v.findViewById(R.id.tv_pendientes);
        tvPeligrosos   = v.findViewById(R.id.tv_peligrosos);
        rvRecientes    = v.findViewById(R.id.rv_recientes);
        progressMeta   = v.findViewById(R.id.progress_meta);
        tvTurnoPlanta  = v.findViewById(R.id.tv_turno_planta);
    }

    private void actualizarDesdePerfil() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_PROFILE, 0);
        String nombre = prefs.getString(KEY_NOMBRE, session.getOperarioNombre());

        int hora = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        String saludo = hora < 12 ? "Buenos días," : hora < 18 ? "Buenas tardes," : "Buenas noches,";
        tvSaludo.setText(saludo);
        tvNombre.setText(nombre + " 👋");

        if (tvTurnoPlanta != null)
            tvTurnoPlanta.setText("Turno " + session.getTurno() + " · " + session.getPlanta());
    }

    private void setupRecycler() {
        adapter = new ResiduoAdapter(residuo -> {});
        rvRecientes.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRecientes.setAdapter(adapter);
        rvRecientes.setNestedScrollingEnabled(false);
        // Evita que el RecyclerView re-mida en cada update
        rvRecientes.setHasFixedSize(false);
    }

    private void observeData() {
        final String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        viewModel.todosLosResiduos.observe(getViewLifecycleOwner(), lista -> {
            if (lista == null || !isAdded()) return;

            double pesoHoy = 0;
            int countHoy = 0, peligrosos = 0, pendientes = 0;

            List<Residuo> recientes = new ArrayList<>();
            int size = Math.min(lista.size(), 5);
            for (int i = 0; i < size; i++) recientes.add(lista.get(i));

            for (Residuo r : lista) {
                if (r.fecha != null && r.fecha.startsWith(hoy)) { pesoHoy += r.pesoKg; countHoy++; }
                if ("peligroso".equalsIgnoreCase(r.tipo)) peligrosos++;
                if (!r.sincronizado) pendientes++;
            }

            adapter.submitList(recientes);
            tvPesoHoy.setText(String.format("%.1f kg", pesoHoy));
            tvRegistrosHoy.setText(String.valueOf(countHoy));
            tvPendientes.setText(String.valueOf(pendientes));
            tvPeligrosos.setText(String.valueOf(peligrosos));
            progressMeta.setProgress((int) Math.min((pesoHoy / 63.0) * 100, 100));
        });
    }

    private void setupQuickActions(View v) {
        NavController nav = Navigation.findNavController(v);
        v.findViewById(R.id.card_nuevo_registro).setOnClickListener(SafeNav.to(nav, R.id.registroFragment));
        v.findViewById(R.id.card_importar).setOnClickListener(SafeNav.to(nav, R.id.importarFragment));
        v.findViewById(R.id.card_reportes).setOnClickListener(SafeNav.to(nav, R.id.reportesFragment));
        v.findViewById(R.id.card_historial).setOnClickListener(SafeNav.to(nav, R.id.historialFragment));
        v.findViewById(R.id.tv_ver_todos).setOnClickListener(SafeNav.to(nav, R.id.historialFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
