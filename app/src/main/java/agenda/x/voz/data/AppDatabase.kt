package agenda.x.voz.data

import agenda.x.voz.data.database.dao.AlarmDao
import agenda.x.voz.data.database.entities.AlarmEntity
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AlarmEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
}