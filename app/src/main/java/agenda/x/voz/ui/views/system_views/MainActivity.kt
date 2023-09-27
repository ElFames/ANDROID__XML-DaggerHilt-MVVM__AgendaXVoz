package agenda.x.voz.ui.views.system_views

import agenda.x.voz.R
import agenda.x.voz.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        createBottomNavigatonView()
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

}