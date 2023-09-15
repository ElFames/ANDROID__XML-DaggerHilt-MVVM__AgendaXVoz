package agenda.x.voz.ui.viewModels

import agenda.x.voz.domain.use_cases.GetTomorrowAlarms
import agenda.x.voz.domain.use_cases.SetAlarmIsComplete
import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.domain.use_cases.PostponeAlarm
import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TomorrowAlarmsViewModel @Inject constructor(
    private val tomorrowAlarms: GetTomorrowAlarms,
    private val setAlarmIsComplete: SetAlarmIsComplete,
    private val postponeAlarm: PostponeAlarm
): ViewModel() {
    val alarms = MutableLiveData<MutableList<Alarm>>()

    fun getTomorrowAlarms() {
        alarms.postValue(tomorrowAlarms())
    }

    fun changeCompleteState(alarm: Alarm) {
        setAlarmIsComplete(alarm)
    }
    suspend fun postponeMyAlarm(alarm: Alarm, activity: Activity) {
        postponeAlarm(alarm, activity)
    }
}