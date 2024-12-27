package com.brainexpensescontrol.controle_de_despesas;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NotificationCheckerWorker extends Worker {

    private static final String CHANNEL_ID = "notification_channel";
    private static final int NOTIFICATION_ID = 123;
    private static final int INTERVAL_HOURS = 1; // Interval in hours

    public NotificationCheckerWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        MyDatabaseHelper myDB = new MyDatabaseHelper(getApplicationContext());
        SQLiteDatabase db = myDB.getReadableDatabase();

        List<String> notificationItems = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM my_library", null);
        if (cursor.moveToFirst()) {
            do {
                String redFlag = cursor.getString(cursor.getColumnIndex("red_flag_notification"));
                String dateStr = cursor.getString(cursor.getColumnIndex("date"));
                String bookTitle = cursor.getString(cursor.getColumnIndex("book_title"));

                if (redFlag != null && dateStr != null) {
                    if (redFlag.equals("NOTIFICAR")) {
                        try {
                            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr);
                            if (date != null) {
                                Calendar today = Calendar.getInstance();
                                Calendar notificationDate = Calendar.getInstance();
                                notificationDate.setTime(date);

                                if (today.get(Calendar.YEAR) == notificationDate.get(Calendar.YEAR) &&
                                        today.get(Calendar.DAY_OF_YEAR) == notificationDate.get(Calendar.DAY_OF_YEAR)) {
                                    notificationItems.add(bookTitle + " está vencendo hoje.");
                                } else if (today.after(notificationDate)) {
                                    notificationItems.add(bookTitle + " está atrasado!");
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        if (!notificationItems.isEmpty()) {
            sendNotification(notificationItems);
        }
        return Result.success();
    }

    private void sendNotification(List<String> notificationItems) {
        // Se houver muitos itens, limite o número de itens para exibição
        int maxItemsToShow = 5; // Limite máximo de itens a serem exibidos na notificação

        StringBuilder notificationText = new StringBuilder();
        for (int i = 0; i < Math.min(maxItemsToShow, notificationItems.size()); i++) {
            notificationText.append(notificationItems.get(i)).append("\n");
        }

        if (notificationItems.size() > maxItemsToShow) {
            notificationText.append("..."); // Indicador de que há mais itens não mostrados na notificação
        }

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel Name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(getApplicationContext(), AnotacoesSalvas.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.drawable.baseline_notifications_active_24)
                        .setContentTitle("Notificação de vencimento")
                        .setContentText("Alguns itens estão vencendo ou atrasados.")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(notificationText.toString()))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }




}
