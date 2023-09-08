package agenda.x.voz.ui.viewModels

import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.domain.use_cases.GetFutureAlarms
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val futureAlarms: GetFutureAlarms
): ViewModel() {
    val alarms = MutableLiveData<MutableList<Alarm>>()
    val datesWithEvents = MutableLiveData<List<Calendar>>()

    fun getFutureAlarms() {
        viewModelScope.launch {
            val futureAlarms = futureAlarms()
            alarms.postValue(futureAlarms)
        }
    }

    fun getDatesWithEvents() {
        val myDates = mutableListOf<Calendar>()
        alarms.value?.forEach { alarm ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, alarm.year!!)
            calendar.set(Calendar.MONTH, alarm.month!! - 1)
            calendar.set(Calendar.DAY_OF_MONTH, alarm.day!!)
            myDates.add(calendar)
        }
        datesWithEvents.postValue(myDates)
    }
}