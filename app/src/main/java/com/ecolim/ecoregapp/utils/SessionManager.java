package com.ecolim.ecoregapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "EcoRegSession";
    private static final String KEY_ID = "operario_id";
    private static final String KEY_NOMBRE = "operario_nombre";
    private static final String KEY_TURNO = "turno";
    private static final String KEY_PLANTA = "planta";
    private static final String KEY_LOGGED = "is_logged";

    private final SharedPreferences prefs;

    public SessionManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void iniciarSesion(String id, String nombre, String turno, String planta) {
        prefs.edit()
                .putString(KEY_ID, id)
                .putString(KEY_NOMBRE, nombre)
                .putString(KEY_TURNO, turno)
                .putString(KEY_PLANTA, planta)
                .putBoolean(KEY_LOGGED, true)
                .apply();
    }

    public void cerrarSesion() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED, false);
    }

    public String getOperarioId()     { return prefs.getString(KEY_ID, ""); }
    public String getOperarioNombre() { return prefs.getString(KEY_NOMBRE, ""); }
    public String getTurno()          { return prefs.getString(KEY_TURNO, ""); }
    public String getPlanta()         { return prefs.getString(KEY_PLANTA, ""); }
}
