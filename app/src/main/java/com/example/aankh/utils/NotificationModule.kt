package com.example.aankh.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.aankh.DashBoard
import com.example.aankh.MainActivity
import com.example.aankh.R
import com.example.aankh.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.example.aankh.utils.Constants.PENDING_INTENT_REQUEST_CODE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped


@Module
@InstallIn(ServiceComponent::class)
object NotificationModule {

    @ServiceScoped
    @Provides
    fun provideNotificationBuilder(@ApplicationContext context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).setAutoCancel(false)
            .setOngoing(true).setSmallIcon(R.drawable.green_check_mark)
            .setContentIntent(providePendingIntent(context))
    }

    @ServiceScoped
    @Provides
    fun providePendingIntent(@ApplicationContext context: Context): PendingIntent? {
        return PendingIntent.getActivity(
            context,
            PENDING_INTENT_REQUEST_CODE,
            Intent(
                context, DashBoard::class.java
            ),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }


    @ServiceScoped
    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}