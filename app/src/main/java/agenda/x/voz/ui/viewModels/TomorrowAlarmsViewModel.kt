package agenda.x.voz.ui.viewModels

import agenda.x.voz.domain.useCases.GetTomorrowAlarms
import agenda.x.voz.domain.useCases.SetAlarmIsComplete
import agenda.x.voz.domain.model.Alarm
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TomorrowAlarmsViewModel @Inject constructor(
    private val tomorrowAlarms: GetTomorrowAlarms,
    private val setAlarmIsComplete: SetAlarmIsComplete
): ViewModel() {
    val alarms = MutableLiveData<MutableList<Alarm>>()

    fun getTomorrowAlarms() {
        alarms.postValue(tomorrowAlarms())
    }

    fun changeCompleteState(alarm: Alarm) {
        setAlarmIsComplete(alarm)
    }
}