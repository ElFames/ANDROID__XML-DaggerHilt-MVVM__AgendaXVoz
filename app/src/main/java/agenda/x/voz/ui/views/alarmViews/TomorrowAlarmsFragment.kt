package agenda.x.voz.ui.views.alarmViews

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
import agenda.x.voz.ui.views.recyclerComponents.adapters.AlarmAdapter
import agenda.x.voz.ui.views.recyclerComponents.interfaces.OnClickAlarmListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint

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
        binding.todayTitle.setOnClickListener {
            findNavController().navigate(R.id.action_tomorrowAlarmsFragment_to_todayAlarmsFragment)
        }
    }

    private fun observerAlarmChange() {
        tomorrowAlarmsViewModel.alarms.observe(this) { alarms ->
            if(alarms.isNullOrEmpty()){}
            //binding.alarmsEmpty.visibility = View.VISIBLE
            else loadRecyclerView(alarms)
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
}


