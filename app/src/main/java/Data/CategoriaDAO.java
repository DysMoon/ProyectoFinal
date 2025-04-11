package Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Models.Categoria;

@Dao
public interface CategoriaDAO {
    @Insert
    long insert(Categoria nuevo);

    @Update
    void update(Categoria editado);

    @Delete
    void delete(Categoria eliminado);

    @Query("SELECT * FROM categorias WHERE id = :id")
    Categoria getCategoriaId(int id);

    @Query("SELECT * FROM categorias")
    LiveData<List<Categoria>> getCategorias();
    @Query("SELECT * FROM categorias")
    List<Categoria> getCategoriasRaw();

    @Query("SELECT * FROM categorias WHERE isGasto = :isGasto")
    LiveData<List<Categoria>> getCategoriaGasto(boolean isGasto);
}
