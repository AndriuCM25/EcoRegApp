package com.ecolim.ecoregapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.ecolim.ecoregapp.R;
import com.ecolim.ecoregapp.utils.SessionManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText etOperarioId, etPassword;
    private Button btnIngresar;
    private ProgressBar progressBar;
    private SessionManager session;

    private static final String[] IDS_VALIDOS   = {"OP-01", "OP-42", "OP-10", "ADMIN"};
    private static final String   PASSWORD_DEMO = "ecolim2026";

    // Executor para no bloquear el hilo principal
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verificar sesión en hilo secundario para no bloquear el arranque
        executor.execute(() -> {
            session = new SessionManager(LoginActivity.this);
            boolean loggedIn = session.isLoggedIn();
            mainHandler.post(() -> {
                if (loggedIn) {
                    irAlMain();
                } else {
                    mostrarLogin();
                }
            });
        });
    }

    private void mostrarLogin() {
        setContentView(R.layout.activity_login);

        etOperarioId = findViewById(R.id.et_operario_id);
        etPassword   = findViewById(R.id.et_password);
        btnIngresar  = findViewById(R.id.btn_ingresar);
        progressBar  = findViewById(R.id.progress_bar);

        btnIngresar.setOnClickListener(v -> intentarLogin());
    }

    private void intentarLogin() {
        String id       = etOperarioId.getText().toString().trim().toUpperCase();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(id)) {
            etOperarioId.setError("Ingresa tu ID de operario");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Ingresa tu contraseña");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnIngresar.setEnabled(false);

        // Validar en hilo secundario
        executor.execute(() -> {
            boolean idValido = false;
            for (String v : IDS_VALIDOS) {
                if (v.equalsIgnoreCase(id)) { idValido = true; break; }
            }

            final boolean ok = idValido && password.equals(PASSWORD_DEMO);
            final String nombre = id.equals("ADMIN") ? "Administrador" : "Operario " + id;

            mainHandler.post(() -> {
                if (ok) {
                    session.iniciarSesion(id, nombre, "Mañana", "Planta A");
                    irAlMain();
                } else {
                    progressBar.setVisibility(View.GONE);
                    btnIngresar.setEnabled(true);
                    Toast.makeText(this, "ID o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void irAlMain() {
        Intent intent = new Intent(this, MainActivity.class);
        // FLAG para limpiar el back stack — evita volver al login con el botón atrás
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        // Transición suave en lugar de corte brusco
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
