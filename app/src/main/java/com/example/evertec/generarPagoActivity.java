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
import android.widget.LinearLayout;
import android.widget.Space;
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

public class generarPagoActivity extends AppCompatActivity {

    //ELEMENTOS INFORMATION REQUEST
    private Button bt;
    private TextView prueba;
    private EditText etTarjeta, etpago;
    private Spinner spinner;
    private TextView tvPagador, tvComprador, etCodT, etYearT, etMonth;
    private EditText etEmailComprador, etEmailPagador, etNameComprador, etNamePagador;
    private LinearLayout infoTarget;

    //ELEMENTOS credits displayInterest requireOtp
    private Spinner spinnerC;
    private TextView tvcalcular, tvInfoCredits;
    private Button btCalcularInteres,btProcessPago;
    private Space s1,s2,s3,s4;

    //ELEMENTOS OTP
    private Space s5,s6;
    private Button bGOtp,bVOtp;
    private LinearLayout llOTP;
    private TextView tvOTP;
    private EditText etOTP;

    //POP
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private TextView popTarget,popTotal,popValor,popDatos;
    private Button popOut;

    //DECLARACION DE VARIABLES
    String nonce, tranKey, seed, myUrl, myUrlI, myUrlGOtp, myUrlVOtp, myUrlP, IP, message,date,expirationMonth,expirationYear,cvv;

    //VARIABLES DE LA TARJEA
    String tarjeta, reference, moneda;
    double pago;

    //VARIABLES DE RESPUESTA INFORMATION REQUEST
    JSONArray installments = null;
    String status, description;
    Boolean displayInterest = null,requireOtp=null;

    //VARIABLES DEL CALCULAR INTERES
    JSONObject valuesO=null;
    Double original,interest,total,installment;
    Integer cuotas;

    //VARIABLES OTP
    String provider,signature;
    Boolean validated;

    //PROCESS
    String nPagador,ePagador,nComprador,eComprador;
    String uno,dos,tres,cuatro,cinco,seis,siete,ocho,diez,once,doce;
    Integer nueve, trece;

    //VARIABLES DEL JSON
    JSONArray values = null, statusA = null, creditsA = null;
    JSONObject creditsO=null, statusO=null;

    //METODO
    codificador cod = new codificador();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_pago);

        //DECLARACION DE ELEMENTOS DEL XML
        //ELEMENTOS INFORMATION REQUEST
        etTarjeta = (EditText)findViewById(R.id.tarjeta);
        etpago = (EditText)findViewById(R.id.vpago);
        bt = (Button) findViewById(R.id.bt);
        infoTarget = (LinearLayout)findViewById(R.id.infoTarget);
        etCodT = (EditText)findViewById(R.id.etCodT);
        etYearT = (EditText)findViewById(R.id.etYearT);
        etMonth = (EditText)findViewById(R.id.etMonth);

        //COMPRADOR
        tvComprador = (TextView)findViewById(R.id.tvComprador);
        etNameComprador = (EditText) findViewById(R.id.etNameComprador);
        etEmailComprador = (EditText)findViewById(R.id.etEmailComprador);

        //PAGADOR
        tvPagador = (TextView)findViewById(R.id.tvPagador);
        etNamePagador = (EditText) findViewById(R.id.etNamePagador);
        etEmailPagador = (EditText)findViewById(R.id.etEmailPagador);

        spinner = (Spinner)findViewById(R.id.spinnerCurrency);
        String [] opciones = {"Tipo de moneda", "COP", "USD", "EUR"};
        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,opciones);
        spinner.setAdapter(adapter);

        //ELEMENTOS credits displayInterest requireOtp
        s1 = (Space)findViewById(R.id.s1);
        tvInfoCredits = (TextView)findViewById(R.id.tvInfoCredits);
        spinnerC = (Spinner)findViewById(R.id.credits);
        s2 = (Space)findViewById(R.id.s2);
        tvcalcular = (TextView)findViewById(R.id.tvcalcular);
        s3 = (Space)findViewById(R.id.s3);
        btCalcularInteres = (Button)findViewById(R.id.btCalcularInteres);
        btProcessPago = (Button)findViewById(R.id.btProcessPago);
        s4 = (Space)findViewById(R.id.s4);

        //ELEMENTOS OTP
        s5 = (Space)findViewById(R.id.s5);
        tvOTP = (TextView)findViewById(R.id.tvOTP);
        etOTP = (EditText)findViewById(R.id.etOTP);
        llOTP = (LinearLayout)findViewById(R.id.llOTP);
        bGOtp = (Button)findViewById(R.id.bGOtp);
        bVOtp = (Button)findViewById(R.id.bVOtp);
        s6 = (Space)findViewById(R.id.s6);

        //BOTON INFORMAR
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //INFORMACION INGRESADA
                tarjeta = etTarjeta.getText().toString();
                reference = cod.aleatorio();
                moneda = spinner.getSelectedItem().toString();
                pago = Double.parseDouble(etpago.getText().toString());
                expirationYear = etYearT.getText().toString();
                expirationMonth = etMonth.getText().toString();
                cvv = etCodT.getText().toString();


                //CREACION DEL TRANKEY NONCE Y SEED
                tranKey = cod.tranKey();
                nonce = cod.nonce();
                seed = cod.fecha();

                //CONSULTA AL SERVIDOR
                myUrl = "https://dev.placetopay.com/rest/"+"gateway/information";
                new InformationRequest().execute(myUrl);

            }
        });

        //CÁLCULO DE INTERES
        btCalcularInteres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //INFORMACION INGRESADA
                tarjeta = etTarjeta.getText().toString();
                reference = cod.aleatorio();
                moneda = spinner.getSelectedItem().toString();
                pago = Double.parseDouble(etpago.getText().toString());

                cuotas = Integer.parseInt(spinnerC.getSelectedItem().toString());

                //CREACION DEL TRANKEY NONCE Y SEED
                tranKey = cod.tranKey();
                nonce = cod.nonce();
                seed = cod.fecha();

                //CONSULTA AL SERVIDOR
                myUrlI = "https://dev.placetopay.com/rest/"+"gateway/interests";
                new calculoInteres().execute(myUrlI);
            }
        });

        //Generar OTP
        bGOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //INFORMACION INGRESADA
                tarjeta = etTarjeta.getText().toString();
                reference = cod.aleatorio();
                moneda = spinner.getSelectedItem().toString();
                pago = Double.parseDouble(etpago.getText().toString());

                //CREACION DEL TRANKEY NONCE Y SEED
                tranKey = cod.tranKey();
                nonce = cod.nonce();
                seed = cod.fecha();

                //CONSULTA AL SERVIDOR
                myUrlGOtp = "https://dev.placetopay.com/rest/"+"gateway/otp/generate";
                new OTPG().execute(myUrlGOtp);
            }
        });

        //Validar OTP
        bVOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //INFORMACION INGRESADA
                tarjeta = etTarjeta.getText().toString();
                reference = cod.aleatorio();
                moneda = spinner.getSelectedItem().toString();
                pago = Double.parseDouble(etpago.getText().toString());

                //CREACION DEL TRANKEY NONCE Y SEED
                tranKey = cod.tranKey();
                nonce = cod.nonce();
                seed = cod.fecha();

                //CONSULTA AL SERVIDOR
                myUrlVOtp = "https://dev.placetopay.com/rest/"+"gateway/otp/validate";
                new OTPV().execute(myUrlVOtp);
            }
        });

        //Process transation
        btProcessPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //INFORMACION INGRESADA
                tarjeta = etTarjeta.getText().toString();
                expirationYear = etYearT.getText().toString();
                expirationMonth = etMonth.getText().toString();
                cvv = etCodT.getText().toString();
                reference = cod.aleatorio();
                moneda = spinner.getSelectedItem().toString();
                pago = Double.parseDouble(etpago.getText().toString());
                cuotas = Integer.parseInt(spinnerC.getSelectedItem().toString());
                IP = cod.getIP();
                nPagador = etNamePagador.getText().toString();
                ePagador = etEmailPagador.getText().toString();
                nComprador = etNameComprador.getText().toString();
                eComprador = etEmailComprador.getText().toString();

                //CREACION DEL TRANKEY NONCE Y SEED
                tranKey = cod.tranKey();
                nonce = cod.nonce();
                seed = cod.fecha();

                //CONSULTA AL SERVIDOR
                String enlace = "";
                if(requireOtp.equals(true)){
                    enlace = "gateway/safe-process";
                }else{
                    enlace = "gateway/process";
                }
                myUrlP = "https://dev.placetopay.com/rest/"+enlace;
                System.out.println(myUrlP);
                new Process().execute(myUrlP);

            }
        });

    }

    //REST INFORMATION REQUEST
    private class InformationRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... param) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return InformationRequestUrl(param[0]);
            } catch (IOException | JSONException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if("OK".equals(status)){
                tvInfoCredits.setVisibility(View.VISIBLE);
                tvInfoCredits.setText("Número de cuotas en meses de " + description);

                spinnerC.setVisibility(View.VISIBLE);
                String [] Screditos = new String[installments.length()];
                for (int x=0; x<installments.length();x++){
                    try {
                        Screditos[x] = String.valueOf(installments.getInt(x));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ArrayAdapter <String> adapterz = new ArrayAdapter<String>(generarPagoActivity.this,android.R.layout.simple_list_item_1,Screditos);
                spinnerC.setAdapter(adapterz);

                s1.setVisibility(View.VISIBLE);
                s2.setVisibility(View.VISIBLE);
                s3.setVisibility(View.VISIBLE);
                s4.setVisibility(View.VISIBLE);


                if(displayInterest.equals(true) && requireOtp.equals(true)){
                    btCalcularInteres.setVisibility(View.VISIBLE);
                    llOTP.setVisibility(View.VISIBLE);
                    bGOtp.setVisibility(View.VISIBLE);


                }else if(requireOtp.equals(true)){
                    llOTP.setVisibility(View.VISIBLE);
                    bGOtp.setVisibility(View.VISIBLE);
                }else if (displayInterest.equals(true)) {
                    btCalcularInteres.setVisibility(View.VISIBLE);
                    btProcessPago.setVisibility(View.VISIBLE);
                    tvComprador.setVisibility(View.VISIBLE);
                    etNameComprador.setVisibility(View.VISIBLE);
                    etEmailComprador.setVisibility(View.VISIBLE);
                    tvPagador.setVisibility(View.VISIBLE);
                    etNamePagador.setVisibility(View.VISIBLE);
                    etEmailPagador.setVisibility(View.VISIBLE);
                    s6.setVisibility(View.VISIBLE);

                }else{
                    btProcessPago.setVisibility(View.VISIBLE);
                    tvComprador.setVisibility(View.VISIBLE);
                    etNameComprador.setVisibility(View.VISIBLE);
                    etEmailComprador.setVisibility(View.VISIBLE);
                    tvPagador.setVisibility(View.VISIBLE);
                    etNamePagador.setVisibility(View.VISIBLE);
                    etEmailPagador.setVisibility(View.VISIBLE);
                    s6.setVisibility(View.VISIBLE);
                }
            }else{
                Toast.makeText(getApplicationContext(), "Tarjeta no encontrada", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String InformationRequestUrl(String myurl) throws IOException, JSONException {
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

        JSONObject instrument = new JSONObject();
        JSONObject card = new JSONObject();

        JSONObject payment = new JSONObject();
        JSONObject amount = new JSONObject();

        //AUTH
        auth.put("login", "6dd490faf9cb87a9862245da41170ff2");
        auth.put("tranKey", tranKey);
        auth.put("nonce", nonce);
        auth.put("seed", seed);

        //INSTRUMENT
        //CARD
        card.put("number",tarjeta);
        instrument.put("card",card);

        //PAYMENT
        payment.put("reference",reference);
        amount.put("total",pago);
        amount.put("currency",moneda);
        payment.put("amount",amount);

        //INFORMATION REQUEST
        principal.put("auth", auth);
        principal.put("instrument",instrument);
        principal.put("payment",payment);

        System.out.println("JSON Peticion:" + principal);

        OutputStream os = conn.getOutputStream();
        os.write(principal.toString().getBytes());
        os.flush();

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        //OBTENER JSON DEL SERVIDOR
        StringBuilder response = new StringBuilder();
        try(BufferedReader br = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            //System.out.println("Json:" + response.toString());
        }

        JSONObject respuesta = new JSONObject(response.toString());
        System.out.println("JSON Respuesta:" + respuesta);

        creditsA = respuesta.getJSONArray("credits");
        creditsO = creditsA.getJSONObject(0);
        installments = creditsO.getJSONArray("installments");
        description = creditsO.getString("description");

        System.out.println(installments.length());

        statusO = respuesta.getJSONObject("status");
        status = statusO.getString("status");

        //PARAMETROS
        displayInterest = respuesta.getBoolean("displayInterest");
        requireOtp = respuesta.getBoolean("requireOtp");
        System.out.println(displayInterest);
        System.out.println(requireOtp);

        conn.disconnect();
        return "res";
    }

    //REST CALCULO INTERES
    private class calculoInteres extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... param) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return calculoInteresUrl(param[0]);
            } catch (IOException | JSONException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            tvcalcular.setVisibility(View.VISIBLE);
            String texto = "Los valores calculados para su tipo de crédito son:"
                            + "\nValor inicial:USD" + original
                            + "\nValor cuota:USD"   + installment
                            + "\nValor interes:USD" + interest
                            + "\nValor total:USD"   + total;
            tvcalcular.setText(texto);
        }
    }

    private String calculoInteresUrl(String myurl) throws IOException, JSONException {
        //PETICION POST
        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        //JSONOBJECT
        JSONObject principalI = new JSONObject();

        JSONObject auth = new JSONObject();

        JSONObject instrument = new JSONObject();
        JSONObject card = new JSONObject();

        JSONObject payment = new JSONObject();
        JSONObject amount = new JSONObject();

        JSONObject creditsCI = new JSONObject();

        //AUTH
        auth.put("login", "6dd490faf9cb87a9862245da41170ff2");
        auth.put("tranKey", tranKey);
        auth.put("nonce", nonce);
        auth.put("seed", seed);

        //INSTRUMENT
        //CARD
        card.put("number",tarjeta);
        instrument.put("card",card);

        //CREDIT
        creditsCI.put("code",creditsO.getInt("code"));
        creditsCI.put("type",creditsO.getString("type"));
        creditsCI.put("groupCode",creditsO.getString("groupCode"));
        creditsCI.put("installment",cuotas);
        instrument.put("credit",creditsCI);

        //PAYMENT
        payment.put("reference",reference);
        amount.put("total",pago);
        amount.put("currency",moneda);
        payment.put("amount",amount);

        //INFORMATION REQUEST
        principalI.put("auth", auth);
        principalI.put("instrument",instrument);
        principalI.put("payment",payment);

        System.out.println("JSON Peticion Interes:" + principalI);

        OutputStream os = conn.getOutputStream();
        os.write(principalI.toString().getBytes());
        os.flush();

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        //OBTENER JSON DEL SERVIDOR
        StringBuilder responseI = new StringBuilder();
        try(BufferedReader br = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                responseI.append(responseLine.trim());
            }
            //System.out.println("Json:" + response.toString());
        }

        JSONObject respuestaI = new JSONObject(responseI.toString());
        System.out.println("JSON Respuesta Interes:" + respuestaI);

        valuesO = respuestaI.getJSONObject("values");
        original = valuesO.getDouble("original");
        installment = valuesO.getDouble("installment");
        interest = valuesO.getDouble("interest");
        total = valuesO.getDouble("total");
        System.out.println("Values: " + valuesO);

        conn.disconnect();
        return "res";
    }

    //REST GENERATE OTP
    private class OTPG extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... param) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return OTPGUrl(param[0]);
            } catch (IOException | JSONException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "OTP generado, provedor " + provider, Toast.LENGTH_LONG).show();
            bGOtp.setVisibility(View.INVISIBLE);
            tvOTP.setVisibility(View.VISIBLE);
            etOTP.setVisibility(View.VISIBLE);
            bVOtp.setVisibility(View.VISIBLE);
        }
    }

    private String OTPGUrl(String myurl) throws IOException, JSONException {
        //PETICION POST
        URL url = new URL(myurl);
        System.out.println(url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        //JSONOBJECT
        JSONObject principalOTPG = new JSONObject();

        JSONObject auth = new JSONObject();

        JSONObject instrument = new JSONObject();
        JSONObject card = new JSONObject();

        JSONObject payment = new JSONObject();
        JSONObject amount = new JSONObject();

        //AUTH
        auth.put("login", "6dd490faf9cb87a9862245da41170ff2");
        auth.put("tranKey", tranKey);
        auth.put("nonce", nonce);
        auth.put("seed", seed);

        //INSTRUMENT
        //CARD
        card.put("number",tarjeta);
        instrument.put("card",card);

        //PAYMENT
        payment.put("reference",reference);
        amount.put("total",pago);
        amount.put("currency",moneda);
        payment.put("amount",amount);

        //INFORMATION REQUEST
        principalOTPG.put("auth", auth);
        principalOTPG.put("instrument",instrument);
        principalOTPG.put("payment",payment);

        System.out.println("JSON PeticionOTPG:" + principalOTPG);

        OutputStream os = conn.getOutputStream();
        os.write(principalOTPG.toString().getBytes());
        os.flush();

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        //OBTENER JSON DEL SERVIDOR
        StringBuilder responseOTPG = new StringBuilder();
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                responseOTPG.append(responseLine.trim());
            }
            //System.out.println("Json:" + response.toString());
        }

        JSONObject respuestaOTPG = new JSONObject(responseOTPG.toString());
        System.out.println("JSON RespuestaOTP:" + respuestaOTPG);

        provider = respuestaOTPG.getString("provider");

        conn.disconnect();
        return "res";
    }

    //REST VALIDACION OTP
    private class OTPV extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... param) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return OTPVUrl(param[0]);
            } catch (IOException | JSONException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(getApplicationContext(), "OTP generado, provedor " + provider, Toast.LENGTH_LONG).show();
            tvComprador.setVisibility(View.VISIBLE);
            etNameComprador.setVisibility(View.VISIBLE);
            etEmailComprador.setVisibility(View.VISIBLE);
            tvPagador.setVisibility(View.VISIBLE);
            etNamePagador.setVisibility(View.VISIBLE);
            etEmailPagador.setVisibility(View.VISIBLE);
            btProcessPago.setVisibility(View.VISIBLE);
            s6.setVisibility(View.VISIBLE);

        }
    }

    private String OTPVUrl(String myurl) throws IOException, JSONException {
        //PETICION POST
        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        //JSONOBJECT
        JSONObject principalOTPV = new JSONObject();

        JSONObject auth = new JSONObject();

        JSONObject instrument = new JSONObject();
        JSONObject card = new JSONObject();

        JSONObject payment = new JSONObject();
        JSONObject amount = new JSONObject();

        //AUTH
        auth.put("login", "6dd490faf9cb87a9862245da41170ff2");
        auth.put("tranKey", tranKey);
        auth.put("nonce", nonce);
        auth.put("seed", seed);

        //INSTRUMENT
        //CARD
        card.put("number",tarjeta);
        instrument.put("card",card);
        instrument.put("otp",etOTP.getText().toString());

        //PAYMENT
        payment.put("reference",reference);
        amount.put("total",pago);
        amount.put("currency",moneda);
        payment.put("amount",amount);

        //INFORMATION REQUEST
        principalOTPV.put("auth", auth);
        principalOTPV.put("instrument",instrument);
        principalOTPV.put("payment",payment);

        System.out.println("JSON PeticionOTPV:" + principalOTPV);

        OutputStream os = conn.getOutputStream();
        os.write(principalOTPV.toString().getBytes());
        os.flush();

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        //OBTENER JSON DEL SERVIDOR
        StringBuilder responseOTPV = new StringBuilder();
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                responseOTPV.append(responseLine.trim());
            }
            //System.out.println("Json:" + response.toString());
        }

        JSONObject respuestaOTPV = new JSONObject(responseOTPV.toString());
        System.out.println("JSON RespuestaOTP:" + respuestaOTPV);

        provider = respuestaOTPV.getString("provider");
        signature = respuestaOTPV.getString("signature");
        System.out.println(signature);
        validated = respuestaOTPV.getBoolean("validated");

        conn.disconnect();
        return "res";
    }

    //REST PROCESS
    private class Process extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... param) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return ProcessUrl(param[0]);
            } catch (IOException | JSONException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            transacion();
            /*Intent out = new Intent(generarPagoActivity.this,menuPrincipalActivity.class);
            startActivity(out);*/
        }
    }

    private String ProcessUrl(String myurlP) throws IOException, JSONException {
        //PETICION POST
        URL url = new URL(myurlP);
        System.out.println(myUrlP);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        //JSONOBJECT
        JSONObject principalP = new JSONObject();

        JSONObject auth = new JSONObject();

        JSONObject instrument = new JSONObject();
        JSONObject card = new JSONObject();

        JSONObject payment = new JSONObject();
        JSONObject amount = new JSONObject();

        JSONObject creditsP = new JSONObject();

        JSONObject payer = new JSONObject();
        JSONObject buyer = new JSONObject();

        //AUTH
        auth.put("login", "6dd490faf9cb87a9862245da41170ff2");
        auth.put("tranKey", tranKey);
        auth.put("nonce", nonce);
        auth.put("seed", seed);

        //PAYMENT
        payment.put("reference",reference);
        amount.put("total",pago);
        amount.put("currency",moneda);
        payment.put("amount",amount);

        //INSTRUMENT
        //CARD
        card.put("number",tarjeta);
        card.put("expirationMonth", expirationMonth);
        card.put("expirationYear",expirationYear);
        card.put("cvv",cvv);
        instrument.put("card",card);

        //CREDIT
        creditsP.put("code",creditsO.getInt("code"));
        creditsP.put("type",creditsO.getString("type"));
        creditsP.put("groupCode",creditsO.getString("groupCode"));
        creditsP.put("installment",cuotas.toString());
        instrument.put("credit",creditsP);

        //OTP
        if(requireOtp.equals(true)){
            instrument.put("otp",etOTP.getText().toString());
        }

        //PAYER
        payer.put("name",nPagador);
        payer.put("email",ePagador);

        //BUYER
        buyer.put("name",nComprador);
        buyer.put("email",eComprador);

        //INFORMATION REQUEST
        principalP.put("auth", auth);
        principalP.put("locale", "es_CO");
        principalP.put("payment",payment);
        principalP.put("ipAddress",IP);
        principalP.put("instrument",instrument);
        principalP.put("payer",payer);
        principalP.put("buyer",buyer);

        System.out.println("JSON Peticion PROCESS:" + principalP);

        OutputStream os = conn.getOutputStream();
        os.write(principalP.toString().getBytes());
        os.flush();

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        //OBTENER JSON DEL SERVIDOR
        StringBuilder responseP = new StringBuilder();
        try(BufferedReader br = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                responseP.append(responseLine.trim());
            }
            //System.out.println("Json:" + response.toString());
        }

        JSONObject respuesta = new JSONObject(responseP.toString());
        System.out.println("JSON Respuesta PROCESS:" + respuesta);

        JSONObject statusP = respuesta.getJSONObject("status");
        message = statusP.getString("message");
        date = statusP.getString("date");

        //Variables que se mostraran de la transacción
        uno = date;
        dos = "USD $" + pago;
        tres = "USD $" + interest.toString();
        cuatro = "USD $" + total.toString();
        cinco = "USD $" + installment.toString();
        seis = description;
        siete = cuotas.toString();
        ocho = respuesta.getString("authorization");
        nueve = respuesta.getInt("receipt");
        diez = respuesta.getString("issuerName");
        once = respuesta.getString("reference");
        doce = statusP.getString("message");
        trece = respuesta.getInt("internalReference");

        conn.disconnect();
        return "res";
    }

    public void transacion() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popView = getLayoutInflater().inflate(R.layout.popup, null);

        popTarget = (TextView) popView.findViewById(R.id.popTarget);
        popTotal = (TextView) popView.findViewById(R.id.popTotal);
        popDatos = (TextView) popView.findViewById(R.id.popDatos);
        popValor = (TextView) popView.findViewById(R.id.popValores);
        popOut = (Button) popView.findViewById(R.id.popOutH);

        popTarget.setText("Tarjeta: " + tarjeta);
        popTotal.setText("Valor a pagar:USD $"+total.toString());
        popDatos.setText("Fecha: " + "\nMonto Original: " + "\nInterés" + "\nTotal pagado: " + "\nValor cuota:" +
                "\nTipo crédito: " + "\nMeses plazo: " + "\nAutorizacion/CUS: " + "\nRecibo: " + "\nBanco: " +
                "\nDireccion IP: " + "\nEstado: " + "\nCódigo Respuesta: " + "\nReference: ");
        popValor.setText(uno + "\n" + dos + "\n" + tres + "\n" + cuatro + "\n" + cinco + "\n" + seis + "\n" +
                         siete + "\n" + ocho + "\n" + nueve + "\n" + diez + "\n" + IP + "\n" + doce + "\n"+trece + "\n"+once);

        dialogBuilder.setView(popView);
        dialog = dialogBuilder.create();
        dialog.show();

        popOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent out = new Intent(generarPagoActivity.this,menuPrincipalActivity.class);
                startActivity(out);
            }
        });
    }
}
