package Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Models.Usuario;

@Dao
public interface UsuarioDAO {
    @Insert
    long insert(Usuario usuario);

    @Update
    void update(Usuario usuario);

    @Delete
    void delete(Usuario usuario);

    @Query("SELECT * FROM Usuarios")
    LiveData<List<Usuario>> getUsuarios();
    @Query("SELECT * FROM Usuarios")
    List<Usuario> getUsuariosRaw();  // ← método sin LiveData

    @Query("SELECT * FROM Usuarios WHERE nickname = :nickname OR email = :email")
    Usuario getUsuarioValidoRaw(String nickname, String email);

}
