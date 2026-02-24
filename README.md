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

## Switch music play x time

switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
```java
    if (isChecked) {

        switch1.setEnabled(false);   // onemogući klik
        playCount = 0;

        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(mp -> {

            playCount++;

            if (playCount < 3) {
                mp.start();  // ponovo pusti
            } else {

                switch1.setChecked(false);   // prebaci na OFF
                switch1.setEnabled(true);    // omogući ponovo
            }
        });
    }
});
```





##---------------------------------

Odlično 🔥 ovo je ozbiljan kolokvijumski zadatak (Servis + Notifikacija + Action dugme).

Radićemo sistematski i jasno.

---

# 🎯 ŠTA TREBA

✔ Checkbox ON → pokreće Service
✔ Service pušta muziku na repeat
✔ Checkbox OFF → zaustavlja Service
✔ Notifikacija ima dugme:

* ako svira → piše **Pause**
* ako je pauza → piše **Play**

---

# 🔥 1️⃣ Napravi Service klasu

`New → Service → MusicService`

## MusicService.java

```java
public class MusicService extends Service {

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    public static final String ACTION_TOGGLE = "ACTION_TOGGLE";

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && ACTION_TOGGLE.equals(intent.getAction())) {
            toggleMusic();
        } else {
            startMusic();
        }

        return START_STICKY;
    }

    private void startMusic() {
        mediaPlayer.start();
        isPlaying = true;
        showNotification();
    }

    private void toggleMusic() {
        if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
        } else {
            mediaPlayer.start();
            isPlaying = true;
        }
        showNotification();
    }

    private void showNotification() {

        String channelId = "music_channel";

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(channelId,
                            "Music Channel",
                            NotificationManager.IMPORTANCE_LOW);

            manager.createNotificationChannel(channel);
        }

        Intent toggleIntent = new Intent(this, MusicService.class);
        toggleIntent.setAction(ACTION_TOGGLE);

        PendingIntent pendingIntent =
                PendingIntent.getService(this, 0, toggleIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String buttonText = isPlaying ? "Pause" : "Play";

        Notification notification =
                new NotificationCompat.Builder(this, channelId)
                        .setContentTitle("Music Service")
                        .setContentText("Melodija je aktivna")
                        .setSmallIcon(android.R.drawable.ic_media_play)
                        .addAction(0, buttonText, pendingIntent)
                        .build();

        startForeground(1, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
}
```

---

# 🔥 2️⃣ Dodaj Service u Manifest

```xml
<service
    android:name=".MusicService"
    android:exported="false"/>
```

---

# 🔥 3️⃣ Checkbox u MainActivity

```java
CheckBox checkBox = findViewById(R.id.checkBox);

checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

    Intent intent = new Intent(this, MusicService.class);

    if (isChecked) {
        startService(intent);
    } else {
        stopService(intent);
    }
});
```

---

# 🧠 Kako ovo radi?

* Checkbox ON → startService()
* Service pušta muziku
* Notifikacija se prikazuje (foreground service)
* Klik na dugme u notifikaciji šalje ACTION_TOGGLE
* Service pauzira ili nastavlja
* Notifikacija menja tekst dugmeta

---

# 📌 Profesor proverava

✔ Znaš šta je Service
✔ Znaš Foreground service
✔ Znaš NotificationChannel
✔ Znaš PendingIntent
✔ Znaš kako radi toggle logika

---

# ⚠ Ako ti app crashuje

Dodaj u Manifest:

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
```

---

# 🚀 Hoćeš sada naprednu verziju?

Možemo dodati:

* da notifikacija nestane kad se servis ugasi
* ili da checkbox prati stanje servisa
* ili da servis radi čak i kad izađeš iz aplikacije

Ti biraš 😎


