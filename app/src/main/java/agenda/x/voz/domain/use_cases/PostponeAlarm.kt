package agenda.x.voz.domain.use_cases

import agenda.x.voz.data.repositories.AlarmRepository
import agenda.x.voz.domain.model.Alarm
import android.app.Activity
import android.content.Context
import javax.inject.Inject

class PostponeAlarm @Inject constructor(
    private val repository: AlarmRepository
) {
    suspend operator fun invoke(alarm: Alarm, activity: Activity) {
        repository.postponeAlarm(alarm, activity)
    }
}