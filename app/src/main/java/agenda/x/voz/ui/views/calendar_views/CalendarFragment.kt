package agenda.x.voz.ui.views.calendar_views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import agenda.x.voz.R
import agenda.x.voz.databinding.FragmentCalendarBinding
import agenda.x.voz.ui.viewModels.CalendarViewModel
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private val calendarViewModel: CalendarViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCalendarBinding.inflate(layoutInflater)
        calendarViewModel.getFutureAlarms()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCalendarProperties()
        observerAlarmsChange()
        onDaySelected()
        onClickViewAllAlarms()
    }

    private fun loadCalendarProperties() {
        binding.calendarView.setDayColumnNames(arrayOf("Lu","Ma","Mi","Ju","Vi","Sa","Do"))
        val formattedDate = calendarDateToFormattedDate(Calendar.getInstance().time)
        binding.calendarDate.text = formattedDate
    }

    private fun calendarDateToFormattedDate(date: Date): String {
        val dateFormat = SimpleDateFormat("MMMM 'de' yyyy", Locale("es", "ES"))
        return dateFormat.format(date).replaceFirstChar { it.uppercase() }
    }

    private fun onClickViewAllAlarms() {
        binding.viewHistoryButton.setOnClickListener {
            findNavController().navigate(R.id.action_calendarFragment_to_allAlarmsFragment)
        }
    }

    private fun observerAlarmsChange() {
        calendarViewModel.alarms.observe(this) {
            calendarViewModel.getDatesWithEvents()
        }
        calendarViewModel.datesWithEvents.observe(this) { dateEventList ->
            dateEventList.forEach { date ->
                binding.calendarView.addEvent(Event(resources.getColor(R.color.red), date.timeInMillis))
            }
        }
    }

    private fun onDaySelected() {
        binding.calendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                val calendar = Calendar.getInstance()
                calendar.time = dateClicked

                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

                val bundle = Bundle()
                bundle.putInt("selected_year", year)
                bundle.putInt("selected_month", month)
                bundle.putInt("selected_day", dayOfMonth)

                findNavController().navigate(R.id.action_calendarFragment_to_alarmsFromDayFragment, bundle)
            }

            override fun onMonthScroll(dateNewMonth: Date) {
                val formattedDate = calendarDateToFormattedDate(dateNewMonth)
                binding.calendarDate.text = formattedDate
            }
        })
    }
}