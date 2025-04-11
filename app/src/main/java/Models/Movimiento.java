package Models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Movimientos", foreignKeys = @ForeignKey(entity = Categoria.class, parentColumns = "id", childColumns = "categoriaId", onDelete = ForeignKey.CASCADE))
public class Movimiento {
    @PrimaryKey(autoGenerate = true)
    protected int id;
    protected double monto;
    protected Date fecha;
    protected int categoriaId;
    protected boolean isGasto;
    protected String descripcion;

    public Movimiento(double monto, Date fecha, int categoriaId, boolean isGasto, String descripcion) {
        this.monto = monto;
        this.fecha = fecha;
        this.categoriaId = categoriaId;
        this.isGasto = isGasto;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public boolean isGasto() {
        return isGasto;
    }

    public void setGasto(boolean gasto) {
        isGasto = gasto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
