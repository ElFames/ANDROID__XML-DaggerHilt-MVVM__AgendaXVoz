package agenda.x.voz.ui.viewModels

import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.domain.useCases.GetFutureAlarms
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllAlarmsViewModel @Inject constructor(
    private val futureAlarms: GetFutureAlarms
): ViewModel() {
    val alarms = MutableLiveData<MutableList<Alarm>>()

    fun getFutureAlarms() {
        viewModelScope.launch {
            val futureAlarms = futureAlarms()
            alarms.postValue(futureAlarms)
        }
    }
}