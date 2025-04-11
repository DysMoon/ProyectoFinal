package Data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Models.Categoria;
import Models.Movimiento;
import Models.Usuario;

@Database(entities = {Categoria.class, Movimiento.class, Usuario.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})

public abstract class AppDataBase extends RoomDatabase {
    public abstract CategoriaDAO categoriaDAO();
    public abstract UsuarioDAO usuarioDAO();
    public abstract MovimientoDAO movimientoDAO();

    private static volatile AppDataBase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDataBase getDataBase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDataBase.class){
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDataBase.class, "BilleteraDB").addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                CategoriaDAO categoriaDAO = INSTANCE.categoriaDAO();
                UsuarioDAO usuarioDAO = INSTANCE.usuarioDAO();
                MovimientoDAO movimientoDAO = INSTANCE.movimientoDAO();

                categoriaDAO.insert(new Categoria("Vivienda", "Gastos relacionados con la casa", "", true));
                categoriaDAO.insert(new Categoria("Comida y Bebida", "Gastos de alimentación", "", true));
                categoriaDAO.insert(new Categoria("Trabajo", "Gastos relacionados con el trabajo", "", true));

                usuarioDAO.insert(new Usuario("Demo01", "Demo01", "admin01", "123abc", "admin@mibilletera.com", "adminDemo", true));

                movimientoDAO.insert(new Movimiento(10.00, new Date(), 1, false, "Ejemplo de Movimiento"));
            });
        }
    };
}
