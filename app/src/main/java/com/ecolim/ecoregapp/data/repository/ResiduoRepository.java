package com.ecolim.ecoregapp.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.ecolim.ecoregapp.data.local.AppDatabase;
import com.ecolim.ecoregapp.data.local.dao.ResiduoDao;
import com.ecolim.ecoregapp.data.local.entity.Residuo;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResiduoRepository {

    private final ResiduoDao dao;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public ResiduoRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.residuoDao();
    }

    // ── INSERT ──
    public void insert(Residuo r, Runnable onDone) {
        executor.execute(() -> {
            r.volumenM3 = r.calcularVolumen();
            dao.insert(r);
            if (onDone != null) onDone.run();
        });
    }

    public void insertAll(List<Residuo> lista, Runnable onDone) {
        executor.execute(() -> {
            for (Residuo r : lista) r.volumenM3 = r.calcularVolumen();
            dao.insertAll(lista);
            if (onDone != null) onDone.run();
        });
    }

    // ── UPDATE / DELETE ──
    public void update(Residuo r) {
        executor.execute(() -> dao.update(r));
    }

    public void delete(Residuo r) {
        executor.execute(() -> dao.delete(r));
    }

    // ── READS ──
    public LiveData<List<Residuo>> getAll() {
        return dao.getAll();
    }

    public LiveData<List<Residuo>> getByOperario(String opId) {
        return dao.getByOperario(opId);
    }

    // ── FILTROS (ejecutar en background) ──
    public void getByFecha(String ini, String fin, Callback<List<Residuo>> cb) {
        executor.execute(() -> cb.onResult(dao.getByFecha(ini, fin)));
    }

    public void getByTipo(String tipo, Callback<List<Residuo>> cb) {
        executor.execute(() -> cb.onResult(dao.getByTipo(tipo)));
    }

    public void getByFechaYTipo(String ini, String fin, String tipo, Callback<List<Residuo>> cb) {
        executor.execute(() -> cb.onResult(dao.getByFechaYTipo(ini, fin, tipo)));
    }

    public void getAllSync(Callback<List<Residuo>> cb) {
        executor.execute(() -> cb.onResult(dao.getAllSync()));
    }

    // ── ESTADÍSTICAS ──
    public void getEstadisticasHoy(String hoy, Callback<int[]> cb) {
        executor.execute(() -> {
            int count = dao.getCountHoy(hoy);
            double peso = dao.getPesoHoy(hoy);
            cb.onResult(new int[]{count, (int) peso});
        });
    }

    public void getTotales(Callback<double[]> cb) {
        executor.execute(() -> {
            double total = dao.getTotalPeso();
            int count = dao.getCount();
            cb.onResult(new double[]{total, count});
        });
    }

    // ── SYNC ──
    public void getPendientesSync(Callback<List<Residuo>> cb) {
        executor.execute(() -> cb.onResult(dao.getPendientesSync()));
    }

    public void marcarSincronizados(List<Integer> ids) {
        executor.execute(() -> dao.marcarSincronizados(ids));
    }

    public interface Callback<T> {
        void onResult(T result);
    }
}
