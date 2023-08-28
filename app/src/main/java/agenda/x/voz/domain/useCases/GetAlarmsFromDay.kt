package agenda.x.voz.domain.useCases

import agenda.x.voz.data.repositories.AlarmRepository
import agenda.x.voz.domain.model.Alarm
import javax.inject.Inject

class GetAlarmsFromDay @Inject constructor(
    private val repository: AlarmRepository
) {
    operator fun invoke(day: Int, month: Int, year: Int):MutableList<Alarm>{
        return repository.getAlarmsFromDay(day, month, year)
    }
}