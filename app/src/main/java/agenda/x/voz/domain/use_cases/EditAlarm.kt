package agenda.x.voz.domain.use_cases

import agenda.x.voz.data.repositories.AlarmRepository
import agenda.x.voz.domain.model.Alarm
import javax.inject.Inject

class EditAlarm @Inject constructor(
    private val repository: AlarmRepository
) {
    operator fun invoke(alarm: Alarm) {
        repository.updateAlarm(alarm)
    }
}