package agenda.x.voz.core.notifications

import agenda.x.voz.domain.model.Alarm
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

object MyAlarmManager {
    @SuppressLint("ScheduleExactAlarm")
    fun notification1HourBefore(alarmToNotify: Alarm, activity: Activity, dateTime: Calendar, hour: Int) {
        val notificationId = "${alarmToNotify.id.toInt()}1111111".toInt()
        val intent = Intent(activity.applicationContext, AlarmNotification::class.java)
        intent.putExtra("title", "Falta 1 HORA para...")
        intent.putExtra("message", alarmToNotify.name)
        intent.putExtra("notificationId", notificationId)
        val pendingIntent = PendingIntent.getBroadcast(
            activity.applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        dateTime.set(Calendar.HOUR_OF_DAY, hour)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, dateTime.timeInMillis + 1000, pendingIntent)
    }

    @SuppressLint("ScheduleExactAlarm")
    fun notificationInTimePassed(alarmToNotify: Alarm, activity: Activity, dateTime: Calendar) {
        val intent = Intent(activity.applicationContext, AlarmNotification::class.java)
        intent.putExtra("title", "A llegado la hora de...")
        intent.putExtra("message", alarmToNotify.name)
        intent.putExtra("notificationId", alarmToNotify.id.toInt())
        val pendingIntent = PendingIntent.getBroadcast(
            activity.applicationContext,
            alarmToNotify.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, dateTime.timeInMillis + 1000, pendingIntent)
    }

    @SuppressLint("ScheduleExactAlarm")
    fun notification3DaysOffline(activity: Activity) {
        val notificationId = 333333333
        val notificationManager = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        val intent = Intent(activity.applicationContext, AlarmNotification::class.java)
        intent.putExtra("title", "Ya no organizas tus tareas...")
        intent.putExtra("message", "Llevas 3 dias sin conectarte, parece que estas teniendo unos dias relajados... También puedes añadir alarmas diarias o semanales!")
        intent.putExtra("notificationId", notificationId)
        val pendingIntent = PendingIntent.getBroadcast(
            activity.applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val dateTime = Calendar.getInstance()
        dateTime.add(Calendar.DAY_OF_MONTH, 3)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, dateTime.timeInMillis + 1000, pendingIntent)
    }
}