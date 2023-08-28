package agenda.x.voz.data.database.entities

import agenda.x.voz.domain.model.Alarm
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "day") var day: Int?,
    @ColumnInfo(name = "month") var month: Int?,
    @ColumnInfo(name = "year") var year: Int?,
    @ColumnInfo(name = "hour") var hour: Int,
    @ColumnInfo(name = "minute") var minute: Int,
    @ColumnInfo(name = "repeat") var repeat: Boolean,
    @ColumnInfo(name = "complete") var complete: Boolean,
    @ColumnInfo(name = "audio_file_path") var audioFilePath: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

fun Alarm.toDatabase(): AlarmEntity {
    val alarmEntity = AlarmEntity(name, day, month, year, hour, minute, repeat, complete, audioFilePath)
    alarmEntity.id = id
    return alarmEntity
}