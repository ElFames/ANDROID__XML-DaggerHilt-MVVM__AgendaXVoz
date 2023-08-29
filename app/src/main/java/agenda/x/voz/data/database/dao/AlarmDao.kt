package agenda.x.voz.data.database.dao

import agenda.x.voz.data.database.entities.AlarmEntity
import androidx.room.*
import retrofit2.http.DELETE

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: AlarmEntity)

    @Query("SELECT * FROM alarms")
    suspend fun getAllAlarms(): MutableList<AlarmEntity>

    @Query("SELECT * FROM alarms WHERE day = :day AND month = :month AND year = :year AND hour = :hour AND minute = :minute")
    suspend fun getAlarmByDate(day: Int, month: Int, year: Int, hour: Int, minute: Int): AlarmEntity?

    @Query("DELETE FROM alarms")
    suspend fun deleteAllAlarms()

    @Update(AlarmEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAlarmCompleteState(alarm: AlarmEntity)

    @Query("SELECT * FROM alarms WHERE id = :id")
    suspend fun getAlarmById(id: Long): AlarmEntity?

    @Query("DELETE FROM alarms WHERE id = :id")
    suspend fun deleteAlarmById(id: Long)
}