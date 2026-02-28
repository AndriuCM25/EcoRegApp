package com.ecolim.ecoregapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.ecolim.ecoregapp.data.local.entity.Residuo;
import java.util.List;

@Dao
public interface ResiduoDao {

    // ── INSERT / UPDATE / DELETE ──
    @Insert
    long insert(Residuo residuo);

    @Insert
    void insertAll(List<Residuo> residuos);

    @Update
    void update(Residuo residuo);

    @Delete
    void delete(Residuo residuo);

    @Query("DELETE FROM residuos")
    void deleteAll();

    // ── QUERIES PRINCIPALES ──
    @Query("SELECT * FROM residuos ORDER BY fecha DESC")
    LiveData<List<Residuo>> getAll();

    @Query("SELECT * FROM residuos ORDER BY fecha DESC")
    List<Residuo> getAllSync();

    @Query("SELECT * FROM residuos WHERE id = :id")
    Residuo getById(int id);

    @Query("SELECT * FROM residuos WHERE sincronizado = 0")
    List<Residuo> getPendientesSync();

    @Query("SELECT * FROM residuos WHERE operarioId = :opId ORDER BY fecha DESC")
    LiveData<List<Residuo>> getByOperario(String opId);

    // ── FILTROS PARA REPORTES ──
    @Query("SELECT * FROM residuos WHERE fecha BETWEEN :inicio AND :fin ORDER BY fecha DESC")
    List<Residuo> getByFecha(String inicio, String fin);

    @Query("SELECT * FROM residuos WHERE tipo = :tipo ORDER BY fecha DESC")
    List<Residuo> getByTipo(String tipo);

    @Query("SELECT * FROM residuos WHERE fecha BETWEEN :inicio AND :fin AND tipo = :tipo ORDER BY fecha DESC")
    List<Residuo> getByFechaYTipo(String inicio, String fin, String tipo);

    // ── ESTADÍSTICAS ──
    @Query("SELECT SUM(pesoKg) FROM residuos")
    double getTotalPeso();

    @Query("SELECT SUM(pesoKg) FROM residuos WHERE tipo = :tipo")
    double getTotalPesoByTipo(String tipo);

    @Query("SELECT SUM(pesoKg) FROM residuos WHERE fecha BETWEEN :inicio AND :fin")
    double getTotalPesoByFecha(String inicio, String fin);

    @Query("SELECT COUNT(*) FROM residuos")
    int getCount();

    @Query("SELECT COUNT(*) FROM residuos WHERE fecha LIKE :hoy || '%'")
    int getCountHoy(String hoy);

    @Query("SELECT SUM(pesoKg) FROM residuos WHERE fecha LIKE :hoy || '%'")
    double getPesoHoy(String hoy);

    // ── MARCAR SINCRONIZADOS ──
    @Query("UPDATE residuos SET sincronizado = 1 WHERE id IN (:ids)")
    void marcarSincronizados(List<Integer> ids);
}
