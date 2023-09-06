package agenda.x.voz.ui.views.calendarViews

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import agenda.x.voz.R
import agenda.x.voz.databinding.FragmentCalendarBinding
import agenda.x.voz.ui.viewModels.CalendarViewModel
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.DayViewDecorator
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private val calendarViewModel: CalendarViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCalendarBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().popBackStack()
        setCurrentTime()
        calendarViewModel.getFutureAlarms()
        observerAlarmsChange()
        onDaySelected()
        onClickViewAllAlarms()
    }

    private fun onClickViewAllAlarms() {
        binding.viewHistoryButton.setOnClickListener {
            findNavController().navigate(R.id.action_calendarFragment_to_allAlarmsFragment)
        }
    }

    private fun observerAlarmsChange() {
        calendarViewModel.alarms.observe(this) { alarms ->
        }
    }

    private fun onDaySelected() {
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val bundle = Bundle()
            bundle.putInt("selected_year", year)
            bundle.putInt("selected_month", month + 1)
            bundle.putInt("selected_day", dayOfMonth)
            findNavController().navigate(R.id.action_calendarFragment_to_alarmsFromDayFragment, bundle)
        }
    }

    private fun setCurrentTime() {
        val calendar = Calendar.getInstance()
        binding.calendarView.date = calendar.timeInMillis
    }

}