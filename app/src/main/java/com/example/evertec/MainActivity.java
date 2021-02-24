package com.example.evertec;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button loginBoton;
    private EditText loginUser, loginPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBoton = (Button)findViewById(R.id.loginBoton);
        loginUser = (EditText)findViewById(R.id.loginUser);
        loginPass = (EditText)findViewById(R.id.loginPass);

        loginBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if("evertec".equals(loginPass.getText().toString()) && "evertec".equals(loginUser.getText().toString())){
                    Intent menu = new Intent(MainActivity.this,menuPrincipalActivity.class);
                    startActivity(menu);
                }else{
                    Toast.makeText(getApplicationContext(), "USUARIO O CLAVE INCORRECTOS", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
