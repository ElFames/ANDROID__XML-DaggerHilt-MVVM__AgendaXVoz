package agenda.x.voz.ui.viewModels

import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.domain.use_cases.DeleteAlarm
import android.app.NotificationManager
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailAlarmViewModel @Inject constructor(
    private val deleteAlarm: DeleteAlarm
): ViewModel() {
    val alarm = MutableLiveData<Alarm>()

    fun setAlarm(myAlarm: Alarm) = alarm.postValue(myAlarm)
    
    fun deleteCurrentAlarm(context: Context) {
        deleteAlarm(alarm.value!!)
        deleteNotification(context, alarm.value!!.id)
    }

    private fun deleteNotification(context: Context, notificationId: Long) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId.toInt())
    }
}