package Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Models.Movimiento;

@Dao
public interface MovimientoDAO {
    @Insert
    long insert(Movimiento movimiento);

    @Update
    void update(Movimiento movimiento);

    @Delete
    void delete(Movimiento movimiento);

    @Query("SELECT * FROM movimientos")
    LiveData<List<Movimiento>> getMovimientos();
    @Query("SELECT * FROM movimientos")
    List<Movimiento> getMovimientosRaw();
    @Query("SELECT * FROM movimientos WHERE isGasto = :isGasto")
    LiveData<List<Movimiento>> getMovimientosGasto(boolean isGasto);

    @Query("SELECT SUM(monto) FROM movimientos WHERE isGasto = 0")
    LiveData<Double> getTotalIngresos();

    @Query("SELECT SUM(monto) FROM movimientos WHERE isGasto = 1")
    LiveData<Double> getTotalGastos();

    @Query("SELECT categoriaId, SUM(monto) AS total FROM movimientos WHERE isGasto = :isGasto GROUP BY categoriaId")
    LiveData<List<CategoriaPair>> getTotalMontoCategoria(boolean isGasto);

    @Query("SELECT * FROM movimientos ORDER BY fecha DESC LIMIT :limite")
    LiveData<List<Movimiento>> getMovimientosRecientes(int limite);
}

class CategoriaPair {
    public int categoriaId;
    public double total;
}
