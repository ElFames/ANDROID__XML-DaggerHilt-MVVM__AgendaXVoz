package agenda.x.voz.ui.viewModels

import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.domain.useCases.InsertNewAlarm
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewAlarmViewModel @Inject constructor(
    private val insertNewAlarm: InsertNewAlarm
): ViewModel() {
    val savedAlarm = MutableLiveData<Alarm>()

    fun saveAlarm(alarmData: MutableMap<String, Any>) {
        val alarm = insertNewAlarm(alarmData)
        savedAlarm.postValue(alarm!!)
        alarm?.let {
            //savedAlarm.postValue(alarm)
        }
    }
}