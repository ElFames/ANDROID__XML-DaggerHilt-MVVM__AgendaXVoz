package agenda.x.voz.ui.views.recycler_components.adapters

import agenda.x.voz.R
import agenda.x.voz.ui.views.recycler_components.interfaces.OnClickAlarmListener
import agenda.x.voz.databinding.AlarmLayoutBinding
import agenda.x.voz.domain.model.Alarm
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class AlarmAdapter(private var alarms: MutableList<Alarm>?,
                   private val activity: FragmentActivity,
                   private val listener: OnClickAlarmListener?,
                   private val showDate: Boolean):RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = AlarmLayoutBinding.bind(view)

        fun setListener(alarm: Alarm) {
            binding.editAlarmButton.setOnClickListener {
                listener?.onClickEditAlarm(alarm)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_layout, parent, false)
        alarms = alarms?.sortedWith(compareBy<Alarm> { it.year }.thenBy { it.month }.thenBy { it.day }.thenBy { it.hour }.thenBy { it.minute })?.toMutableList()
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarm = alarms!![position]
        with(holder) {
            if (listener != null) {
                onClickRoot(binding, alarm)
                setListener(alarm)
                binding.completeAlarmButton.setOnClickListener {
                    listener.onClickCompleteAlarm(alarm)
                }
            }
            val date = "${alarm.day} / ${alarm.month} / ${alarm.year}"
            binding.date.text = date
            binding.date.visibility = if (showDate) View.VISIBLE else View.GONE
            binding.name.text = alarm.name
            binding.hours.text = String.format("%02d", alarm.hour)
            binding.minutes.text = String.format("%02d", alarm.minute)
            setAlarmState(binding, alarm)
            binding.deleteAlarmButton.setOnClickListener {
                listener?.onClickDeleteAlarm(alarm)
                notifyItemRemoved(position)
            }
        }
    }

    private fun onClickRoot(binding: AlarmLayoutBinding, alarm: Alarm) {
        binding.root.setOnClickListener {
        }
    }

    private fun setAlarmState(binding: AlarmLayoutBinding, alarm: Alarm) {
        if (alarm.repeat) {
            binding.state.text = activity.resources.getString(R.string.repeat_symbol)
            binding.state.textSize = 24f
            binding.labelState.text = "Semanalmente"
        } else if (alarm.repeatDay) {
            binding.state.text = activity.resources.getString(R.string.repeat_symbol)
            binding.state.textSize = 24f
            binding.labelState.text = "Diariamente"
        } else {
            if (alarm.complete) {
                binding.state.text = activity.resources.getString(R.string.green_circle)
                binding.labelState.text = "Completado"
                binding.completeAlarmButton.text = "Pendiente"
                binding.completeAlarmButton.setTextColor(activity.resources.getColor(R.color.orange))
            } else {
                binding.completeAlarmButton.text = "Completado"
                binding.completeAlarmButton.setTextColor(activity.resources.getColor(R.color.green))
                if (isDateTimePassed(alarm)) {
                    binding.state.text = activity.resources.getString(R.string.red_circle)
                    binding.labelState.text = "Pasado"
                } else {
                    binding.state.text = activity.resources.getString(R.string.orange_circle)
                    binding.labelState.text = "Pendiente"
                }
            }
        }
    }

    private fun isDateTimePassed(alarm: Alarm): Boolean {
        val date = "${alarm.day}/${alarm.month}/${alarm.year}"
        val time = "${alarm.hour}:${alarm.minute}"
        val dateTimePattern = "dd/MM/yyyy HH:mm"
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat(dateTimePattern, Locale.getDefault())
        val dateTime = dateFormat.parse("$date $time") ?: return false
        return dateTime.before(currentDate)
    }

    override fun getItemCount(): Int {
        return alarms!!.size
    }

}