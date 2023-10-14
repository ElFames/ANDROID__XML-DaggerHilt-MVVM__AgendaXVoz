package agenda.x.voz.ui.viewModels

import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.domain.use_cases.DeleteAlarm
import agenda.x.voz.domain.use_cases.GetAlarmsFromDay
import agenda.x.voz.domain.use_cases.GetFutureAlarms
import agenda.x.voz.domain.use_cases.GetLatestVersion
import agenda.x.voz.domain.use_cases.PostponeAlarm
import agenda.x.voz.domain.use_cases.SetAlarmIsComplete
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val futureAlarms: GetFutureAlarms,
    private val latestVersion: GetLatestVersion,
    private val myAlarms: GetAlarmsFromDay,
    private val setAlarmIsComplete: SetAlarmIsComplete,
    private val postponeAlarm: PostponeAlarm,
    private val deleteAlarm: DeleteAlarm
): ViewModel() {
    init {
        getFutureAlarms()
    }

    private val _alarms = MutableLiveData<MutableList<Alarm>>()
    val alarms: LiveData<MutableList<Alarm>> = _alarms

    private val _datesWithEvents = MutableLiveData<List<Calendar>>()
    val datesWithEvents: LiveData<List<Calendar>> = _datesWithEvents

    private val _myLatestVersion = MutableLiveData<Int>()
    val myLatestVersion: LiveData<Int> = _myLatestVersion

    private val _alarmsForRecyclerList = MutableLiveData<MutableList<Alarm>>()
    val alarmsForRecyclerList: LiveData<MutableList<Alarm>> = _alarmsForRecyclerList

    fun deleteAlarm(context: Context, alarm: Alarm) {
        deleteAlarm(alarm)
        alarmsForRecyclerList.value?.remove(alarm)
        deleteNotification(context, alarm.id)
    }

    private fun deleteNotification(context: Context, notificationId: Long) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId.toInt())
    }
    fun getFutureAlarms() {
        viewModelScope.launch {
            val futureAlarms = futureAlarms()
            _alarms.value = futureAlarms
        }
    }

    fun refreshData() {
        getFutureAlarms()
    }
    fun getLatestVersion() {
        viewModelScope.launch {
            val latestVersion = latestVersion()
            _myLatestVersion.value = latestVersion
        }
    }

    fun getAlarmsFromDay(day: Int, month: Int, year: Int) {
        _alarmsForRecyclerList.value = myAlarms(day, month, year)
    }
    fun changeCompleteState(alarm: Alarm) {
        setAlarmIsComplete(alarm)
    }
    suspend fun postponeMyAlarm(alarm: Alarm, activity: Activity) {
        postponeAlarm(alarm, activity)
    }

    fun getDatesWithEvents() {
        val myDates = mutableListOf<Calendar>()
        alarms.value?.forEach { alarm ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, alarm.year!!)
            calendar.set(Calendar.MONTH, alarm.month!! - 1)
            calendar.set(Calendar.DAY_OF_MONTH, alarm.day!!)
            myDates.add(calendar)
        }
        _datesWithEvents.value = myDates
    }
}