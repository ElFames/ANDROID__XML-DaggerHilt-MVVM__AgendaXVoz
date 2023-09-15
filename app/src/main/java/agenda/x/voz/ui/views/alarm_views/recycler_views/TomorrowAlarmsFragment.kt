package agenda.x.voz.ui.views.alarm_views.recycler_views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agenda.x.voz.R
import agenda.x.voz.data.model.toModel
import agenda.x.voz.databinding.FragmentTomorrowAlarmsBinding
import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.ui.viewModels.TomorrowAlarmsViewModel
import agenda.x.voz.ui.views.recycler_components.adapters.AlarmAdapter
import agenda.x.voz.ui.views.recycler_components.interfaces.OnClickAlarmListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.*

@AndroidEntryPoint
class TomorrowAlarmsFragment : Fragment(), OnClickAlarmListener {
    private lateinit var binding: FragmentTomorrowAlarmsBinding
    private val tomorrowAlarmsViewModel: TomorrowAlarmsViewModel by viewModels()
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private var recyclerAlarmList = mutableListOf<Alarm>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTomorrowAlarmsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tomorrowAlarmsViewModel.getTomorrowAlarms()
        observerAlarmChange()
        onClickNewAlarmButton()
        binding.todayTitle.setOnClickListener {
            findNavController().navigate(R.id.action_tomorrowAlarmsFragment_to_todayAlarmsFragment)
        }
    }

    private fun observerAlarmChange() {
        tomorrowAlarmsViewModel.alarms.observe(this) { alarms ->
            binding.recyclerIsEmpty.visibility =
                if (alarms.isNullOrEmpty()) View.VISIBLE
                else View.GONE
            loadRecyclerView(alarms)
        }
    }

    private fun loadRecyclerView(alarms: MutableList<Alarm>) {
        recyclerAlarmList = alarms
        alarmAdapter = AlarmAdapter(recyclerAlarmList, requireActivity(), this, false)
        linearLayoutManager = LinearLayoutManager(context)
        startRecyclerView()
    }

    private fun onClickNewAlarmButton() {
        binding.newAlarmButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val selectedYear = calendar.get(Calendar.YEAR)
            val selectedMonth = calendar.get(Calendar.MONTH) + 1
            val selectedDay = calendar.get(Calendar.DAY_OF_MONTH) + 1
            val bundle = Bundle()
            bundle.putInt("selected_year", selectedYear)
            bundle.putInt("selected_month", selectedMonth)
            bundle.putInt("selected_day", selectedDay)
            findNavController().navigate(R.id.action_tomorrowAlarmsFragment_to_newAlarmFragment, bundle)
        }
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
        findNavController().navigate(R.id.action_tomorrowAlarmsFragment_to_detailAlarmFragment, bundle)
    }

    override fun onClickEditAlarm(alarm: Alarm) {
        val bundle = Bundle()
        bundle.putParcelable("alarm", alarm.toModel())
        findNavController().navigate(R.id.action_tomorrowAlarmsFragment_to_editAlarmsFragment, bundle)
    }

    override fun onClickCompleteAlarm(alarm: Alarm) {
        alarm.complete = !alarm.complete
        tomorrowAlarmsViewModel.changeCompleteState(alarm)
        tomorrowAlarmsViewModel.getTomorrowAlarms()
        observerAlarmChange()
    }

    override fun onClickPostponeAlarm(alarm: Alarm) {
        runBlocking { tomorrowAlarmsViewModel.postponeMyAlarm(alarm, requireActivity()) }
        tomorrowAlarmsViewModel.getTomorrowAlarms()
        observerAlarmChange()
    }
}


