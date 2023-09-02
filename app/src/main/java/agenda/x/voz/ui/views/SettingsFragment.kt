package agenda.x.voz.ui.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agenda.x.voz.R
import agenda.x.voz.databinding.FragmentCalendarBinding
import agenda.x.voz.databinding.FragmentSettingsBinding
import agenda.x.voz.ui.viewModels.CalendarViewModel
import androidx.fragment.app.viewModels

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    //private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.notificationSettings.setOnClickListener {
            binding.notificationLabel.visibility = View.VISIBLE
            binding.notificationSwitch.visibility = View.VISIBLE
        }

        binding.notificationSwitch.setOnClickListener {
            it.isSelected = !it.isSelected
            binding.notificationLabel.text = if (it.isSelected) "Notificaciones desactivadas" else "Notificaciones Activas"
        }
    }
}