package agenda.x.voz.ui.views.alarmViews

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agenda.x.voz.R
import agenda.x.voz.data.model.toModel
import agenda.x.voz.ui.views.recyclerComponents.adapters.AlarmAdapter
import agenda.x.voz.ui.views.recyclerComponents.interfaces.OnClickAlarmListener
import agenda.x.voz.databinding.FragmentTodayAlarmsBinding
import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.ui.viewModels.TodayAlarmsViewModel
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class TodayAlarmsFragment : Fragment(), OnClickAlarmListener {
    private lateinit var binding: FragmentTodayAlarmsBinding
    private val todayAlarmsViewModel: TodayAlarmsViewModel by viewModels()
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private var recyclerAlarmList = mutableListOf<Alarm>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTodayAlarmsBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todayAlarmsViewModel.getTodayAlarms()
        onClickListeners()
        observerAlarmsChange()
    }

    private fun observerAlarmsChange() {
        todayAlarmsViewModel.alarms.observe(this) { alarms ->
            if(alarms.isNullOrEmpty()){}
            //binding.alarmsEmpty.visibility = View.VISIBLE
            else loadRecyclerView(alarms)
        }
    }

    private fun onClickListeners() {
        onClickNewAlarmButton()
        onClickViewTomorrowAlarms()
    }
    private fun onClickViewTomorrowAlarms() {
        binding.tomorrowTitle.setOnClickListener {
            findNavController().navigate(R.id.action_todayAlarmsFragment_to_tomorrowAlarmsFragment)
        }
    }
    private fun onClickNewAlarmButton() {
        binding.newAlarmButton.setOnClickListener {
            findNavController().navigate(R.id.action_todayAlarmsFragment_to_newAlarmFragment)
        }
    }
    private fun loadRecyclerView(alarms: MutableList<Alarm>) {
        recyclerAlarmList = alarms
        alarmAdapter = AlarmAdapter(recyclerAlarmList, requireActivity(), this, resources)
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
        findNavController().navigate(R.id.action_todayAlarmsFragment_to_detailAlarmFragment, bundle)
    }

    override fun onClickEditAlarm(alarm: Alarm) {
        val bundle = Bundle()
        bundle.putParcelable("alarm", alarm.toModel())
        findNavController().navigate(R.id.action_todayAlarmsFragment_to_editAlarmsFragment, bundle)
    }

    override fun onClickCompleteAlarm(alarm: Alarm) {
        alarm.complete = !alarm.complete
        todayAlarmsViewModel.changeCompleteState(alarm)
        todayAlarmsViewModel.getTodayAlarms()
        observerAlarmsChange()
    }

}