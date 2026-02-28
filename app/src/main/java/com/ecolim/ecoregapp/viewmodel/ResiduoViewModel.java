package com.ecolim.ecoregapp.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.ecolim.ecoregapp.data.local.entity.Residuo;
import com.ecolim.ecoregapp.data.repository.ResiduoRepository;
import java.util.List;

public class ResiduoViewModel extends AndroidViewModel {

    private final ResiduoRepository repo;
    public final LiveData<List<Residuo>> todosLosResiduos;

    // Estado para UI
    private final MutableLiveData<Boolean> guardadoExitoso = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();
    private final MutableLiveData<List<Residuo>> resultadoFiltro = new MutableLiveData<>();
    private final MutableLiveData<double[]> estadisticas = new MutableLiveData<>();
    private final MutableLiveData<Integer> importadosCount = new MutableLiveData<>();

    public ResiduoViewModel(Application app) {
        super(app);
        repo = new ResiduoRepository(app);
        todosLosResiduos = repo.getAll();
    }

    public void guardarResiduo(Residuo r) {
        repo.insert(r, () -> guardadoExitoso.postValue(true));
    }

    public void importarLista(List<Residuo> lista) {
        repo.insertAll(lista, () -> importadosCount.postValue(lista.size()));
    }

    public void eliminar(Residuo r) {
        repo.delete(r);
    }

    public void filtrarPorFecha(String ini, String fin) {
        repo.getByFecha(ini, fin, result -> resultadoFiltro.postValue(result));
    }

    public void filtrarPorTipo(String tipo) {
        repo.getByTipo(tipo, result -> resultadoFiltro.postValue(result));
    }

    public void filtrarPorFechaYTipo(String ini, String fin, String tipo) {
        repo.getByFechaYTipo(ini, fin, tipo, result -> resultadoFiltro.postValue(result));
    }

    public void cargarTodos() {
        repo.getAllSync(result -> resultadoFiltro.postValue(result));
    }

    public void cargarEstadisticas() {
        repo.getTotales(result -> estadisticas.postValue(result));
    }

    public LiveData<Boolean> getGuardadoExitoso() { return guardadoExitoso; }
    public LiveData<String> getMensajeError() { return mensajeError; }
    public LiveData<List<Residuo>> getResultadoFiltro() { return resultadoFiltro; }
    public LiveData<double[]> getEstadisticas() { return estadisticas; }
    public LiveData<Integer> getImportadosCount() { return importadosCount; }

    public void resetGuardado() { guardadoExitoso.setValue(null); }
}
