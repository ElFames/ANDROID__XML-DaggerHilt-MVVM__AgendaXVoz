package agenda.x.voz.ui.viewModels

import agenda.x.voz.domain.useCases.GetAlarmsFromDay
import agenda.x.voz.domain.useCases.SetAlarmIsComplete
import agenda.x.voz.domain.model.Alarm
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlarmsFromDayViewModel @Inject constructor(
    private val myAlarms: GetAlarmsFromDay,
    private val setAlarmIsComplete: SetAlarmIsComplete
): ViewModel() {
    val alarms = MutableLiveData<MutableList<Alarm>>()

    fun getAlarmsFromDay(day: Int, month: Int, year: Int) {
        alarms.postValue(myAlarms(day, month, year))
    }
    fun changeCompleteState(alarm: Alarm) {
        setAlarmIsComplete(alarm)
    }
}