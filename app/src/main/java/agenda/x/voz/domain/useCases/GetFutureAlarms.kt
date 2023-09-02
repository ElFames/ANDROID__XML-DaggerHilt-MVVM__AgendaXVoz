package agenda.x.voz.domain.useCases

import agenda.x.voz.data.repositories.AlarmRepository
import agenda.x.voz.domain.model.Alarm
import javax.inject.Inject

class GetFutureAlarms @Inject constructor(
    private val repository: AlarmRepository
) {
    suspend operator fun invoke():MutableList<Alarm>{
        return repository.getFutureAlarms()
    }
}