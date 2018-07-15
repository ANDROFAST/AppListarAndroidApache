package com.androidmorefast.moden.ws_insertar;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText dni;
    private EditText nombre;
    private EditText telefono;
    private EditText email;
    private Button insertar;
    private Button mostrar;
    private ImageButton mas;
    private ImageButton menos;
    private int posicion=0;

    private List<Personas> listaPersonas;
    private Personas personas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_main);

        listaPersonas=new ArrayList<Personas>();
        dni= (EditText)findViewById(R.id.txtDni);
        nombre= (EditText)findViewById(R.id.txtNombre);
        telefono = (EditText)findViewById(R.id.txtTelefono);
        email = (EditText)findViewById(R.id.txtEmail);
        insertar = (Button)findViewById(R.id.btnInsertar);
        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!dni.getText().toString().trim().equalsIgnoreCase("")||
                        !nombre.getText().toString().trim().equalsIgnoreCase("")||
                        !telefono.getText().toString().trim().equalsIgnoreCase("")||
                        !email.getText().toString().trim().equalsIgnoreCase(""))

                    new Insertar(MainActivity.this).execute();

                else
                    Toast.makeText(MainActivity.this, "Hay información por rellenar", Toast.LENGTH_LONG).show();
            }
        });

        //Mostrar boton
        mostrar = (Button)findViewById(R.id.btnMostrar);
        mostrar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Mostrar().execute();
            }
        });
        mas=(ImageButton)findViewById(R.id.btnMas);
        mas.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(!listaPersonas.isEmpty()){
                    if(posicion>=listaPersonas.size()-1){
                        posicion=listaPersonas.size()-1;
                        mostrarPersona(posicion);
                    }else{
                        posicion++;

                        mostrarPersona(posicion);
                    }
                }
            }

        });
        //Se mueve por nuestro ArrayList mostrando el objeto anterior
        menos=(ImageButton)findViewById(R.id.btnMenos);

        menos.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(!listaPersonas.isEmpty()){
                    if(posicion<=0){
                        posicion=0;
                        mostrarPersona(posicion);
                    }
                    else{
                        posicion--;
                        mostrarPersona(posicion);
                    }
                }
            }
        });
    }


    //Insertamos los datos a nuestra webService
    private boolean insertar(){
        HttpClient httpClient;
        List<NameValuePair> nameValuePairs;
        HttpPost httpPost;
        httpClient = new DefaultHttpClient();
        httpPost = new HttpPost("http://192.168.1.33/ws_insertar/insertar.php");//url del servidor
        //empezamos añadir nuestros datos
        nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("dni",dni.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("nombre",nombre.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("telefono",telefono.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("email",email.getText().toString().trim()));
        try {
             httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpClient.execute(httpPost);
            return true;


        } catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (ClientProtocolException e){
            e.printStackTrace();

        }catch (IOException e){
            e.printStackTrace();
        }
        return  false;
    }
    //AsyncTask para insertar Personas
    class Insertar extends AsyncTask<String,String,String> {

        private Activity context;

        Insertar(Activity context){
            this.context=context;
        }

        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            if(insertar())
                context.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Toast.makeText(context, "Persona insertada con éxito", Toast.LENGTH_LONG).show();
                        nombre.setText("");
                        dni.setText("");
                        telefono.setText("");
                        email.setText("");
                    }
                });
            else
                context.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Toast.makeText(context, "Persona no insertada con éxito", Toast.LENGTH_LONG).show();
                    }
                });
            return null;
        }
    }
    private String mostrar(){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://192.168.1.33/ws_insertar/listar.php");
        String resultado="";
        HttpResponse response;
        try {
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream instream = entity.getContent();
            resultado= convertStreamToString(instream);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultado;
    }

    private String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    private boolean filtrarDatos(){
        listaPersonas.clear();
        String data=mostrar();
        if(!data.equalsIgnoreCase("")){
            JSONObject json;
            try {
                json = new JSONObject(data);
                JSONArray jsonArray = json.optJSONArray("personas");
                for (int i = 0; i < jsonArray.length(); i++) {
                    personas=new Personas();
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    personas.setDni(jsonArrayChild.optString("dni"));
                    personas.setNombre(jsonArrayChild.optString("nombre"));
                    personas.setTelefono(jsonArrayChild.optString("telefono"));
                    personas.setEmail(jsonArrayChild.optString("email"));
                    listaPersonas.add(personas);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
    private  void mostrarPersona(final int posicion){
    //private void mostrarPersona(final int posicion){
        runOnUiThread(new Runnable(){

            public void run() {
                if (listaPersonas != null && listaPersonas.size() != 0) {
                    Personas personas = listaPersonas.get(posicion);

                    nombre.setText(personas.getNombre());
                    dni.setText(personas.getDni());
                    telefono.setText(personas.getTelefono());
                    email.setText(personas.getEmail());
                }
            }
        });
    }
    class Mostrar extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            if(filtrarDatos())mostrarPersona(posicion);
            return null;
        }
    }


}
