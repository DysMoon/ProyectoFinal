package Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {
    /*private final String TABLE_PRUEBA =
            "CREATE TABLE Prueba(" +
                    "idTabla INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
                    ");";*/

    public DbOpenHelper(Context context, int version) {
        super(context, "BilleteraDB", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //PRUEBA
        //db.execSQL(TABLE_PRUEBA);

        //SIN DEPENDENCIA
        db.execSQL(tablaUsuarios());
        db.execSQL(tablaCategorias());

        //CON DEPENDENCIA
        db.execSQL(tablaMovimientos());
        db.execSQL(tableReportes());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //CON DEPENDENCIA
        db.execSQL("DROP TABLE IF EXISTS Movimientos");
        db.execSQL("DROP TABLE IF EXISTS Reportes");

        //SIN DEPENDENCIA
        db.execSQL("DROP TABLE IF EXISTS Categorias");
        db.execSQL("DROP TABLE IF EXISTS Usuarios");

        onCreate(db);
    }

    private String tablaUsuarios() {
        return "CREATE TABLE Usuarios(" +
                "idUsuario INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + //0
                "nombre VARCHAR(150) NOT NULL," + //1
                "apellido VARCHAR(250) NOT NULL," + //2
                "nickname VARCHAR(100) NOT NULL," + //3
                "password TEXT NOT NULL," + //4
                "email TEXT," + //5
                "rol VARCHAR(100)," + //6
                "sincNube INTEGER" + //7
                ");";
    }

    private String tablaCategorias() {
        return "CREATE TABLE Categorias(" +
                "idCategoria INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + //0
                "nombre VARCHAR(100) NOT NULL," + //1
                "descripcion TEXT," + //2
                "icono TEXT" + //3
                ");";
    }

    private String tablaMovimientos() {
        return "CREATE TABLE Movimientos(" +
                "idMovimiento INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + //0
                "monto DOUBLE NOT NULL," + //1
                "descripcion TEXT," + //2
                "fecha DATE NOT NULL," + //3
                "esGasto INTEGER NOT NULL," + //4
                "idCategoria INTEGER NOT NULL," + //5

                //RELACIONES
                "FOREIGN KEY(idCategoria) REFERENCES Categorias(idCategoria)" +
                ");";
    }

    private String tableReportes() {
        return "CREATE TABLE Reportes(" +
                "idReporte INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + //0
                "uriPDF TEXT NOT NULL," + //1
                "fechaConsulta DATE NOT NULL," + //2
                "fechaPerIni DATE NOT NULL," + //3
                "fechaPerFin DATE NOT NULL," + //4
                "idCategoria INTEGER NOT NULL," + //5

                //RELACIONES
                "FOREIGN KEY(idCategoria) REFERENCES Categorias(idCategoria)" +
                ");";
    }
}
