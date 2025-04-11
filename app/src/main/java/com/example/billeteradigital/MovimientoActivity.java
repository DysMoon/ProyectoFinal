package com.example.billeteradigital;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.Date;

import Data.AppDataBase;
import Models.Movimiento;

public class MovimientoActivity extends AppCompatActivity {

    private static final int REQ_SELECT_CATEGORIA = 101;

    private MaterialButton btn_ingreso, btn_gasto;
    private TextView txt_signo, txt_disponible, txt_categoria;
    private EditText txt_monto, txt_descripcion;
    private Button btn_categoria, btn_cancelar, btn_confirmar;
    private boolean esIngreso = true;
    private int categoriaIdSeleccionada = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movimiento);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_ingreso = findViewById(R.id.btn_ingreso);
        btn_gasto = findViewById(R.id.btn_gasto);
        txt_signo = findViewById(R.id.txt_signo);
        txt_disponible = findViewById(R.id.txt_disponible);
        txt_monto = findViewById(R.id.txt_monto);
        txt_descripcion = findViewById(R.id.txt_descripcion);
        btn_categoria = findViewById(R.id.btn_categoria);
        txt_categoria = findViewById(R.id.txt_categoria);
        btn_cancelar = findViewById(R.id.btn_cancelar);
        btn_confirmar = findViewById(R.id.btn_confirmar);

        mostrarDisponible();

        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggleGroupTipo);
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) cambiarTipoMovimiento(checkedId == R.id.btn_ingreso);
        });

        btn_categoria.setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoriaActivity.class);
            startActivityForResult(intent, REQ_SELECT_CATEGORIA);
        });

        btn_cancelar.setOnClickListener(v -> limpiarCampos("Operación cancelada"));

        btn_confirmar.setOnClickListener(v -> registrarMovimiento());
    }

    private void cambiarTipoMovimiento(boolean ingreso) {
        esIngreso = ingreso;
        txt_signo.setText(ingreso ? "+" : "-");

        btn_ingreso.setBackgroundTintList(ContextCompat.getColorStateList(this,
                ingreso ? R.color.primarybtnColor : R.color.unselectbtnColor));
        btn_gasto.setBackgroundTintList(ContextCompat.getColorStateList(this,
                ingreso ? R.color.unselectbtnColor : R.color.primarybtnColor));
    }

    private void mostrarDisponible() {
        AppDataBase db = AppDataBase.getDataBase(getApplicationContext());

        db.movimientoDAO().getTotalIngresos().observe(this, ingresos -> {
            db.movimientoDAO().getTotalGastos().observe(this, gastos -> {
                double totalIngresos = ingresos != null ? ingresos : 0;
                double totalGastos = gastos != null ? gastos : 0;
                double disponible = totalIngresos - totalGastos;

                txt_disponible.setText(String.format("Disponible: S/ %.2f", disponible));
            });
        });
    }

    private void registrarMovimiento() {
        String montoTexto = txt_monto.getText().toString().trim();
        String descripcion = txt_descripcion.getText().toString().trim();
        String categoriaTexto = txt_categoria.getText().toString();

        if (montoTexto.isEmpty() || categoriaIdSeleccionada == -1 || categoriaTexto.contains("seleccionada")) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(montoTexto);
            if (monto <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        Movimiento nuevo = new Movimiento(monto, new Date(), categoriaIdSeleccionada, !esIngreso, descripcion);

        new Thread(() -> {
            AppDataBase.getDataBase(getApplicationContext()).movimientoDAO().insert(nuevo);
            runOnUiThread(() -> {
                Toast.makeText(this, "Movimiento registrado", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK); // 👈 Para activar el onActivityResult
                finish(); // 👈 Vuelve al DashboardActivity
            });
        }).start();
    }


    private void limpiarCampos(String mensaje) {
        txt_monto.setText("");
        txt_descripcion.setText("");
        txt_categoria.setText("[Categoría seleccionada]");
        categoriaIdSeleccionada = -1;
        txt_signo.setText(esIngreso ? "+" : "-");

        if (!mensaje.isEmpty())
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_SELECT_CATEGORIA && resultCode == RESULT_OK) {
            String nombreCategoria = data.getStringExtra("nombreCategoria");
            int idCategoria = data.getIntExtra("idCategoria", -1);

            if (idCategoria != -1) {
                txt_categoria.setText(nombreCategoria);
                categoriaIdSeleccionada = idCategoria;
            }
        }
    }
}
