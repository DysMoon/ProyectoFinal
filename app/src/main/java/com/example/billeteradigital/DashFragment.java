package com.example.billeteradigital;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import Adapters.MovimientoAdapter;
import Data.AppDataBase;
import Data.CategoriaDAO;
import Data.DbOpenHelper;
import Data.MovimientoDAO;
import Models.Categoria;
import Models.ChartHelper;
import Models.Movimiento;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class DashFragment extends Fragment {

    private DbOpenHelper dboh;
    private CategoriaDAO categoriaDAO;
    private MovimientoDAO movimientoDAO;
    private MovimientoAdapter movimientoAdapter;
    private ExecutorService executorService;

    private PieChart pie_dash;
    private TextView tv_balance, tv_presupuesto, tv_ingresos;
    private MaterialButton btn_dash_ingreso, btn_dash_gasto;
    private Button btn_dash_pdf;
    private RecyclerView rvDashMov;
    private Button btn_ver_reportes;

    private boolean mostrarGastos = true;
    private List<Categoria> categoriaList;
    private List<Movimiento> movimientoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_dash, container, false);
        initialViews(view);

        AppDataBase db = AppDataBase.getDataBase(requireContext());
        categoriaDAO = db.categoriaDAO();
        movimientoDAO = db.movimientoDAO();

        executorService = Executors.newSingleThreadExecutor();

        setupRecyclerView();
        setupPieChart();
        setupButtons();
        loadData();

        return view;
    }

    private void initialViews(View view) {
        tv_balance = view.findViewById(R.id.tv_balance);
        tv_presupuesto = view.findViewById(R.id.tv_presupuesto);
        tv_ingresos = view.findViewById(R.id.tv_ingresos);

        btn_ver_reportes = view.findViewById(R.id.btn_ver_reportes);

        btn_dash_ingreso = view.findViewById(R.id.btn_dash_ingreso);
        btn_dash_gasto = view.findViewById(R.id.btn_dash_gasto);
        btn_dash_pdf = view.findViewById(R.id.btn_dash_pdf);

        pie_dash = view.findViewById(R.id.pie_dash);
        rvDashMov = view.findViewById(R.id.rvDashMov);
    }

    private void setupRecyclerView() {
        movimientoAdapter = new MovimientoAdapter(requireContext());
        rvDashMov.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvDashMov.setAdapter(movimientoAdapter);
    }

    private void setupPieChart() {
        ChartHelper.setupPieChart(pie_dash);
    }

    private void setupButtons() {
        btn_dash_ingreso.setOnClickListener(v -> {
            mostrarGastos = false;
            updateChartData();
            mostrarResumenPorCategoria();
            highlightActiveButton();
        });

        btn_dash_gasto.setOnClickListener(v -> {
            mostrarGastos = true;
            updateChartData();
            mostrarResumenPorCategoria();
            highlightActiveButton();
        });


        btn_dash_pdf.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            } else {
                exportarMovimientosAPDF();
            }
        });
        btn_ver_reportes.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ReporteActivity.class);
            startActivity(intent);
        });

        highlightActiveButton();
    }

    private void exportarMovimientosAPDF() {
        PdfDocument documento = new PdfDocument();
        Paint paint = new Paint();
        int y = 50;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = documento.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        paint.setTextSize(12f);
        paint.setFakeBoldText(true);
        canvas.drawText("REPORTE DE MOVIMIENTOS", 10, y, paint);
        paint.setFakeBoldText(false);
        y += 25;

        canvas.drawText("Monto", 10, y, paint);
        canvas.drawText("Fecha", 80, y, paint);
        canvas.drawText("Descripción", 160, y, paint);
        y += 15;

        for (Movimiento m : movimientoList) {
            if (y > 580) break;

            String signo = m.isGasto() ? "- " : "+ ";
            String montoTexto = signo + String.format("S/ %.2f", m.getMonto());
            String fechaTexto = new java.text.SimpleDateFormat("dd/MM/yyyy").format(m.getFecha());
            String descripcion = m.getDescripcion().length() > 12 ? m.getDescripcion().substring(0, 12) + "…" : m.getDescripcion();

            canvas.drawText(montoTexto, 10, y, paint);
            canvas.drawText(fechaTexto, 80, y, paint);
            canvas.drawText(descripcion, 160, y, paint);

            y += 15;
        }

        documento.finishPage(page);

        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            java.io.File file = new java.io.File(path, "reporte_movimientos.pdf");
            documento.writeTo(new java.io.FileOutputStream(file));
            Toast.makeText(requireContext(), "PDF guardado en Descargas", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error al guardar PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        documento.close();
    }


    private void loadData() {
        executorService.execute(() -> {
            categoriaList = categoriaDAO.getCategoriasRaw();
            movimientoList = movimientoDAO.getMovimientosRaw();

            List<Movimiento> filtrados = new ArrayList<>();
            for (Movimiento m : movimientoList) {
                if (m.isGasto() == mostrarGastos) {
                    filtrados.add(m);
                }
            }

            requireActivity().runOnUiThread(() -> {
                movimientoAdapter.setCategoria(categoriaList);
                movimientoAdapter.setMovimientoList(filtrados);
                updateChartData();
            });
        });
    }

    private void updateChartData() {
        if (movimientoList == null || categoriaList == null) return;

        double totalIngresos = 0;
        double totalGastos = 0;

        for (Movimiento m : movimientoList) {
            if (m.isGasto()) {
                totalGastos += m.getMonto();
            } else {
                totalIngresos += m.getMonto();
            }
        }

        double balance = totalIngresos - totalGastos;

        tv_balance.setText(String.format(Locale.getDefault(), "S/ %.2f", balance));

        tv_ingresos.setText(String.format(Locale.getDefault(), "S/ %.2f", totalGastos));

        double presupuestoMensual = 200.00;
        tv_presupuesto.setText(String.format("S/ %.2f", presupuestoMensual));

        if (totalGastos > presupuestoMensual) {
            tv_presupuesto.setTextColor(ContextCompat.getColor(requireContext(), R.color.dangerbtnColor));
            Snackbar.make(tv_presupuesto, "⚠️ ¡Has superado tu presupuesto mensual!", Snackbar.LENGTH_LONG).show();
        } else {
            tv_presupuesto.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        }

        Map<Categoria, Double> datos = new HashMap<>();
        for (Movimiento m : movimientoList) {
            if (m.isGasto() != mostrarGastos) continue;

            Categoria categoria = null;
            for (Categoria c : categoriaList) {
                if (c.getId() == m.getCategoriaId()) {
                    categoria = c;
                    break;
                }
            }

            if (categoria != null) {
                double actual = datos.getOrDefault(categoria, 0.0);
                datos.put(categoria, actual + m.getMonto());
            }
        }

        ChartHelper.updatePieChartWithCategories(pie_dash, datos);
    }


    public void recargarDatos() {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }

        executorService.execute(() -> {
            categoriaList = categoriaDAO.getCategoriasRaw();
            movimientoList = movimientoDAO.getMovimientosRaw();

            List<Movimiento> filtrados = new ArrayList<>();
            for (Movimiento m : movimientoList) {
                if (m.isGasto() == mostrarGastos) {
                    filtrados.add(m);
                }
            }

            filtrados.sort((m1, m2) -> m2.getFecha().compareTo(m1.getFecha()));

            requireActivity().runOnUiThread(() -> {
                movimientoAdapter.setCategoria(categoriaList);
                movimientoAdapter.setMovimientoList(filtrados);
                updateChartData();
            });
        });
    }




    private void highlightActiveButton() {
        btn_dash_gasto.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(),
                mostrarGastos ? R.color.primarybtnColor : R.color.unselectbtnColor));

        btn_dash_ingreso.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(),
                !mostrarGastos ? R.color.primarybtnColor : R.color.unselectbtnColor));
    }
    private void mostrarResumenPorCategoria() {
        if (movimientoList == null || categoriaList == null) return;

        List<Movimiento> movimientosFiltrados = new ArrayList<>();

        for (Movimiento m : movimientoList) {
            if (m.isGasto() == mostrarGastos) {
                movimientosFiltrados.add(m);
            }
        }

        movimientosFiltrados.sort((m1, m2) -> m2.getFecha().compareTo(m1.getFecha()));

        for (Movimiento m : movimientosFiltrados) {
            Categoria cat = null;
            for (Categoria c : categoriaList) {
                if (c.getId() == m.getCategoriaId()) {
                    cat = c;
                    break;
                }
            }
            String nombreCat = (cat != null) ? cat.getNombre() : "Desconocida";
            android.util.Log.d("MOVIMIENTO_LISTA", "ID: " + m.getId() +
                    " | Monto: " + m.getMonto() +
                    " | Gasto: " + m.isGasto() +
                    " | CatID: " + m.getCategoriaId() +
                    " | Categoría: " + nombreCat +
                    " | Fecha: " + m.getFecha());
        }

        movimientoAdapter.setCategoria(categoriaList);
        movimientoAdapter.setMovimientoList(movimientosFiltrados);
    }





}
