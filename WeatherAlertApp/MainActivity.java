package com.example.weatheralertapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.weatheralertapp.connection.WeatherApiClient;
import com.example.weatheralertapp.connection.WeatherJsonParser;
import com.example.weatheralertapp.db.PreferencesManager;
import com.example.weatheralertapp.model.WeatherData;

public class MainActivity extends AppCompatActivity {
    private PreferencesManager prefsManager;
    private EditText cityInput;
    private TextView tempText, descText, windText, humidityText;
    private Switch notificationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefsManager = new PreferencesManager(this);
        initializeViews();
        setupListeners();
        loadSavedCity();
    }

    private void initializeViews() {
        cityInput = findViewById(R.id.editTextCity);
        tempText = findViewById(R.id.textViewTemp);
        descText = findViewById(R.id.textViewDescription);
        windText = findViewById(R.id.textViewWind);
        humidityText = findViewById(R.id.textViewHumidity);
        notificationSwitch = findViewById(R.id.switchNotifications);
    }

    private void setupListeners() {
        findViewById(R.id.buttonFetch).setOnClickListener(v -> fetchWeatherData());
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsManager.setNotificationsEnabled(isChecked);
        });
    }

    private void loadSavedCity() {
        String savedCity = prefsManager.getSavedCity();
        cityInput.setText(savedCity);
        notificationSwitch.setChecked(prefsManager.areNotificationsEnabled());
    }

    private void fetchWeatherData() {
        String city = cityInput.getText().toString();
        new Thread(() -> {
            String jsonData = WeatherApiClient.fetchWeatherData(city);
            WeatherData weather = WeatherJsonParser.parseWeatherData(jsonData);

            runOnUiThread(() -> updateUI(weather));

            if (weather != null) {
                prefsManager.saveCity(city);
                if (prefsManager.areNotificationsEnabled()) {
                    showWeatherNotification(weather);
                }
            }
        }).start();
    }

    private void updateUI(WeatherData weather) {
        if (weather != null) {
            tempText.setText(String.format("%.1f°C", weather.getTemperature()));
            descText.setText(weather.getDescription());
            windText.setText(String.format("Wind: %.1f m/s", weather.getWindSpeed()));
            humidityText.setText(String.format("Humidity: %d%%", weather.getHumidity()));
        }
    }

    private void showWeatherNotification(WeatherData weather) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "weather_channel",
                    "Weather Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "weather_channel")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Weather Update")
                .setContentText(String.format("Temperature: %.1f°C, %s",
                        weather.getTemperature(), weather.getDescription()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }
}