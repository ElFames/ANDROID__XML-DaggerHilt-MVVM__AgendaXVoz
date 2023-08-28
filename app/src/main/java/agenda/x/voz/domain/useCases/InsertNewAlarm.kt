package agenda.x.voz.domain.useCases

import agenda.x.voz.data.repositories.AlarmRepository
import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.domain.model.toDomain
import javax.inject.Inject

class InsertNewAlarm @Inject constructor(
    private val repository: AlarmRepository
) {
    operator fun invoke(alarmData: MutableMap<String, Any>): Alarm? {
        return repository.insertNewAlarm(alarmData)?.toDomain()
    }
}