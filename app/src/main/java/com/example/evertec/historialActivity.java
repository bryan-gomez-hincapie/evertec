package com.example.evertec;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class historialActivity extends AppCompatActivity {

    //HISTORIAL
    private Button btBuscar,btBuscarReference;
    private EditText etReference, etReference2,etHtotal;
    private Spinner spinnerCurrency;
    //VARIABLES HISTORIAL
    String nonce, tranKey, seed, referencia, referencia2,myUrl;

    //POP
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button popOutH, popModH;
    private TextView tvDatosH,tvValoresH;
    //VARIABLES POP
    String uno,dos,tres,cuatro,cinco,seis,siete,ocho,nueve,diez,once,variable,Htotal,Hcurrency;
    Integer doce;

    //METODO
    codificador cod = new codificador();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        //HISTORIAL
        btBuscar = (Button)findViewById(R.id.btBuscar);
        etReference = (EditText)findViewById(R.id.etReference);

        btBuscarReference = (Button)findViewById(R.id.btBuscarReference);
        etReference2 = (EditText) findViewById(R.id.etReference2);
        etHtotal = (EditText)findViewById(R.id.etHtotal);

        spinnerCurrency = (Spinner)findViewById(R.id.spinnerCurrency);
        String [] opciones = {"Tipo de moneda", "COP", "USD", "EUR"};
        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,opciones);
        spinnerCurrency.setAdapter(adapter);

        btBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                referencia = etReference.getText().toString();
                if("".equals(referencia)){
                    Toast.makeText(getApplicationContext(), "Llenar el campo para la busqueda por Internal Reference", Toast.LENGTH_LONG).show();
                }else{
                    //CREACION DEL TRANKEY NONCE Y SEED
                    tranKey = cod.tranKey();
                    nonce = cod.nonce();
                    seed = cod.fecha();

                    variable = "query";

                    //CONSULTA AL SERVIDOR
                    myUrl = "https://dev.placetopay.com/rest/"+"gateway/query";
                    new consultaT().execute(myUrl);
                }
            }
        });

        btBuscarReference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                referencia2 = etReference2.getText().toString();
                Htotal =  etHtotal.getText().toString();
                Hcurrency = spinnerCurrency.getSelectedItem().toString();

                if("".equals(referencia2) || "".equals(Htotal) || "".equals(Hcurrency)){
                    Toast.makeText(getApplicationContext(), "Llenar los campos para la busqueda por reference", Toast.LENGTH_LONG).show();
                }else{
                    //CREACION DEL TRANKEY NONCE Y SEED
                    tranKey = cod.tranKey();
                    nonce = cod.nonce();
                    seed = cod.fecha();

                    variable = "search";

                    //CONSULTA AL SERVIDOR
                    myUrl = "https://dev.placetopay.com/rest/"+"gateway/search";
                    new consultaT().execute(myUrl);
                }
            }
        });
    }
    //REST INFORMATION REQUEST
    private class consultaT extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... param) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return consultaTUrl(param[0]);
            } catch (IOException | JSONException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if("query".equals(variable) || "search".equals(variable)){
                transacion();
            }else{
                Toast.makeText(getApplicationContext(), "Estado modificado", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String consultaTUrl(String myurl) throws IOException, JSONException {
        //PETICION POST
        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        //JSONOBJECT
        JSONObject principal = new JSONObject();

        JSONObject auth = new JSONObject();
        JSONObject amount = new JSONObject();

        //AUTH
        auth.put("login", "6dd490faf9cb87a9862245da41170ff2");
        auth.put("tranKey", tranKey);
        auth.put("nonce", nonce);
        auth.put("seed", seed);

        //INFORMATION REQUEST
        principal.put("auth", auth);

        if("transaction".equals(variable)){
            principal.put("internalReference",doce);
            principal.put("authorization",ocho);
            principal.put("action","reverse");
        }else if("query".equals(variable)){
            principal.put("internalReference",referencia);
        }else if("search".equals(variable)){
            amount.put("currency",Hcurrency);
            amount.put("total",Htotal);
            principal.put("reference",referencia2);
            principal.put("amount",amount);
        }

        System.out.println("JSON Peticion CONSULTA TRANSACCIÓN:" + principal);

        OutputStream os = conn.getOutputStream();
        os.write(principal.toString().getBytes());
        os.flush();

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        //OBTENER JSON DEL SERVIDOR
        StringBuilder responseC = new StringBuilder();
        try(BufferedReader br = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                responseC.append(responseLine.trim());
            }
            //System.out.println("Json:" + response.toString());
        }

        JSONObject respuestaC = new JSONObject(responseC.toString());
        System.out.println("JSON Respuesta:" + respuestaC);

        if ("query".equals(variable)) {
            //Variables que se mostraran de la transacción
            uno = respuestaC.getJSONObject("status").getString("date");
            dos = respuestaC.getJSONObject("amount").getString("currency") + respuestaC.getJSONObject("amount").getDouble("total");
            tres = "USD $" + respuestaC.getJSONObject("additional").getDouble("interestAmount");
            cuatro = "USD $" + respuestaC.getJSONObject("additional").getDouble("totalAmount");
            cinco = "USD $" + respuestaC.getJSONObject("additional").getDouble("installmentAmount");
            seis = respuestaC.getString("provider");
            siete = respuestaC.getJSONObject("additional").getJSONObject("credit").getString("installments");
            ocho = respuestaC.getString("authorization");
            nueve = respuestaC.getString("receipt");
            diez = respuestaC.getString("issuerName");
            once = respuestaC.getJSONObject("status").getString("message");
            doce = respuestaC.getInt("internalReference");
        }else if("search".equals(variable)){
            //Variables que se mostraran de la transacción
            JSONArray transactionsA = respuestaC.getJSONArray("transactions");
            JSONObject transactionsO = transactionsA.getJSONObject(0);
            System.out.println(transactionsO);
            uno = transactionsO.getString("date");
            dos = transactionsO.getJSONObject("amount").getString("currency") + transactionsO.getJSONObject("amount").getDouble("total");
            tres = "USD $" + transactionsO.getJSONObject("additional").getDouble("interestAmount");
            cuatro = "USD $" + transactionsO.getJSONObject("additional").getDouble("totalAmount");
            cinco = "USD $" + transactionsO.getJSONObject("additional").getDouble("installmentAmount");
            seis = transactionsO.getString("provider");
            siete = transactionsO.getJSONObject("additional").getJSONObject("credit").getString("installments");
            ocho = transactionsO.getString("authorization");
            nueve = transactionsO.getString("receipt");
            diez = transactionsO.getString("issuerName");
            once = transactionsO.getJSONObject("status").getString("message");
            doce = transactionsO.getInt("internalReference");
        }

        conn.disconnect();
        return "res";
    }

    //66889484 - 8392 - 3000 - USD
    public void transacion() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popView = getLayoutInflater().inflate(R.layout.pophistorial, null);

        tvDatosH = (TextView) popView.findViewById(R.id.tvDatosH);
        tvValoresH = (TextView) popView.findViewById(R.id.tvValoresH);
        popOutH = (Button) popView.findViewById(R.id.popOutH);
        popModH = (Button) popView.findViewById(R.id.popModH);

        tvDatosH.setText("Fecha: " + "\nMonto Original: " + "\nInterés" + "\nTotal pagado: " + "\nValor cuota:" +
                "\nProveedor: " + "\nMeses plazo: " + "\nAutorizacion/CUS: " + "\nRecibo: " + "\nBanco: " +
                "\nEstado: " + "\nCódigo Respuesta: ");
        tvValoresH.setText(uno + "\n" + dos + "\n" + tres + "\n" + cuatro + "\n" + cinco + "\n" + seis + "\n" +
                siete + "\n" + ocho + "\n" + nueve + "\n" + diez + "\n"  + once + "\n"+doce);

        dialogBuilder.setView(popView);
        dialog = dialogBuilder.create();
        dialog.show();

        popOutH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        popModH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                variable = "transaction";

                //CREACION DEL TRANKEY NONCE Y SEED
                tranKey = cod.tranKey();
                nonce = cod.nonce();
                seed = cod.fecha();

                //CONSULTA AL SERVIDOR
                myUrl = "https://dev.placetopay.com/rest/"+"gateway/transaction";
                new consultaT().execute(myUrl);
            }
        });
    }
}
