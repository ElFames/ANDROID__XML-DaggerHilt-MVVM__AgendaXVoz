package agenda.x.voz.ui.viewModels

import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.domain.use_cases.InsertNewAlarm
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewAlarmViewModel @Inject constructor(
    private val insertNewAlarm: InsertNewAlarm
): ViewModel() {
    val savedAlarm = MutableLiveData<Alarm>()

    fun saveAlarm(alarmData: MutableMap<String, Any>) {
        viewModelScope.launch {
            val alarm = insertNewAlarm(alarmData)
            savedAlarm.postValue(alarm!!)
        }
    }
}