package agenda.x.voz.ui.views.alarm_views.recycler_views

import agenda.x.voz.R
import agenda.x.voz.data.model.toModel
import agenda.x.voz.databinding.FragmentTodayAlarmsBinding
import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.ui.viewModels.AlarmsFromDayViewModel
import agenda.x.voz.ui.views.recycler_components.adapters.AlarmAdapter
import agenda.x.voz.ui.views.recycler_components.interfaces.OnClickAlarmListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmsFromDayFragment: Fragment(), OnClickAlarmListener {
    private lateinit var binding: FragmentTodayAlarmsBinding
    private val alarmsFromDayViewModel: AlarmsFromDayViewModel by viewModels()
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private var recyclerAlarmList = mutableListOf<Alarm>()
    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDay = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTodayAlarmsBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedYear = arguments?.getInt("selected_year", 0) ?: 0
        selectedMonth = arguments?.getInt("selected_month", 0) ?: 0
        selectedDay = arguments?.getInt("selected_day", 0) ?: 0

        binding.tomorrowTitle.visibility = View.GONE
        binding.todayTitle.text = "$selectedDay - $selectedMonth - $selectedYear"

        alarmsFromDayViewModel.getAlarmsFromDay(selectedDay,selectedMonth,selectedYear)
        observerAlarmsChange()
        onClickNewAlarm()
    }

    private fun observerAlarmsChange() {
        alarmsFromDayViewModel.alarms.observe(this) { alarms ->
            binding.recyclerIsEmpty.visibility =
                if (alarms.isNullOrEmpty()) View.VISIBLE
                else View.GONE
            loadRecyclerView(alarms)
        }
    }

    private fun onClickNewAlarm() {
        binding.newAlarmButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("selected_year", selectedYear)
            bundle.putInt("selected_month", selectedMonth)
            bundle.putInt("selected_day", selectedDay)
            findNavController().navigate(R.id.action_alarmsFromDayFragment_to_newAlarmFragment, bundle)
        }
    }
    private fun loadRecyclerView(alarms: MutableList<Alarm>) {
        recyclerAlarmList = alarms
        alarmAdapter = AlarmAdapter(recyclerAlarmList, requireActivity(), this, false)
        linearLayoutManager = LinearLayoutManager(context)
        startRecyclerView()
    }

    private fun startRecyclerView() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = alarmAdapter
        }
    }
    override fun onClickViewAlarm(alarm: Alarm) {
        val bundle = Bundle()
        bundle.putParcelable("alarm", alarm.toModel())
        findNavController().navigate(R.id.action_alarmsFromDayFragment_to_detailAlarmFragment, bundle)
    }

    override fun onClickEditAlarm(alarm: Alarm) {
        val bundle = Bundle()
        bundle.putParcelable("alarm", alarm.toModel())
        findNavController().navigate(R.id.action_alarmsFromDayFragment_to_editAlarmsFragment, bundle)
    }

    override fun onClickCompleteAlarm(alarm: Alarm) {
        alarm.complete = !alarm.complete
        alarmsFromDayViewModel.changeCompleteState(alarm)
        alarmsFromDayViewModel.getAlarmsFromDay(selectedDay,selectedMonth,selectedYear)
        observerAlarmsChange()
    }

    override fun onClickPostponeAlarm(alarm: Alarm) {
        alarmsFromDayViewModel.postponeMyAlarm(alarm)
        alarmsFromDayViewModel.getAlarmsFromDay(selectedDay,selectedMonth,selectedYear)
        observerAlarmsChange()
    }
}