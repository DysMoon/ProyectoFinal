package com.example.billeteradigital;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Adapters.CategoriaAdapter;
import Data.AppDataBase;
import Models.Categoria;

public class CategoriaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoriaAdapter adapter;
    private AppDataBase db;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            db.categoriaDAO().getCategorias().observe(this, categorias -> {
                adapter.setData(categorias);
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_categoria);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDataBase.getDataBase(getApplicationContext());
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CategoriaAdapter(new ArrayList<>(), new CategoriaAdapter.OnCategoriaClickListener() {
            @Override
            public void onCategoriaClick(Categoria categoria) {
                Intent result = new Intent();
                result.putExtra("idCategoria", categoria.getId());
                result.putExtra("nombreCategoria", categoria.getNombre());
                setResult(RESULT_OK, result);
                finish();
            }


            @Override
            public void onCategoriaEditar(Categoria categoria) {
                Intent intent = new Intent(CategoriaActivity.this, NuevaCategoriaActivity.class);
                intent.putExtra("categoria_id", categoria.getId());
                intent.putExtra("categoria_nombre", categoria.getNombre());
                intent.putExtra("categoria_icono", categoria.getIcon());
                intent.putExtra("categoria_isGasto", categoria.isGasto());
                startActivityForResult(intent, 100);
            }

            @Override
            public void onCategoriaEliminar(Categoria categoria) {
                new Thread(() -> {
                    db.categoriaDAO().delete(categoria);
                    runOnUiThread(() -> {
                        Toast.makeText(CategoriaActivity.this, "Eliminado correctamente", Toast.LENGTH_SHORT).show();
                    });
                }).start();
            }
        });

        recyclerView.setAdapter(adapter);

        Button btnNuevaCategoria = findViewById(R.id.btnNuevaCategoria);
        btnNuevaCategoria.setOnClickListener(v -> {
            Intent intent = new Intent(CategoriaActivity.this, NuevaCategoriaActivity.class);
            startActivityForResult(intent, 100);
        });

        db.categoriaDAO().getCategorias().observe(this, categorias -> {
            for (Categoria cat : categorias) {
                android.util.Log.d("CATEGORIA_DB", "ID: " + cat.getId() + " - Nombre: " + cat.getNombre());
            }
            adapter.setData(categorias);
        });
    }
}
