package agenda.x.voz.ui.views.alarm_views.recycler_views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agenda.x.voz.R
import agenda.x.voz.core.notifications.MyAlarmManager
import agenda.x.voz.data.model.toModel
import agenda.x.voz.ui.views.recycler_components.adapters.AlarmAdapter
import agenda.x.voz.ui.views.recycler_components.interfaces.OnClickAlarmListener
import agenda.x.voz.databinding.FragmentTodayAlarmsBinding
import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.ui.viewModels.TodayAlarmsViewModel
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.*

@AndroidEntryPoint
class TodayAlarmsFragment : Fragment(), OnClickAlarmListener {
    private lateinit var binding: FragmentTodayAlarmsBinding
    private val todayAlarmsViewModel: TodayAlarmsViewModel by viewModels()
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private var recyclerAlarmList = mutableListOf<Alarm>()
    private lateinit var myPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTodayAlarmsBinding.inflate(layoutInflater)
        MyAlarmManager.notification3DaysOffline(requireActivity())
        firstSharedConfiguration()
        checkLastConection()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkForUpdates()
        todayAlarmsViewModel.getTodayAlarms()
        observerAlarmsChange()
        onClickListeners()
    }

    private fun checkForUpdates() {
        val appVersion = getAppVersion()
        todayAlarmsViewModel.getLatestVersion()
        todayAlarmsViewModel.myLatestVersion.observe(this) {
            if (it > appVersion) {
                showUpdateVersionAlertDialog()
            }
        }

    }

    private fun showUpdateVersionAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Actualización disponible")
            .setMessage("Hay una nueva versión de la aplicación! Descargala en Play Store")
            .setPositiveButton("Actualizar") { _: DialogInterface, _: Int ->
                val url = "https://play.google.com/store/apps/details?id=agenda.x.voz"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
            .setOnDismissListener {
                showUpdateVersionAlertDialog()
            }
            .create()
            .show()
    }

    private fun getAppVersion(): Int {
        return try {
            val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            0
        }
    }

    private fun observerAlarmsChange() {
        todayAlarmsViewModel.alarms.observe(this) { alarms ->
            binding.recyclerIsEmpty.visibility =
                if (alarms.isNullOrEmpty()) View.VISIBLE
                else View.GONE
            loadRecyclerView(alarms)
        }
    }

    private fun onClickListeners() {
        onClickNewAlarmButton()
        onClickViewTomorrowAlarms()
    }

    private fun firstSharedConfiguration() {
        myPreferences = requireActivity().getSharedPreferences("MyDarkModePreferences", Context.MODE_PRIVATE)
        val darkMode = myPreferences.getBoolean("dark_mode",false)
        if (!darkMode) {
            myPreferences.apply {
                edit {
                    putBoolean("dark_mode", false)
                    apply()
                }
            }
        }
    }

    private fun checkLastConection() {
        val darkMode = myPreferences.getBoolean("dark_mode", false)
        if (darkMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun onClickViewTomorrowAlarms() {
        binding.tomorrowTitle.setOnClickListener {
            findNavController().navigate(R.id.action_todayAlarmsFragment_to_tomorrowAlarmsFragment)
        }
    }

    private fun onClickNewAlarmButton() {
        binding.newAlarmButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val selectedYear = calendar.get(Calendar.YEAR)
            val selectedMonth = calendar.get(Calendar.MONTH) + 1
            val selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
            val bundle = Bundle()
            bundle.putInt("selected_year", selectedYear)
            bundle.putInt("selected_month", selectedMonth)
            bundle.putInt("selected_day", selectedDay)
            findNavController().navigate(R.id.action_todayAlarmsFragment_to_newAlarmFragment, bundle)
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

    override fun onClickPostponeAlarm(alarm: Alarm) {
        runBlocking { todayAlarmsViewModel.postponeMyAlarm(alarm, requireActivity()) }
        todayAlarmsViewModel.getTodayAlarms()
        observerAlarmsChange()
    }

}