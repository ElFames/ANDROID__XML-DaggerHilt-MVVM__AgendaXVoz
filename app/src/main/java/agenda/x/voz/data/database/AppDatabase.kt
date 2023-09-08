package agenda.x.voz.data.database

import agenda.x.voz.data.database.dao.AlarmDao
import agenda.x.voz.data.database.entities.AlarmEntity
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [AlarmEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE alarms ADD COLUMN repeat_day INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
    abstract fun alarmDao(): AlarmDao
}