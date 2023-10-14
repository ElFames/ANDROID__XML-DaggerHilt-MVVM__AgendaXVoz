package agenda.x.voz.ui.views.alarm_views

import agenda.x.voz.R
import agenda.x.voz.data.model.toModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agenda.x.voz.databinding.FragmentTodayAlarmsBinding
import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.ui.viewModels.AllAlarmsViewModel
import agenda.x.voz.ui.views.recycler_components.adapters.AlarmAdapter
import agenda.x.voz.ui.views.recycler_components.interfaces.OnClickAlarmListener
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import okhttp3.internal.cache2.Relay.Companion.edit

@AndroidEntryPoint
class AllAlarmsFragment : Fragment(), OnClickAlarmListener {
    private lateinit var binding: FragmentTodayAlarmsBinding
    private val alarmsFromDayViewModel: AllAlarmsViewModel by viewModels()
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private var recyclerAlarmList = mutableListOf<Alarm>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodayAlarmsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newAlarmButton.visibility = View.GONE
        binding.tomorrowTitle.text = "Historial"
        binding.todayTitle.text = "Próximas Tareas"

        alarmsFromDayViewModel.getFutureAlarms()
        observerAlarmsChange()
        onClickListeners()
    }

    private fun onClickListeners() {
        onClickViewPastAlarms()
    }

    private fun observerAlarmsChange() {
        alarmsFromDayViewModel.alarms.observe(viewLifecycleOwner) { alarms ->
            binding.recyclerIsEmpty.visibility =
                if (alarms.isNullOrEmpty()) View.VISIBLE
                else View.GONE
            loadRecyclerView(alarms)
        }
    }

    private fun onClickViewPastAlarms() {
        binding.tomorrowTitle.setOnClickListener {
            Toast.makeText(requireContext(), "Próximamente!", Toast.LENGTH_SHORT).show()
            binding.tomorrowTitle.isEnabled = false
            Handler().postDelayed( { binding.tomorrowTitle.isEnabled = true },4000)
        }
    }

    private fun loadRecyclerView(alarms: MutableList<Alarm>) {
        recyclerAlarmList = alarms
        alarmAdapter = AlarmAdapter(recyclerAlarmList, requireActivity(), this, true)
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

    override fun onClickDeleteAlarm(alarm: Alarm) {
        val bundle = Bundle()
        bundle.putParcelable("alarm", alarm.toModel())
        findNavController().navigate(R.id.action_allAlarmFragment_to_detailAlarmFragment, bundle)
    }

    override fun onClickEditAlarm(alarm: Alarm) {
        val bundle = Bundle()
        bundle.putParcelable("alarm", alarm.toModel())
        findNavController().navigate(R.id.action_allAlarmFragment_to_editAlarmsFragment, bundle)
    }

    override fun onClickCompleteAlarm(alarm: Alarm) {
        alarm.complete = !alarm.complete
        alarmsFromDayViewModel.changeCompleteState(alarm)
        observerAlarmsChange()
    }

    override fun onClickPostponeAlarm(alarm: Alarm) {
        runBlocking { alarmsFromDayViewModel.postponeMyAlarm(alarm, requireActivity()) }
        observerAlarmsChange()
    }
}