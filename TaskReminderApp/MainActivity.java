package com.example.taskreminderapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskreminderapp.db.TaskDbHelper;
import com.example.taskreminderapp.model.Task;
import com.example.taskreminderapp.view.TaskProgressView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TaskDbHelper dbHelper;
    private ListView taskList;
    private TaskProgressView progressView;
    private ArrayAdapter<String> adapter;
    private ArrayList<Task> tasks;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "task_reminder_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize components
        dbHelper = new TaskDbHelper(this);
        initializeViews();
        createNotificationChannel();
        loadTasks();
    }

    private void initializeViews() {
        taskList = findViewById(R.id.taskList);
        progressView = findViewById(R.id.taskProgressView);
        Button addButton = findViewById(R.id.btnAddTask);

        tasks = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                new ArrayList<String>());
        taskList.setAdapter(adapter);

        // Set up click listeners
        addButton.setOnClickListener(v -> showAddTaskDialog());
        taskList.setOnItemClickListener((parent, view, position, id) ->
                showTaskOptionsDialog(position));
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);

        EditText titleInput = dialogView.findViewById(R.id.editTextTitle);
        EditText descInput = dialogView.findViewById(R.id.editTextDescription);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);

        builder.setView(dialogView)
                .setTitle("Add New Task")
                .setPositiveButton("Add", (dialog, which) -> {
                    String title = titleInput.getText().toString();
                    String description = descInput.getText().toString();

                    // Get date from DatePicker
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(datePicker.getYear(),
                            datePicker.getMonth(),
                            datePicker.getDayOfMonth());

                    if (!title.isEmpty()) {
                        addTask(title, description, calendar.getTimeInMillis());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addTask(String title, String description, long dueDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskDbHelper.COLUMN_TITLE, title);
        values.put(TaskDbHelper.COLUMN_DESCRIPTION, description);
        values.put(TaskDbHelper.COLUMN_DUE_DATE, dueDate);
        values.put(TaskDbHelper.COLUMN_IS_COMPLETED, 0);

        long newRowId = db.insert(TaskDbHelper.TABLE_TASKS, null, values);

        if (newRowId != -1) {
            loadTasks();
            scheduleNotification(title, dueDate);
            showTaskAddedNotification(title);
        }
    }

    private void loadTasks() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        tasks.clear();
        adapter.clear();

        String[] projection = {
                TaskDbHelper.COLUMN_ID,
                TaskDbHelper.COLUMN_TITLE,
                TaskDbHelper.COLUMN_DESCRIPTION,
                TaskDbHelper.COLUMN_DUE_DATE,
                TaskDbHelper.COLUMN_IS_COMPLETED
        };

        Cursor cursor = db.query(
                TaskDbHelper.TABLE_TASKS,
                projection,
                null,
                null,
                null,
                null,
                TaskDbHelper.COLUMN_DUE_DATE + " ASC"
        );

        while (cursor.moveToNext()) {
            Task task = new Task(
                    cursor.getLong(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_DESCRIPTION)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_DUE_DATE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(TaskDbHelper.COLUMN_IS_COMPLETED)) == 1
            );
            tasks.add(task);
            adapter.add(task.getTitle() + " (Due: " + getFormattedDate(task.getDueDate()) + ")");
        }
        cursor.close();

        // Update progress view
        updateProgressView();
    }

    private void updateProgressView() {
        if (tasks.size() > 0) {
            int completedTasks = 0;
            for (Task task : tasks) {
                if (task.isCompleted()) completedTasks++;
            }
            float progress = (float) completedTasks / tasks.size();
            progressView.setProgress(progress);
        } else {
            progressView.setProgress(0);
        }
    }

    private void showTaskOptionsDialog(int position) {
        Task task = tasks.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String[] options = {"Mark as Complete", "Share", "Delete"};
        builder.setTitle(task.getTitle())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Mark as Complete
                            toggleTaskCompletion(task);
                            break;
                        case 1: // Share
                            shareTask(task);
                            break;
                        case 2: // Delete
                            deleteTask(task);
                            break;
                    }
                })
                .show();
    }

    private void toggleTaskCompletion(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskDbHelper.COLUMN_IS_COMPLETED, !task.isCompleted());

        db.update(TaskDbHelper.TABLE_TASKS,
                values,
                TaskDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(task.getId())});

        loadTasks();
    }

    private void deleteTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TaskDbHelper.TABLE_TASKS,
                TaskDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
        loadTasks();
    }

    private void shareTask(Task task) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Task: " + task.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Task: " + task.getTitle() + "\n" +
                        "Description: " + task.getDescription() + "\n" +
                        "Due Date: " + getFormattedDate(task.getDueDate()));

        startActivity(Intent.createChooser(shareIntent, "Share task via"));
    }

    private void scheduleNotification(String taskTitle, long dueDate) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Task Due Soon")
                .setContentText(taskTitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void showTaskAddedNotification(String taskTitle) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("New Task Added")
                .setContentText(taskTitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Task Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for task reminders");

            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private String getFormattedDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}