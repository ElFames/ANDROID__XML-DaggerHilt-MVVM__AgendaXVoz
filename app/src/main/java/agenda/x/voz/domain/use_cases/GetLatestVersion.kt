package agenda.x.voz.domain.use_cases

import agenda.x.voz.data.repositories.AlarmRepository
import javax.inject.Inject

class GetLatestVersion @Inject constructor(
    private val repository: AlarmRepository
) {
    suspend operator fun invoke(): Int {
        return repository.getLatestVersion()
    }
}