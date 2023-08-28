package agenda.x.voz.ui.views.recyclerComponents.interfaces
import agenda.x.voz.domain.model.Alarm

interface OnClickAlarmListener {
    fun onClickViewAlarm(alarm: Alarm)
    fun onClickEditAlarm(alarm: Alarm)
    fun onClickCompleteAlarm(alarm: Alarm)
}