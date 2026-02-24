Odlično 🔥
Sad ti dajem **tačne isečke iz koda** po funkcionalnostima, da možeš odmah da prepoznaš gde se šta nalazi.

---

# 📍 1️⃣ LOKACIJA

## ✅ Permission traženje

```java
if (ActivityCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {

    ActivityCompat.requestPermissions(this,
            new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            },
            1);   // requestCode = 1
    return;
}
```

---

## ✅ Dobijanje lokacije

```java
fusedLocationProviderClient.getLastLocation()
        .addOnSuccessListener(this, location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();

                textView.setText("Lat: " + lat + "\nLon: " + lon);
            }
        });
```

---

## ✅ onRequestPermissionsResult za lokaciju

```java
if (requestCode == 1) {
    if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        getLocation();
    }
}
```

---

# 📷 2️⃣ KAMERA

## ✅ Otvaranje kamere

```java
Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
startActivityForResult(intent, 200);   // requestCode = 200
```

---

## ✅ Runtime permission za kameru

```java
ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.CAMERA},
        200);
```

---

## ✅ onActivityResult (prikaz slike)

```java
@Override
protected void onActivityResult(int requestCode,
                                int resultCode,
                                Intent data) {

    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == 200 && resultCode == RESULT_OK) {

        Bitmap image = (Bitmap) data.getExtras().get("data");
        imageView.setImageBitmap(image);
    }
}
```

---

## ✅ onRequestPermissionsResult za kameru

```java
if (requestCode == 200) {
    if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        openCamera();
    }
}
```

---

# 🎵 3️⃣ MUZIKA (MediaPlayer)

## ✅ Kreiranje MediaPlayer

```java
mediaPlayer = MediaPlayer.create(this, R.raw.music);
```

---

## ✅ Switch logika

```java
switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
    if (isChecked) {
        mediaPlayer.start();
    } else {
        mediaPlayer.pause();
    }
});
```

---

## ✅ Oslobađanje resursa

```java
@Override
protected void onDestroy() {
    super.onDestroy();
    mediaPlayer.release();
}
```

---

# 🔄 4️⃣ ŽIROSKOP

## ✅ Dobijanje senzora

```java
sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
```

---

## ✅ Registrovanje listenera

```java
sensorManager.registerListener(this,
        gyroSensor,
        SensorManager.SENSOR_DELAY_NORMAL);
```

---

## ✅ onSensorChanged

```java
@Override
public void onSensorChanged(SensorEvent event) {

    float x = event.values[0];
    float y = event.values[1];
    float z = event.values[2];

    Toast.makeText(this,
            "Gyro X: " + x + "\nGyro Y: " + y + "\nGyro Z: " + z,
            Toast.LENGTH_SHORT).show();
}
```

---

# 📊 5️⃣ AKCELEROMETAR

## ✅ Dobijanje senzora

```java
accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
```

---

## ✅ Real-time promena teksta dugmeta

```java
@Override
public void onSensorChanged(SensorEvent event) {

    float x = event.values[0];
    float y = event.values[1];
    float z = event.values[2];

    buttonAccel.setText("X: " + x +
            "\nY: " + y +
            "\nZ: " + z);
}
```

👉 OVO je deo koji menja tekst dugmeta.

---

# 🌐 6️⃣ RETROFIT

## ✅ Kreiranje Retrofit instance

```java
Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();

ApiService apiService = retrofit.create(ApiService.class);
```

---

## ✅ GET poziv

```java
Call<List<Proizvod>> call = apiService.getProizvodi();

call.enqueue(new Callback<List<Proizvod>>() {
    @Override
    public void onResponse(Call<List<Proizvod>> call,
                           Response<List<Proizvod>> response) {

        if (response.isSuccessful()) {
            List<Proizvod> lista = response.body();
        }
    }

    @Override
    public void onFailure(Call<List<Proizvod>> call, Throwable t) {
        t.printStackTrace();
    }
});
```


