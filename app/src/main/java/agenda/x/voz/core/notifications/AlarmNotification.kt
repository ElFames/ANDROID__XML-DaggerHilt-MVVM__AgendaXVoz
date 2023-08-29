package agenda.x.voz.core.notifications

import agenda.x.voz.R
import agenda.x.voz.ui.views.MainActivity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class AlarmNotification: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("title") ?: "El tiempo pasa volando"
        val message = intent?.getStringExtra("message") ?: "Tienes una tarea pendiente"
        val notificationId = intent?.getIntExtra("notificationId", 0) ?: 0

        createNotification(context, title, message, notificationId)
    }

    private fun createNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int
    ) {
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, notificationId, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val channel = NotificationChannel("myChannel", "channel", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, "myChannel")
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