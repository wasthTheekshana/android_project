package com.example.userprofilefetcher;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.userprofilefetcher.connection.UserApiClient;
import com.example.userprofilefetcher.db.UserPreferences;
import com.example.userprofilefetcher.model.User;
import com.example.userprofilefetcher.view.AvatarView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private UserPreferences userPrefs;
    private EditText userIdInput;
    private AvatarView avatarView;
    private TextView nameText, emailText, phoneText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        userPrefs = new UserPreferences(this);
        initializeViews();
        setupListeners();
        loadSavedUserName();
    }

    private void initializeViews() {
        userIdInput = findViewById(R.id.editTextUserId);
        avatarView = findViewById(R.id.avatarView);
        nameText = findViewById(R.id.textViewName);
        emailText = findViewById(R.id.textViewEmail);
        phoneText = findViewById(R.id.textViewPhone);
    }

    private void setupListeners() {
        findViewById(R.id.buttonFetch).setOnClickListener(v -> fetchUserProfile());

        phoneText.setOnClickListener(v -> {
            String phone = phoneText.getText().toString();
            if (!phone.isEmpty()) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phone));
                startActivity(dialIntent);
            }
        });
    }

    private void loadSavedUserName() {
        String savedName = userPrefs.getSavedUserName();
        if (!savedName.isEmpty()) {
            nameText.setText(savedName);
        }
    }

    private void fetchUserProfile() {
        String userId = userIdInput.getText().toString();
        if (userId.isEmpty()) return;

        new Thread(() -> {
            String jsonData = UserApiClient.fetchUserProfile(userId);
            try {
                JSONObject json = new JSONObject(jsonData);
                User user = new User(
                        json.getString("id"),
                        json.getString("name"),
                        json.getString("email"),
                        json.getString("phone")
                );
//System.err.println(user.getName());getName
                runOnUiThread(() -> updateUI(user));
                userPrefs.saveUserName(user.getName());
                showProfileFetchedNotification(user.getName());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateUI(User user) {
        nameText.setText(user.getName());
        emailText.setText(user.getEmail());
        phoneText.setText(user.getPhone());
        avatarView.setUserDetails(user.getName(), user.getAvatarColor());
    }

    private void showProfileFetchedNotification(String userName) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "profile_channel",
                    "Profile Updates",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "profile_channel")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Profile Fetched")
                .setContentText("Successfully loaded profile for " + userName)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }
}