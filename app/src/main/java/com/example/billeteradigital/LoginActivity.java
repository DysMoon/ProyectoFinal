package com.example.billeteradigital;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Data.AppDataBase;
import Models.Usuario;

public class LoginActivity extends AppCompatActivity {
    private AppDataBase adb;
    private ExecutorService executorService;
    private List<Usuario> usuarioList;
    private EditText txt_usuario, txt_contraseña;
    private Button btn_ingresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        adb = AppDataBase.getDataBase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        cargarUsuarios();
        insertarUsuarioPorDefecto();
        txt_usuario = findViewById(R.id.txt_usuario);
        txt_contraseña = findViewById(R.id.txt_contraseña);
        btn_ingresar = findViewById(R.id.btn_ingresar);
        btn_ingresar.setOnClickListener(v -> control());
    }

    private void control() {
        if (txt_usuario.getText().toString().isEmpty()) {
            txt_usuario.setError("Campo obligatorio");
            txt_usuario.requestFocus();
            return;
        }
        else if (txt_contraseña.getText().toString().isEmpty()) {
            txt_contraseña.setError("Campo obligatorio");
            txt_contraseña.requestFocus();
            return;
        }
        else {
            String nickname = txt_usuario.getText().toString().trim();
            String password = txt_contraseña.getText().toString().trim();
            String email = nickname;

            validarDatos(nickname, password, email);
        }
    }
    private void insertarUsuarioPorDefecto() {
        executorService.execute(() -> {
            List<Usuario> usuarios = adb.usuarioDAO().getUsuariosRaw();
            if (usuarios == null || usuarios.isEmpty()) {
                Usuario admin = new Usuario(
                        "Admin",
                        "Principal",
                        "admin",
                        "admin",
                        "admin@correo.com",
                        "Administrador",
                        false
                );
                adb.usuarioDAO().insert(admin);
                System.out.println("Usuario admin insertado.");
            } else {
                System.out.println("Usuarios ya existentes.");
            }
        });
    }



    private void validarDatos(String nickname, String password, String email) {
        executorService.execute(() -> {
            Usuario usuario = adb.usuarioDAO().getUsuarioValidoRaw(nickname, email);

            System.out.println("Buscando usuario con nickname/email: " + nickname + " / " + email);

            if (usuario == null) {
                System.out.println("Usuario no encontrado.");
                runOnUiThread(() -> {
                    txt_usuario.setError("Usuario no encontrado");
                    txt_usuario.requestFocus();
                });
                return;
            }

            System.out.println("Usuario encontrado: " + usuario.getNickname() + " - " + usuario.getPassword());

            if ((usuario.getNickname().equals(nickname) || usuario.getEmail().equals(email)) &&
                    usuario.getPassword().equals(password)) {
                System.out.println("Login exitoso.");
                runOnUiThread(() -> {
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                });
            } else {
                System.out.println("Contraseña incorrecta: ingresada=" + password + " / real=" + usuario.getPassword());
                runOnUiThread(() -> {
                    txt_contraseña.setError("Contraseña incorrecta");
                    txt_contraseña.requestFocus();
                });
            }
        });
    }




    private void cargarUsuarios() {
        executorService.execute(() -> {
            usuarioList = adb.usuarioDAO().getUsuarios().getValue();
            if (usuarioList == null) {
                usuarioList = new ArrayList<>();
            }
        });
    }
}