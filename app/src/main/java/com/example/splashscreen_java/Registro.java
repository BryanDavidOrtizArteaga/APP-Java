package com.example.splashscreen_java;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.*;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.ProgressBar;

public class Registro extends AppCompatActivity {

    EditText Et_correo, Et_contrasena, Et_confirmar_contrasena;
    Button Btn_registar_usuario;
    TextView Txt_login;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    String correo = "" , contrasena = "" , confirmar_contrasena = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Registrar");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        Et_correo = findViewById(R.id.Et_correo);
        Et_contrasena = findViewById(R.id.Et_contrasena);
        Et_confirmar_contrasena = findViewById(R.id.Et_confirmar_contrasena);
        Btn_registar_usuario = findViewById(R.id.Btn_registar_usuario);
        Txt_login = findViewById(R.id.Txt_login);

        firebaseAuth = FirebaseAuth.getInstance();

        Btn_registar_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidarDatos();
            }
        });

        Txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registro.this, Login_email.class));
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void ValidarDatos(){
        correo = Et_correo.getText().toString().trim();
        contrasena = Et_contrasena.getText().toString().trim();
        confirmar_contrasena = Et_confirmar_contrasena.getText().toString().trim();

        if (TextUtils.isEmpty(correo)){
            Et_correo.setError("Enter your email");
            Et_correo.requestFocus();
        }
        else if (TextUtils.isEmpty(contrasena)){
            Et_contrasena.setError("Enter your password");
            Et_contrasena.requestFocus();
        }
        else if (TextUtils.isEmpty(confirmar_contrasena)) {
            Et_confirmar_contrasena.setError("Enter your password");
            Et_confirmar_contrasena.requestFocus();
        }

        else if (!contrasena.equals(confirmar_contrasena)) {
            Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show();

        } else {
            CrearCuenta();
        }
    }

    private void CrearCuenta() {

        firebaseAuth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressBar.setVisibility(View.GONE);

                        GuardarInformacion();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Registro.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void GuardarInformacion() {

        progressBar.setVisibility(View.VISIBLE);

        String uid = firebaseAuth.getUid();

        HashMap<String, String> Datos = new HashMap<>();
        Datos.put("uid", uid);
        Datos.put("correo", correo);
        Datos.put("contraseña", contrasena);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        assert uid != null;
        databaseReference.child(uid)
                .setValue(Datos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Registro.this, "Account created", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Registro.this, MenuPrincipal.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Registro.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return true;
    }

}
