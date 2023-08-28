package agenda.x.voz.core.notifications

import agenda.x.voz.R
import agenda.x.voz.data.repositories.AlarmRepository
import agenda.x.voz.ui.views.MainActivity
import agenda.x.voz.ui.views.alarmViews.NewAlarmFragment
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import javax.inject.Inject

class AlarmNotification: BroadcastReceiver() {
    companion object {
        const val CHANNEL_ID = "myChannel"
        const val notificationId = 1
        const val title = "title"
        const val message = "message"
    }
    override fun onReceive(context: Context, intent: Intent?) {
        createNotification(context, title, message, notificationId)
    }

    private fun createNotification(context: Context, title: String, message: String, notificationId: Int) {
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val channel = NotificationChannel(CHANNEL_ID, "channel", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)

        // Build the notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_calendar)
            .setContentTitle(title)
            .setContentText("Recordatorio de Tareas")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }
}