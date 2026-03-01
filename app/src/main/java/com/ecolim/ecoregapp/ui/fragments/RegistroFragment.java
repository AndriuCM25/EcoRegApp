package com.ecolim.ecoregapp.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.ecolim.ecoregapp.R;
import com.ecolim.ecoregapp.data.local.entity.Residuo;
import com.ecolim.ecoregapp.utils.FileManager;
import com.ecolim.ecoregapp.utils.SafeNav;
import com.ecolim.ecoregapp.utils.SessionManager;
import com.ecolim.ecoregapp.viewmodel.ResiduoViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class RegistroFragment extends Fragment {

    private ResiduoViewModel viewModel;
    private SessionManager session;
    private ChipGroup chipGroupTipo;
    private TextInputEditText etPeso, etUbicacion, etZona, etObservaciones;
    private TextInputLayout tilPeso;
    private LinearLayout layoutEPP, layoutFotoPreview;
    private CheckBox cbGuantes, cbMascarilla, cbLentes;
    private Button btnGuardar, btnGuardarNuevo;
    private ImageView ivFotoEvidencia;
    private TextView tvFotoNombre;
    private String tipoSeleccionado = "plastico";
    private Uri photoUri;
    private String rutaFoto = null;

    // ── Cámara: usa getCacheDir() igual que Perfil ──────────────────────────
    private final ActivityResultLauncher<Intent> cameraLauncher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && photoUri != null) {
                procesarFotoEnThread(photoUri);
            }
        });

    private final ActivityResultLauncher<String> permLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) abrirCamara();
            else Toast.makeText(requireContext(), "Permiso de cámara requerido", Toast.LENGTH_SHORT).show();
        });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        session   = new SessionManager(requireContext());
        viewModel = new ViewModelProvider(requireActivity()).get(ResiduoViewModel.class);
        bindViews(view);
        setupChipsTipo();
        setupCamara(view);
        setupBotones();
        observeViewModel();
        view.findViewById(R.id.btn_back).setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void bindViews(View v) {
        chipGroupTipo     = v.findViewById(R.id.chip_group_tipo);
        etPeso            = v.findViewById(R.id.et_peso);
        etUbicacion       = v.findViewById(R.id.et_ubicacion);
        etZona            = v.findViewById(R.id.et_zona);
        etObservaciones   = v.findViewById(R.id.et_observaciones);
        tilPeso           = v.findViewById(R.id.til_peso);
        layoutEPP         = v.findViewById(R.id.layout_epp);
        cbGuantes         = v.findViewById(R.id.cb_guantes);
        cbMascarilla      = v.findViewById(R.id.cb_mascarilla);
        cbLentes          = v.findViewById(R.id.cb_lentes);
        btnGuardar        = v.findViewById(R.id.btn_guardar);
        btnGuardarNuevo   = v.findViewById(R.id.btn_guardar_nuevo);
        ivFotoEvidencia   = v.findViewById(R.id.iv_foto_evidencia);
        layoutFotoPreview = v.findViewById(R.id.layout_foto_preview);
        tvFotoNombre      = v.findViewById(R.id.tv_foto_nombre);
    }

    private void setupChipsTipo() {
        chipGroupTipo.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            Chip chip = group.findViewById(checkedIds.get(0));
            if (chip == null) return;
            tipoSeleccionado = chip.getTag().toString();
            layoutEPP.setVisibility(tipoSeleccionado.equals("peligroso") ? View.VISIBLE : View.GONE);
            if (!tipoSeleccionado.equals("peligroso")) {
                cbGuantes.setChecked(false); cbMascarilla.setChecked(false); cbLentes.setChecked(false);
            }
        });
    }

    private void setupCamara(View v) {
        v.findViewById(R.id.btn_tomar_foto).setOnClickListener(x -> verificarPermisoCamara());
        v.findViewById(R.id.btn_quitar_foto).setOnClickListener(x -> {
            rutaFoto = null; photoUri = null;
            layoutFotoPreview.setVisibility(View.GONE);
        });
    }

    private void verificarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            permLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    // ── Usa getCacheDir() — sin necesidad de WRITE_EXTERNAL_STORAGE ─────────
    private void abrirCamara() {
        try {
            File temp = File.createTempFile("EVIDENCIA_", ".jpg", requireContext().getCacheDir());
            photoUri = FileProvider.getUriForFile(
                    requireContext(), "com.ecolim.ecoregapp.fileprovider", temp);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            cameraLauncher.launch(intent);
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Error al abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    // ── Procesa en hilo secundario para no trabar la UI ─────────────────────
    private void procesarFotoEnThread(Uri uri) {
        new Thread(() -> {
            try {
                // Decodificar con muestreo
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                InputStream is = requireContext().getContentResolver().openInputStream(uri);
                if (is == null) throw new Exception("No se pudo abrir la imagen");
                BitmapFactory.decodeStream(is, null, opts);
                is.close();

                int sample = 1;
                while (opts.outWidth / sample > 800 || opts.outHeight / sample > 800) sample *= 2;

                opts.inJustDecodeBounds = false;
                opts.inSampleSize = sample;
                is = requireContext().getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is, null, opts);
                if (is != null) is.close();
                if (bmp == null) throw new Exception("Bitmap nulo");

                rutaFoto = uri.toString();

                requireActivity().runOnUiThread(() -> {
                    ivFotoEvidencia.setImageBitmap(bmp);
                    tvFotoNombre.setText("📷 Foto de evidencia adjunta");
                    layoutFotoPreview.setVisibility(View.VISIBLE);
                    Toast.makeText(requireContext(), "✅ Foto capturada", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), "Error al procesar la foto", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private boolean validar() {
        String pesoStr = etPeso.getText() != null ? etPeso.getText().toString().trim() : "";
        if (TextUtils.isEmpty(pesoStr)) { tilPeso.setError("Ingresa el peso"); return false; }
        tilPeso.setError(null);
        try {
            if (Double.parseDouble(pesoStr.replace(",", ".")) <= 0) {
                tilPeso.setError("El peso debe ser mayor a 0"); return false;
            }
        } catch (NumberFormatException e) { tilPeso.setError("Peso inválido"); return false; }
        if (tipoSeleccionado.equals("peligroso") &&
                (!cbGuantes.isChecked() || !cbMascarilla.isChecked() || !cbLentes.isChecked())) {
            Toast.makeText(requireContext(), "Confirma el uso de todos los EPP", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void guardarResiduo(boolean nuevoFormulario) {
        double peso = Double.parseDouble(etPeso.getText().toString().trim().replace(",", "."));
        Residuo r = new Residuo(tipoSeleccionado, peso, FileManager.fechaActual(),
                etUbicacion.getText() != null ? etUbicacion.getText().toString().trim() : "",
                etZona.getText() != null ? etZona.getText().toString().trim() : "",
                session.getOperarioId(), session.getOperarioNombre());
        r.observaciones  = etObservaciones.getText() != null ? etObservaciones.getText().toString().trim() : "";
        r.eppConfirmado  = tipoSeleccionado.equals("peligroso") && cbGuantes.isChecked() && cbMascarilla.isChecked() && cbLentes.isChecked();
        if (rutaFoto != null) r.observaciones += (r.observaciones.isEmpty() ? "" : " | ") + "📷 Con foto evidencia";
        viewModel.guardarResiduo(r);
        if (nuevoFormulario) { limpiarFormulario(); Toast.makeText(requireContext(), "✅ Guardado", Toast.LENGTH_SHORT).show(); }
    }

    private void setupBotones() {
        btnGuardar.setOnClickListener(x -> { if (validar()) guardarResiduo(false); });
        btnGuardarNuevo.setOnClickListener(x -> { if (validar()) guardarResiduo(true); });
    }

    private void limpiarFormulario() {
        etPeso.setText(""); etUbicacion.setText(""); etZona.setText(""); etObservaciones.setText("");
        chipGroupTipo.check(R.id.chip_plastico);
        layoutEPP.setVisibility(View.GONE); layoutFotoPreview.setVisibility(View.GONE);
        cbGuantes.setChecked(false); cbMascarilla.setChecked(false); cbLentes.setChecked(false);
        tipoSeleccionado = "plastico"; rutaFoto = null; photoUri = null;
    }

    private void observeViewModel() {
        viewModel.getGuardadoExitoso().observe(getViewLifecycleOwner(), ok -> {
            if (ok != null && ok) {
                viewModel.resetGuardado();
                SafeNav.go(Navigation.findNavController(requireView()), R.id.successFragment);
            }
        });
    }
}
