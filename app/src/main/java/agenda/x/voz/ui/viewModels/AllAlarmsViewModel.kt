package agenda.x.voz.ui.viewModels

import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.domain.use_cases.GetFutureAlarms
import agenda.x.voz.domain.use_cases.PostponeAlarm
import agenda.x.voz.domain.use_cases.SetAlarmIsComplete
import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllAlarmsViewModel @Inject constructor(
    private val futureAlarms: GetFutureAlarms,
    private val setAlarmIsComplete: SetAlarmIsComplete,
    private val postponeAlarm: PostponeAlarm,
): ViewModel() {
    val alarms = MutableLiveData<MutableList<Alarm>>()

    fun getFutureAlarms() {
        viewModelScope.launch {
            val futureAlarms = futureAlarms()
            alarms.postValue(futureAlarms)
        }
    }

    fun changeCompleteState(alarm: Alarm) {
        setAlarmIsComplete(alarm)
    }
    suspend fun postponeMyAlarm(alarm: Alarm, activity: Activity) {
        postponeAlarm(alarm, activity)
    }
}