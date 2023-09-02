package agenda.x.voz.ui.views.alarmViews

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agenda.x.voz.databinding.FragmentTodayAlarmsBinding
import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.ui.viewModels.AllAlarmsViewModel
import agenda.x.voz.ui.views.recyclerComponents.adapters.AlarmAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllAlarmsFragment : Fragment() {
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
        binding.tomorrowTitle.text = "Ver Historial"
        binding.todayTitle.text = "Futuras Tareas"

        alarmsFromDayViewModel.getFutureAlarms()
        observerAlarmsChange()
        onClickListeners()
    }

    private fun onClickListeners() {
        onClickViewPastAlarms()
    }

    private fun observerAlarmsChange() {
        alarmsFromDayViewModel.alarms.observe(this) { alarms ->
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
        }
    }

    private fun loadRecyclerView(alarms: MutableList<Alarm>) {
        recyclerAlarmList = alarms
        alarmAdapter = AlarmAdapter(recyclerAlarmList, requireActivity(), null, true)
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
}