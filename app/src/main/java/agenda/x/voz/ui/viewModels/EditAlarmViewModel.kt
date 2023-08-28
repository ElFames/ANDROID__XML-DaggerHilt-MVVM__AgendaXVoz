package agenda.x.voz.ui.viewModels

import agenda.x.voz.domain.useCases.EditAlarm
import agenda.x.voz.domain.model.Alarm
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditAlarmViewModel @Inject constructor(
    private val editAlarm: EditAlarm
): ViewModel() {
    val alarmToEditAlarm = MutableLiveData<Alarm>()

    fun setAlarm(alarm: Alarm) = alarmToEditAlarm.postValue(alarm)
    fun editMyAlarm(alarm: Alarm) = editAlarm(alarm)
}