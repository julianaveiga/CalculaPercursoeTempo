package com.example.juliana.calculapercursoetempo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    double latitude, longitude;

    public static final int REQUEST_GPS = 1;

    Chronometer chronometer;
    Button buttonIniciarPercurso;
    Button buttonTerminarPercurso ;
    Button buttonPermission ;
    Button buttonAtivar ;
    Button buttonDesativar;

    TextView txtPercurso;
    TextView txtTempo;

    double distancia;

    boolean inicio = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
         buttonPermission = (Button) findViewById(R.id.buttonPermissionGps);
         buttonAtivar =  (Button)findViewById(R.id.buttonAtivarGPS);
         buttonDesativar = (Button) findViewById(R.id.buttonDesativar);
         buttonIniciarPercurso = (Button) findViewById(R.id.buttonIniciarPercurso);
         buttonTerminarPercurso = (Button) findViewById(R.id.buttonTerminarPercurso);
        chronometer = new Chronometer(this);
        txtPercurso = (TextView) findViewById(R.id.textViewDistancia);
        txtTempo = (TextView) findViewById(R.id.textViewTempo);

        defineEvents();
    }

    private void defineEvents() {
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                txtTempo.setText(chronometer.getText());
            }
        });

        buttonIniciarPercurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicio = true;
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
            }
        });

        buttonAtivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        });

        buttonDesativar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager.removeUpdates(locationListener);
            }
        });

        buttonTerminarPercurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "Total Percorrido: " + String.format("%.2f",distancia) + "M em " + chronometer.getText(), Toast.LENGTH_SHORT).show();

                distancia = 0;
                latitude = 0;
                longitude = 0;
                inicio = false;
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.stop();
            }
        });

        buttonPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    //verifica se deve-se exibir uma explicação sobre a necessidade da
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)){
                        Toast.makeText(MainActivity.this, "Para exibir coordenadas o app precisa do GPS", Toast.LENGTH_SHORT).show();
                    }
                    //pede permissão
                    ActivityCompat.requestPermissions(MainActivity.this, new String []
                            {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
                }

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_GPS:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED){

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locationListener);
                    }
                }
                else{
                    Toast.makeText(this, "GPS desabilitado por falta de permissão.", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            if(!inicio)
                return;

            if(latitude == 0)
                latitude = location.getLatitude();
            if(longitude == 0)
                longitude = location.getLongitude();


            float dis[] = new float[5];

            Location.distanceBetween(latitude,longitude,location.getLatitude(), location.getLongitude(), dis);

            txtPercurso.setText(String.format("%.2f M" ,distancia ));

            distancia += dis[0];
            latitude = location.getLatitude();
            longitude = location.getLongitude();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
