package com.ecolim.ecoregapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.ecolim.ecoregapp.R;
import com.ecolim.ecoregapp.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etOperarioId, etPassword;
    private Button btnIngresar;
    private ProgressBar progressBar;
    private SessionManager session;

    // Credenciales de prueba (en producción validar contra API)
    private static final String[] IDS_VALIDOS    = {"OP-01", "OP-42", "OP-10", "ADMIN"};
    private static final String   PASSWORD_DEMO  = "ecolim2026";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        // Si ya hay sesión activa, ir directo al main
        if (session.isLoggedIn()) {
            irAlMain();
            return;
        }

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

        // Simulación de validación (reemplazar con llamada API)
        boolean idValido = false;
        for (String v : IDS_VALIDOS) {
            if (v.equalsIgnoreCase(id)) { idValido = true; break; }
        }

        if (idValido && password.equals(PASSWORD_DEMO)) {
            String nombre = "Operario " + id;
            if (id.equals("ADMIN")) nombre = "Administrador";
            session.iniciarSesion(id, nombre, "Mañana", "Planta A");
            irAlMain();
        } else {
            progressBar.setVisibility(View.GONE);
            btnIngresar.setEnabled(true);
            Toast.makeText(this, "ID o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }

    private void irAlMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
