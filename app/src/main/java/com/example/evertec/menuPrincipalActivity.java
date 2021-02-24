package com.example.evertec;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class menuPrincipalActivity extends AppCompatActivity {

    private Button pago, historial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        pago = (Button)findViewById(R.id.pago);
        historial = (Button)findViewById(R.id.historial);



        pago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pago = new Intent(menuPrincipalActivity.this, generarPagoActivity.class);
                startActivity(pago);
            }
        });

        historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historial = new Intent(menuPrincipalActivity.this,historialActivity.class);
                startActivity(historial);
            }
        });

    }
}
