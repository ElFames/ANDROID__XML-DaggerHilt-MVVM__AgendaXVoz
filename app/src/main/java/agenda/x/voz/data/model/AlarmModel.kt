package agenda.x.voz.data.model

import agenda.x.voz.domain.model.Alarm
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class AlarmModel(
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
) : Parcelable

fun Alarm.toModel() = AlarmModel(id, name, day, month, year, hour, minute, repeat, repeatDay, complete, audioFilePath)