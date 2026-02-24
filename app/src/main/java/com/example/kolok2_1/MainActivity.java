package com.example.kolok2_1;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Button;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    TextView textView;
    ImageButton imageButton;
    ImageView imageView;
    Switch switch1;
    Button button;

//    Location Instanc
    FusedLocationProviderClient fusedLocationClient;
//    Camera instanc
    private static final int CAMERA_REQUEST = 100;

    SensorManager sensorManager;
    Sensor gyroscope;
    float gyroX, gyroY, gyroZ;

    MediaPlayer mediaPlayer;
    int switchCounter = 0; // trebaće kasnije za zadatak 10

    Sensor accelerometer;

    ApiService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

//      Instanca za location i poziva se na getLocation()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

        textView = findViewById(R.id.textView);
        imageButton = findViewById(R.id.imageButton);
        imageView = findViewById(R.id.imageView);
        switch1 = findViewById(R.id.switch1);
        button = findViewById(R.id.button);

//        mp3 media
        mediaPlayer = MediaPlayer.create(this, R.raw.music);

        button.setOnClickListener(v -> {

            apiService.getProizvodi().enqueue(new Callback<List<Proizvod>>() {
                @Override
                public void onResponse(Call<List<Proizvod>> call,
                                       Response<List<Proizvod>> response) {

                    if (response.isSuccessful() && response.body() != null) {

                        List<Proizvod> lista = response.body();

                        if (!lista.isEmpty()) {

                            Proizvod p = lista.get(0);

                            textView.setText(
                                    "ID: " + p.getId() +
                                            "\nTitle: " + p.getTitle() +
                                            "\nDesc: " + p.getDescription()
                            );
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Proizvod>> call, Throwable t) {
                    Toast.makeText(MainActivity.this,
                            "Greska: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });

        });

//        pustanje muzike
        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                mediaPlayer.start();
                switchCounter++;
            } else {
                mediaPlayer.pause();
            }

        });

//        otvaranje kamere
        imageButton.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        200);

            } else {
                openCamera();
            }


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);
        });

//        location
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//        accelerometar
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//         neophodan registerListener
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                gyroX = event.values[0];
                gyroY = event.values[1];
                gyroZ = event.values[2];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                button.setText("X: " + x +
                        "\nY: " + y +
                        "\nZ: " + z);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);



    }

//    Activity za kameru aktivira se kada se uslika slika
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            Bitmap image = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(image);

            Toast.makeText(this,
                    "Gyro X: " + gyroX +
                            "\nGyro Y: " + gyroY +
                            "\nGyro Z: " + gyroZ,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void getLocation() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

        } else {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {

                            double lat = location.getLatitude();
                            double lon = location.getLongitude();

                            textView.setText("Lat: " + lat + "\nLon: " + lon);
                        }
                    });
        }
    }

    private void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

//    Potrebna radi overavanje premissions
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getLocation();
            }
        }

        if (requestCode == 200) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}