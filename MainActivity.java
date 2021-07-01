package com.example.practica4;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.practica4.SQlite_OpenHelper.SQlite_OpenHelper;
import com.example.practica4.interfaces.api;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//7.1 hacer que el mainActivity implemente ViewOnClickListener
public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final Object URL ="http://192.168.56.1:80/developeru/insertar.php";
    //variables BD
    SQLiteDatabase database;
    SQLiteOpenHelper openhelper;

//6.1 Llenar el spinner
    Spinner categoria;
    final String[] datos = new String[]{"Categoría","Actividad Física","Trabajo","Compras", "Recreativo","Otros"};
    ArrayAdapter<String> adaptador;
//1.2 INTENT definir variables de controles
EditText txtName, txtLast, txtPhone, txtFecha, txtHora;
Button btnGuardar, btnBuscar, btnEliminar, btnActualizar, btnLista;
    EditText buscador;

    //7.2 variables para fecha y hora
    EditText etFecha, etHora;
    ImageButton ibObtenerFecha, ibObtenerHora;

    //8.1 definir variables
    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";
    private static final String BARRA = "/";
    public final Calendar c = Calendar.getInstance();

    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //openhelper BD
        openhelper = new SQlite_OpenHelper(getApplicationContext());


//6.2   agregar al oncreate
        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, datos);
        categoria = (Spinner) findViewById(R.id.sp_category);
        categoria.setAdapter(adaptador);
// 7.3 referenciar los controles de layout
        etFecha =  (EditText) findViewById(R.id.et_mostrar_fecha_picker);
        etHora =  (EditText) findViewById(R.id.et_mostrar_hora_picker);

        ibObtenerFecha = (ImageButton) findViewById(R.id.ib_obtener_fecha);
        ibObtenerHora = (ImageButton) findViewById(R.id.ib_obtener_hora);

//7.4 agregar el evento setonclicklistener a los botones
        ibObtenerFecha.setOnClickListener(this);
        ibObtenerHora.setOnClickListener(this);
//1.3 INTENT agregue el findview a los controles previos
        txtName = findViewById(R.id.txtName);
        txtLast = findViewById(R.id.txtLastName);
        txtPhone = findViewById(R.id.txtPhone);
       // txtFecha = findViewById(R.id.et_mostrar_fecha_picker);
       // txtHora = findViewById(R.id.et_mostrar_hora_picker);

        btnGuardar = findViewById(R.id.guardar);
        btnGuardar.setOnClickListener(this);

        btnBuscar = findViewById(R.id.buscar);
        btnBuscar.setOnClickListener(this);
        buscador = findViewById(R.id.busca);

        btnEliminar = findViewById(R.id.eliminar);
        btnEliminar.setOnClickListener(this);

        btnActualizar = findViewById(R.id.actualizar);
        btnActualizar.setOnClickListener(this);

    }

    //get data

    //metodo insertar datos en bd
    public void insertData(Cita cita){
        database = openhelper.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("nombre",cita.getNombre());
        valores.put("apellido",cita.getApellido());
        valores.put("telefono",cita.getTelefono());
        valores.put("fecha",cita.getFecha());
        valores.put("hora",cita.getHora());
        valores.put("category",cita.getCategory());
        long id = database.insert("citas", null,valores);
    }

    //1.5 AGREGAR METODO QUE HARA CAMBIO DE ACTIVITY
    //public void cambioActivity(View v){
    //}
    //7.5 definir logica del evento setonclicklistener
    @Override
    public void onClick(View view) {
        ejecutarServicio((String) URL);
//8 DEFINIR LOGICA PARA OBTENER FECHA Y HORA

//8.2 codificar logica para extraer la fecha y la hora
    if(view == ibObtenerFecha){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                etFecha.setText(dayOfMonth+BARRA+(monthOfYear+1)+BARRA+year);
            }
        }
                ,dia,mes,anio);
        datePickerDialog.show();
    }

        if(view == ibObtenerHora){

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                    etHora.setText(hourOfDay+DOS_PUNTOS+minute);
                }
            }
                    ,hora,minuto,false);
            timePickerDialog.show();
        }

        if(view.getId()==R.id.guardar){

            Intent i = new Intent(this, ListDatesActivity.class);
            //4 agregar el metodo donde se agrego intent:
            i.putExtra("cita",getDataUI());

//GUARDAN DATOS DE DIFERENTE FORMA EL METODO GUARDA EN LA BD Y EL STARTACTIVITY GUARDA TEMPORALMENTE
            //llamar metodo insert data
            insertData(getDataUI());//metodo guardar datos
            //inicializar con el metodo start Activity el intent (i)
            startActivity(i);
            clear();
        }

        if(view.getId()==R.id.buscar){
            consultar();
        }

        if(view.getId()==R.id.eliminar){
            elimina();
        }

        if(view.getId()==R.id.actualizar){
            actualiza();
        }
    }

    private void actualiza(){
        SQLiteDatabase db = openhelper.getWritableDatabase();
        String[] parametros = {buscador.getText().toString()};
        ContentValues valores = new ContentValues();

        valores.put("nombre", txtName.getText().toString());
        valores.put("apellido", txtLast.getText().toString());
        valores.put("telefono", txtPhone.getText().toString());
        valores.put("fecha", etFecha.getText().toString());
        valores.put("hora", etHora.getText().toString());
        valores.put("category", categoria.getSelectedItem().toString());

        db.update("citas",valores,"id=?",parametros);
        Toast.makeText(getApplicationContext(), "DATO ACTUALIZADO", Toast.LENGTH_LONG).show();
        buscador.setText("");
        clear();
        db.close();
    }

    public void consultar(){

        SQLiteDatabase db = openhelper.getWritableDatabase();
        String[] parametros = {buscador.getText().toString()};
        String [] campos = {"id","nombre", "apellido","telefono", "fecha","hora","category"};

        try {

            Cursor cursor = db.query("citas",campos,"nombre=?",parametros,null,null,null);
            if (cursor.moveToFirst())
            {
                do {

                    Toast.makeText(this,"DATO ENCONTRADO", Toast.LENGTH_SHORT).show();

                    buscador.setText(cursor.getString(0));
                    txtName.setText(cursor.getString(1));
                    txtLast.setText(cursor.getString(2));
                    txtPhone.setText(cursor.getString(3));

                    etFecha.setText(cursor.getString(4));
                    etHora.setText(cursor.getString(5));

                    if(cursor.getString(6) == "Categoría"){

                        categoria.setSelection(0);
                    }
                    if(cursor.getString(6) == "Actividad Física"){

                        categoria.setSelection(1);
                    }
                    if(cursor.getString(6) == "Trabajo"){

                        categoria.setSelection(2);
                    }
                    if(cursor.getString(6) == "Compras"){

                        categoria.setSelection(3);
                    }
                    if(cursor.getString(6) == "Otros"){

                        categoria.setSelection(4);
                    }


                }while(cursor.moveToNext());


            }else{ Toast.makeText(this,"DATO NO ENCONTRADO", Toast.LENGTH_SHORT).show();

            }
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "NO EXISTE ESE DATO", Toast.LENGTH_LONG).show();
            clear();
        }

    }
    private void clear()
    {
        txtName.setText("");
        txtLast.setText("");
        txtPhone.setText("");
        categoria.setSelection(0);
        etFecha.setText("");
        etHora.setText("");
    }

    private void elimina(){
        SQLiteDatabase db = openhelper.getWritableDatabase();
        String[] parametros = {buscador.getText().toString()};
        db.delete("citas","id=?",parametros);
        Toast.makeText(getApplicationContext(),"ELIMINADO", Toast.LENGTH_LONG).show();

        buscador.setText("");//manda un vacio a la variable buscador
        clear();//limpia la interfaz
        db.close();//cierra las operaciones
    }

    public Cita getDataUI(){
        return new Cita(txtName.getText().toString(),txtLast.getText().toString(), Integer.parseInt(txtPhone.getText().toString()),etFecha.getText().toString(),etHora.getText().toString(),categoria.getSelectedItem().toString());
    }

    private void ejecutarServicio(String URL){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "operacion exitoso", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<String, String>();
                parametros.put("nombre",txtName.getText().toString());
                parametros.put("apellido",txtLast.getText().toString());
                parametros.put("telefono",txtPhone.getText().toString());
                parametros.put("fecha",txtHora.getText().toString());
                parametros.put("hora",txtFecha.getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        final Request<String> add =(stringRequest);
    }

    public  void getAllUsers(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIConstants.BASE_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api= retrofit.create(api.class);
        call<List<User>> call = api.getAllUsers();
        call.enqueue(new CallBack<List<User>>(){
            @Override
            public void onResponse(call<List<User>> call, Response<List<User>> response){

                if(!response.isSuccess()){
                    Toast.makeText(getApplicationContext(),"codigo"+response.code().Toast.LENGH_SHORT).show();
                    return;
                }
                List<User> users= response.body();
                result.setText("");

                for(User user : users){
                    String content="";
                    content+= String.format("id: %s\n",user.getId());
                    content+= String.format("name: %s\n",user.getId());
                    content+= String.format("edad: %s\n\n",user.getId());
                    result.append(content);

                }
            }
            @Override
            public void onFailure(Call<List<User>>call,Throwable t){}


        }


    });
}
