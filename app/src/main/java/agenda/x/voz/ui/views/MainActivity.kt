package agenda.x.voz.ui.views

import agenda.x.voz.R
import agenda.x.voz.core.notifications.AlarmNotification
import agenda.x.voz.databinding.ActivityMainBinding
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var myPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notification3DaysOffline()
        firstSharedConfiguration()
        checkLastConection()

        supportActionBar?.hide()
        createBottomNavigatonView()
    }
    private fun notification3DaysOffline() {
        val notificationId = 333333333
        val intent = Intent(applicationContext, AlarmNotification::class.java)
        intent.putExtra("title", "Ya no organizas tus tareas...")
        intent.putExtra("message", "Llevas 3 dias sin conectarte, parece que estas teniendo unos dias relajados... También puedes añadir alarmas diarias o semanales!")
        intent.putExtra("notificationId", notificationId)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val dateTime = Calendar.getInstance()
        dateTime.add(Calendar.DAY_OF_MONTH, 3)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, dateTime.timeInMillis + 1000, pendingIntent)
    }
    private fun createBottomNavigatonView() {
        val bottomNavigationView = binding.bottomNavigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.todayAlarmsFragment,
            R.id.calendarFragment,
            R.id.settingsFragment
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            if (nd.id == R.id.todayAlarmsFragment || nd.id == R.id.tomorrowAlarmsFragment || nd.id == R.id.calendarFragment || nd.id == R.id.settingsFragment)
                bottomNavigationView.visibility = View.VISIBLE
            else bottomNavigationView.visibility = View.GONE
        }
    }
    private fun firstSharedConfiguration() {
        myPreferences = getSharedPreferences("MyDarkModePreferences", Context.MODE_PRIVATE)
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
}