package agenda.x.voz.ui.views.calendar_views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import agenda.x.voz.R
import agenda.x.voz.data.model.toModel
import agenda.x.voz.databinding.FragmentCalendarBinding
import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.ui.viewModels.CalendarViewModel
import agenda.x.voz.ui.views.recycler_components.adapters.AlarmAdapter
import agenda.x.voz.ui.views.recycler_components.interfaces.OnClickAlarmListener
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CalendarFragment : Fragment(), OnClickAlarmListener {
    private lateinit var binding: FragmentCalendarBinding
    private val calendarViewModel: CalendarViewModel by viewModels()
    private lateinit var myPreferences: SharedPreferences
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private var recyclerAlarmList = mutableListOf<Alarm>()
    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDay = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkForAppUpdates()

        firstSharedPreferencesConfig()
        checkNightModeFromLastConection()

        setInitialCalendarProperties()

        observerAlarmsChange()

        onDaySelected()
        onClickNewAlarm()
    }

    private fun firstSharedPreferencesConfig() {
        myPreferences = requireActivity().getSharedPreferences("MyDarkModePreferences", Context.MODE_PRIVATE)
        val darkMode = myPreferences.getBoolean("dark_mode", false)
        if (!darkMode) {
            myPreferences.apply {
                edit {
                    putBoolean("dark_mode", false)
                    apply()
                }
            }
        }
    }

    private fun checkNightModeFromLastConection() {
        val darkMode = myPreferences.getBoolean("dark_mode", false)
        if (darkMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun checkForAppUpdates() {
        try {
            val appVersion = getAppVersion()
            calendarViewModel.getLatestVersion()
            calendarViewModel.myLatestVersion.observe(viewLifecycleOwner) {
                if (it > appVersion) {
                    showUpdateVersionAlertDialog()
                }
            }
        } catch (e: Exception) {

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
            val packageInfo =
                requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            0
        }
    }

    private fun setInitialCalendarProperties() {
        binding.calendarView.setDayColumnNames(arrayOf("Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do"))
        val currentDate = Calendar.getInstance()
        selectedYear = currentDate.get(Calendar.YEAR)
        selectedMonth = currentDate.get(Calendar.MONTH) + 1
        selectedDay = currentDate.get(Calendar.DAY_OF_MONTH)
        val formattedDate = calendarDateToFormattedDate(currentDate.time)
        binding.calendarDate.text = formattedDate
        val stringMonth = formattedDate.replace("de 2023","")
        binding.toolbar.text = "$selectedDay de $stringMonth"
        calendarViewModel.getAlarmsFromDay(selectedDay, selectedMonth, selectedYear)
    }

    private fun calendarDateToFormattedDate(date: Date): String {
        val dateFormat = SimpleDateFormat("MMMM 'de' yyyy", Locale("es", "ES"))
        return dateFormat.format(date).replaceFirstChar { it.uppercase() }
    }

    private fun onClickNewAlarm() {
        binding.newAlarmButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("selected_year", selectedYear)
            bundle.putInt("selected_month", selectedMonth)
            bundle.putInt("selected_day", selectedDay)
            findNavController().navigate(R.id.action_calendarFragment_to_newAlarmFragment, bundle)
        }
    }

    private fun observerAlarmsChange() {
        calendarViewModel.alarms.observe(viewLifecycleOwner) {
            calendarViewModel.getDatesWithEvents()
        }

        calendarViewModel.datesWithEvents.observe(viewLifecycleOwner) { dateEventList ->
            dateEventList.forEach { date ->
                binding.calendarView.addEvent(
                    Event(
                        resources.getColor(R.color.red),
                        date.timeInMillis
                    )
                )
            }
        }

        calendarViewModel.alarmsForRecyclerList.observe(viewLifecycleOwner) { alarms ->
            binding.recyclerIsEmpty.visibility =
                if (alarms.isNullOrEmpty()) View.VISIBLE
                else View.GONE
            loadRecyclerView(alarms)
        }
    }

    private fun onDaySelected() {
        binding.calendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                val calendar = Calendar.getInstance()
                calendar.time = dateClicked
                selectedYear = calendar.get(Calendar.YEAR)
                selectedMonth = calendar.get(Calendar.MONTH) + 1
                selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
                calendarViewModel.getAlarmsFromDay(selectedDay, selectedMonth, selectedYear)
                val formattedDate = calendarDateToFormattedDate(dateClicked).replace("de 2023","")
                binding.toolbar.text = "$selectedDay de $formattedDate"
            }

            override fun onMonthScroll(dateNewMonth: Date) {
                val formattedDate = calendarDateToFormattedDate(dateNewMonth)
                binding.calendarDate.text = formattedDate
            }
        })
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

    override fun onClickDeleteAlarm(alarm: Alarm) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Tarea")
            .setMessage("¿Estás seguro que quieres eliminar esta tarea?")
            .setPositiveButton("Eliminar") { _,_ ->
                calendarViewModel.deleteAlarm(requireContext(), alarm)
            }
            .setNegativeButton("Cancelar") { alertDialog, _ ->
                alertDialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onClickEditAlarm(alarm: Alarm) {
        val bundle = Bundle()
        bundle.putParcelable("alarm", alarm.toModel())
        findNavController().navigate(R.id.action_calendarFragment_to_editAlarmsFragment, bundle)
    }

    override fun onClickCompleteAlarm(alarm: Alarm) {
        alarm.complete = !alarm.complete
        calendarViewModel.changeCompleteState(alarm)
        observerAlarmsChange()
    }

    override fun onClickPostponeAlarm(alarm: Alarm) {
        runBlocking { calendarViewModel.postponeMyAlarm(alarm, requireActivity()) }
        observerAlarmsChange()
    }


}