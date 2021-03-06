package hu.petrik.gpsapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Permissions;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class MainActivity extends AppCompatActivity
    implements OnRequestPermissionsResultCallback {

    private TextView pozicioEditText;
    private Button is_save_btn;
    private LocationManager locationManager;

    private boolean isPositionSave = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 12) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // Ha sikerul
                startGps();
            }
            else {
                // Ha nem kaptunk jogot
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        is_save_btn = (Button) this.findViewById((R.id.is_save_btn));
        is_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPositionSave) {
                    isPositionSave = true;
                    is_save_btn.setText("Pozíció mentésének befejezése");
                } else {
                    isPositionSave = false;
                    is_save_btn.setText("Pozíció mentés");

                }

            }
        });

        pozicioEditText = (TextView)this.findViewById(R.id.pozicio_text);


        locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    12
            );
        }
        else {
            startGps();
        }
    }

    @SuppressLint("MissingPermission")
    private void startGps() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                pozicioEditText.setText(latitude + " " + longitude);

                if(isPositionSave)
                {
                    pozicioMentes(latitude, longitude);
                }

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
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void pozicioMentes(double latitude, double longitude) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "gps_adat.txt");
            try {
                FileWriter writer = new FileWriter(file, true);

                Instant timestamp = Instant.now();
                LocalDateTime ldt = LocalDateTime.ofInstant(timestamp, ZoneId.of("Europe/Budapest"));


                writer.write(ldt.getYear() +"."+ ldt.getMonthValue() + "." + ldt.getDayOfMonth() +";"+ latitude + ";"+ longitude +"\n");


                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
