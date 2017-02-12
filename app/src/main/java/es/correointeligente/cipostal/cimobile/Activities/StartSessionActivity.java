package es.correointeligente.cipostal.cimobile.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import es.correointeligente.cipostal.cimobile.Model.Notificador;
import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.DBHelper;
import es.correointeligente.cipostal.cimobile.Util.Util;

public class StartSessionActivity extends AppCompatActivity implements View.OnClickListener , AdapterView.OnItemSelectedListener{

    Spinner mSpinnerNotificadores;
    Button mButton_inciarSesion;
    SharedPreferences sp;
    String notificador;
    String delegacion;
    String codigoNotificador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_session);

        sp = this.getSharedPreferences("login", MODE_PRIVATE);

        //Si SharedPreferences contiene el dato de la sesion se salta la pantalla de inicio de sesion
        if (sp.contains("notificador")) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }

        mButton_inciarSesion = (Button) findViewById(R.id.button_iniciar_sesion);
        mButton_inciarSesion.setOnClickListener(this);

        // Inicializa la base de datos
        DBHelper db = new DBHelper(getApplicationContext());

        // Iniciliza el Spinner de notificadores (Realiza de momento la carga desde un metodo en Util que carga usuarios por defecto)
        mSpinnerNotificadores = (Spinner) findViewById(R.id.spinner_notificador);
        mSpinnerNotificadores.setOnItemSelectedListener(this);
        ArrayAdapter notificadoresAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item, Util.obtenerNotificadores());
        mSpinnerNotificadores.setAdapter(notificadoresAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_iniciar_sesion:
                if (notificador != null) {
                    SharedPreferences.Editor e = sp.edit();
                    e.putString("notificador", notificador);
                    e.putString("delegacion", delegacion);
                    e.putString("codigoNotificador", codigoNotificador);
                    e.commit();

                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        Notificador notifAux = (Notificador) adapterView.getSelectedItem();
        notificador = notifAux.getNombre();
        delegacion = notifAux.getDelegacion();
        codigoNotificador = notifAux.getCodigo();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
