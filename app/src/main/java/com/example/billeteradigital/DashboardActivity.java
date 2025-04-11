package com.example.billeteradigital;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardActivity extends AppCompatActivity {
    private FloatingActionButton btnfl_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard); // Asegúrate que este layout exista

        // Ajuste de padding para pantalla completa
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Botón para registrar movimiento
        btnfl_add = findViewById(R.id.btnfl_add);
        btnfl_add.setOnClickListener(v -> registrarMovimiento());

        // Cargar fragmento solo una vez
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_dashboard, new DashFragment())
                    .commit();
        }
    }

    private static final int REQ_MOVIMIENTO = 200;

    private void registrarMovimiento() {
        Intent intent = new Intent(DashboardActivity.this, MovimientoActivity.class);
        startActivityForResult(intent, REQ_MOVIMIENTO); // 👈 importante
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_MOVIMIENTO && resultCode == RESULT_OK) {
            DashFragment fragment = (DashFragment) getSupportFragmentManager().findFragmentById(R.id.fl_dashboard);
            if (fragment instanceof DashFragment) {
                fragment.recargarDatos(); // ✅ ahora sí funcionará
            }
        }
    }



}
