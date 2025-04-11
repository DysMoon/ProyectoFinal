package com.example.billeteradigital;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapters.ColorAdapter;
import Adapters.IconoAdapter;
import Data.AppDataBase;
import Models.Categoria;

public class NuevaCategoriaActivity extends AppCompatActivity {

    private EditText edtNombreCategoria;
    private GridView gridColores, gridIconos;
    private Button btnGuardarCategoria;
    private ColorAdapter colorAdapter;
    private IconoAdapter iconoAdapter;
    private int[] colores = {
            Color.parseColor("#2196F3"), // Azul
            Color.parseColor("#4CAF50"), // Verde
            Color.parseColor("#F44336"), // Rojo
            Color.parseColor("#FF9800"), // Naranja
            Color.parseColor("#9C27B0")  // Morado
    };

    private String[] iconos = {
            "ic_add_circle_outline",
            "ic_shopping_cart",
            "ic_directions_car",
            "ic_restaurant",
            "ic_attach_money"
    };

    private int colorSeleccionado = colores[0];
    private String iconoSeleccionado = iconos[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_categoria);

        edtNombreCategoria = findViewById(R.id.edtNombreCategoria);
        gridColores = findViewById(R.id.grid_colores);
        gridIconos = findViewById(R.id.grid_iconos);
        btnGuardarCategoria = findViewById(R.id.btnGuardarCategoria);

        configurarGridColores();
        configurarGridIconos();

        btnGuardarCategoria.setOnClickListener(v -> guardarCategoria());
        int categoriaId = getIntent().getIntExtra("categoria_id", -1);
        if (categoriaId != -1) {
            new Thread(() -> {
                Categoria c = AppDataBase.getDataBase(getApplicationContext()).categoriaDAO().getCategoriaId(categoriaId);
                runOnUiThread(() -> cargarCategoria(c));
            }).start();
        }


    }

    private void configurarGridColores() {
        List<Integer> listaColores = new ArrayList<>();
        for (int color : colores) listaColores.add(color);

        colorAdapter = new ColorAdapter(this, listaColores);
        gridColores.setAdapter(colorAdapter);

        gridColores.setOnItemClickListener((parent, view, position, id) -> {
            colorAdapter.setSelectedPosition(position);
            colorSeleccionado = colores[position];
        });
    }

    private void configurarGridIconos() {
        iconoAdapter = new IconoAdapter(this, iconos);
        gridIconos.setAdapter(iconoAdapter);

        gridIconos.setOnItemClickListener((parent, view, position, id) -> {
            iconoAdapter.setSelectedPosition(position);
            iconoSeleccionado = iconos[position];
        });
    }
    private void cargarCategoria(Categoria c) {
        edtNombreCategoria.setText(c.getNombre());
        iconoSeleccionado = c.getIcon();
    }


    private void guardarCategoria() {
        String nombre = edtNombreCategoria.getText().toString().trim();
        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingrese un nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        Categoria categoria = new Categoria(nombre, "Creado manualmente", iconoSeleccionado, true);

        new Thread(() -> {
            int categoriaId = getIntent().getIntExtra("categoria_id", -1);

            if (categoriaId != -1) {
                categoria.setId(categoriaId);
                AppDataBase.getDataBase(getApplicationContext()).categoriaDAO().update(categoria);
            } else {
                AppDataBase.getDataBase(getApplicationContext()).categoriaDAO().insert(categoria);
            }

            List<Categoria> lista = AppDataBase.getDataBase(getApplicationContext())
                    .categoriaDAO().getCategoriasRaw();

            for (Categoria cat : lista) {
                android.util.Log.d("CATEGORIA_INSERT", "ID: " + cat.getId() + " - Nombre: " + cat.getNombre());
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Categoría registrada", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            });
        }).start();
    }


}
