package agenda.x.voz.domain.model

import agenda.x.voz.data.database.entities.AlarmEntity
import agenda.x.voz.data.model.AlarmModel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Alarm(
    val id: Long,
    var name: String,
    var day: Int?,
    var month: Int?,
    var year: Int?,
    var hour: Int,
    var minute: Int,
    var repeat: Boolean,
    var complete: Boolean,
    var audioFilePath: String
)
fun AlarmModel.toDomain() = Alarm(id, name,day,month,year,hour,minute,repeat,complete,audioFilePath)
fun AlarmEntity.toDomain() = Alarm(id, name,day,month,year,hour,minute,repeat,complete,audioFilePath)