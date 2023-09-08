package agenda.x.voz.ui.views.recycler_components.interfaces
import agenda.x.voz.domain.model.Alarm

interface OnClickAlarmListener {
    fun onClickViewAlarm(alarm: Alarm)
    fun onClickEditAlarm(alarm: Alarm)
    fun onClickCompleteAlarm(alarm: Alarm)
    fun onClickPostponeAlarm(alarm: Alarm)
}