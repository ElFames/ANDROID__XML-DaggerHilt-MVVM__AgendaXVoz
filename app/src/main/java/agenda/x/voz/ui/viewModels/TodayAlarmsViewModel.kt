package agenda.x.voz.ui.viewModels

import agenda.x.voz.domain.useCases.GetTodayAlarms
import agenda.x.voz.domain.useCases.SetAlarmIsComplete
import agenda.x.voz.domain.model.Alarm
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TodayAlarmsViewModel @Inject constructor(
    private val todayAlarms: GetTodayAlarms,
    private val setAlarmIsComplete: SetAlarmIsComplete
): ViewModel() {
    val alarms = MutableLiveData<MutableList<Alarm>>()

    fun getTodayAlarms() {
        alarms.postValue(todayAlarms())
    }
    fun changeCompleteState(alarm: Alarm) {
        setAlarmIsComplete(alarm)
    }
}