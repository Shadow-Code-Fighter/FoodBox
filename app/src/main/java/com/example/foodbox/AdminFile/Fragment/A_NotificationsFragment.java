package com.example.foodbox.AdminFile.Fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.example.foodbox.R;

public class A_NotificationsFragment extends Fragment {

    private NotificationManager notificationManager;
    private static final String CHANNEL_ID = "My Channel";




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.a_fragment_notifications, container, false);


        Button showNotificationButton = view.findViewById(R.id.show);
        showNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showNotification(getContext(), "FoodBox", "This is the notification message.");
//                createNotificationChannel();
            }
        });
        notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createNotificationChannel();
        }


        return view;
    }
//    private void createNotificationChannel() {
//        String channelName = "Default Channel";
//        String channelDescription = "Default Channel Description";
//        int importance = NotificationManager.IMPORTANCE_DEFAULT;
//
//        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
//        channel.setDescription(channelDescription);
//        channel.enableLights(true);
//        channel.setLightColor(Color.BLUE);
//        channel.enableVibration(true);
//
//        // Create the notification channel
//        notificationManager.createNotificationChannel(channel);
//    }

    private void showNotification(Context context, String title, String message) {
        // Create a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
              //  .setSmallIcon(R.drawable.img_5)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Show the notification
        notificationManager.notify(0, builder.build());
    }

}