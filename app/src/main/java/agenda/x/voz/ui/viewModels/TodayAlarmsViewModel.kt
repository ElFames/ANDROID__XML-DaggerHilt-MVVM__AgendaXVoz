package agenda.x.voz.ui.viewModels

import agenda.x.voz.domain.use_cases.GetTodayAlarms
import agenda.x.voz.domain.use_cases.SetAlarmIsComplete
import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.domain.use_cases.PostponeAlarm
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TodayAlarmsViewModel @Inject constructor(
    private val todayAlarms: GetTodayAlarms,
    private val setAlarmIsComplete: SetAlarmIsComplete,
    private val postponeAlarm: PostponeAlarm
): ViewModel() {
    val alarms = MutableLiveData<MutableList<Alarm>>()

    fun getTodayAlarms() {
        alarms.postValue(todayAlarms())
    }
    fun changeCompleteState(alarm: Alarm) {
        setAlarmIsComplete(alarm)
    }
    fun postponeMyAlarm(alarm: Alarm) {
        postponeAlarm(alarm)
    }
}