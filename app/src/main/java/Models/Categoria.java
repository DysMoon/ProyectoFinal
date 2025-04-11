package Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categorias")
public class Categoria {
    @PrimaryKey(autoGenerate = true)
    protected int id;
    protected String nombre;
    protected String descripcion;
    protected String icon;
    protected boolean isGasto;

    public Categoria(String nombre, String descripcion, String icon, boolean isGasto) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.icon = icon;
        this.isGasto = isGasto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isGasto() {
        return isGasto;
    }

    public void setGasto(boolean gasto) {
        isGasto = gasto;
    }
}
