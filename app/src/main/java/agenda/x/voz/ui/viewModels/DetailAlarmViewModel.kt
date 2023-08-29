package agenda.x.voz.ui.viewModels

import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.domain.useCases.DeleteAlarm
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
    fun deleteCurrentAlarm() = deleteAlarm(alarm.value!!)
}