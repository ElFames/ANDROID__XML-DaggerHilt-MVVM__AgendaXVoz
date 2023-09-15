package agenda.x.voz.domain.model

import agenda.x.voz.data.database.entities.AlarmEntity
import agenda.x.voz.data.model.AlarmModel
import java.util.*

data class Alarm(
    val id: Long,
    var name: String,
    var day: Int?,
    var month: Int?,
    var year: Int?,
    var hour: Int,
    var minute: Int,
    var repeat: Boolean,
    var repeatDay: Boolean,
    var complete: Boolean,
    var audioFilePath: String
) {
    fun getDate(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year!!)
        calendar.set(Calendar.MONTH, month!! )
        calendar.set(Calendar.DAY_OF_MONTH, day!!)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        return calendar
    }
}
fun AlarmModel.toDomain() = Alarm(id, name,day,month,year,hour,minute,repeat,repeatDay,complete,audioFilePath)
fun AlarmEntity.toDomain(): Alarm {
    return Alarm(id, name,day,month,year,hour,minute,repeat, repeatDay ,complete,audioFilePath)
}